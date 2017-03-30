/**
*Class:             ServerEvent.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven
*Date of Update:    30/03/2017
*Version:           1.3.1
*
*Purpose:           "Sends" packets to server at certain time over a certain period
*					Can be used to schedule commands, info, or errors.
*
* 
*Update Log			v1.3.1
*						- zero padding added
*					v1.3.0
*						- added method to return detailed string representing event
*					v1.2.2
*						- printing to DSKY fixed (typo)
*					v1.2.1
*						- override clear method
*						- toJSON added
*						- fromJSON added
*					v1.2.0
*						- date and time stored internally
*						- DSKY saved as a static variable, init once seperate from constructor of instances
*						- constructors rewritten
*						- toString rewritten
*					v1.1.0
*						- name field added to help keep track of what each event does
*						- instances have pointer back to Server DSKY, so they can print a message when triggered
*					v1.0.1
*						- toString added for testing
*					v1.0.0
*						- null
*/
package server.datatypes;


import java.util.LinkedList;
//import libraries
import java.util.TimerTask;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

//import packages
import network.*;
import server.*;
import io.json.JsonException;
import io.json.JsonFile;
import io.json.ToJSONFile;



public class ServerEvent extends TimerTask implements ToJSONFile
{
	//static class constants
	protected static final int PORT = MainServer.PORT;
	protected static final String PREFIX = "EVENT >> ";
	
	//local class variables
	protected static ServerDSKY display = null;
	
	//declaring local instance variables
	protected String eventName;
	protected TimeAndDate trigger;
	protected PacketWrapper[] commands;
	

