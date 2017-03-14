/**
*Class:             TextView.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    08/03/2017                                              
*Version:           1.0.0                                         
*                                                                                   
*Purpose:           It shows text in a particular color
*					
* 
*Update Log			v1.0.0
*						- null
*/
package terminal.dialogs;


//imports
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import java.awt.Window.Type;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.JTextArea;



public class TextView extends JFrame 
{
	//declaring static class constants
	private static final int DEFAULT_WINDOW_X = 825;
	private static final int DEFAULT_WINDOW_Y = 600;
	private static final Font DEFAULT_FONT = new Font("Monospaced", Font.PLAIN, 13);
	
	//generic constructor
	public TextView(String title, String text, Color textColor, Color backgroundColor)
	{
		//set up main window frame
		super(title);
		this.setType(Type.UTILITY);
		this.setAlwaysOnTop(true);
		this.setBounds(100, 100, DEFAULT_WINDOW_X, DEFAULT_WINDOW_Y);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(TextView.class.getResource("/com/sun/java/swing/plaf/windows/icons/File.gif")));
		
		//set up text area in scroll
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		JTextArea textArea = new JTextArea();
		textArea.setTabSize(4);
		textArea.setEditable(false);
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setFont(DEFAULT_FONT);
		textArea.setBackground(backgroundColor);
		textArea.setForeground(textColor);
		textArea.setCaretColor(textColor);
		textArea.setText(text);
		scrollPane.setViewportView(textArea);
		
		//make visible
		this.setVisible(true);
	}
}
