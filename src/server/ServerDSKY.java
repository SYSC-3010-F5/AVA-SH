/**
*Class:             ServerDSKY.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    23/03/2017                                              
*Version:           2.2.1                                         
*                                                                                   
*Purpose:           Displays plain text with time stamps (DiSplay).
*					Displays registry for server.
*					A few buttons for basic server control, should be avoided (KeYboard).
*			
*					**NOTE
*					Only methods dealing with the display:
*					 {ie println(String), println(), clear()}
*					Are guaranteed thread safe.
*					
* 
*Update Log			v2.2.1
*						- option to have custom handling of close button added
*						- constructors tidied
*					v2.2.0
*						- east panel registered devices and header split into separate GUI objects
*						- soft reset replaced with update for events
*					v2.1.1
*						- changed pause/resume button to clear events
*					v2.1.0
*						- methods to print to display now thread safe
*						  (as we now have DayScheduler daemon threads calling the print)
*						- optional fullscreen mode added
*					v2.0.1
*						- buttons not saved as instance variables now
*						- added method for printing blank lines
*					v2.0.0
*						- completely overhauled and remodeled into DSKY style
*					v1.0.0
*						- initial design to show plain text
*/
package server;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import server.datatypes.ServerEvent;

import java.awt.GridLayout;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.awt.Toolkit;
import java.awt.BorderLayout;
import javax.swing.JSplitPane;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class ServerDSKY extends JFrame implements ActionListener
{
	//ASCII art
	private static final String ASCII_HEADER = 
			"\t    ___ _    _____ \n" +
			"\t   /   | |  / /   |\n" +
			"\t  / /| | | / / /| |\tMain\n" +
			"\t / ___ | |/ / ___ |\tServer\n" +
			"\t/_/  |_|___/_/  |_|\n\n" ;
	
	//declaring static class constants
	public static final String BTN_SOFT_SHUTDOWN = "btn/softshutdown";
	public static final String BTN_HARD_SHUTDOWN = "btn/hardshutdown";
	public static final String BTN_UPDATE_EVENTS = "btn/updateevents";
	public static final String BTN_HARD_RESET = "btn/hardreset";
	public static final String BTN_CLEAR = "btn/cleardisplay";
	public static final String BTN_UPDATE_REGISTRY = "btn/updateregistry";
	public static final String BTN_ERASE_REGISTRY = "btn/eraseregistry";
	public static final String BTN_CLEAR_EVENTS = "btn/clearevents";
	private static final String WINDOW_TITLE = MainServer.SERVER_NAME;
	private static final int DEFAULT_WINDOW_X = 1000;
	private static final int DEFAULT_WINDOW_Y = 725;
	private static final Font DEFAULT_FONT = new Font("Monospaced", Font.PLAIN, 13);
	private static final Font BUTTON_FONT = new Font("Monospaced", Font.BOLD, 13);
	private static final Color DEFAULT_BACKGROUND_COLOR = Color.BLACK;
	private static final Color DEFAULT_TEXT_COLOR = Color.ORANGE;
	
	//declaring local instance variables
	private JTextArea display;
	private JTextArea registryText, eventText;
	private JButton btnClearEvents;
	
	
	//return current time
	public static String getCurrentTime() 
	{
	    return new SimpleDateFormat("HH:mm:ss").format(new Date());
	}
	
	
	
	//constructor from v2.0.0
	public ServerDSKY(String title, String location, ActionListener listener)
	{
		this(title, location, listener, false, null);
	}
	//constructor from v2.2.0
	public ServerDSKY(String title, String location, ActionListener listener, boolean isFullScreen)
	{
		this(title, location, listener, isFullScreen, null);
	}
	//generic constructor
	public ServerDSKY(String title, String location, ActionListener listener, boolean isFullScreen, WindowAdapter closeOverride) 
	{
		//set up main window frame
		super(title + "@" + location);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(ServerDSKY.class.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif")));
		this.setBounds(100, 0, DEFAULT_WINDOW_X, DEFAULT_WINDOW_Y);
		this.getContentPane().setBackground(DEFAULT_BACKGROUND_COLOR);
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		
		
		//add scroll pane for scrolling output
		JScrollPane mainOutputScroll = new JScrollPane();
		getContentPane().add(mainOutputScroll, BorderLayout.CENTER);
		
		//add text area for output
		display = new JTextArea();
		display.setTabSize(4);
		display.setEditable(false);
		display.setFont(DEFAULT_FONT);
		display.setBackground(DEFAULT_BACKGROUND_COLOR);
		display.setForeground(DEFAULT_TEXT_COLOR);
		display.setCaretColor(DEFAULT_TEXT_COLOR);;
		mainOutputScroll.setViewportView(display);
		
		//add panel to contain east objects
		JPanel eastPanel = new JPanel();
		eastPanel.setPreferredSize(new Dimension(350,1));
		getContentPane().add(eastPanel, BorderLayout.EAST);
		eastPanel.setLayout(new BorderLayout(0, 0));
		
		
		//add header for east panel
		JTextArea header = new JTextArea();
		header.setTabSize(4);
		header.setEditable(false);
		header.setText(ASCII_HEADER + "\t@" + location);
		header.setFont(DEFAULT_FONT);
		header.setBackground(DEFAULT_BACKGROUND_COLOR);
		header.setForeground(DEFAULT_TEXT_COLOR);
		header.setCaretColor(DEFAULT_TEXT_COLOR);;
		eastPanel.add(header, BorderLayout.NORTH);
		
		
		//add split pane for registry/live events
		JSplitPane eastSplit = new JSplitPane();
		eastSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
		eastSplit.setResizeWeight(0.5);
		eastPanel.add(eastSplit, BorderLayout.CENTER);
		
		
		//add text area for event info in scroll
		JScrollPane eventScroll = new JScrollPane();
		eventText = new JTextArea();
		eventText.setTabSize(4);
		eventText.setEditable(false);
		eventText.setFont(DEFAULT_FONT);
		eventText.setBackground(DEFAULT_BACKGROUND_COLOR);
		eventText.setForeground(DEFAULT_TEXT_COLOR);
		eventText.setCaretColor(DEFAULT_TEXT_COLOR);;
		eventScroll.setViewportView(eventText);
		eastSplit.setBottomComponent(eventScroll);
		
		//add text area for registry info in scroll
		JScrollPane registryScroll = new JScrollPane();
		registryText = new JTextArea();
		registryText.setTabSize(4);
		registryText.setEditable(false);
		registryText.setFont(DEFAULT_FONT);
		registryText.setBackground(DEFAULT_BACKGROUND_COLOR);
		registryText.setForeground(DEFAULT_TEXT_COLOR);
		registryText.setCaretColor(DEFAULT_TEXT_COLOR);;
		registryScroll.setViewportView(registryText);
		eastSplit.setTopComponent(registryScroll);
		
		
		//add panel for buttons in south area of eastern panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setPreferredSize(new Dimension(90,90));
		buttonPanel.setLayout(new GridLayout(2, 3, 0, 0));
		buttonPanel.setBackground(DEFAULT_BACKGROUND_COLOR);
		eastPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		JButton btnUpdateEvents = new JButton("<html>Update<br />Events</html>");
		btnUpdateEvents.setActionCommand(BTN_UPDATE_EVENTS);
		btnUpdateEvents.addActionListener(listener);
		btnUpdateEvents.setFont(BUTTON_FONT);
		btnUpdateEvents.setBackground(DEFAULT_BACKGROUND_COLOR);
		btnUpdateEvents.setForeground(DEFAULT_TEXT_COLOR);
		
		JButton btnHardReset = new JButton("<html>Hard<br />Reset</html>");
		btnHardReset.setActionCommand(BTN_HARD_RESET);
		btnHardReset.addActionListener(listener);
		btnHardReset.setFont(BUTTON_FONT);
		btnHardReset.setBackground(DEFAULT_BACKGROUND_COLOR);
		btnHardReset.setForeground(DEFAULT_TEXT_COLOR);
		
		JButton btnClearDisplay = new JButton("<html>Clear<br />Display</html>");
		btnClearDisplay.setActionCommand(BTN_CLEAR);
		btnClearDisplay.addActionListener(this);
		btnClearDisplay.setFont(BUTTON_FONT);
		btnClearDisplay.setBackground(DEFAULT_BACKGROUND_COLOR);
		btnClearDisplay.setForeground(DEFAULT_TEXT_COLOR);
		
		JButton btnUpdateRegistry = new JButton("<html>Update<br />Registry</html>");
		btnUpdateRegistry.setActionCommand(BTN_UPDATE_REGISTRY);
		btnUpdateRegistry.addActionListener(listener);
		btnUpdateRegistry.setFont(BUTTON_FONT);
		btnUpdateRegistry.setBackground(DEFAULT_BACKGROUND_COLOR);
		btnUpdateRegistry.setForeground(DEFAULT_TEXT_COLOR);
		
		JButton btnEraseRegistry = new JButton("<html>Erase<br />Registry</html>");
		btnEraseRegistry.setActionCommand(BTN_ERASE_REGISTRY);
		btnEraseRegistry.addActionListener(listener);
		btnEraseRegistry.setFont(BUTTON_FONT);
		btnEraseRegistry.setBackground(DEFAULT_BACKGROUND_COLOR);
		btnEraseRegistry.setForeground(DEFAULT_TEXT_COLOR);
		
		JButton btnHardShutdown = new JButton("<html>Hard<br />Shutdown</html>");
		btnHardShutdown.setActionCommand(BTN_HARD_SHUTDOWN);
		btnHardShutdown.addActionListener(listener);
		btnHardShutdown.setFont(BUTTON_FONT);
		btnHardShutdown.setBackground(DEFAULT_BACKGROUND_COLOR);
		btnHardShutdown.setForeground(DEFAULT_TEXT_COLOR);
		
		JButton btnSoftShutdown = new JButton("<html>Soft<br />Shutdown</html>");
		btnSoftShutdown.setActionCommand(BTN_SOFT_SHUTDOWN);
		btnSoftShutdown.addActionListener(listener);
		btnSoftShutdown.setFont(BUTTON_FONT);
		btnSoftShutdown.setBackground(DEFAULT_BACKGROUND_COLOR);
		btnSoftShutdown.setForeground(DEFAULT_TEXT_COLOR);
		
		
		btnClearEvents = new JButton("<html>Clear<br />Events</html>");
		btnClearEvents.setActionCommand(BTN_CLEAR_EVENTS);
		btnClearEvents.addActionListener(listener);
		btnClearEvents.setFont(BUTTON_FONT);
		btnClearEvents.setBackground(DEFAULT_BACKGROUND_COLOR);
		btnClearEvents.setForeground(DEFAULT_TEXT_COLOR);
		
		buttonPanel.add(btnHardReset);
		buttonPanel.add(btnUpdateEvents);
		buttonPanel.add(btnSoftShutdown);
		buttonPanel.add(btnHardShutdown);
		buttonPanel.add(btnEraseRegistry);
		buttonPanel.add(btnUpdateRegistry);
		buttonPanel.add(btnClearDisplay);
		buttonPanel.add(btnClearEvents);
		
		//set up close button custom
		if(closeOverride != null)
		{
			this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			this.addWindowListener(closeOverride);
		}
		
		//set visible
		try 
		{
			String flavor = "in windowed mode...";
			if(isFullScreen)
			{
				this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
				this.setUndecorated(true);
				flavor = "in fullscreen mode...";
			}
			this.setVisible(true);
			this.updateRegistry(null);
			this.updateEvent(null, null);
			this.println("DSKY running " + flavor);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			System.exit(0);
		}
	}

	
	//close UI
	public void close()
	{
		this.dispose();
	}
	
	
	//use dialogs to get yes or no
	public boolean getBoolean(String msg)
	{
		int i = JOptionPane.showConfirmDialog(this, msg, WINDOW_TITLE, JOptionPane.YES_NO_OPTION);
		if (i == JOptionPane.YES_OPTION)
		{
			return true;
		}
		return false;
	}
	
	
	//print an error that was not supposed to happen
	public void printError(String string)
	{
		println("ERROR >> " + string);
	}
	
	
	//generic print
	public synchronized void println(String string)
	{
		string = string.replaceAll("\n", "\n        \t");
		display.append(getCurrentTime() + "\t" + string + "\n");
		display.setCaretPosition(display.getDocument().getLength());
	}
	
	
	//print empty line
	public synchronized void println()
	{
		display.append("\n");
		display.setCaretPosition(display.getDocument().getLength());
	}
	
	
	//generic clear
	public synchronized void clear()
	{
		display.setText("");
		display.setCaretPosition(display.getDocument().getLength());
	}
	
	
	//update the registry overview
	public void updateRegistry(HashMap<String,InetSocketAddress> registry)
	{
		String string = "\n";
		
		//iterate through all entries
		if(registry != null)
		{
			Set<String> keys = registry.keySet();
			for(String key : keys)
			{
				InetSocketAddress address = registry.get(key);
				string += " \"" + key + "\" @ " + address.toString() + "\n";
			}
		}
		//set the text
		registryText.setText(string);
		registryText.setCaretPosition(0);
	}
	
	
	//update the event overview
	public void updateEvent(ArrayList<ServerEvent> npe, ArrayList<ServerEvent> pe)
	{
		String s = "Update @ " + getCurrentTime() + "\n\n";
		if(npe != null)
		{
			for(ServerEvent event : npe)
			{
				s += event.toString() +"\n";
			}
		}
		if(pe != null)
		{
			for(ServerEvent event : pe)
			{
				s += event.toString() + "\n";
			}
		}
		eventText.setText(s);
		eventText.setCaretPosition(0);
	}
	

	@Override
	//handl button presses
	public void actionPerformed(ActionEvent arg0) 
	{
		this.clear();
		this.println("BUTTON >> CLEAR");
	}
}
