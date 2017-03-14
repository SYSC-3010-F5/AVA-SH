/**
*Class:             InterferanceGenerator.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven
*Date of Update:    05/03/2017
*Version:           1.0.0
*
*Purpose:           Makes garbage.
*					Sends garbage.
*					Murders your computer.
*					Similar to my git commits.
*
* 
*Update Log			v1.0.0
*						- null
*/
package testbench;


//import external
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;

//import packages
import network.DataMultiChannel;
import network.NetworkException;



public class InterferenceGenerator implements ActionListener
{
	//declaring local instance variables
	private Display display;
	private InetAddress address;
	private int port;
	private Random random;
	private DataMultiChannel[] channels;
	private int arrSize;
	private boolean runFlag;
	
	
	//generic constructor
	public InterferenceGenerator() throws SocketException
	{
		//init
		random = new Random();
		display = new Display("Interference Generator", this);
		address = display.getAddress();
		port = display.getInt("Enter AVA Server Port", 3010);
		arrSize = display.getInt("Enter number of DataChannels to use", 5);
		channels = new DataMultiChannel[arrSize];
		for(int i=0; i<arrSize; i++)
		{
			channels[i] = new DataMultiChannel();
			channels[i].hijackChannel(address, port);
		}
		runFlag = true;
	}
	
	
	//send trash
	public void sendTrash() throws NetworkException
	{
		if(runFlag)
		{
			//pick a channel and how much garbage to send
			int c =random.nextInt(arrSize);
			int packetSize = random.nextInt(1025);
			
			//fill a byte array with trash
			byte[] trash = new byte[packetSize];
			for(int i=0; i<packetSize; i++)
			{
				trash[i] = (byte)random.nextInt(256);
			}
			
			//send trash on channel
			channels[c].sendPacket(trash);
			display.println(trash, c);
		}
	}
	
	
	@Override
	//pause/resume
	public void actionPerformed(ActionEvent arg0) 
	{
		runFlag = !runFlag;
		display.setButton(runFlag);
	}
	
	
	//run 
	public static void main(String[] args) throws SocketException, NetworkException 
	{
		InterferenceGenerator gen = new InterferenceGenerator();
		while(true)
		{
			gen.sendTrash();
		}
	}
	
	
	
	
	
	
	
	//CLASSES IN CLASSES
	private class Display extends JFrame
	{
		//local instance variables
		JTextArea display;
		JButton button;
		
		
		//generic constructor
		public Display(String title, ActionListener listener)
		{
			//set up main window frame
			super(title);
			this.setIconImage(Toolkit.getDefaultToolkit().getImage(Display.class.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif")));
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setBounds(100, 0, 500, 750);
			this.getContentPane().setBackground(Color.BLACK);
			this.getContentPane().setLayout(new BorderLayout(0, 0));
			
			//add scroll pane for scrolling output
			JScrollPane mainOutputScroll = new JScrollPane();
			this.getContentPane().add(mainOutputScroll, BorderLayout.CENTER);
			
			//add text area for output
			display = new JTextArea();
			display.setEditable(false);
			display.setLineWrap(true);
			display.setWrapStyleWord(true);
			display.setFont(new Font("Monospaced", Font.PLAIN, 13));
			display.setBackground(Color.BLACK);
			display.setForeground(Color.ORANGE);
			display.setCaretColor(Color.ORANGE);;
			mainOutputScroll.setViewportView(display);
			
			//add pause button
			button = new JButton("Pause");
			button.setFont(new Font("Monospaced", Font.BOLD, 20));
			setButton(true);
			button.addActionListener(listener);
			this.getContentPane().add(button, BorderLayout.SOUTH);
			
			//make visible
			this.setVisible(true);
		}
		
		
		//print
		public void println(byte[] arr, int channel)
		{
			String printable = "Packet sent on channel " + channel + " || length=" + arr.length + ", containing : {";
			for(byte b : arr)
			{
				printable = printable + "0x" + (String.format("%02x", b)).toUpperCase() + " ";
			}
			display.append(printable + "}\n\n");
			display.setCaretPosition(display.getDocument().getLength());
		}
		
		
		//get ip
		public InetAddress getAddress()
		{
			while(true)
			{
				String string = (String)JOptionPane.showInputDialog(
	                    this,
	                    "Enter AVA Server IPv4 Address\n",
	                    "Interferance Generator", 
	                    JOptionPane.PLAIN_MESSAGE,
	                    null,
	                    null,
	                    "xxx.xxx.xxx.xxx");
				try
				{
					//declaring temporary method variables
					byte[] addr;
					String subStrBytes[];
	
					//parse addr
					subStrBytes = (string).split("\\.");
					
					addr = new byte[subStrBytes.length];
					for(int i=0; i<subStrBytes.length; i++)
					{
						addr[i] = (byte)Integer.parseInt(subStrBytes[i]);
					}
					
					//save as ip
					return InetAddress.getByAddress(addr);
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(
							this,
						    "Invalid IPAddress\nMust be of form \"xxx.xxx.xxx.xxx\"",
						    "Interferance Generator",
						    JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		
		
		//get ip
		public int getInt(String msg, int defaultInt)
		{
			while(true)
			{
				String string = (String)JOptionPane.showInputDialog(
	                    this,
	                    msg,
	                    "Interferance Generator", 
	                    JOptionPane.PLAIN_MESSAGE,
	                    null,
	                    null,
	                    ""+defaultInt);
				
				try
				{
					int value = Integer.parseInt(string);
					if(value>0)
					{
						return value;
					}
					else
					{
						JOptionPane.showMessageDialog(
								this,
							    "Invalid Entry\nMust be greater than 0",
							    "Interferance Generator",
							    JOptionPane.ERROR_MESSAGE);
					}
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(
							this,
						    "Invalid Entry\nMust be of valid 32bit integer",
						    "Interferance Generator",
						    JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		
		
		//set button text
		public void setButton(boolean running)
		{
			if(running)
			{
				button.setText("<html>Interference Generator Running...<br />Click to Pause</html>");
				button.setBackground(Color.GREEN);
			}
			else
			{
				button.setText("<html>Interference Generator Paused...<br />Click to Resume</html>");
				button.setBackground(Color.RED);
			}
		}
	}
}
