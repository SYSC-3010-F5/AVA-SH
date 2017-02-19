/**
*Class:             TerminalUI.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    19/02/2017                                              
*Version:           0.3.1                                         
*                                                                                   
*Purpose:           Local interface to main AVA server.
*					Basic Terminal form for text commands.
*					Send/Receive packets from server.
*					
* 
*Update Log			v0.3.1
*						- dialog added to get data for an alarm (day, time, name)
*						- imports tidied
*					v0.3.0
*						- getParsedInput() visibility changed to private
*						- input now gotten from blocking on a new method, getInput()
*						- ActionEvents from user input handled internally
*						- UI no longer runs on separate thread than control
*						- reqClose now returns if the window was closed
*						- echo framework for voice synthesis added
*					v0.2.2
*						- starting size of components actually works now
*					v0.2.1
*						- refactored into 2 classes to fit MVC model
*							\-->  TerminalUI.java	(view)
*							 \--> Terminal.java		(controller)
*						- many methods made public
*						- some additonal methods added
*					v0.2.0
*						- status pane updates added
*						- cool logo added
*						- terminal resizable
*						- menu bar disappearing fixed
*						- cmdMap changed from HashMap --> TreeMap in order to print commands in alphabetical order
*						- help menu + color selection made much more efficent+maintainable from old TFTP Project code
*						- word wrapping fixed
*					v0.1.0
*						- general framework
*						- console i/o
*						- window format
*						- basic methods for i/o
*/
package terminal;


//external imports
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

//import packages
import server.datatypes.Alarm;
import terminal.dialogs.DayAndTimeDialog;



public class TerminalUI extends JFrame implements ActionListener, KeyListener
{
	//ASCII art
	private static final String ASCII_AVA_LOGO = 
			"    ___ _    _____ \n" +
			"   /   | |  / /   |\n" +
			"  / /| | | / / /| |\n" +
			" / ___ | |/ / ___ |\n" +
			"/_/  |_|___/_/  |_|" ;

	
	//declaring class constants
	public static final String CONSOLE_IN = "txt/in";
	public static final String MENU_CLOSE = "m/file/close";
	private static final Font DEFAULT_CONSOLE_FONT = new Font("Monospaced", Font.PLAIN, 13);
	private static final Color DEFAULT_BACKGROUND_COLOR = Color.BLACK;
	private static final Color DEFAULT_TEXT_COLOR = Color.ORANGE;
	private static final int CMD_HISTORY_SIZE = 25;
	private static final int DEFAULT_WINDOW_X = 1250;
	private static final int DEFAULT_WINDOW_Y = 600;
	
	//declaring local instance constants
	private final String TERMINAL_NAME;
	private final String CMD_NOT_FOUND;
	
	//declaring local instance variables
	private CappedBuffer inputBuffer;
	private TreeMap<String, String> cmdMap;					
	private HashMap<String, Color[]> colorMap;
	private boolean inputReady;
	private String[] input;
	private boolean echo;
	
