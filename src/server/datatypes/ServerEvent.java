/**
*Class:             ServerEvent.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven
*Date of Update:    02/03/2017
*Version:           1.0.0
*
*Purpose:           "Sends" packets to server at certain time over a certain period
*					Can be used to schedule commands, info, or errors.
*
*					Should only be used for commands
*
*
* 
*Update Log			v1.2.0
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


//import libraries
import java.util.TimerTask;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

//import packages
import network.*;
import server.*;



public class ServerEvent extends TimerTask 
{
	//static class constants
	private static final int PORT = MainServer.PORT;
	
	//local class variables
	private static ServerDSKY display = null;
	
	//declaring local instance variables
	private PacketWrapper[] commands;
	private TimeAndDate trigger;
	private String eventName;
	

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
			display.println(printable);
		}
		else
		{
			System.out.println(printable);
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
			println("EVENT >> " + this.toString() + " triggered!");
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
	//show as a string (good for testing)
	public String toString()
	{
		return "\"" + eventName + "\" @ " + trigger.toString();
	}
}
