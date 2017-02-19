/**
*Class:             Terminal.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    18/02/2017                                              
*Version:           0.4.0                                         
*                                                                                   
*Purpose:           Local interface to main AVA server.
*					Basic Terminal form for text commands.
*					Send/Receive packets from server.
*					
* 
*Update Log			v0.4.0
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
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.DateTimeException;
import java.util.TreeMap;
import javax.swing.JFrame;
import javax.swing.UIManager;

//import packages
import network.DataChannel;
import server.datatypes.Alarm;
import terminal.dialogs.DayAndTimeDialog;



public class Terminal extends JFrame implements ActionListener
{
	//declaring local class constants
	public static final int CLOSE_OPTION_RESET = 0;
	public static final int CLOSE_OPTION_ERROR = 1;
	public static final int CLOSE_OPTION_USER = 2;
	private static final String TERMINAL_NAME = "AVA Terminal";
	private static final String VERSION = "v0.4.0";
	private static final String CMD_NOT_FOUND = "Command not recongnized";
	private static final int RETRY_QUANTUM = 10;
	
	//declaring local instance variables
	private boolean runFlag;
	private int closeReason;
	private TerminalUI ui;
	private DataChannel dataChannel;
	
	
	//generic constructor
	public Terminal()
	{
		//init ui
		ui = new TerminalUI(TERMINAL_NAME+" "+VERSION, this, CMD_NOT_FOUND);
		ui.println("Initializing command map...");
		ui.initCmdMap(this.initCmdMap());
		
		//init variables
		dataChannel = new DataChannel();
		runFlag = true;
		
		//update ui
		ui.updateStatus(this.statusToString());
		ui.println("Starting control on Thread <" + Thread.currentThread().getId() + ">...");
	}
	
	
	//main run-loop of the terminal
	public int run()
	{
		//initial setup
		if(establishConnection(null))
		{
			ui.println("Connection established!");
		}
		else
		{
			ui.println("Connection FAILED");
		}
		
		
		//wait before clearing log
		try 
		{
			Thread.sleep(3000);
		} 
		catch (InterruptedException e) {e.printStackTrace();}
		ui.clear();
		
		//main input-parse loop
		ui.println("Waiting for input...");
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
		//TODO disconnect from server
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
		
		cmdMap.put("connect", "Establish/Reestablish a connection to the main server\n"										//TODO implement this
					+ "\tparam1= n/a             || Attempt to establish server connection at default server address\n"
					+ "\tparam1= default ::      || Attempt to establish server connection at default server address\n"
					+ "\tparam1= local ::        || Attempt to establish server conncetion at the local IPv4 address\n"
					+ "\tparam1= xxx.xxx.xxx.xxx || Attempt to establish server connection to this IPv4 address\n"
					+ "\tparam2= n/a ::          || Attempt to establish server connect to the default port\n"
					+ "\tparam2= <INT> ::        || Attempt to establish server connection to port <INT>");
		
		cmdMap.put("disconnect", "Disconnect from main server");															//TODO implement this
		
		cmdMap.put("ip", "Request and return the IP of a module\n"															//TODO implement this
					+ "\tparam1= n/a   || Print the IPv4 address of the local machine\n"
					+ "\tparam1= local || Print the IPv4 address of the local machine\n"
					+ "\tparam1= <STR> || Sends a request to the server for the IPv4 address of module with String identifier <STR>");
		
		cmdMap.put("color", "Change the color theme of the terminal\n"
					+ "\tparam1= n/a   || Set the color scheme to the default\n"
					+ "\tparam1= all   || Show all color schemes\n"
					+ "\tparam1= <STR> || Set the color schemes to <STR>\n"
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
		
		return cmdMap;
	}
	
	
	//connect to server
	private boolean establishConnection(Inet4Address address)
	{
		/*
		 * TODO 
		 * establish a connection
		 * try some n amount of times to do the handshake
		 */
		for(int i=0; i<RETRY_QUANTUM; i++)
		{
			ui.println("Establishing connection...");
		}
		
		return false;
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
					ui.printHelp(false);
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
					ui.colorScheme(null);
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
					//request ip of module
					else
					{
						String moduleString = input[1];
						ui.println("TODO");
						/*
						 * TODO request ip of module
						 */
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
						/*
						 * TODO send the actual data to server
						 * For now we just echo it!
						 */
						ui.println("TODO >> Send this to server!\n"+alarm.toJSON("").toString());
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
								daysArr[0] = true;
								break;
							case("tue"):
								daysArr[1] = true;
								break;
							case("wed"):
								daysArr[2] = true;
								break;
							case("thu"):
								daysArr[3] = true;
								break;
							case("fri"):
								daysArr[4] = true;
								break;
							case("sat"):
								daysArr[5] = true;
								break;
							case("sun"):
								daysArr[6] = true;
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
					
					/*
					 * TODO send the actual data to server
					 * For now we just echo it!
					 */
					ui.println("TODO >> Send this to server!\n"+alarm.toJSON("").toString());
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
	
	
	//summize the status as a string
	private String statusToString()
	{
		//returnable string
		String status = "";
		
		if(dataChannel.getConnected())
		{
			status += "Server: CONNECTED\n"
					+ "        " + dataChannel.getPairedAddress().toString()
					+ "        p: " + dataChannel.getPairedPort() + "\n\n" ;
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
	
	
	//instantiate a Terminal
	public static void main(String[] e)
	{	
		boolean relaunch = true;
		while(relaunch)
		{
			//start terminal, enter main loop
			Terminal terminal = new Terminal();
			int close = terminal.run();
			terminal = null;
			
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