	//declaring local ui elements
	private JTextField consoleInput;
	private JTextArea consoleOutput;
	private JTextArea statusOverview;
	private JMenuItem mntmClose;

	
	//generic constructor
	public TerminalUI(String title, ActionListener listener, String cmdNotFound)
	{
		//set up main window frame
		super(title);
		this.setBounds(100, 100, DEFAULT_WINDOW_X, DEFAULT_WINDOW_Y);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setBackground(DEFAULT_BACKGROUND_COLOR);
		
		
		//initialize non-gui elements
		TERMINAL_NAME = title;
		CMD_NOT_FOUND = cmdNotFound;
		inputBuffer = new CappedBuffer(CMD_HISTORY_SIZE);
		cmdMap = new TreeMap<String, String>();
		echo = false;
		initColorMap();
		
		
		//set up menu bar
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, DEFAULT_WINDOW_X, 26);
		this.setJMenuBar(menuBar);
		
		
		//add categories to menu bar
		JMenu mnFile = new JMenu("File");
		JMenu mnEdit = new JMenu("Edit");
		JMenu mnOptions = new JMenu("Options");
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnFile);
		menuBar.add(mnEdit);
		menuBar.add(mnOptions);
		menuBar.add(mnHelp);
		
		
		//add to "File" category
		mntmClose = new JMenuItem("Close");
		mntmClose.setActionCommand(MENU_CLOSE);
		mntmClose.addActionListener(listener);
		mnFile.add(mntmClose);
		
		
		//set up plane for status/console
		JSplitPane mainSplitPane = new JSplitPane();
		mainSplitPane.setResizeWeight(0.14);
		mainSplitPane.setBounds(10, 37, 1174, 524);
		this.getContentPane().add(mainSplitPane);
		
		
		//set up pane for console i/o
		JSplitPane consolePane = new JSplitPane();
		JScrollPane outputPane = new JScrollPane();
		consolePane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		consolePane.setLeftComponent(outputPane);
		consolePane.setResizeWeight(1.00);
		mainSplitPane.setRightComponent(consolePane);
		
		
		//set up input text to console pane
		consoleInput = new JTextField();
		consoleInput.setActionCommand(CONSOLE_IN);
		consoleInput.addActionListener(this);
		consoleInput.addKeyListener(this);
		consoleInput.setColumns(10);
		consoleInput.setFont(DEFAULT_CONSOLE_FONT);
		consoleInput.setBackground(DEFAULT_BACKGROUND_COLOR);
		consoleInput.setForeground(DEFAULT_TEXT_COLOR);
		consoleInput.setCaretColor(DEFAULT_TEXT_COLOR);
		consolePane.setRightComponent(consoleInput);
		
		
		//set up output text to console pane
		consoleOutput = new JTextArea();
		consoleOutput.setEditable(false);
		consoleOutput.setWrapStyleWord(true);
		consoleOutput.setLineWrap(true);
		consoleOutput.setFont(DEFAULT_CONSOLE_FONT);
		consoleOutput.setBackground(DEFAULT_BACKGROUND_COLOR);
		consoleOutput.setForeground(DEFAULT_TEXT_COLOR);
		consoleOutput.setCaretColor(DEFAULT_TEXT_COLOR);
		outputPane.setViewportView(consoleOutput);
		
		
		//set up Pane+textArea for status overview
		JScrollPane statusPane = new JScrollPane();
		mainSplitPane.setLeftComponent(statusPane);
		statusOverview = new JTextArea();
		statusOverview.setLineWrap(true);
		statusOverview.setEditable(false);
		statusOverview.setWrapStyleWord(true);
		statusOverview.setFont(DEFAULT_CONSOLE_FONT);
		statusOverview.setBackground(DEFAULT_BACKGROUND_COLOR);
		statusOverview.setForeground(DEFAULT_TEXT_COLOR);
		statusOverview.setCaretColor(DEFAULT_TEXT_COLOR);
		statusPane.setViewportView(statusOverview);
		
		//set visible
		try 
		{
			this.setVisible(true);
			this.println("Starting interfaces on Thread <" + Thread.currentThread().getId() + ">...");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	
	//generic accessors
	public boolean getEcho()
	{
		return echo;
	}
	
	
	//generic mutators
	public void setEcho(boolean echo)
	{
		this.echo = echo;
	}
	

	//change color scheme
	//return false if invalid scheme
	public void colorScheme(String scheme)
	{
		if (scheme != null)
		{
			scheme = scheme.toLowerCase();
			
			//check if special case all
			if(scheme.equals("all"))							//TODO why doesn't this work?
			{
				//iterate through all colors
				Set<String> keys = colorMap.keySet();
				for(String key : keys)
				{
					this.colorScheme(key);
					
					if(key != null)
					{
						this.println(key);
					}
					else
					{
						this.println("<default>");
					}
					
					try 
					{
						Thread.sleep(750);
					} 
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
				}
				this.colorScheme(null);
				return;
			}
		}
		
		//search mapping for scheme
		Color[] colors = colorMap.get(scheme);
		if(colors != null)
		{
			//set console input
			consoleInput.setBackground(colors[0]);
			consoleInput.setForeground(colors[1]);
			consoleInput.setCaretColor(colors[1]);
			
			//set console output
			consoleOutput.setBackground(colors[0]);
			consoleOutput.setForeground(colors[1]);
			consoleOutput.setCaretColor(colors[1]);
			
			//set status overview
			statusOverview.setBackground(colors[0]);
			statusOverview.setForeground(colors[1]);
			statusOverview.setCaretColor(colors[1]);
		}
		else
		{
			println("\"" + scheme + "\" is not a valid color scheme");
		}
	}
	
	
	//initialize command map
	public void initCmdMap(TreeMap<String,String> cmdMap)
	{
		this.cmdMap = cmdMap;
	}
	
	
	//initialize color scheme map
	private void initColorMap()
	{
		colorMap = new HashMap<String, Color[]>();
		
		colorMap.put(null, new Color[]{DEFAULT_BACKGROUND_COLOR, DEFAULT_TEXT_COLOR});
		colorMap.put("aperture", new Color[]{Color.BLACK, Color.ORANGE});
		colorMap.put("bluescreen", new Color[]{Color.BLUE, Color.WHITE});
		colorMap.put("bumblebee", new Color[]{Color.BLACK, Color.YELLOW});
		colorMap.put("dark", new Color[]{Color.BLACK, Color.WHITE});
		colorMap.put("light", new Color[]{Color.WHITE, Color.BLACK});
		colorMap.put("matrix", new Color[]{Color.BLACK, Color.GREEN});
		colorMap.put("ocean", new Color[]{Color.CYAN, Color.DARK_GRAY});
		colorMap.put("prettyinpink", new Color[]{Color.BLACK, Color.MAGENTA});
		colorMap.put("xmas", new Color[]{Color.RED, Color.GREEN});
	}
	
	
	//return the users input if there is any, otherwise block
	public synchronized String[] getInput()
	{
		//check if input is ready
		while(!inputReady)
		{
			//block until notified
			try
			{
				wait();
			} 
			catch (InterruptedException e) {e.printStackTrace();}
		}
		
		//set flag and return
		inputReady = false;
		return input;
	}
	
	
	//return user input, parsed at spaces
	//return null if format error
	private String[] getParsedInput()
	{	
		//Prepare local method variables & save input and clear input line
		String input =  consoleInput.getText();
		LinkedList<String> stringList = new LinkedList<>();
		consoleInput.setText("");
		
		if(input.length() > 0)
		{
			//push to buffer
			inputBuffer.push(input);
		
			//echo input in proper format
			consoleOutput.append(">" + input + "\n");
			consoleOutput.setCaretPosition(consoleOutput.getDocument().getLength());
			
			//split input at ' ' (returns " " as single string)
			String toAdd = "";
			char[] rawInput = input.toCharArray();
			boolean ignoreSpace = false;
			for(int i=0; i<rawInput.length; i++)
			{
				//no closing quotation found, add character regardless
				if(ignoreSpace)
				{
					//check if char  is closing quotation
					if (rawInput[i] == '"')
					{
						ignoreSpace = false;
						stringList.add(toAdd);
						toAdd = "";
					}
					else
					{
						toAdd += rawInput[i];
					}
				}
				else
				{
					//check if char  is opening quotation
					if (rawInput[i] == '"')
					{
						ignoreSpace = true;
					}
					else if (rawInput[i] == ' ')
					{
						if (toAdd.length() > 0)
						{
							stringList.add(toAdd);
							toAdd = "";
						}
					}
					else
					{
						toAdd += rawInput[i];
					}
				}
			}
			
			//add final string if needed
			if (toAdd.length() > 0)
			{
				stringList.add(toAdd);
			}
			
			//terminating quotation was not found
			if (ignoreSpace)
			{
				this.printError("Terminating quotation not found.");
				return null;
			}
			else
			{
				return stringList.toArray(new String[0]);
			}
		}
		else
		{
			return null;
		}
	}
	
	
	//RAW print to the console, no formating
	//try not to use -- use println(...)
	public void print(String printable)
	{
		if(echo)
		{
			//TODO synthesis
		}
		consoleOutput.append(printable);
		consoleOutput.setCaretPosition(consoleOutput.getDocument().getLength());
	}
	
	
	//print a line in the console
	public void println(String printable)
	{
		printable = printable.replaceAll("\n", "\n ");
		print(" " + printable + "\n");
	}
	public void println()
	{
		this.print("\n");
	}
	
	
	//print an error
	public void printError(String msg)
	{
		if (msg != null)
		{
			this.println("Error: " + msg);
			JOptionPane.showMessageDialog(this, msg, TERMINAL_NAME, JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
	//print the general help menu
	public void printHelp(boolean all)
	{
		if(!all)
		{
			println("**Enter \"help <CMD>\" to view details on a specific command");
		}
		
		println("--------------- COMMAND LIST ---------------");
		Set<String> keys = cmdMap.keySet();
		
		//list all available commands
		for(String cmd : keys)
		{
			println(cmd);
			
			//print details
			if(all)
			{
				println(cmdMap.get(cmd) + "\n");
			}
		}
		
		println("--------------------------------------------\n");
	}
	
	
	//print a specific entry in the help menu
	public void printHelp(String key)
	{
		if(key.equals("all"))
		{
			this.printHelp(true);
		}
		else
		{
			String s = cmdMap.get(key);
			if (s != null)
			{
				this.println(s);
			}
			else
			{
				this.println(CMD_NOT_FOUND);
			}
		}
	}
	
	
	//update the status overview
	public void updateStatus(String newStatus)
	{
		//status string
		String status = ASCII_AVA_LOGO + "\n\n*****************************\n" + newStatus;
		
		//set the text to the string
		statusOverview.setText(status);
	}
	
	
	//clear the console
	public void clear()
	{
		consoleOutput.setText("");
		consoleOutput.setCaretPosition(consoleOutput.getDocument().getLength());
	}
	
	
	//request to close terminal
	public boolean reqClose()
	{
		//get user yes or no
		int i = JOptionPane.showConfirmDialog(this, "Are you sure you wish to exit this terminal\n(The main AVA Server will continue to run)", TERMINAL_NAME, JOptionPane.YES_NO_OPTION);
		if (i == JOptionPane.YES_OPTION)
		{
			this.close();
			return true;
		}
		return false;
	}
	//actually close
	public void close()
	{
		this.dispose();
	}
	
	
	//get an alarm for the user via dialog
	public Alarm getAlarm()
	{
		//get info from dialog
		DayAndTimeDialog dialog = new DayAndTimeDialog(this, this.TERMINAL_NAME);
		if(dialog.getCloseMode() == DayAndTimeDialog.OK_OPTION)
		{
			return dialog.getAlarm();
		}
		else
		{
			return null;
		}
	}
	
	
	@Override
	//handle key press
	public void keyPressed(KeyEvent ke)
	{	
		int keyCode = ke.getKeyCode();
		String bufferReturn = null;
		
		//keycode handler logic
		switch(keyCode)
		{
			//up arrow press, navigate BACKWARDS through buffer
			case (KeyEvent.VK_UP):
	        	bufferReturn = inputBuffer.getOlder();
	        	if (bufferReturn != null)
	        	{
	        		consoleInput.setText(bufferReturn);
	        	}
	        	break;
	        
	        //down arrow press, navigate FORWARDS through buffer
	        case (KeyEvent.VK_DOWN):
	        	bufferReturn = inputBuffer.getNewer();
	        	if (bufferReturn != null)
	        	{
	        		consoleInput.setText(bufferReturn);
	        	}
	        	break;
		}
	}


	@Override
	public void keyReleased(KeyEvent arg0) {}


	@Override
	public void keyTyped(KeyEvent arg0) {}

	
	//set the state of input and inputReady
	private synchronized void setInputState(String[] in)
	{
		if(input !=  null)
		{
			//input is good, set flag and notify
			inputReady = true;
			notifyAll();
		}
	}
	
	
	@Override
	//respond to user input
	public void actionPerformed(ActionEvent arg0) 
	{
		//get and parse input
		input = this.getParsedInput();
		setInputState(input);
	}
}
