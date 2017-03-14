/**
*Class:             Terminal.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    13/03/2017                                              
*Version:           0.5.1                                         
*                                                                                   
*Purpose:           Local interface to main AVA server.
*					Basic Terminal form for text commands.
*					Send/Receive packets from server.
*					
* 
*Update Log			v0.5.2
*						- terminal disconnect error bug patched (issue #15 on github)
*					v0.5.1
*						- terminal can set up timers (cmd or dialog)
*						- terminal can remove timers
*						- terminal can request list of all np-events
*						- terminal can request current weather
*					v0.5.0
*						- pinging added
*						- connection establishing with server added
*						- connection command and associated method re-written
*						- default server address/port added, default device name added
*						- alarm setting added (we actually send to the server now)
*						- request time command functionality added
*						- auto attempts to connect at startup
*						- disconnect functionality added (+ disconnect on reboot/close)
*						- ip command implemented
*						- setters and getters for default serverIPv4/port/registry name
*						- color command launchs dialog instead of setting to default
*					v0.4.0
*						- reboot capability added
*						- alarm setting adding (doesn't do anything with the data, just gets it)
*						- help menu format improved
*						- alarm dialog added
*						- alarm command added
*					v0.3.1
*						- some commands refined and built up
*						- added DataChannel
*					v0.3.0
*						- input method rebuilt to allow for multiple input lines
*						- echo setting/echoing added
*						- some new command prototypes added
*						- exiting repaired (no longer crashes the system)
*					v0.2.1
*						- refactored into 2 classes to fit MVC model
*							\-->  TerminalUI.java	(view)
*							 \--> Terminal.java		(controller)
*						- logic refactored for class autonomy
*						- imports removed
*					v0.2.0
*						- status pane updates added
*						- cool logo added
*						- terminal resizable
*						- menu bar disappearing fixed
*						- cmdMap changed from HashMap --> TreeMap in order to print commands in alphabetical order
*						- help menu + color selection made much more efficient+maintainable from old TFTP Project code
*						- word wrapping fixed
*					v0.1.0
*						- general framework
*						- console i/o
*						- window format
*						- basic methods for i/o
*/
package terminal;


//external imports
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.DateTimeException;
import java.util.TreeMap;
import javax.swing.JFrame;

//import packages
import network.DataChannel;
import network.NetworkException;
import network.PacketWrapper;
import server.datatypes.Alarm;
import terminal.dialogs.TimeDialog;
import server.datatypes.WeatherData;


public class Terminal extends JFrame implements ActionListener
{
	//declaring local class constants
	public static final int CLOSE_OPTION_RESET = 0;
	public static final int CLOSE_OPTION_ERROR = 1;
	public static final int CLOSE_OPTION_USER = 2;
	private static final String TERMINAL_NAME = "AVA Terminal";
	private static final String VERSION = "v0.5.0";
	private static final String CMD_NOT_FOUND = "Command not recongnized";
	private static final int RETRY_QUANTUM = 5;	
	
	//declaring local instance variables
	private String 		defaultDeviceName;
	private InetAddress defaultServerAddress;
	private int 		defaultServerPort;
	
