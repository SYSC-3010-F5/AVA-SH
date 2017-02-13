/**
*Class:             Terminal.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    12/02/2017                                              
*Version:           0.3.0                                         
*                                                                                   
*Purpose:           Local interface to main AVA server.
*					Basic Terminal form for text commands.
*					Send/Receive packets from server.
*					
* 
*Update Log			v0.3.0
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
import java.util.TreeMap;
import javax.swing.JFrame;



public class Terminal extends JFrame implements ActionListener, Runnable
{
	//declaring local class constants
	private static final String TERMINAL_NAME = "AVA Terminal";
	private static final String VERSION = "v0.2.0";
	private static final String CMD_NOT_FOUND = "Command not recongnized";
	private static final int RETRY_QUANTUM = 10;
	
	//declaring local instance variables
	private boolean runFlag;
	private TerminalUI ui;
	private Inet4Address serverAddress;
	private int pairedPort;
	
	
	//generic constructor
	public Terminal()
	{
		//init ui
		ui = new TerminalUI(TERMINAL_NAME+" "+VERSION, this, CMD_NOT_FOUND);
		ui.initCmdMap(this.initCmdMap());
		
		//init variables
		serverAddress = null;
		pairedPort = 0;
		runFlag = true;
		
		//update ui
		ui.updateStatus(this.statusToString());
		ui.println("Starting control on Thread <" + Thread.currentThread().getId() + ">...");
	}
	
	
	@Override
	//main run-loop of the terminal
	public void run()
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
	}
	
	
	//close the terminal
	public void close()
	{
		//TODO disconnect from server
		runFlag = false;
	}
	
	
	//initialize command map
	private TreeMap<String,String> initCmdMap()
	{
		TreeMap<String,String> cmdMap = new TreeMap<String,String>();
		
		cmdMap.put("help", "Print help/details on command usage\n"
					+ "\tparam1= n/a ::   Print all commands to screen\n"
					+ "\tparam1= all ::   Print details on all commands\n"
					+ "\tparam1= <CMD> :: Print details on command <CMD>");
		
		cmdMap.put("clear", "Removes all text from the console output pane");
		
		cmdMap.put("close", "Exit the local terminal");
		
		cmdMap.put("connect", "Establish/Reestablish a connection to the main server\n"										//TODO implement this
					+ "\tparam1= n/a ::             Attempt to establish server connection at default server address\n"
					+ "\tparam1= xxx.xxx.xxx.xxx :: Attempt to establish server connection to this IPv4 address");
		
		cmdMap.put("disconnect", "Disconnect from main server");															//TODO implement this
		
		cmdMap.put("serverip", "Echo/Change the currently set server address\n"												//TODO implement this
					+ "\tparam1= n/a ::             Echo the IPv4 address serverAddress is set to\n"
					+ "\tparam1= xxx.xxx.xxx.xxx :: Change serverAddress to entered IPv4 address\n"
					+ "\tparam1= default ::         Change the serverAddress to the default value\n"
					+ "\tparam1= local ::           Change the serverAddress to the local machine");
		
		cmdMap.put("ip", "Request and return the IP of a module\n"															//TODO implement this
					+ "\tparam1= n/a ::   Print the IPv4 address of the local machine\n"
					+ "\tparam1= local :: Print the IPv4 address of the local machine\n"
					+ "\tparam1= <STR> :: Sends a request to the server for the IPv4 address of module with String identifier <STR>");
		
		cmdMap.put("color", "Change the color theme of the terminal\n"
					+ "\tparam1= n/a ::   Set the color scheme to the default\n"
					+ "\tparam1= all ::   Show all color schemes\n"
					+ "\tparam1= <STR> :: Set the color schemes to <STR>\n"
					+ "\tTHEMES:          aperture, bluescreen, bumblebee, dark,\n" 
					+ "\t                 light, matrix, ocean, prettyinpink, xmas");
		
		cmdMap.put("update", "Manually force update for the status overview");
		
		cmdMap.put("echo", "Test/toggle the voice synthesis of the system\n"												//TODO implement this
					+ "\tparam1= na ::     Show the current state of voice echo\n"
					+ "\tparam1= true ::   Set the terminal to synthesize all text as voice\n"
					+ "\tparam1= false ::  Set the terminal to stop synthesis of all text as voice\n"
					+ "\tparam1 = <STR> :: Synthesize the entered String to voice");
		
		cmdMap.put("alarm", "Schedual an alarm at a certain time\n"															//TODO implement this
					+ "\tparam1: n/a      ::  Launch dialog to schedual alarm\n"
					+ "\tparam1: ddd:hh:mm :: Set the alarm to go off on day ddd, hour hh, minute mm\n"
					+ "\t                     (ddd such that mon, tue, wed, thu, fri, sat, sun)");
		
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
							this.close();
							//TODO close
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
		
		if(serverAddress != null)
		{
			status += "Server: CONNECTED\n"
					+ "        " + serverAddress.toString()
					+ "        p: " + pairedPort + "\n\n" ;
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
					this.close();
				}
				break;
		}
	}
	
	
	//instantiate a Terminal
	public static void main(String[] e)
	{
		//start terminal, enter main loop
		Terminal terminal = new Terminal();
		terminal.run();
	}
}
