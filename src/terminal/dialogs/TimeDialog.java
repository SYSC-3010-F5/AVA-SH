/**
*Class:             TimeDialog.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    25/03/2017                                              
*Version:           2.0.1                                         
*                                                                                   
*Purpose:           Allow to user to select a time for a timer
*					
* 
*Update Log			v2.0.1
*						- tab order fixed
*					v2.0.0
*						- Entire graphic aspect redone
*					v1.0.0
*						- null
*/
package terminal.dialogs;


//import libraries
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
import terminal.dialogs.traversals.FocusTraversalOnArray;
import java.awt.Component;



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
	private int[] time;
	private String name;
	private JSpinner spMin,spSec, spHour;
	private JButton btnOk, btnCancel; 
	private JTextField txtName;
	
	
	//generic constructor
	public TimeDialog(JFrame callingFrame, String windowName)
	{
		//set up dialog box
		super(callingFrame, true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setTitle(windowName);
		this.setSize(330, 182);
		this.setResizable(false);
		this.setType(Type.POPUP);
		this.getContentPane().setLayout(null);
		
		//initalize non-gui elements
		closeMode = WINDOW_CLOSE_OPTION;
		name = null;
		time = new int[]{0,0,0};
		
		
		//add name text field
		txtName = new JTextField();
		txtName.setHorizontalAlignment(SwingConstants.CENTER);
		txtName.setEditable(true);
		txtName.setText("Timer");
		txtName.setFont(SPINNER_FONT);
		txtName.setBounds(10, 21, 302, 46);
		getContentPane().add(txtName);
		txtName.setColumns(10);
		
		//add label(s) for spinners
		JTextField flavourHr = new JTextField();
		flavourHr.setHorizontalAlignment(SwingConstants.CENTER);
		flavourHr.setEditable(false);
		flavourHr.setBorder(null);
		flavourHr.setBackground(SystemColor.menu);
		flavourHr.setText("Hour");
		flavourHr.setFont(new Font("Tahoma", Font.PLAIN, 14));
		flavourHr.setBounds(10, 124, 61, 15);
		flavourHr.setColumns(10);
		this.getContentPane().add(flavourHr);
		
		JTextField txtFlavMin = new JTextField();
		txtFlavMin.setText("Minute");
		txtFlavMin.setHorizontalAlignment(SwingConstants.CENTER);
		txtFlavMin.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtFlavMin.setEditable(false);
		txtFlavMin.setColumns(10);
		txtFlavMin.setBorder(null);
		txtFlavMin.setBackground(SystemColor.menu);
		txtFlavMin.setBounds(81, 124, 61, 15);
		this.getContentPane().add(txtFlavMin);
		
		JTextField txtFlavSec = new JTextField();
		txtFlavSec.setText("Second");
		txtFlavSec.setHorizontalAlignment(SwingConstants.CENTER);
		txtFlavSec.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtFlavSec.setEditable(false);
		txtFlavSec.setColumns(10);
		txtFlavSec.setBorder(null);
		txtFlavSec.setBackground(SystemColor.menu);
		txtFlavSec.setBounds(152, 124, 61, 15);
		this.getContentPane().add(txtFlavSec);
		
		JTextField txtDiv1 = new JTextField();
		txtDiv1.setHorizontalAlignment(SwingConstants.CENTER);
		txtDiv1.setText(":");
		txtDiv1.setFont(new Font("Tahoma", Font.PLAIN, 35));
		txtDiv1.setEditable(false);
		txtDiv1.setColumns(10);
		txtDiv1.setBorder(null);
		txtDiv1.setBackground(SystemColor.menu);
		txtDiv1.setBounds(141, 78, 11, 46);
		this.getContentPane().add(txtDiv1);
		
		JTextField txtDiv2 = new JTextField();
		txtDiv2.setText(":");
		txtDiv2.setHorizontalAlignment(SwingConstants.CENTER);
		txtDiv2.setFont(new Font("Tahoma", Font.PLAIN, 35));
		txtDiv2.setEditable(false);
		txtDiv2.setColumns(10);
		txtDiv2.setBorder(null);
		txtDiv2.setBackground(SystemColor.menu);
		txtDiv2.setBounds(71, 78, 11, 46);
		this.getContentPane().add(txtDiv2);
		
		
		//add hour spinner
		spHour = new JSpinner();
		spHour.setModel(new SpinnerNumberModel(0, 0, 99, 1));
		spHour.setFont(SPINNER_FONT);
		spHour.setBounds(10, 78, 61, 46);
		getContentPane().add(spHour);
		
		//add minute spinner
		spMin = new JSpinner();
		spMin.setModel(new SpinnerNumberModel(0, 0, 99, 1));
		spMin.setFont(SPINNER_FONT);
		spMin.setBounds(81, 78, 61, 46);
		this.getContentPane().add(spMin);
		
		//add second spinner
		spSec = new JSpinner();
		spSec.setModel(new SpinnerNumberModel(0, 0, 99, 1));
		spSec.setFont(SPINNER_FONT);
		spSec.setBounds(152, 78, 61, 46);
		this.getContentPane().add(spSec);
		
	
		//add okay button
		btnOk = new JButton("Ok");
		btnOk.setBounds(223, 78, 89, 23);
		btnOk.addActionListener(this);
		this.getContentPane().add(btnOk);
		
		//add cancel button
		btnCancel = new JButton("Cancel");
		btnCancel.setBounds(223, 101, 89, 23);
		btnCancel.addActionListener(this);
		this.getContentPane().add(btnCancel);
		
		
		//set visible and tabbing order
		this.setLocationRelativeTo(callingFrame);
		setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{
				txtName, 
				spHour, 
				spMin, 
				spSec, 
				btnOk, 
				btnCancel}));
		this.setVisible(true);
	}
	
	
	//generic getters
	public int[] getTime()
	{
		return time;
	}
	public int getTimeInSeconds()
	{
		return ((time[0]*60*60) + (time[1]*60) + time[2]);
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
			time[2] = (int)spSec.getValue();
			time[1] = (int)spMin.getValue();
			time[0] = (int)spHour.getValue();
			name = txtName.getText();
			
			//set exit mode
			closeMode = OK_OPTION;
		}
		else
		{
			//set exit mode
			closeMode = CANCEL_OPTION;
		}
		
		//close dialog
		this.dispose();
	}
}
