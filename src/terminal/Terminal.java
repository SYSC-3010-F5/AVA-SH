/**
*Class:             Terminal.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    18/01/2016                                              
*Version:           0.1.0                                         
*                                                                                   
*Purpose:           Local interface to main AVA server.
*					Basic Terminal form for text commands.
*					
* 
*Update Log			v0.1.0
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
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class Terminal extends JFrame implements KeyListener, ActionListener, Runnable
{
	//declaring class constants
	private static final String TERMINAL_NAME = "AVA Terminal";
	private static final String CMD_NOT_FOUND = "Command not recongnized";
	private static final Font DEFAULT_CONSOLE_FONT = new Font("Monospaced", Font.PLAIN, 13);
	private static final Color DEFAULT_BACKGROUND_COLOR = Color.BLACK;
	private static final Color DEFAULT_TEXT_COLOR = Color.ORANGE;
	private static final int CMD_HISTORY_SIZE = 25;
	private static final int DEFAULT_WINDOW_X = 1200;
	private static final int DEFAULT_WINDOW_Y = 600;
	
	//declaring local instance variables
	private CappedBuffer inputBuffer;
	private HashMap<String, String> cmdMap;
	private JTextField consoleInput;
	private JTextArea consoleOutput;
	private JTextArea statusOverview;
	private JMenuItem mntmClose;

	
	//generic constructor
	public Terminal(String title)
	{
		//set up main window frame
		super(title);
		this.setResizable(false);
		this.setBounds(100, 100, DEFAULT_WINDOW_X, DEFAULT_WINDOW_Y);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(null);
		this.getContentPane().setBackground(DEFAULT_BACKGROUND_COLOR);
		
		
		//initialize non-gui elements
		inputBuffer = new CappedBuffer(CMD_HISTORY_SIZE);
		cmdMap = new HashMap();
		initCmdMap();
		
		
		//set up menu bar
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, DEFAULT_WINDOW_X, 26);
		this.getContentPane().add(menuBar);
		
		
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
		mntmClose.addActionListener(this);
		mnFile.add(mntmClose);
		
		
		//set up plane for status/console
		JSplitPane mainSplitPane = new JSplitPane();
		mainSplitPane.setBounds(10, 37, 1174, 524);
		this.getContentPane().add(mainSplitPane);
		
		
		//set up pane for console i/o
		JSplitPane consolePane = new JSplitPane();
		JScrollPane outputPane = new JScrollPane();
		consolePane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		consolePane.setLeftComponent(outputPane);
		mainSplitPane.setRightComponent(consolePane);
		
		
		//set up input text to console pane
		consoleInput = new JTextField();
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
		consoleOutput.setFont(DEFAULT_CONSOLE_FONT);
		consoleOutput.setBackground(DEFAULT_BACKGROUND_COLOR);
		consoleOutput.setForeground(DEFAULT_TEXT_COLOR);
		consoleOutput.setCaretColor(DEFAULT_TEXT_COLOR);
		outputPane.setViewportView(consoleOutput);
		
		
		//set up Pane+textArea for status overview
		JScrollPane statusPane = new JScrollPane();
		mainSplitPane.setLeftComponent(statusPane);
		statusOverview = new JTextArea();
		statusOverview.setEditable(false);
		statusOverview.setWrapStyleWord(true);
		statusOverview.setFont(DEFAULT_CONSOLE_FONT);
		statusOverview.setBackground(DEFAULT_BACKGROUND_COLOR);
		statusOverview.setForeground(DEFAULT_TEXT_COLOR);
		statusOverview.setCaretColor(DEFAULT_TEXT_COLOR);
		statusPane.setViewportView(statusOverview);
	}
	
	
	@Override
	//called on thread start
	public void run() 
	{
		//set visible
		try 
		{
			this.setVisible(true);
			this.println("Starting terminal on Thread <" + Thread.currentThread() + ">...");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	
	//initialize command map
	private void initCmdMap()
	{
		cmdMap.put(null, CMD_NOT_FOUND);
		cmdMap.put("help", "Exactly what it says on the tin");
		cmdMap.put("clear", "Removes all text from the console output pane");
		cmdMap.put("close", "Exit the local terminal");
	}
	
	
	//print the general help menu
	private void printHelp()
	{
		//TODO
		this.println("Someone remind me to build this (custom if shortform, or use main cmdMapping for details?)");
	}
	
	
	//clear the console
	public void clear()
	{
		consoleOutput.setText("");
		consoleOutput.setCaretPosition(consoleOutput.getDocument().getLength());
	}
	
	
	//request to close terminal
	public void reqClose()
	{
		//get user yes or no
		int i = JOptionPane.showConfirmDialog(this, "Are you sure you wish to exit this terminal\n(The main AVA Server will continue to run)", TERMINAL_NAME, JOptionPane.YES_NO_OPTION);
		if (i == JOptionPane.YES_OPTION)
		{
			this.close();
		}
	}
	//actually close
	public void close()
	{
		this.dispose();
		System.exit(ABORT);
	}
	
	
	//RAW print to the console, no formating
	//try not to use -- use println(...)
	private void print(String printable)
	{
		consoleOutput.append(printable);
		consoleOutput.setCaretPosition(consoleOutput.getDocument().getLength());
	}
	
	
	//print a line in the console
	public void println(String printable)
	{
		printable = printable.replaceAll("\n", "\n ");
		consoleOutput.append(" " + printable + "\n");
		consoleOutput.setCaretPosition(consoleOutput.getDocument().getLength());
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
			JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
	//return user input, parsed at spaces
	//return null if format error
	public String[] getParsedInput()
	{	
		//Prep local method variables
		//save input and clear input line
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
					this.clear();
				}
				else
				{
					println(CMD_NOT_FOUND);
				}
				break;
			
			//print help menu
			case("help"):
				if(length == 1)
				{
					this.printHelp();
				}
				else if (length == 2)
				{
					String s = cmdMap.get(input[1]);
					if (s != null)
					{
						this.println(s);
					}
					else
					{
						this.println(CMD_NOT_FOUND);
					}
				}
				else
				{
					println(CMD_NOT_FOUND);
				}
				break;
			
			//close
			case("close"):
				if (length == 1)
				{
					this.close();
				}
				else
				{
					println(CMD_NOT_FOUND);
				}
				break;
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


	@Override
	//handle input
	public void actionPerformed(ActionEvent e) 
	{
		Object src = e.getSource();
		
		//consoleInput
		if (src == consoleInput)
		{
			String[] input = this.getParsedInput();
			if(input != null)
			{
				if(input.length > 0)
				{
					this.handleConsoleInput(input);
				}
			}
		}
		
		//other here
		else if (src == mntmClose)
		{
			this.reqClose();
		}
	}
	
	
	//quick dirty test
	public static void main(String[] e)
	{
		Terminal t = new Terminal("Title");
		t.run();
	}
}
