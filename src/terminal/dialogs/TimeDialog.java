/**
*Class:             TimeDialog.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    07/03/2017                                              
*Version:           1.0.0                                         
*                                                                                   
*Purpose:           Allow to user to select a time for a timer
*					
* 
*Update Log			v1.0.0
*						- null
*/
package terminal.dialogs;


//import libraires
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

//import packages
import server.datatypes.Alarm;



public class TimeDialog extends JDialog implements ActionListener
{
	//declaring static class constants
	public static final int WINDOW_CLOSE_OPTION = -1;
	public static final int OK_OPTION = 0;
	public static final int CANCEL_OPTION = 1;
	private static final Font SPINNER_FONT = new Font("Tahoma", Font.PLAIN, 24);
	private static final int DEFAULT_WIDTH = 435;
	private static final int DEFAULT_HEIGHT = 165;
	
	//declaring local instance variables
	private int closeMode;
	private int hour;
	private int minute;
	private String name;
	private final String windowName;
	
	private JSpinner spHour;
	private JSpinner spMin;
	private JButton btnOk;
	private JButton btnCancel; 
	private JTextField txtName;
	
	
	//generic constructor
	public TimeDialog(JFrame callingFrame, String windowName)
	{
		//set up dialog box
		super(callingFrame, true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setTitle(windowName);
		this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		this.setResizable(false);
		this.setType(Type.POPUP);
		this.getContentPane().setLayout(null);
		
		//initalize non-gui elements
		closeMode = this.WINDOW_CLOSE_OPTION;
		this.windowName = windowName;
		
		//add name text field
		txtName = new JTextField();
		txtName.setHorizontalAlignment(SwingConstants.CENTER);
		txtName.setEditable(true);
		txtName.setText("Timer");
		txtName.setFont(SPINNER_FONT);
		txtName.setBounds(10, 21, 406, 46);
		getContentPane().add(txtName);
		txtName.setColumns(10);
		
		//add label(s) for spinners
		JTextField txtTime = new JTextField();
		JTextField txtDiv = new JTextField();
		txtTime.setEditable(false);
		txtTime.setBorder(null);
		txtTime.setBackground(SystemColor.menu);
		txtTime.setText("Time:");
		txtTime.setFont(new Font("Tahoma", Font.BOLD, 24));
		txtTime.setBounds(10, 78, 74, 46);
		txtTime.setColumns(10);
		txtDiv.setHorizontalAlignment(SwingConstants.CENTER);
		txtDiv.setText(":");
		txtDiv.setFont(new Font("Tahoma", Font.PLAIN, 35));
		txtDiv.setEditable(false);
		txtDiv.setColumns(10);
		txtDiv.setBorder(null);
		txtDiv.setBackground(SystemColor.menu);
		txtDiv.setBounds(194, 78, 13, 48);
		this.getContentPane().add(txtTime);
		getContentPane().add(txtDiv);
		
		//add hour spinner
		spHour = new JSpinner();
		spHour.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		spHour.setFont(SPINNER_FONT);
		spHour.setBounds(84, 78, 100, 46);
		this.getContentPane().add(spHour);
		
		//add minute spinner
		spMin = new JSpinner();
		spMin.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		spMin.setFont(SPINNER_FONT);
		spMin.setBounds(217, 78, 100, 46);
		this.getContentPane().add(spMin);
		
		//add okay button
		btnOk = new JButton("Ok");
		btnOk.setBounds(327, 78, 89, 23);
		btnOk.addActionListener(this);
		this.getContentPane().add(btnOk);
		
		//add cancel button
		btnCancel = new JButton("Cancel");
		btnCancel.setBounds(327, 101, 89, 23);
		btnCancel.addActionListener(this);
		this.getContentPane().add(btnCancel);
		
		//set visible
		this.setLocationRelativeTo(callingFrame);
		this.setVisible(true);
	}
	
	
	//generic getters
	public int getMinutes()
	{
		return (hour*60 + minute);
	}
	public int getCloseMode()
	{
		return closeMode;
	}
	public String getTimerName()
	{
		return name;
	}


	@Override
	public void actionPerformed(ActionEvent ae) 
	{
		//determine source
		Object src = ae.getSource();
		if(src == btnOk)
		{
			//save state
			hour = (int)spHour.getValue();
			minute = (int)spMin.getValue();
			name = txtName.getText();
			
			//set exit mode
			closeMode = this.OK_OPTION;
		}
		else
		{
			//set exit mode
			closeMode = this.CANCEL_OPTION;
		}
		
		//close dialog
		this.dispose();
	}
}
