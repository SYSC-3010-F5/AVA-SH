/**
*Class:             DayAndTimeDialog.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    18/02/2017                                              
*Version:           1.0.0                                         
*                                                                                   
*Purpose:           Allow to user to select a time and dates for an alarm.
*					
* 
*Update Log			v1.0.0
*						- null
*/
package terminal.dialogs;


//import external libraries
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SpinnerDateModel;
import java.util.Date;
import java.util.Calendar;
import javax.swing.JButton;
import javax.swing.SwingConstants;



public class DayAndTimeDialog extends JDialog implements ActionListener
{
	//declaring static class constants
	public static final int WINDOW_CLOSE_OPTION = -1;
	public static final int OK_OPTION = 0;
	public static final int CANCEL_OPTION = 1;
	private static final String[] DAYS = {"Monday", 
										"Tuesday", 
										"Wednesday", 
										"Thursday", 
										"Friday", 
										"Saturday",
										"Sunday"};
	private static final Font checkBoxFont = new Font("Tahoma", Font.PLAIN, 14);
	private static final Font spinnerFont = new Font("Tahoma", Font.PLAIN, 24);
	private static final int DEFAULT_WIDTH = 635;
	private static final int DEFAULT_HEIGHT = 145;
	
	//declaring local instance variables
	private int exitMode;
	private int hour;
	private int minute;
	
	private JCheckBox[] days;
	private JSpinner spHour;
	private JSpinner spMin;
	private JButton btnOk;
	private JButton btnCancel; 
	
	
	//generic constructor
	public DayAndTimeDialog(JFrame callingFrame, String windowName)
	{
		//set up dialog box
		super(callingFrame, windowName);
		this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		this.setResizable(false);
		this.getContentPane().setLayout(null);
		
		//initalize non-gui elements
		exitMode = this.WINDOW_CLOSE_OPTION;
		
		//set up panel for days
		JPanel dayPanel = new JPanel();
		dayPanel.setBounds(10, 11, 609, 35);
		dayPanel.setBorder(null);
		dayPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		this.getContentPane().add(dayPanel);
		
		//add day options to panel
		days = new JCheckBox[7];
		for(int i=0; i<7; i++)
		{
			days[i] = new JCheckBox(DAYS[i]);
			days[i].setFont(checkBoxFont);
			dayPanel.add(days[i]);
		}
		
		//add label(s) for spinners
		JTextField txtTime = new JTextField();
		JTextField txtDiv = new JTextField();
		txtTime.setEditable(false);
		txtTime.setBorder(null);
		txtTime.setBackground(SystemColor.menu);
		txtTime.setText("Time:");
		txtTime.setFont(new Font("Tahoma", Font.BOLD, 24));
		txtTime.setBounds(85, 59, 88, 46);
		txtTime.setColumns(10);
		txtDiv.setHorizontalAlignment(SwingConstants.CENTER);
		txtDiv.setText(":");
		txtDiv.setFont(new Font("Tahoma", Font.PLAIN, 35));
		txtDiv.setEditable(false);
		txtDiv.setColumns(10);
		txtDiv.setBorder(null);
		txtDiv.setBackground(SystemColor.menu);
		txtDiv.setBounds(312, 57, 13, 48);
		this.getContentPane().add(txtTime);
		getContentPane().add(txtDiv);
		
		//add hour spinner
		spHour = new JSpinner();
		spHour.setModel(new SpinnerNumberModel(0, 0, 24, 1));
		spHour.setFont(spinnerFont);
		spHour.setBounds(202, 59, 100, 46);
		this.getContentPane().add(spHour);
		
		//add minute spinner
		spMin = new JSpinner();
		spMin.setModel(new SpinnerNumberModel(0, 0, 59, 1));
		spMin.setFont(spinnerFont);
		spMin.setBounds(335, 59, 100, 46);
		this.getContentPane().add(spMin);
		
		//add ok button
		btnOk = new JButton("Ok");
		btnOk.setBounds(445, 59, 89, 23);
		btnOk.addActionListener(this);
		this.getContentPane().add(btnOk);
		
		//add cancel button
		btnCancel = new JButton("Cancel");
		btnCancel.setBounds(445, 82, 89, 23);
		btnCancel.addActionListener(this);
		this.getContentPane().add(btnCancel);
		
		//set visible
		this.setLocationRelativeTo(callingFrame);
		this.setVisible(true);
	}


	@Override
	public void actionPerformed(ActionEvent ae) 
	{
		//determine source
		Object src = ae.getSource();
		if(src == btnOk)
		{
			//set exit mode
			exitMode = this.OK_OPTION;
			
			//save state
			
		}
		else
		{
			//set exit mode
			exitMode = this.CANCEL_OPTION;
		}
		
		//close dialog
		this.dispose();
	}
}








