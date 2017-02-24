/**
*Class:             ServerDSKY.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    23/02/2017                                              
*Version:           2.0.0                                         
*                                                                                   
*Purpose:           Displays plain text with time stamps (DiSplay).
*					Displays registry for server.
*					A few buttons for basic server control, should be avoided (KeYboard).
*					
* 
*Update Log			v2.0.0
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
import java.awt.GridLayout;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
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

public class ServerDSKY extends JFrame implements ActionListener
{
	//declaring static class constants
	public static final String BTN_SOFT_SHUTDOWN = "btn/softshutdown";
	public static final String BTN_HARD_SHUTDOWN = "btn/hardshutdown";
	public static final String BTN_SOFT_RESET = "btn/softreset";
	public static final String BTN_HARD_RESET = "btn/hardreset";
	public static final String BTN_CLEAR = "btn/cleardisplay";
	public static final String BTN_UPDATE_REGISTRY = "btn/updateregistry";
	public static final String BTN_ERASE_REGISTRY = "btn/eraseregistry";
	public static final String BTN_PAUSE_OR_RESUME = "btn/pauseorresume";
	private static final int DEFAULT_WINDOW_X = 1000;
	private static final int DEFAULT_WINDOW_Y = 725;
	private static final Font DEFAULT_FONT = new Font("Monospaced", Font.PLAIN, 13);
	private static final Font BUTTON_FONT = new Font("Monospaced", Font.BOLD, 13);
	private static final Color DEFAULT_BACKGROUND_COLOR = Color.BLACK;
	private static final Color DEFAULT_TEXT_COLOR = Color.ORANGE;
	
	//declaring local instance variables
	private JTextArea display;
	private JTextArea registryText;
	private JButton btnSoftShutdown;
	private JButton btnHardShutdown;
	private JButton btnSoftReset;
	private JButton btnHardReset; 
	private JButton btnClearDisplay;
	private JButton btnEraseRegistry;
	private JButton btnUpdateRegistry;
	private JButton btnPauseOrResume;
	
	
	//return current time
	public static String getCurrentTime() 
	{
	    return new SimpleDateFormat("HH:mm:ss").format(new Date());
	}
	
	
	//generic constructor
	public ServerDSKY(String title, ActionListener listener) 
	{
		//set up main window frame
		super(title);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(ServerDSKY.class.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif")));
		this.setBounds(100, 0, DEFAULT_WINDOW_X, DEFAULT_WINDOW_Y);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
		eastPanel.add(registryScroll, BorderLayout.CENTER);
		
		
		//add panel for buttons in south area of eastern panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setPreferredSize(new Dimension(90,90));
		buttonPanel.setLayout(new GridLayout(2, 3, 0, 0));
		buttonPanel.setBackground(DEFAULT_BACKGROUND_COLOR);
		eastPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		btnSoftReset = new JButton("<html>Soft<br />Reset</html>");
		btnSoftReset.setActionCommand(BTN_SOFT_RESET);
		btnSoftReset.addActionListener(listener);
		btnSoftReset.setFont(BUTTON_FONT);
		btnSoftReset.setBackground(DEFAULT_BACKGROUND_COLOR);
		btnSoftReset.setForeground(DEFAULT_TEXT_COLOR);
		
		btnHardReset = new JButton("<html>Hard<br />Reset</html>");
		btnHardReset.setActionCommand(BTN_HARD_RESET);
		btnHardReset.addActionListener(listener);
		btnHardReset.setFont(BUTTON_FONT);
		btnHardReset.setBackground(DEFAULT_BACKGROUND_COLOR);
		btnHardReset.setForeground(DEFAULT_TEXT_COLOR);
		
		btnClearDisplay = new JButton("<html>Clear<br />Display</html>");
		btnClearDisplay.setActionCommand(BTN_CLEAR);
		btnClearDisplay.addActionListener(this);
		btnClearDisplay.setFont(BUTTON_FONT);
		btnClearDisplay.setBackground(DEFAULT_BACKGROUND_COLOR);
		btnClearDisplay.setForeground(DEFAULT_TEXT_COLOR);
		
		btnUpdateRegistry = new JButton("<html>Update<br />Registry</html>");
		btnUpdateRegistry.setActionCommand(BTN_UPDATE_REGISTRY);
		btnUpdateRegistry.addActionListener(listener);
		btnUpdateRegistry.setFont(BUTTON_FONT);
		btnUpdateRegistry.setBackground(DEFAULT_BACKGROUND_COLOR);
		btnUpdateRegistry.setForeground(DEFAULT_TEXT_COLOR);
		
		btnEraseRegistry = new JButton("<html>Erase<br />Registry</html>");
		btnEraseRegistry.setActionCommand(BTN_ERASE_REGISTRY);
		btnEraseRegistry.addActionListener(listener);
		btnEraseRegistry.setFont(BUTTON_FONT);
		btnEraseRegistry.setBackground(DEFAULT_BACKGROUND_COLOR);
		btnEraseRegistry.setForeground(DEFAULT_TEXT_COLOR);
		
		btnHardShutdown = new JButton("<html>Hard<br />Shutdown</html>");
		btnHardShutdown.setActionCommand(BTN_HARD_SHUTDOWN);
		btnHardShutdown.addActionListener(listener);
		btnHardShutdown.setFont(BUTTON_FONT);
		btnHardShutdown.setBackground(DEFAULT_BACKGROUND_COLOR);
		btnHardShutdown.setForeground(DEFAULT_TEXT_COLOR);
		
		btnSoftShutdown = new JButton("<html>Soft<br />Shutdown</html>");
		btnSoftShutdown.setActionCommand(BTN_SOFT_SHUTDOWN);
		btnSoftShutdown.addActionListener(listener);
		btnSoftShutdown.setFont(BUTTON_FONT);
		btnSoftShutdown.setBackground(DEFAULT_BACKGROUND_COLOR);
		btnSoftShutdown.setForeground(DEFAULT_TEXT_COLOR);
		
		
		btnPauseOrResume = new JButton("<html>Pause<br />Server</html>");
		btnPauseOrResume.setActionCommand(BTN_PAUSE_OR_RESUME);
		btnPauseOrResume.addActionListener(listener);
		btnPauseOrResume.setFont(BUTTON_FONT);
		btnPauseOrResume.setBackground(DEFAULT_BACKGROUND_COLOR);
		btnPauseOrResume.setForeground(DEFAULT_TEXT_COLOR);
		
		buttonPanel.add(btnHardReset);
		buttonPanel.add(btnSoftReset);
		buttonPanel.add(btnSoftShutdown);
		buttonPanel.add(btnHardShutdown);
		buttonPanel.add(btnEraseRegistry);
		buttonPanel.add(btnUpdateRegistry);
		buttonPanel.add(btnClearDisplay);
		buttonPanel.add(btnPauseOrResume);
		

		//set visible
		try 
		{
			this.setVisible(true);
			this.updateRegistry(null);
			this.println("DSKY started for " + title);
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
	
	
	//generic print
	public void println(String string)
	{
		string = string.replaceAll("\n", "\n        \t");
		//TODO get current time and append to start
		display.append(getCurrentTime() + "\t" + string + "\n");
		display.setCaretPosition(display.getDocument().getLength());
	}
	
	
	//generic clear
	public void clear()
	{
		display.setText("");
		display.setCaretPosition(display.getDocument().getLength());
	}
	
	
	//switch the text on the resume/pause button
	public void setResumeClear(boolean resume)
	{
		if(resume)
		{
			btnPauseOrResume.setText("<html>Resume<br />Server</html>");
		}
		else
		{
			btnPauseOrResume.setText("<html>Pause<br />Server</html>");
		}
	}
	
	
	//update the registry overview
	public void updateRegistry(HashMap<String,InetSocketAddress> registry)
	{
		String string = "\t\tMODULE REGISTRY\n===========================================\n\n";
		
		//iterate through all entries
		if(registry != null)
		{
			Set<String> keys = registry.keySet();
			for(String key : keys)
			{
				InetSocketAddress address = registry.get(key);
				string += "\"" + key + "\" @ " + address.toString() + "\n";
			}
		}
		//set the text
		registryText.setText(string);
		registryText.setCaretPosition(registryText.getDocument().getLength());
		
	}
	

	@Override
	//handl button presses
	public void actionPerformed(ActionEvent arg0) 
	{
		this.clear();
		this.println("BUTTON >> CLEAR");
	}
}
