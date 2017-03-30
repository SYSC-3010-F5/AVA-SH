/**
*Class:             TerminalUI.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    09/03/2017                                              
*Version:           1.2.1                                         
*                                                                                   
*Purpose:           Local interface to main AVA server.
*					Basic Terminal form for text commands.
*					
* 
*Update Log			v1.2.1
*						- dialog-related methods renamed for clairity
*						- dialog for selecting 1 of n options added
*					v1.2.0
*						- external window listener added for close button
*						- method added for info pop-ups
*						- method added for blocking on input for on x seconds
*					v1.1.1
*						- optional fullscreen added
*						- separate command-list window added
*						- print all command details patched
*						- colorMap changed from HashMap --> TreeMap & new scheme added
*					v1.1.0
*						- statusOverview shrunk to just display connection status
*						- new JText area added for predictive command help
*						- key events added for predictive command help
*						- default window size increased
*					v1.0.0
*						- UI finalized
*						- splitPane replaced with static sized JPanels
*						- menu items added
*						- initialization now calls colorScheme method with a default scheme name instead of
*						  manually initializing each component color
*					v0.3.2
*						- error dialog altered so multi-line in dialog will appear as a -- in console
*						- icon added
*					v0.3.1
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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

//import packages
import server.datatypes.Alarm;
import terminal.dialogs.DayAndTimeDialog;
import terminal.dialogs.TextView;



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
	public static final String MENU_CMD_LIST = "m/file/cmdlist";
	private static final String MENU_COLOR_SCHEME = "m/options/colorscheme";
	private static final String MENU_FULLSCREEN = "m/options/fullscreen";
	private static final Font DEFAULT_CONSOLE_FONT = new Font("Monospaced", Font.PLAIN, 13);
	private static final String DEFAULT_COLOR_SCHEME = "aperture";
	private static final int CMD_HISTORY_SIZE = 25;
	private static final int DEFAULT_WINDOW_X = 1400;
	private static final int DEFAULT_WINDOW_Y = 750;
	private static final int AUX_PANEL_WIDTH = 275;
	private static final int STATUS_PANE_HEIGHT = 250;
	
	//declaring local instance constants
	private final String TERMINAL_NAME;
	private final String CMD_NOT_FOUND;
	
	//declaring local instance variables
	private CappedBuffer inputBuffer;
	private String allCommands;
	private String lastCmd;
	private TreeMap<String, String> cmdMap;				
	private TreeMap<String, Color[]> colorMap;
	private boolean inputReady;
	private String[] input;
	private boolean echo;
	private TextView textViewer;
	
	//declaring local ui elements
	private JTextField consoleInput;
	private JTextArea consoleOutput;
	private JTextArea statusOverview;
	private JTextArea cmdHelp;

	//v1.0.0 constructor
	public TerminalUI(String title, ActionListener listener, String cmdNotFound)
	{
		this(title, listener, cmdNotFound, false, null);
	}
	//v1.1.1 constructor
	public TerminalUI(String title, ActionListener listener, String cmdNotFound, boolean isFullScreen)
	{
		this(title, listener, cmdNotFound, isFullScreen, null);
	}
	//generic constructor
	public TerminalUI(String title, ActionListener listener, String cmdNotFound, boolean isFullScreen, WindowAdapter closeOverride)
	{
		//set up main window frame
		super(title);
		this.setBounds(100, 100, DEFAULT_WINDOW_X, DEFAULT_WINDOW_Y);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(TerminalUI.class.getResource("/com/sun/java/swing/plaf/windows/icons/Computer.gif")));

		
		//initialize non-gui elements
		TERMINAL_NAME = title;
		CMD_NOT_FOUND = cmdNotFound;
		inputBuffer = new CappedBuffer(CMD_HISTORY_SIZE);
		cmdMap = new TreeMap<String, String>();
		echo = false;
		lastCmd = "";
		textViewer = null;
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
		JMenuItem mntmClose = new JMenuItem("Close");
		mntmClose.setActionCommand(MENU_CLOSE);
		mntmClose.addActionListener(listener);
		mnFile.add(mntmClose);
		
		
		//add to "Options" category
		JMenuItem mntmColor = new JMenuItem("Color Scheme");
		mntmColor.setActionCommand(MENU_COLOR_SCHEME);
		mntmColor.addActionListener(this);
		mnOptions.add(mntmColor);
		
		
		//add to "Help" category
		JMenuItem mntmCmds = new JMenuItem("Command List");
		mntmCmds.setActionCommand(MENU_CMD_LIST);
		mntmCmds.addActionListener(this);
		mnHelp.add(mntmCmds);
		
		
		//set up content pane for console/aux split
		JPanel contentPane = (JPanel)this.getContentPane();
		contentPane.setLayout(new BorderLayout(0,0));
		
		
		//set up pane for console i/o
		JPanel consolePane = new JPanel();
		consolePane.setLayout(new BorderLayout(0, 0));
		contentPane.add(consolePane, BorderLayout.CENTER);
		
		
		//set up input text to console pane
		consoleInput = new JTextField();
		consoleInput.setActionCommand(CONSOLE_IN);
		consoleInput.addActionListener(this);
		consoleInput.addKeyListener(this);
		JScrollPane outputPane = new JScrollPane();
		consolePane.add(outputPane, BorderLayout.CENTER);
		
		
		//set up output text to console pane
		consoleOutput = new JTextArea();
		consoleOutput.setEditable(false);
		consoleOutput.setWrapStyleWord(true);
		consoleOutput.setLineWrap(true);
		consoleOutput.setFont(DEFAULT_CONSOLE_FONT);
		outputPane.setViewportView(consoleOutput);
		consoleInput.setColumns(10);
		consoleInput.setFont(DEFAULT_CONSOLE_FONT);
		consolePane.add(consoleInput, BorderLayout.SOUTH);
		
		
		//set up auxiliary panel
		JPanel auxPanel = new JPanel();
		auxPanel.setLayout(new BorderLayout(0,0));
		getContentPane().add(auxPanel, BorderLayout.WEST);
		
		
		//set up scroll+textArea for status overview
		JScrollPane statusPane = new JScrollPane();
		statusPane.setPreferredSize(new Dimension(AUX_PANEL_WIDTH,STATUS_PANE_HEIGHT));
		auxPanel.add(statusPane, BorderLayout.NORTH);
		statusOverview = new JTextArea();
		statusOverview.setLineWrap(true);
		statusOverview.setEditable(false);
		statusOverview.setWrapStyleWord(true);
		statusOverview.setFont(DEFAULT_CONSOLE_FONT);
		statusPane.setViewportView(statusOverview);
		
		
		//set up textArea for predictive cmd help
		JScrollPane cmdPane = new JScrollPane();
		cmdPane.setPreferredSize(new Dimension(AUX_PANEL_WIDTH,0));
		auxPanel.add(cmdPane, BorderLayout.CENTER);
		cmdHelp = new JTextArea();
		cmdHelp.setEditable(false);
		cmdHelp.setTabSize(2);
		cmdHelp.setLineWrap(true);
		cmdHelp.setWrapStyleWord(true);
		cmdHelp.setFont(DEFAULT_CONSOLE_FONT);
		cmdPane.setViewportView(cmdHelp);
		
		
		//set up close button custom
		if(closeOverride != null)
		{
			this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			this.addWindowListener(closeOverride);
		}
		
		
		//set visible and color
		this.colorScheme(DEFAULT_COLOR_SCHEME);
		try 
		{
			String flavor = "windowed mode...";
			if(isFullScreen)
			{
				this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
				this.setUndecorated(true);
				flavor = "fullscreen mode...";
			}
			this.setVisible(true);
			this.println("Starting TerminalUI v1.2.1 on Thread <" + Thread.currentThread().getId() + "> in " + flavor);
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
			if(scheme.equals("all"))
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
		else
		{
			scheme = DEFAULT_COLOR_SCHEME;
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
			
			//set command help
			cmdHelp.setBackground(colors[0]);
			cmdHelp.setForeground(colors[1]);
			cmdHelp.setCaretColor(colors[1]);
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
		String all = "";
		for(String key : cmdMap.keySet())
		{
			all += key+"\n";
		}
		allCommands = all;
	}
	
	
	//initialize color scheme map
	private void initColorMap()
	{
		colorMap = new TreeMap<String, Color[]>();
		
		colorMap.put("aperture", new Color[]{Color.BLACK, Color.ORANGE});
		colorMap.put("bluescreen", new Color[]{Color.BLUE, Color.WHITE});
		colorMap.put("bumblebee", new Color[]{Color.BLACK, Color.YELLOW});
		colorMap.put("dark", new Color[]{Color.BLACK, Color.WHITE});
		colorMap.put("light", new Color[]{Color.WHITE, Color.BLACK});
		colorMap.put("matrix", new Color[]{Color.BLACK, Color.GREEN});
		colorMap.put("ocean", new Color[]{Color.BLUE, Color.CYAN});
		colorMap.put("prettyinpink", new Color[]{Color.BLACK, Color.MAGENTA});
		colorMap.put("xmas", new Color[]{Color.RED, Color.GREEN});
		colorMap.put("50shades", new Color[]{Color.DARK_GRAY, Color.LIGHT_GRAY});
		colorMap.put("flamingo", new Color[]{Color.DARK_GRAY, Color.PINK});
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
	
	
	//return the user input if there is any, only block for ms seconds
	public synchronized String[] getInput(int timeout)
	{
		//wait for input from maximum of timeout ms
		if (!inputReady)
		{
			try
			{
				wait(timeout);
			} 
			catch (InterruptedException e) {e.printStackTrace();}
		}
		
		
		if(inputReady)
		{
			//set flag and return
			inputReady = false;
			return input;
		}
		else
		{
			return null;
		}
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
			this.println("Error: " + msg.replaceAll("\n", " -- "));
			JOptionPane.showMessageDialog(this, msg, TERMINAL_NAME, JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
	//return all commands + details
	private String getAllCommandsAndDetails()
	{
		String s = "";
		s += "--------------- COMMAND LIST ---------------";
		Set<String> keys = cmdMap.keySet();
		
		//list all available commands
		for(String cmd : keys)
		{
			//add command and details
			s += cmd + "\n" + cmdMap.get(cmd) + "\n\n";
		}
		
		s += "--------------------------------------------\n";
		return s;
	}
	
	
	//print the general help menu
	public void printHelpAllCommands(boolean details)
	{
		if(!details)
		{
			println("**Enter \"help <CMD>\" to view details on a specific command");
		}
		
		println("--------------- COMMAND LIST ---------------");
		Set<String> keys = cmdMap.keySet();
		
		//list all available commands
		for(String cmd : keys)
		{
			//print command
			println(cmd);
			//print details
			if(details)
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
			this.printHelpAllCommands(true);
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
		String status = ASCII_AVA_LOGO + "\n\n\n" + newStatus;
		
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
	
	
	//popup info message
	public void dialogInfo(String msg)
	{
		//print to console
		println(msg);
		
		//open dialog
		JOptionPane.showMessageDialog(this, msg, TERMINAL_NAME, JOptionPane.INFORMATION_MESSAGE);
	}
	
	
	//get an alarm for the user via dialog
	public Alarm dialogGetAlarm()
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
	
	
	//get one option out of an array of options
	public Object dialogGetOptions(String msg, Object[] options)
	{
		return JOptionPane.showInputDialog(this, msg, TERMINAL_NAME, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
	}
	
	
	//get a color via dialog
	public void dialogSetColor()
	{
		//get a valid color scheme using system dialog
		String[] keys = colorMap.keySet().toArray(new String[0]);
		String selected = (String)JOptionPane.showInputDialog
		(
			this, 
			"Select a color scheme to use", 
			TERMINAL_NAME,
			JOptionPane.QUESTION_MESSAGE,
			null,
			keys,
			keys[0]
		);
		
		//change to the selected scheme
		if(selected != null)
		{
			colorScheme(selected);
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
	public void keyReleased(KeyEvent arg0) 							//TODO needs formating!
	{
		//check if input matchs any commands
		try
		{
			String currentCmd = consoleInput.getText().split(" ")[0];
			if(!lastCmd.equals(currentCmd))
			{
				String output = allCommands;
		    	Set<String> keys = cmdMap.keySet();
		    	for(String key : keys)
		    	{
		    		if(key.equals(currentCmd))
		    		{
		    			output = key + "\n" + cmdMap.get(key);
		    			break;
		    		}
		    	}
		    	cmdHelp.setText("\n".concat(output));
			}
			lastCmd = currentCmd;
		}
		catch (ArrayIndexOutOfBoundsException e){};
	}


	@Override
	public void keyTyped(KeyEvent arg0)
	{
 
	}

	
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
	public void actionPerformed(ActionEvent ae) 
	{
		//determine actions based on command 
		String cmd = ae.getActionCommand();
		switch(cmd)
		{
			//color scheme menu item pressed
			case(MENU_COLOR_SCHEME):
				dialogSetColor();
				break;
			
				
			//command list menu item pressed
			case(MENU_CMD_LIST):
				if (textViewer != null)
				{
					if(!textViewer.isDisplayable())
					{
						textViewer = new TextView(TERMINAL_NAME, getAllCommandsAndDetails(), consoleOutput.getForeground(), consoleOutput.getBackground());
					}
					else
					{
						textViewer.setFocusableWindowState(true);
					}
				}
				else
				{
					textViewer = new TextView(TERMINAL_NAME, getAllCommandsAndDetails(), consoleOutput.getForeground(), consoleOutput.getBackground());
				}
				break;
			
			
			//normal text input
			default:
				//get and parse input
				input = this.getParsedInput();
				setInputState(input);
				break;
		};
	}
}