	private boolean runFlag;
	private boolean connecting;
	private int closeReason;
	private TerminalUI ui;
	private DataChannel dataChannel;
	
	
	//generic constructor
	public Terminal(boolean isFullScreen)
	{	
		//init ui
		ui = new TerminalUI(TERMINAL_NAME+" "+VERSION, this, CMD_NOT_FOUND, isFullScreen);
		ui.println("Initializing command map...");
		ui.initCmdMap(this.initCmdMap());
		
		//init variables
		try 
		{
			ui.println("Binding Socket...");
			dataChannel = new DataChannel();
			ui.println("Obtaining local address...");
			defaultServerAddress = InetAddress.getLocalHost();
			defaultDeviceName = "terminal";
			defaultServerPort = 3010;
			connecting = false;
		} 
		catch (SocketException e) 
		{
			ui.printError("Socket could not be bound\n" + e.getMessage() + "\n\nExiting...");
			e.printStackTrace();
			System.exit(ERROR);
		}
		catch (UnknownHostException e)
		{
			ui.printError("Local address could not be found\n" + e.getMessage() + "\n\nExiting...");
			e.printStackTrace();
			System.exit(ERROR);
		}
		runFlag = true;
		
		//update ui
		ui.updateStatus(this.statusToString());
		ui.println("Starting control on Thread <" + Thread.currentThread().getId() + ">...");
	}
	
	
	//main run-loop of the terminal
	public int run()
	{
		//initial handshake
		establishConnection(defaultServerAddress, defaultServerPort, defaultDeviceName);
		
		//main input-parse loop
		ui.println("\n************************* INIT COMPLETE *************************\nWaiting for input...");
		while(runFlag)
		{
			String[] in = ui.getInput();
			handleConsoleInput(in);
		}
		
		//exit and return
		return closeReason;
	}
	
	
	//added to keep older code running, default close is via user
	public void close()
	{
		close(CLOSE_OPTION_USER);
	}
	//close the terminal
	public void close(int reason)
	{
		ui.println("Closing terminal...");
		try 
		{
			if(dataChannel.getConnected())
			{
				dataChannel.disconnect("user");
			}
		} 
		catch (NetworkException e) 
		{
			ui.printError("Error disconnecting from server\n" + e.getMessage());
		}
		dataChannel.close();
		closeReason = reason;
		ui.close();
		runFlag = false;
	}
	
	
	//initialize command map
	private TreeMap<String,String> initCmdMap()
	{
		TreeMap<String,String> cmdMap = new TreeMap<String,String>();
		
		cmdMap.put("help", "Print help/details on command usage\n"
					+ "\tparam1= n/a   || Print all commands to screen\n"
					+ "\tparam1= all   || Print details on all commands\n"
					+ "\tparam1= <CMD> || Print details on command <CMD>");
		
		cmdMap.put("clear", "Removes all text from the console output pane");
		
		cmdMap.put("close", "Exit the local terminal");
		
		cmdMap.put("connect", "Establish/Reestablish a connection to the main server\n"
					+ "\tparam1= n/a             || Attempt to establish server connection at default server address\n"
					+ "\tparam1= default ::      || Attempt to establish server connection at default server address\n"
					+ "\tparam1= local ::        || Attempt to establish server conncetion at the local IPv4 address\n"
					+ "\tparam1= xxx.xxx.xxx.xxx || Attempt to establish server connection to this IPv4 address\n"
					+ "\tparam2= n/a ::          || Attempt to establish server connect to the default port\n"
					+ "\tparam2= <INT> ::        || Attempt to establish server connection to port <INT>");
		
		cmdMap.put("disconnect", "Disconnect from main server");
		
		cmdMap.put("ip", "Request and return the IP of a module\n"	
					+ "\tparam1= n/a    || Print the IPv4 address of the local machine\n"
					+ "\tparam1= local  || Print the IPv4 address of the local machine\n"
					+ "\tparam1= server || Print the IPv4 address of the connected server\n"
					+ "\tparam1= <STR>  || Sends a request to the server for the IPv4 address of module with String identifier <STR>");
		
		cmdMap.put("color", "Change the color theme of the terminal\n"
					+ "\tparam1= n/a   || Set the color scheme via dialog\n"
					+ "\tparam1= all   || Demo all color schemes\n"
					+ "\tparam1= <STR> || Set the color scheme to <STR>\n"
					+ "\tTHEMES:          aperture, bluescreen, bumblebee, dark,\n" 
					+ "\t                 light, matrix, ocean, prettyinpink, xmas");
		
		cmdMap.put("update", "Manually force update for the status overview");
		
		cmdMap.put("echo", "Test/toggle the voice synthesis of the system\n"												//TODO implement this
					+ "\tparam1= na     || Show the current state of voice echo\n"
					+ "\tparam1= true   || Set the terminal to synthesize all text as voice\n"
					+ "\tparam1= false  || Set the terminal to stop synthesis of all text as voice\n"
					+ "\tparam1 = <STR> || Synthesize the entered String to voice");
		
		cmdMap.put("alarm", "Schedual an alarm at a certain time\n"															
					+ "\tparam1: n/a      || Launch dialog to schedual alarm\n"
					+ "\tparam1: ddd      || Set the alarm to go off on day ddd\n"
					+ "\t                    multiple days should be seperated by a comma such that ddd,ddd\n"
					+ "\t                    (ddd such that mon, tue, wed, thu, fri, sat, sun)\n"
					+ "\tparam2: hh:mm    || Set the alarm to go at time hh:mm (24h format)\n"
					+ "\tparam3: <STR>    || Set the alarm label to String <STR>\n"
					+ "\tparam3: n/a      || Set the alarm label to default \"Generic Alarm\"");
		
		cmdMap.put("reboot", "Reboot the main server or this terminal\n"
					+ "\tparam1: n/a    || Reboot this instance of terminal\n"
					+ "\tparam1: local  || Reboot this instance of terminal\n"
					+ "\tparam1: server || Reboot the main server\n"
					+ "\tparam1: <STR>  || Reboot the device assosiated with <STR>");
		
		cmdMap.put("ping", "Ping the server\n"
					+ "\tparam1: n/a   || Ping the server 5 times\n"
					+ "\tparam1: <INT> || Ping the server <INT> times");
		
		cmdMap.put("time", "Get and print the current time from the server");
		
		cmdMap.put("d-serverport", "Get or set the default server port\n"
					+ "\tparam1: n/a   || Print the default server port\n"
					+ "\tparam1: <INT> || Set the default server port to <INT>");
		
		cmdMap.put("d-serverip", "Get or set the default server IPv4 address\n"
				+ "\tparam1: n/a             || Print the default server IPv4 address\n"
				+ "\tparam1: xxx.xxx.xxx.xxx || Set the default server IPv4 address to xxx.xxx.xxx.xxx");
		
		cmdMap.put("d-name", "Get or set the default module-registry name of terminal\n"
				+ "\tparam1: n/a   || Print the default module-registry name of terminal\n"
				+ "\tparam1: <STR> || Set the default module-registry name of terminal to <STR>");
		
		cmdMap.put("timer-new", "Set a new timer to go off in a set amount of minutes\n"
				+ "\tparam1: n/a   || Launch system dialog to set a new timer\n"
				+ "\tparam1: <INT> || The number of minutes you want the timer to trigger in\n"
				+ "\tparam2: <STR> || The name for the timer");
		
		cmdMap.put("timer-remove", "Remove a timer currently scheduled"
				+ "\tparam1: n/a   || Launch system dialog to select a timer to remove\n"
				+ "\tparam1: <STR> || Remove timer with name <STR> from scheduling");
		
		cmdMap.put("timer-get", "Request list of all timers currently scheduled");
		
		cmdMap.put("weather", "Request the current weather of Ottawa, Ontario");
		
		return cmdMap;
	}
	
	
	//connect to server
	private void establishConnection(InetAddress address, int port, String name)
	{
		if(!dataChannel.getConnected())
		{
			connecting = true;
			try
			{
				for(int i=0; i<RETRY_QUANTUM && !dataChannel.getConnected(); i++)
				{
					ui.println("Establishing connection...");
					try 
					{
						dataChannel.connect(address, port, name);
					} 
					catch (IOException e1) 
					{
						//timeout has occurred
					}
				}
				
				if(dataChannel.getConnected())
				{
					ui.println("Connection established @ " + address.toString() + ":" + port + " under name \"" + name + "\"");
				}
				else
				{
					ui.println("Connection could not be established!");
				}
			}
			catch (NetworkException e)
			{
				ui.printError(e.getMessage());
			}
			connecting = false;
		}
		else
		{
			ui.printError("Already connected!\nPlease disconnect first");
		}
		ui.updateStatus(statusToString());
	}
	
	
	//disconnect from server
	private void disconnect(String msg)
	{
		try 
		{
			dataChannel.disconnect(msg);
			ui.updateStatus(this.statusToString());
			ui.println("Sucessfully disconnected from main AVA Server!");
		} 
		catch (NetworkException e) 
		{
			ui.printError(e.getMessage());
		}
	}
	
	
	//handle the input
	private void handleConsoleInput(String[] input)
	{
		int length = input.length;
		//handle based on prime noun
		switch(input[0])
		{
			//clear console
			case("clear"):
				if(length == 1)
				{
					ui.clear();
				}
				else
				{
					ui.println(CMD_NOT_FOUND);
				}
				break;
			
				
			//print help menu
			case("help"):
				if(length == 1)
				{
					ui.printHelpAllCommands(false);
				}
				else if (length == 2)
				{
					ui.printHelp(input[1]);
				}
				else
				{
					ui.println(CMD_NOT_FOUND);
				}
				break;
			
				
			//close
			case("close"):
				if (length == 1)
				{
					//confirm exit
					ui.println("Are you sure you wish to exit this terminal\nThe main AVA Server will continue to run (y/n)");
					String[] in = ui.getInput();
					if(in.length == 1)
					{
						if(in[0].equals("y"))
						{
							ui.close();
							this.close(CLOSE_OPTION_USER);
							break;
						}
						else
						{
							ui.println("Canceling exit...");
						}
					}
					else
					{
						ui.println("Canceling exit...");
					}
				}
				else
				{
					ui.println(CMD_NOT_FOUND);
				}
				break;
				
				
			//change color scheme
			case("color"):
				if (length == 1)
				{
					ui.colorDialog();
				}
				else if (length == 2)
				{
					ui.colorScheme(input[1]);
				}
				else
				{
					ui.println(CMD_NOT_FOUND);
				}
				break;
			
				
			//force status overview update
			case("update"):
				if (length == 1)
				{
					ui.updateStatus(this.statusToString());
				}
				else
				{
					ui.println(CMD_NOT_FOUND);
				}
				break;
			
			
			//toggle and/or turn on synthesis
			case("echo"):
				if(length == 1)
				{
					ui.println("Echo is set: " + ui.getEcho());
				}
				else if (length == 2)
				{
					if(input[1].equals("true") || input[1].equals("on"))
					{
						ui.setEcho(true);
					}
					else if (input[1].equals("false") || input[1].equals("off"))
					{
						ui.setEcho(false);
					}
					else
					{
						ui.println(input[1]);
					}
				}
				else
				{
					ui.println(CMD_NOT_FOUND);
				}
				break;
				
				
			//print and/or request info on the ip address of various connected devices
			case("ip"):
				if(input.length == 1)
				{
					try 
					{
						ui.println(InetAddress.getLocalHost().toString());
					} 
					catch (UnknownHostException e) {e.printStackTrace();}
				}
				else if (input.length == 2)
				{
					//print local ip
					if(input[1].equals("local") || input[1].equals("this"))
					{
						try 
						{
							ui.println(InetAddress.getLocalHost().toString());
						} 
						catch (UnknownHostException e) {e.printStackTrace();}
					}
					//print server ip
					else if(input[1].equals("server") || input[1].equals("ava"))
					{
						ui.println(dataChannel.getPairedAddress()+":"+dataChannel.getPairedPort());
					}
					//request ip of module
					else
					{
						String moduleString = input[1];
						//send and receive info
						try 
						{
							dataChannel.sendCmd("req ip", moduleString);
							PacketWrapper packet = dataChannel.receivePacket();
							if(packet.type() == DataChannel.TYPE_INFO)
							{
								ui.println("\"" + moduleString + "\" @ " + packet.info());
							}
							else
							{
								ui.println(packet.errorMessage());
							}
						}
						catch (NetworkException e) 
						{
							ui.printError(e.getMessage());
						}
					}
				}
				//command not found
				else
				{
					ui.print(CMD_NOT_FOUND);
				}
				break;
				
			
			//reboot the server/this/module
			case("reboot"):
				//implicate reboot of this terminal
				if(input.length == 1)
				{
					close(CLOSE_OPTION_RESET);
				}
				else if (input.length == 2)
				{
					//determine target
					switch(input[1])
					{
						case("local"):
							close(CLOSE_OPTION_RESET);
							break;
						case("server"):
						case("ava"):
							//TODO reset AVA server
							ui.println("TODO");
							break;
						default:
							//TODO reset module with string id
							ui.println("TODO");
							break;
					}
				}
				else
				{
					ui.print(CMD_NOT_FOUND);
				}
				break;
			
				
			//Schedule an alarm
			case("alarm"):
				//no params, launch dialog
				if(input.length == 1)
				{
					//get info from dialog
					Alarm alarm = ui.getAlarm();
					if(alarm != null)
					{
						//send alarm
						try 
						{
							dataChannel.sendCmd("new alarm", alarm.toJSON(""));
						} 
						catch (NetworkException e) 
						{
							ui.printError(e.getMessage());
						}
					}
				}
				//cmd alarm, generic name
				else if(input.length == 3 || input.length == 4)
				{
					Alarm alarm = new Alarm();
					//parse day into
					boolean[] daysArr = new boolean[7];
					String[] daysInput = input[1].split(",");
					for(String day : daysInput)
					{
						switch(day)
						{
							case("mon"):
								daysArr[1] = true;
								break;
							case("tue"):
								daysArr[2] = true;
								break;
							case("wed"):
								daysArr[3] = true;
								break;
							case("thu"):
								daysArr[4] = true;
								break;
							case("fri"):
								daysArr[5] = true;
								break;
							case("sat"):
								daysArr[6] = true;
								break;
							case("sun"):
								daysArr[0] = true;
								break;
							default:
								ui.printError("Unknown date");
								return;
						}
					}
					alarm.setDays(daysArr);
					//parse time info
					String[] hourMin = input[2].split(":");
					try
					{
						int hour =  Integer.parseInt(hourMin[0]);
						int min = Integer.parseInt(hourMin[1]);
						alarm.setHour(hour);
						alarm.setMinute(min);
					}
					catch (NumberFormatException e)
					{
						ui.printError("Hour/Minute must be a valid integer");
						return;
					}
					catch (DateTimeException e)
					{
						ui.printError(e.getMessage());
						return;
					}
					//parse name info
					if(input.length == 4)
					{
						alarm.setName(input[3]);
					}
					
					//send alarm
					try 
					{
						dataChannel.sendCmd("new alarm", alarm.toJSON(""));
					} 
					catch (NetworkException e) 
					{
						ui.printError(e.getMessage());
					}
				}
				else
				{
					ui.println(CMD_NOT_FOUND);
				}
				break;
				
				
			//ping server
			case("ping"):
				if(input.length == 1)
				{
					pingServer(5);
				}
				else if (input.length == 2)
				{
					try
					{
						pingServer(Integer.parseInt(input[1]));
					}
					catch (NumberFormatException e)
					{
						ui.printError("input must be a valid integer");
					}
				}
				else
				{
					ui.println(CMD_NOT_FOUND);
				}
				break;
				
			
			//attempt to connect to the server
			case("connect"):
				if (input.length <= 4)
				{
					//default values
					InetAddress address = defaultServerAddress;
					int port = defaultServerPort;
					String name = defaultDeviceName;
					
					//non-default values -- parse and set address and port
					if (input.length >= 2)
					{
						//set address
						if (input[1].equals("default"))
						{
							address = defaultServerAddress;
						}
						else if (input[1].equals("local"))
						{
							try
							{
								address = InetAddress.getLocalHost();
							}
							catch (UnknownHostException e)
							{
								ui.printError("Could not obtain local IPv4 address");
								return;
							}
						}
						else
						{
							try
							{
								//declaring temporary method variables
								byte[] addr;
								String subStrBytes[];

								//parse addr
								subStrBytes = (input[1]).split("\\.");
								
								addr = new byte[subStrBytes.length];
								for(int i=0; i<subStrBytes.length; i++)
								{
									addr[i] = (byte)Integer.parseInt(subStrBytes[i]);
								}
								
								//save as ip
								address = InetAddress.getByAddress(addr);
							}
							catch (NumberFormatException|UnknownHostException e)
							{
								ui.printError("Invalid IPAddress\nMust be of form \"xxx.xxx.xxx.xxx\"");
								return;
							}	
						}
						
						//set port
						if (input.length >= 3)
						{
							if(input[2].equals("default"))
							{
								port = defaultServerPort;
							}
							else
							{
								try
								{
									port = Integer.parseInt(input[2]);
								}
								catch (NumberFormatException e)
								{
									ui.printError("Invalid Port\nMust be a valid 32bit integer");
									return;
								}
							}
							
							//set name
							if(input.length == 4)
							{
								if(input[3].equals("default"))
								{
									name = defaultDeviceName;
								}
								else
								{
									name = input[3];
								}
							}
						}
					}
					
					//try to connect
					establishConnection(address, port, name);
					ui.updateStatus(this.statusToString());
				}
				else
				{
					ui.println(CMD_NOT_FOUND);
				}
				break;
				
			
			//get time from server
			case("time"):
				try 
				{
					dataChannel.sendCmd("req time");
					PacketWrapper wrapper = dataChannel.receivePacket(5000);
					ui.println(wrapper.info());
				} 
				catch (NetworkException e) 
				{
					ui.printError(e.getMessage());
				}
				catch (SocketException e)
				{
					ui.printError(e.getMessage());
				}
				break;
				
				
			//disconnect
			case("disconnect"):
				disconnect("user request");
				break;
				
				
			//get/set default server port
			case("d-serverport"):
				if(input.length == 1)
				{
					ui.println(defaultServerPort+"");
				}
				else if (input.length == 2)
				{
					//set default server port
					try
					{
						defaultServerPort = Integer.parseInt(input[1]);
					}
					catch (NumberFormatException e)
					{
						ui.printError("Invalid Port\nMust be a valid 32bit integer");
					}
				}
				else
				{
					ui.println(CMD_NOT_FOUND);
				}
				break;
				
			
			//get/set the default server IPv4
			case("d-serverip"):
				if(input.length == 1)
				{
					ui.println(defaultServerAddress.toString());
				}
				else if (input.length == 2)
				{
					try
					{
						//declaring temporary method variables
						byte[] addr;
						String subStrBytes[];

						//parse address
						subStrBytes = (input[1]).split("\\.");
						
						addr = new byte[subStrBytes.length];
						for(int i=0; i<subStrBytes.length; i++)
						{
							addr[i] = (byte)Integer.parseInt(subStrBytes[i]);
						}
						
						//save as ip
						defaultServerAddress = InetAddress.getByAddress(addr);
					}
					catch (NumberFormatException|UnknownHostException e)
					{
						ui.printError("Invalid IPAddress\nMust be of form \"xxx.xxx.xxx.xxx\"");
					}
				}
				else
				{
					ui.println(CMD_NOT_FOUND);
				}
				break;
			
			
			//get/set the default module-registry name
			case("d-name"):
				if(input.length == 1)
				{
					ui.println(defaultDeviceName);
				}
				else if (input.length == 2)
				{
					defaultDeviceName = input[1];
				}
				else
				{
					ui.println(CMD_NOT_FOUND);
				}
				break;
				
				
			//set up a timer					TODO a bit smelly, could use clean up
			case("timer-new"):
				if(input.length == 1)
				{
					TimeDialog d = new TimeDialog(ui, TERMINAL_NAME);
					if (d.getCloseMode() == TimeDialog.OK_OPTION)
					{
						//send timer command
						String json = "{\n\t\"name\" : \"" + d.getTimerName() + "\"\n\t\"timeUntilTrigger\" : " + d.getTimeInSeconds() + "\n}";
						try 
						{
							dataChannel.sendCmd("set timer", json);
							PacketWrapper response = dataChannel.receivePacket();
							
							//parse response
							if(response.type() == DataChannel.TYPE_INFO)
							{
								ui.println("Timer added!");
							}
							else if (response.type() == DataChannel.TYPE_ERR)
							{
								ui.printError(response.errorMessage());
							}
							else
							{
								ui.printError("Unknown response from server!\n"+response.toString());
							}
						} 
						catch (NetworkException e) 
						{
							ui.printError(e.getMessage());
						}
					}
				}
				else if(input.length == 3)
				{
					try 
					{
						//check that minute param is valid int
						int seconds = 60*Integer.parseInt(input[1]);
						//send timer command
						String json = "{\n\t\"name\" : \"" + input[2] + "\"\n\t\"timeUntilTrigger\" : " + seconds + "\n}";
						dataChannel.sendCmd("set timer", json);
						PacketWrapper response = dataChannel.receivePacket();
						
						//parse response
						if(response.type() == DataChannel.TYPE_INFO)
						{
							ui.println("Timer added!");
						}
						else if (response.type() == DataChannel.TYPE_ERR)
						{
							ui.printError(response.errorMessage());
						}
						else
						{
							ui.printError("Unknown response from server!\n"+response.toString());
						}
					} 
					catch (NetworkException e) 
					{
						ui.printError(e.getMessage());
					}
					catch (NumberFormatException e)
					{
						ui.printError("Invalid Time\nMust be a valid 32bit integer");
					}
				}
				else
				{
					ui.println(CMD_NOT_FOUND);
				}
				break;
			
				
			//get current weather
			case("weather"):
				if(input.length == 1)
				{
					try 
					{
						dataChannel.sendCmd("req current weather");
						PacketWrapper wrapper = dataChannel.receivePacket();
						WeatherData weather = new WeatherData(wrapper.info());
						
						String[] weatherData = weather.getWeatherData();
						ui.println("Weather data for Ottawa, Ontario.");		//TODO change this line! Dont hard code it
						ui.println("Current temperature: " + weatherData[WeatherData.TEMPERATURE] + " degrees Celsius");
						ui.println("Current humidity: " + weatherData[WeatherData.HUMIDITY] + "%");
						ui.println("Current weather: " + weatherData[WeatherData.WEATHER_TYPE] + ": " + weatherData[WeatherData.WEATHER_DESCRIPTION]);
					} 
					catch (NetworkException e) 
					{
						ui.printError(e.getMessage());
					}
				}
				else
				{
					ui.println(CMD_NOT_FOUND);
				}
				break;
			
			
			//remove a non-periodic event (timer or reminder)
			case("timer-remove"):
				if(input.length == 1)
				{
					
				}
				else if (input.length == 2)
				{
					try
					{
						//send and wait for response
						dataChannel.sendCmd("del np-event", input[1]);
						PacketWrapper response = dataChannel.receivePacket();
						
						//parse response
						if(response.type() == DataChannel.TYPE_INFO)
						{
							ui.println("\"" + input[1] + "\" removed!");
						}
						else if (response.type() == DataChannel.TYPE_ERR)
						{
							ui.printError(response.errorMessage());
						}
						else
						{
							ui.printError("Unknown response from server!\n"+response.toString());
						}
					}
					catch (NetworkException e)
					{
						ui.printError(e.getMessage());
					}
				}
				else
				{
					ui.println(CMD_NOT_FOUND);
				}
				break;
			
			//get list of active non-periodic events
			case("timer-get"):
				if(input.length == 1)
				{
					try
					{
						dataChannel.sendCmd("req np-events");
						PacketWrapper wrapper = dataChannel.receivePacket();
						ui.println(wrapper.extraInfo());
					}
					catch (NetworkException e)
					{
						ui.printError(e.getMessage());
					}
				}
				else
				{
					ui.println(CMD_NOT_FOUND);
				}
				break;
			
			case("location"):
				if(input.length == 2 || input.length == 3)
				{
					try
					{
						//send information and command
						if(input.length == 2)
						{
							dataChannel.sendCmd("set location", input[1]);
						}
						else
						{
							dataChannel.sendCmd("set location", input[1] + "," + input[2]);
						}
						
						//wait for response
						PacketWrapper wrapper = dataChannel.receivePacket();
						if(wrapper.type() != DataChannel.TYPE_INFO)
						{
							if(wrapper.type() == DataChannel.TYPE_ERR)
							{
								ui.printError(wrapper.errorMessage());
							}
							else
							{
								ui.printError("Received invalid packet type!");
							}
						}
					}
					catch (NetworkException e)
					{
						ui.printError(e.getMessage());
					}
				}
				else
				{
					ui.println(CMD_NOT_FOUND);
				}
				break;
				
			//cmd not found
			default:
				ui.println(CMD_NOT_FOUND);
				break;
		}
		
	}
	
	
	//ping the server
	private void pingServer(int amount)
	{
		//declaring method variables
		long pre, post;
		
		//ping 5 times
		for(int i=0; i<amount; i++)
		{
			//pause between pinging
			try {Thread.sleep(50);}
			catch (InterruptedException e1) {e1.printStackTrace();}
			
			try 
			{
				//send ping
				pre = System.currentTimeMillis();
				dataChannel.sendCmd("ping");
				
				//wait for response
				PacketWrapper wrapper = dataChannel.receivePacket(5000);
				if(wrapper.type() == DataChannel.TYPE_INFO)
				{
					post = System.currentTimeMillis();
					ui.println("Response from server, delay of " + (post-pre) + "ms");
				}
				else
				{
					ui.println("Unexpected packet recieved!");
				}
			}
			catch (NetworkException e)
			{
				ui.println(e.getMessage());
			}
			catch (SocketException e)
			{
				ui.println("No response");
			}
		}
	}
	
	
	//the status as a string
	private String statusToString()
	{
		//returnable string
		String status = "";
		
		if(dataChannel.getConnected())
		{
			status += "Server: CONNECTED\n"
					+ "        @" + dataChannel.getPairedAddress().toString() + ":" + dataChannel.getPairedPort() + "\n"
					+ "        under \"" + dataChannel.getRegisteredName() + "\"\n";
		}
		else
		{
			status += "Server: DISCONNECTED\n\n";
		}
		
		return status;
	}

	
	


