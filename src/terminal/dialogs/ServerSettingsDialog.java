package terminal.dialogs;


//import libraries
import java.awt.Font;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JButton;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.IllegalFormatException;

import javax.swing.SwingConstants;

import terminal.dialogs.traversals.FocusTraversalOnArray;



public class ServerSettingsDialog extends JDialog implements ActionListener
{
	//declaring static class constants
	public static final int CLOSE_MODE_ACCEPT = 0;
	public static final int CLOSE_MODE_CONNECT = 1;
	public static final int CLOSE_MODE_CANCEL = -1;
	private static final int DEFAULT_WIDTH = 400;
	private static final int DEFAULT_HEIGHT = 175;
	private static final Font INPUT_FONT = new Font("Tahoma", Font.PLAIN, 13);
	private static final Font LABEL_FONT = new Font("Tahoma", Font.BOLD, 13);
	
	//declaring local constants
	private final String WINDOW_NAME;
	
	//declaring local instance variables
	private JTextField inputPort, inputName, inputAddress;
	private JButton btnAccept, btnCancel, btnConnect;
	private InetAddress address;
	private int port;
	private String name;
	private int closeMode;

	
	//generic constructor
	public ServerSettingsDialog(JFrame callingFrame, String windowName, InetAddress address, int port, String name)
	{
		//set up dialog box
		super(callingFrame, true);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setTitle(windowName);
		this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		this.setResizable(false);
		this.setType(Type.POPUP);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		//init non-gui
		this.address = address;
		this.port = port;
		this.name = name;
		this.WINDOW_NAME = windowName;
		this.closeMode = CLOSE_MODE_CANCEL;
		
		//set up panels for buttons/inputs
		JPanel inputPanel = new JPanel();
		getContentPane().add(inputPanel);
		inputPanel.setLayout(new GridLayout(3, 0, 35, 5));
		
		JPanel buttonPanel = new JPanel();
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		
		
		//add label for IP
		JTextField labelAddress = new JTextField();
		labelAddress.setHorizontalAlignment(SwingConstants.CENTER);
		labelAddress.setEditable(false);
		labelAddress.setText("Server IPv4 Address");
		labelAddress.setFont(LABEL_FONT);
		labelAddress.setColumns(10);
		labelAddress.setBorder(null);
		labelAddress.setBackground(this.getBackground());
		inputPanel.add(labelAddress);
		
		//add IP input)
		inputAddress = new JTextField();
		inputAddress.setText(address.toString().split("/")[1]);
		inputAddress.setFont(INPUT_FONT);
		inputAddress.setColumns(10);
		inputPanel.add(inputAddress);
		
		//add port label
		JTextField labelPort = new JTextField();
		labelPort.setHorizontalAlignment(SwingConstants.CENTER);
		labelPort.setEditable(false);
		labelPort.setText("Server Port");
		labelPort.setFont(LABEL_FONT);
		labelPort.setColumns(10);
		labelPort.setBorder(null);
		labelPort.setBackground(this.getBackground());
		inputPanel.add(labelPort);
		
		//add port input
		inputPort = new JTextField();
		inputPort.setText(port+"");
		inputPort.setColumns(10);
		inputPort.setFont(INPUT_FONT);
		inputPanel.add(inputPort);
		
		//add name label
		JTextField labelName = new JTextField();
		labelName.setHorizontalAlignment(SwingConstants.CENTER);
		labelName.setEditable(false);
		labelName.setText("Terminal Name");
		labelName.setColumns(10);
		labelName.setFont(LABEL_FONT);
		labelName.setBorder(null);
		labelName.setBackground(this.getBackground());
		inputPanel.add(labelName);
		
		//add name input
		inputName = new JTextField();
		inputName.setText(name);
		inputName.setColumns(10);
		inputName.setFont(INPUT_FONT);
		inputPanel.add(inputName);

		//add button to change defaults
		btnAccept = new JButton("Accept");
		btnAccept.setPreferredSize(new Dimension(89,23));
		btnAccept.addActionListener(this);
		buttonPanel.add(btnAccept);
		
		//add button to change defaults + connect
		btnConnect = new JButton("Connect");
		btnConnect.setPreferredSize(new Dimension(89,23));
		btnConnect.addActionListener(this);
		buttonPanel.add(btnConnect);
		
		//add button to cancel
		btnCancel = new JButton("Cancel");
		btnCancel.setPreferredSize(new Dimension(89,23));
		btnCancel.addActionListener(this);
		buttonPanel.add(btnCancel);
		
		//set visible + tab order
		this.setLocationRelativeTo(callingFrame);
		this.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{
				inputAddress,
				inputPort,
				inputName,
				btnAccept,
				btnConnect,
				btnCancel}));
		this.setVisible(true);
	}
	
	
	//generic accessors
	public InetAddress getAddress()
	{
		return address;
	}
	public int getPort()
	{
		return port;
	}
	public String getNameInput()			// <-- apparently a getName() method already exists...
	{
		return name;
	}
	public int getCloseMode()
	{
		return closeMode;
	}
	
	
	@Override
	//handle button presses
	public void actionPerformed(ActionEvent ae) 
	{
		//determine source
		Object src = ae.getSource();
		try
		{
			if(src == btnAccept || (src == btnConnect))
			{
				//set states
				address = InetAddress.getByName(inputAddress.getText());
				port = Integer.parseInt(inputPort.getText());
				name = inputName.getText();
				if (name == null)
				{
					throw new IllegalArgumentException(); //just hijacking this to exit try block
				}
				else if(name == "")
				{
					throw new IllegalArgumentException(); //just hijacking this to exit try block
				}
				
				//set close mode
				if (src == btnAccept)	closeMode = CLOSE_MODE_ACCEPT;
				else					closeMode = CLOSE_MODE_CONNECT;
				
				//exit
				this.dispose();
			}
			else
			{
				//set states
				address = null;
				port = -1;
				name = null;
				
				//exit
				this.dispose();
			}
		}
		catch (UnknownHostException e)
		{
			JOptionPane.showMessageDialog(this, "Invalid IPv4 address\nMust be of form xxx.xxx.xxx.xxx", WINDOW_NAME, JOptionPane.ERROR_MESSAGE);
		}
		catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(this, "Invalid Port\nMust be between 0 and 2147483647", WINDOW_NAME, JOptionPane.ERROR_MESSAGE);
		}
		catch (IllegalArgumentException e)
		{
			JOptionPane.showMessageDialog(this, "Invalid Name\nName cannot be blank", WINDOW_NAME, JOptionPane.ERROR_MESSAGE);
		}
	}
}
