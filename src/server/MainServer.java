/**
*Class:             MainServer.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven
*Date of Update:    18/03/2017
*Version:           0.6.0
*
*Purpose:           The main controller of the AVA system
*
* 
*Update Log			v0.6.0
*						- packet forwarding based on prefix
*						- packet forwarding made generic to allow forwarding to any module
*						- server now forwards info packets to interfaces (when info packet is what it
*						  (fetches during fetch-execute loop)
*						- prefixes added
*					v0.5.0
*						- method added for forwarding commands to hardware modules/external controllers
*						- server can remotely turn light on alarm controller on/off/PWM
*						- server can remotely turn alarm on alarm controller on/off
*						- server timers now trigger alarm
*					v0.4.0
*						- changed to use new ServerTimer subclass
*						- added button functionality for updating event info
*						- changing location for weather added
*					v0.3.0
*						- button for clearing all events added
*						- getting current weather added
*						- removing timers added
*						- getting list of all timers added
*						- checks for adding timers added
*					v0.2.0
*						- Scheduler hooked in
*						- methods to schedule ServerEvents added
*						- adding timers working
*						- timer triggering functional
*						- confirmation for system exit added
*					v0.1.2
*						- disconnect now supported
*						- address lookup now supported
*						- server can be run on thread outside of calling thread
*						- shutdown added
*					v0.1.1
*						- if device name already exists in registry, error packet is sent
*						- if handshake is bad, error packet sent
*						- ServerDSKY used for output
*						- new alarm prototype added
*						- req time command added
*						- ip given in log + window title
*					v0.1.0
*						- registry added for devices
*						- handshaking added
*						- responding to pings added
*/
package server;


//import libraries
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;

//import packages
import network.DataMultiChannel;
import network.NetworkException;
import network.PacketWrapper;
import server.database.CrudeDatabase;
import server.datatypes.Alarm;
import server.datatypes.ServerEvent;
import server.datatypes.ServerTimer;
import io.json.JsonException;
import network.DataChannel;



public class MainServer extends Thread implements ActionListener
{
	//declaring static class constants
	public static final String SERVER_NAME = "AVA Server v0.6.0";
	public static final int PORT = 3010;
	public static final byte TYPE_HANDSHAKE = 0;
	public static final byte TYPE_CMD = 1;
	public static final byte TYPE_INFO = 2;
	public static final byte TYPE_ERR = 3;
	public static final byte TYPE_DISCONNECT = 4;
	public static final int MAX_PACKET_SIZE = 1024;
	private static final String HANDSHAKE = "1: A robot may not injure a human being or, through inaction, allow a human being to come to harm.";
	public static final String PREFIX_ALARM = 			"a";
	public static final String PREFIX_COFEEE_MAKER = 	"c";
	public static final String PREFIX_INTERFACE = 		"i";
	
	//declaring local instance variables
	private HashMap<String,InetSocketAddress> registry;
	private DataMultiChannel multiChannel;
	private Scheduler scheduler;
	private boolean pauseFlag;
	private boolean runFlag;
	private int closeMode;
	private int locationID;
	private ServerDSKY display;
	
	
	//generic constructor
	public MainServer(boolean isFullScreen) throws SocketException, UnknownHostException
	{
		//initialize
		registry = new HashMap<String,InetSocketAddress>();
		multiChannel = new DataMultiChannel(PORT);
		scheduler = new Scheduler("AVA Scheduler");
		display = new ServerDSKY(SERVER_NAME, InetAddress.getLocalHost().toString()+":"+PORT, this, isFullScreen);
		runFlag = true;
		pauseFlag = false;
		locationID = Weather.OTTAWA_OPENWEATHER_ID;
		
		ServerEvent.hookDSKY(display);
		display.println("Server running @ " + InetAddress.getLocalHost() + ":" + PORT + " !");
	}
	
	
	//return pointer to DSKY
	public ServerDSKY getDKSY()
	{
		return display;
	}
	
	
	//shutdown server
	public void shutdown()
	{
		multiChannel.close();
		display.close();
	}
	
	
	//schedule a new event
	public void scheduleEvent(String eventJson) throws JsonException
	{
		//get event
		ServerEvent event = new ServerEvent();
		event.fromJSON(eventJson);
		
		//schedule
		display.println("Scheduling event: " + event.toString());
		scheduler.schedule(event);
	}
	