	@Override
	//handle input
	public void actionPerformed(ActionEvent e) 
	{
		//determine source via cmd parse
		String src = e.getActionCommand();
		
		/* only respond to action events if not attempting to connect (leads to unknown and bad states
		 * due to the fact that action events basically act like interrupts
		 * (bug patch for git issue #15)
		 */
		if(!connecting)
		{
			switch(src)
			{
				//"file-close" button pressed
				case(TerminalUI.MENU_CLOSE):
					boolean closed = ui.reqClose();
					if(closed)
					{
						this.close(CLOSE_OPTION_USER);
					}
					break;
			}
		}
	}
	
	
	//instantiate a Terminal
	public static void main(String[] e)
	{	
		boolean relaunch = true;
		while(relaunch)
		{
			//start terminal, enter main loop
			Terminal terminal = new Terminal(false);		//true=fullscreen, false=windowed
			int close = terminal.run();
			
			//determine and handle reason for close
			switch(close)
			{
				//closed from error
				case(Terminal.CLOSE_OPTION_ERROR):
					System.out.println("An error has occured");
					relaunch = false;
					break;
				
				//closed from reset
				case(Terminal.CLOSE_OPTION_RESET):
					relaunch = true;
					break;
					
				default:
					relaunch = false;
			}
		}
	}
}