	//null constructor
	public ServerEvent()
	{
		this("", new PacketWrapper[0], new TimeAndDate());
	}
	//generic constructor
	public ServerEvent(String eventName, PacketWrapper[] commands, TimeAndDate trigger)
	{
		super();
		this.commands = commands;
		this.eventName = eventName;
		this.trigger = trigger;
	}
	
	
	//hook a DSKY for the event to print to
	public static void hookDSKY(ServerDSKY dsky)
	{
		display = dsky;
	}
	
	
	//generic accessors
	public PacketWrapper[] getCommands()
	{
		return commands;
	}
	public TimeAndDate getTrigger()
	{
		return trigger;
	}
	public String getEventName()
	{
		return eventName;
	}
	
	
	//print the display
	private void println(String printable)
	{
		if(display != null)
		{
			display.println(PREFIX + printable + "\n");
		}
		else
		{
			System.out.println(PREFIX + printable);
		}
	}
	
	
	@Override
	public void run() 
	{
		//instantiate a DataChannel to send with
		//get local IP
		InetAddress localAddress = null;
		DataMultiChannel channel = null;
		try 
		{
			localAddress = InetAddress.getLocalHost();
			channel = new DataMultiChannel();
		}
		catch (SocketException e) {e.printStackTrace();} 
		catch (UnknownHostException e) {e.printStackTrace();}
		
		//send each packet data to the server
		//packets that are not COMMAND, INFO, or ERROR not supported for scheduling and are ignored
		try
		{
			println(this.toString() + " triggered!");
			channel.hijackChannel(localAddress, PORT);
			for(PacketWrapper wrapper : commands)
			{
				switch(wrapper.type())
				{
					//send cmd packet
					case(DataChannel.TYPE_CMD):
						channel.sendCmd(wrapper.commandKey(), wrapper.extraInfo());
						break;
					
					//send info packet
					case(DataChannel.TYPE_INFO):
						channel.sendInfo(wrapper.info());
						break;
					
					//send error packet
					case(DataChannel.TYPE_ERR):
						channel.sendErr(wrapper.errorMessage());
						break;
				}
			}
		}
		catch (NetworkException e){e.printStackTrace();}
		
		//close socket
		channel.close();
		channel = null;
	}
	
	
	@Override
	//marks the ServerEvent to not trigger
	public boolean cancel()
	{
		return cancel(true);
	}
	//marks the ServerEvent to not trigger without print
	protected boolean cancel(boolean print)
	{
		if(print)
		{
			println(this.toString() + " Canceled!");
		}
		return super.cancel();
	}
	
	
	@Override
	//show as a string (good for testing)
	public String toString()
	{
		String s = "\"" + eventName + "\" @ ";
		if(trigger != null)
		{
			s += trigger.toString();
		}
		else
		{
			s += "<no trigger information>";
		}
		return s;
	}
	
	
	//show as a detailed string
	public String toDetailedString()
	{
		String s = "-----------\n" + this.toString() + " triggers:\n";
		for(PacketWrapper cmd : commands)
		{
			s += cmd.toString() + "\n";
		}
		return (s + "-----------");
		
	}

	
	@Override
	//to Json
	public JsonFile toJSON(String baseOffset) 
	{
		//create empty json with prime block
		JsonFile json = new JsonFile(baseOffset);
		json.newBlock();
		
		//add event name and trigger
		json.addField("eventName", eventName);
		json.addField("trigger", trigger.toJSON(baseOffset+"\t"));
		
		//add all commands
		json.newBlock("commands");
		for(PacketWrapper cmd : commands)
		{
			json.add(cmd.toJSON(baseOffset+"\t\t").toString());
		}
		json.endBlock();
		
		//end prime block and return
		json.endBlock();
		return json;
	}

	
	@Override
	//from Json 
	public void fromJSON(String jsonFile) throws JsonException 
	{
		try
		{
			//declaring method variables
			int line = 0;
			
			/*split at newlines, remove all tabs, remove spaces
			 * keep one copy of tab-space filtered, separated at \n		fileLine
			 * keep one copy of tab filtered, separated at \n			fileLineSpace
			 */
			String intermediate = jsonFile.replaceAll("\t", "");
			String[] fileLineSpace = intermediate.split("\n");
			intermediate = intermediate.replaceAll(" ", "");
			String[] fileLine = intermediate.split("\n");
			intermediate = null;
			
			//make sure there is a starting block
			if (!fileLine[line].equals("{"))
			{
				throw new JsonException("No starting block", JsonException.ERR_FORMAT);
			}
			line++;
			
			//check for eventName field
			if (!fileLine[line].contains("\"eventName\":"))
			{
				throw new JsonException("\"eventName\" field not found", JsonException.ERR_BAD_FIELD);
			}
			//extract name
			String tempString = fileLineSpace[line].split(":",2)[1];
			this.eventName = tempString.substring(tempString.indexOf("\"")+1, tempString.length()-1);
			line++;
			
			//check for trigger object
			if (!fileLine[line].contains("\"trigger\":{"))
			{
				throw new JsonException("\"trigger\" object not found", JsonException.ERR_BAD_FIELD);
			}
			line++;
			//extract trigger object JSON
			String triggerJSON = "{\n";
			while(!fileLine[line].equals("}"))
			{
				triggerJSON += fileLineSpace[line]+"\n";
				line++;
			}
			triggerJSON += "}\n";
			trigger = new TimeAndDate();
			trigger.fromJSON(triggerJSON);
			line++;
			
			//check for command array
			if (!fileLine[line].contains("\"commands\":{"))
			{
				throw new JsonException("\"commands\" array not found", JsonException.ERR_BAD_FIELD);
			}
			line++;
			//extract all complex entries
			LinkedList<String> entries = new LinkedList<String>();
			while(!fileLine[line].equals("}"))
			{
				if(fileLine[line].equals("{") && fileLine[line+5].equals("}"))
				{
					String s="";
					for(int i=0; i<6; i++)
					{
						s += fileLineSpace[line] + "\n";
						line++;
					}
					entries.add(s);
				}
				else
				{
					throw new JsonException("Bad format for entry in \"commands\"", JsonException.ERR_FORMAT);
				}
			}
			//convert and add to array
			commands = new PacketWrapper[entries.size()];
			for(int i=0; i<commands.length; i++)
			{
				PacketWrapper w = new PacketWrapper();
				w.fromJSON(entries.get(i));
				commands[i] = w;
			}
		}
		catch(JsonException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new JsonException("Cannot build object: " + e.getMessage(), JsonException.ERR_COULD_NOT_BUILD);
		}
	}

	
	@Override
	//from raw bytes
	public void fromJSON(byte[] jsonFile) throws JsonException 
	{
		/* TODO there must be a better way to do this
		 * using char array so I don't need to make a new string each char add (each string is immutable)
		 */

		//cast each byte as a char
		char[] brokenString = new char[jsonFile.length];
		for(int i=0; i < jsonFile.length; i++)
		{
			brokenString[i] = (char)jsonFile[i];
		}
		
		//generate object from json file, now in String form
		fromJSON(new String(brokenString));
	}
}
