/**
*Class:             DayAndTimeDialog.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    19/02/2017                                              
*Version:           1.0.2                                         
*                                                                                   
*Purpose:           Allow to user to select a time and dates for an alarm.
*					
* 
*Update Log			v1.0.3
*						- hotkeys added to the dialog (alt+1 for mon, alt+2 for tues, etc)
*					v1.0.2
*						- bug where user could select no days for valid alarm patched
*					v1.0.1
*						- bug where using cancel button returned invalid data patched
*						- state saved in Alarm object instead of separate primitives
*						- JTextField for alarm name added
*						- bug where hour spinner could be set to 24 fixed
*					v1.0.0
*						- null
*/
package terminal.dialogs;


//import external libraries
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.SystemColor;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.SwingConstants;

//import packages
import server.datatypes.Alarm;



public class DayAndTimeDialog extends JDialog implements ActionListener
{
	//declaring static class constants
	public static final int WINDOW_CLOSE_OPTION = -1;
	public static final int OK_OPTION = 0;
	public static final int CANCEL_OPTION = 1;
	private static final String[] DAYS = {"Sunday",
										"Monday", 
										"Tuesday", 
										"Wednesday", 
										"Thursday", 
										"Friday", 
										"Saturday"};
	private static final Font CHECKBOX_FONT = new Font("Tahoma", Font.PLAIN, 14);
	private static final Font SPINNER_FONT = new Font("Tahoma", Font.PLAIN, 24);
	private static final int DEFAULT_WIDTH = 635;
	private static final int DEFAULT_HEIGHT = 145;
	
	//declaring local instance variables
	private int closeMode;
	private Alarm alarm;
	private final String windowName;
	
	private JCheckBox[] days;
	private JSpinner spHour;
	private JSpinner spMin;
	private JButton btnOk;
	private JButton btnCancel; 
	private JTextField txtName;
	
	
	//generic constructor
	public DayAndTimeDialog(JFrame callingFrame, String windowName)
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
		alarm = null;
		
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
			days[i].setFont(CHECKBOX_FONT);
			days[i].setMnemonic(i+0x31);		//KeyEvent.VK_# is the same as ASCII value for num
			dayPanel.add(days[i]);
		}
		
		//add name text field
		txtName = new JTextField();
		txtName.setEditable(true);
		txtName.setText("Generic Alarm");
		txtName.setFont(SPINNER_FONT);
		txtName.setBounds(10, 57, 198, 46);
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
		txtTime.setBounds(213, 57, 74, 46);
		txtTime.setColumns(10);
		txtDiv.setHorizontalAlignment(SwingConstants.CENTER);
		txtDiv.setText(":");
		txtDiv.setFont(new Font("Tahoma", Font.PLAIN, 35));
		txtDiv.setEditable(false);
		txtDiv.setColumns(10);
		txtDiv.setBorder(null);
		txtDiv.setBackground(SystemColor.menu);
		txtDiv.setBounds(397, 57, 13, 48);
		this.getContentPane().add(txtTime);
		getContentPane().add(txtDiv);
		
		//add hour spinner
		spHour = new JSpinner();
		spHour.setModel(new SpinnerNumberModel(0, 0, 23, 1));
		spHour.setFont(SPINNER_FONT);
		spHour.setBounds(287, 57, 100, 46);
		this.getContentPane().add(spHour);
		
		//add minute spinner
		spMin = new JSpinner();
		spMin.setModel(new SpinnerNumberModel(0, 0, 59, 1));
		spMin.setFont(SPINNER_FONT);
		spMin.setBounds(420, 57, 100, 46);
		this.getContentPane().add(spMin);
		
		//add ok button
		btnOk = new JButton("Ok");
		btnOk.setBounds(530, 57, 89, 23);
		btnOk.addActionListener(this);
		this.getContentPane().add(btnOk);
		
		//add cancel button
		btnCancel = new JButton("Cancel");
		btnCancel.setBounds(530, 80, 89, 23);
		btnCancel.addActionListener(this);
		this.getContentPane().add(btnCancel);
		
		//set visible
		this.setLocationRelativeTo(callingFrame);
		this.setVisible(true);
	}
	
	
	//generic accessors
	public int getCloseMode()
	{
		return closeMode;
	}
	public Alarm getAlarm()
	{
		if(closeMode == OK_OPTION)
		{
			return alarm;
		}
		else
		{
			return null;
		}
	}


	@Override
	public void actionPerformed(ActionEvent ae) 
	{
		//determine source
		Object src = ae.getSource();
		if(src == btnOk)
		{
			//save state
			alarm = new Alarm();
			boolean daySelected = false;
			boolean[] daysArr = new boolean[7];
			for(int i=0; i<days.length; i++)
			{
				daysArr[i] = days[i].isSelected();
				if(daysArr[i] && !daySelected)
				{
					daySelected = true;
				}
			}
			//user did not select a day
			if(!daySelected)
			{
				JOptionPane.showMessageDialog(this, "You must selected at least one day to set the alarm", windowName, JOptionPane.ERROR_MESSAGE);
				return;
			}
			alarm.setDays(daysArr);
			alarm.setName(txtName.getText());
			alarm.setHour((int)spHour.getValue());
			alarm.setMinute((int)spMin.getValue());
			
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