	//receive packet
	private PacketWrapper receivePacket() throws NetworkException
	{
		display.println();
		display.println("Waiting for packet...");
		PacketWrapper wrapper = multiChannel.receivePacket();
		display.println("Packet received!\nContents: {" + wrapper.toString() + "}");
		return wrapper;
	}
	private PacketWrapper receivePacket(int timeout) throws NetworkException, SocketException
	{
		display.println();
		display.println("Waiting for packet...");
		PacketWrapper wrapper = multiChannel.receivePacket(timeout);
		display.println("Packet received!\nContents: {" + wrapper.toString() + "}");
		return wrapper;
	}
	
	
	//send a ping
	private void sendPing(InetSocketAddress dest)
	{
		//send an empty info packet to act as a ping
		try 
		{
			display.println("Sending empty ping response...");
			multiChannel.hijackChannel(dest.getAddress(), dest.getPort());
			multiChannel.sendInfo("");
		} 
		catch (NetworkException e) {e.printStackTrace();}
	}
	
	
	//schedual a new timer
	public boolean setTimer(String json) throws JsonException
	{
		String timerName;
		int triggerTime;
		
		//parse info from json
		int line = 0;
		/*split at newlines, remove all tabs, remove spaces
		 * keep one copy of tab-space filtered, separated at \n		fileLine
		 * keep one copy of tab filtered, separated at \n			fileLineSpace
		 */
		String intermediate = json.replaceAll("\t", "");
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
		if (!fileLine[line].contains("\"name\":"))
		{
			throw new JsonException("\"name\" field not found", JsonException.ERR_BAD_FIELD);
		}
		//extract name
		String tempString = fileLineSpace[line].split(":",2)[1];
		timerName = tempString.substring(tempString.indexOf("\"")+1, tempString.length()-1);
		line++;
		
		//check for eventName field
		if (!fileLine[line].contains("\"timeUntilTrigger\":"))
		{
			throw new JsonException("\"timeUntilTrigger\" field not found", JsonException.ERR_BAD_FIELD);
		}
		//extract minutes
		String minute = fileLine[line].split(":")[1];
		try
		{
			triggerTime = (Integer.parseInt(minute));
		}
		catch (NumberFormatException e)
		{
			throw new JsonException("minute field must be a valid 32bit integer", JsonException.ERR_BAD_VALUE);
		}
		
		//create new ServerEvent for time, schedule it
		ServerTimer event = new ServerTimer(timerName, triggerTime, scheduler);
		boolean success = scheduler.schedule(event);
		
		if(success)
		{
			//print confirmation
			int[] time = {0,0,0};
			time[0] = triggerTime/(60*60);
			time[1] = (triggerTime%(60*60))/60;
			time[2] = (triggerTime%(60*60))%60;
			String printTime = time[0]+":"+time[1]+":"+time[2];
			display.println("Timer: \"" + timerName + "\" added! Will trigger in " + printTime);
		}
		else
		{
			display.println("Timer with name \"" + timerName + "\" already exists!\nSending error packet...");
		}
		display.updateEvent(scheduler.getNonPeriodicEvents(), scheduler.getPeriodicEvents());
		return success;
	}
	
	
	//send IPv4:socket address as String
	private void sendAddress(InetSocketAddress dest, String key)
	{
		try
		{
			display.println("Accessing registry with key: \"" + key + "\"...");
			InetSocketAddress address = registry.get(key);
			if(address != null)
			{
				display.println("Value found: \"" + address.toString() + "\"\nSending info packet...");
				multiChannel.hijackChannel(dest.getAddress(), dest.getPort());
				multiChannel.sendInfo(address.toString());
			}
			else
			{
				display.println("Value not found\nSending error packet...");
				multiChannel.hijackChannel(dest.getAddress(), dest.getPort());
				multiChannel.sendErr("No module registered under \"" + key + "\"");
			}
		}
		catch (NetworkException e) {e.printStackTrace();}
	}
	
	
	//forward a packet
	private void forwardPacket(PacketWrapper packet, String targetPrefix)
	{
		boolean found = false;
		
		//look for alarm controller(s)
		display.println("Attempting packet forward...\nScanning registry for prefix \"" + targetPrefix + "\\\"...");
		Set<String> keys = registry.keySet();
		for(String key : keys)
		{
			if(key.contains("\\"))
			{
				String prefix = key.split("\\\\")[0];		// WHY ORACLE WHY WOULD YOU DO THIS
				if(prefix.equals(targetPrefix))
				{
					try
					{
						InetSocketAddress alarm = registry.get(key);
						display.println("\"" + key + "\" found @ "  + alarm.toString());
						multiChannel.hijackChannel(alarm.getAddress(), alarm.getPort());
						switch(packet.type())
						{
							case(DataChannel.TYPE_CMD):
								multiChannel.sendCmd(packet.commandKey(), packet.extraInfo());
								break;
							case(DataChannel.TYPE_ERR):
								multiChannel.sendErr(packet.errorMessage());
								break;
							case(DataChannel.TYPE_INFO):
								multiChannel.sendInfo(packet.info());
								break;
							default:
								display.printError("Unknown packet type for forwarding: " + packet.type());
								break;
						}
						found = true;
					}
					catch (NetworkException e){e.printStackTrace();}
				}
			}
		}
		
		if(!found)
		{
			display.printError("No device registered with prefix \"" + targetPrefix + "\\\" found!");
		}
	}
	
	
	//set the location
	private void setLocation(String cc, InetSocketAddress dest) throws JsonException
	{
		String[] loc = new String[2];
		if(cc.contains(","))
		{
			loc = cc.split(",");
			if(loc.length != 2)
			{
				throw new JsonException("Invalid city,country format", JsonException.ERR_FORMAT);
			}
		}
		else
		{
			loc[0] = cc;
			loc[1] = CrudeDatabase.COUNTRY_CODE;
		}
		try
		{
			multiChannel.hijackChannel(dest.getAddress(), dest.getPort());
			display.println("Querying database for \"" + loc[0] + ", " + loc[1] + "\"");
			Integer code = Weather.db.query(loc[0], loc[1]);
			if(code != null)
			{
				display.println("Query success! Setting location ID = " + code + "\nSending empty info packet...");
				locationID = code;
				multiChannel.sendInfo("");
			}
			else
			{
				display.println("Query failure!\nSending error packet...");
				multiChannel.sendErr("Location: \"" + loc[0] + ", " + loc[1] + "\" not in database");
			}
		}
		catch (NetworkException e) {e.printStackTrace();}
	}
	
	
	//send the current weather data as unformatted JSON
	private void sendCurrentWeather(InetSocketAddress dest)
	{
		Weather weatherRequest = new Weather();
		JSONObject weatherData = null;
		try
		{
			weatherData = weatherRequest.currentWeatherAtCity(locationID);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		catch(JSONException je)
		{
			je.printStackTrace();
		}
		try
		{
			display.println("Sending weather data...");
			multiChannel.hijackChannel(dest.getAddress(), dest.getPort());
			if(weatherData == null)
			{
				display.println("Weather data not found! Sending error packet...");
				multiChannel.sendErr("Weather data not found");
			}
			else
			{
				multiChannel.sendInfo(weatherData.toString());
			}
		}
		catch(NetworkException e)
		{
			e.printStackTrace();
		}
	}
	
	
	//make a new alarm
	private void newAlarm(String alarmJSON) throws JsonException
	{
		display.println("Creating new alarm...");
		Alarm alarm = new Alarm();
		alarm.fromJSON(alarmJSON);
		display.println("Alarm created!");
		//TODO add alarm as a ServerEvent
	}
	
	
	//send the server time
	private void sendTime(InetSocketAddress dest)
	{
		try
		{
			display.println("Sending time...");
			multiChannel.hijackChannel(dest.getAddress(), dest.getPort());
			multiChannel.sendInfo(ServerDSKY.getCurrentTime());
		} 
		catch (NetworkException e) {e.printStackTrace();}
	}
	
	
	//send the JSON representation of non periodic events
	private void sendEvents(boolean periodic, InetSocketAddress dest) throws NetworkException				//TODO this is janked up
	{
		//get event list
		ArrayList<ServerEvent> events;
		if(periodic)
		{
			events = scheduler.getPeriodicEvents();
		}
		else
		{
			events = scheduler.getNonPeriodicEvents();
		}
		
		//convert to JSON
		String json = "{\n";
		for(ServerEvent event : events)
		{
			json += event.toJSON("\t").toString();
		}
		json += "}\n";
		System.out.println(json);
		//send
		multiChannel.hijackChannel(dest.getAddress(), dest.getPort());
		multiChannel.sendInfo(json);
	}
	
	
	@Override
	//main server input-control-wait loop
	public void run()
	{
		while(runFlag)
		{
			try
			{
				//receive
				PacketWrapper packet = null;
				packet = this.receivePacket();
				
				//decide what to do with the packet
				switch(packet.type())
				{
					//new device for the registry
					case(TYPE_HANDSHAKE):
						display.println("Device attempting pairing...");
						//check handshake
						if(packet.handshakeKey().equals(HANDSHAKE))
						{
							//add to registry
							display.println("Device handshake correct!\nAdding to registry...");
							if(!registry.containsKey(packet.deviceName()))
							{
								registry.put(packet.deviceName(), packet.source());
								display.println("Device added to registry under name \"" + packet.deviceName() + "\", value: \"" + packet.source().toString() + "\"");
								
								//respond to handshake with empty handshake
								try
								{
									multiChannel.respondHandshake(packet.source().getAddress(), packet.source().getPort());
								}
								catch (NetworkException e)
								{
									display.println("EXCEPTION >> " + e.getMessage());
								}
							}
							else
							{
								display.println("Device name already in used, error packet sent");
								//device name already registered, respond with error
								try 
								{
									multiChannel.hijackChannel(packet.source().getAddress(), packet.source().getPort());
									multiChannel.sendErr("Device name already in used\nPlease choose another device name to register under");
								} 
								catch (NetworkException e) 
								{
									display.println("EXCEPTION >> " + e.getMessage());
	
								}
							}
						}
						else
						{
							display.println("Device handshake incorrect!\nPairing FAILED");
							//TODO respond to bad handshake
						}
						display.updateRegistry(registry);
						break;
					
						
						
					//disconnecting device	
					case(TYPE_DISCONNECT):								//TODO consider replacement of this with custom bidirectional hashmap
						boolean foundFlag=false;
						display.println("Device attempting disconnect...\nChecking for device in registry...");
						for(String s : registry.keySet())
						{
							if(registry.get(s).equals(packet.source()))
							{
								registry.remove(s);
								display.println("Device registered under \"" + s + "\" removed from registry with reason:\n\"" + packet.disconnectMessage() + "\"");
								foundFlag = true;
								display.updateRegistry(registry);
								break;
							}
						}
						if(!foundFlag)
						{
							display.println("Device not found in registry!");
						}
						break;
						
						
					
					//some command from an interface
					case(DataChannel.TYPE_CMD):
						//determine what to do based on command key
						switch(packet.commandKey())
						{
							//new basic timer is being added
							case("set timer"):
								try
								{
									boolean s = setTimer(packet.extraInfo());
									multiChannel.hijackChannel(packet.source().getAddress(), packet.source().getPort());
									if(s)
									{
										multiChannel.sendInfo("");
									}
									else
									{
										multiChannel.sendErr("Timer with selected name already exists");
									}
								}
								catch (JsonException e)
								{
									display.println("ERROR >> " + e.getMessage());
								}
								break;
						
							//somebody is pinging server, respond
							case("ping"):
								sendPing(packet.source());
								break;
								
							//the current weather is requested
							case("req current weather"):
								sendCurrentWeather(packet.source());
								break;
								
							//change the current server location
							case("set location"):
								try
								{
									setLocation(packet.extraInfo(), packet.source());
								}
								catch (JsonException e)
								{
									display.println("ERROR >> " + e.getMessage());
									multiChannel.hijackChannel(packet.source().getAddress(), packet.source().getPort());
									multiChannel.sendErr(e.getMessage());
								}
								break;
								
							//new alarm added
							case("new alarm"):
								try
								{
									newAlarm(packet.extraInfo());
								}
								catch (JsonException e)
								{
									display.println("ERROR >> " + e.getMessage());
								}
								break;
								
							//the server time is requested
							case("req time"):
								sendTime(packet.source());
								break;
								
							//a module address is requested
							case("req ip"):
								sendAddress(packet.source(), packet.extraInfo());
								break;
								
							//new event being scheduled
							case("sch event"):
								try
								{
									scheduleEvent(packet.extraInfo());
								}
								catch (JsonException e)
								{
									display.println("ERROR >> " + e.getMessage());
								}
								break;
							
							//return information on all scheduled single-triggered events
							case("req np-events"):
								sendEvents(true, packet.source());
								break;
							
							//return information on all scheduled periodic events
							case("req p-events"):
								sendEvents(false, packet.source());
								break;
							
							//remove a non-periodic event
							case("del np-event"):
								String toRemove = packet.extraInfo();
								display.println("Attemping to remove non-periodic event \"" + toRemove + "\"");
								boolean removed = scheduler.removeNonPeriodic(toRemove);
								display.updateEvent(scheduler.getNonPeriodicEvents(), scheduler.getPeriodicEvents());
								if(removed)
								{
									display.println("Sending empty info packet...");
									multiChannel.hijackChannel(packet.source().getAddress(), packet.source().getPort());
									multiChannel.sendInfo("");
								}
								else
								{
									String err = "No event with name \"" + packet.extraInfo() + "\" found";
									display.println(err + "\nSending error packet...");
									multiChannel.sendErr(err);
								}
								break;
							
							//commands forwarded to alarm
							case("alarm on"):
							case("alarm off"):
							case("led on"):
							case("led off"):
							case("led pwm"):
								forwardPacket(packet, PREFIX_ALARM);
								break;
								
							//commands forwarding
						}
						break;
					
						
						
					//some info from an interface
					case(DataChannel.TYPE_INFO):
						forwardPacket(packet, PREFIX_INTERFACE);
						break;
					
					
					
					//an error from one of the devices
					case(DataChannel.TYPE_ERR):
						break;
				}
			}
			catch (NetworkException e){}
		}
	}
	
	
	@Override
	//Handle button presses
	public void actionPerformed(ActionEvent ae) 
	{
		//parse based on action command
		String cmd = ae.getActionCommand();
		switch(cmd)
		{
			//erase the registry
			case(ServerDSKY.BTN_ERASE_REGISTRY):
				display.println("BUTTON >> ERASE REGISTRY");
				display.println("Registry cleared");
				registry.clear();
				display.updateRegistry(registry);
				break;
			
			//force updating the registry view
			case(ServerDSKY.BTN_UPDATE_REGISTRY):
				display.println("BUTTON >> UPDATE REGISTRY");
				display.updateRegistry(registry);
				break;
			
			//soft shutdown of system
			case(ServerDSKY.BTN_SOFT_SHUTDOWN):
				display.println("BUTTON >> SOFT SHUTDOWN");
				//TODO
				break;
			
			//hard shutdown of system
			case(ServerDSKY.BTN_HARD_SHUTDOWN):
				display.println("BUTTON >> HARD SHUTDOWN");
				if(display.getBoolean("Are you sure you wish to perform a hard shutdown on the AVA Server?\nThis can lead to unexpected events on modules and lost data"))
				{
					System.exit(0);
				}
				else
				{
					display.println("Shutdown canceled!");
				}
				break;
			
			//soft reset of system
			case(ServerDSKY.BTN_UPDATE_EVENTS):
				display.println("BUTTON >> UPDATE EVENTS");
				display.updateEvent(scheduler.getNonPeriodicEvents(), scheduler.getPeriodicEvents());
				break;
			
			//hard reset of system
			case(ServerDSKY.BTN_HARD_RESET):
				//TODO
				break;
			
			//pause of resume server
			case(ServerDSKY.BTN_CLEAR_EVENTS):
				display.println("BUTTON >> CLEAR EVENTS");
				scheduler.clearAll();
				break;
		}
		
	}
	
	
	
	//main method
	public static void main(String[] arg)
	{
		try 
		{
			MainServer server = new MainServer(false);		//true=fullscreen, false=windowed
			server.run();
		}
		catch (UnknownHostException e) 
		{	
			System.out.println("EXCEPTION >> UnknownHostException\n" + e.getMessage());
			e.printStackTrace();
		}
		catch (SocketException e)
		{
			System.out.println("EXCEPTION >> SocketException\n" + e.getMessage());
			e.printStackTrace();
		}
	}
}








