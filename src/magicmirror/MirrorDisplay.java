/**
*Class:             MirrorDisplay.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    31/03/2017                                              
*Version:           1.0.0
*                                                                                   
*Purpose:           Displays the weather info to a screen.
*					Also some ASCII art because ASCII art > actual image files
*					
* 
*Update Log			v1.0.0
*						- null
*
*/
package magicmirror;


//external imports
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JFrame;

//import packages
import server.datatypes.WeatherData;



public class MirrorDisplay extends JFrame 
{
	//declaring static class constants
	private static final int DEFAULT_WINDOW_X = 1000;
	private static final int DEFAULT_WINDOW_Y = 725;
	private static final Font DEFAULT_FONT = new Font("Monospaced", Font.PLAIN, 13);
	private static final Color DEFAULT_BACKGROUND_COLOR = Color.BLACK;
	private static final Color DEFAULT_TEXT_COLOR = Color.ORANGE;		// <-- this would probably need to be white for real applications

	//declaring local instance variables
	private JTextArea output;
	
	
	//generic constructor
	public MirrorDisplay(boolean fullscreen, String windowTitle) 
	{
		//set up frame
		super(windowTitle);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, DEFAULT_WINDOW_X, DEFAULT_WINDOW_Y);
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		this.getContentPane().setBackground(DEFAULT_BACKGROUND_COLOR);
		
		
		//set up west output
		output = new JTextArea();
		output.setEditable(false);
		output.setFont(DEFAULT_FONT);
		output.setBackground(DEFAULT_BACKGROUND_COLOR);
		output.setForeground(DEFAULT_TEXT_COLOR);
		output.setCaretColor(DEFAULT_TEXT_COLOR);
		output.setLineWrap(true);
		output.setWrapStyleWord(true);
		this.getContentPane().add(output, BorderLayout.CENTER);
		
		
		//set visible
		if(fullscreen)
		{
			this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
			this.setUndecorated(true);
		}
		this.setVisible(true);
	}
	
	
	//update weather displayed
	public void update(WeatherData weather)
	{
		String s = WeatherIcons.CLEAR_SKY_DAY + "\n\n";
		String[] weatherData = weather.getWeatherData();
		s += ("  Weather data for " + weatherData[WeatherData.CITY] + ", " + weatherData[WeatherData.COUNTRY] + ".\n");
		s += ("  Current temperature: " + weatherData[WeatherData.TEMPERATURE] + " degrees Celsius\n");
		s += ("  Current humidity: " + weatherData[WeatherData.HUMIDITY] + "%\n");
		s += ("  Current weather: " + weatherData[WeatherData.WEATHER_TYPE] + ": " + weatherData[WeatherData.WEATHER_DESCRIPTION] + "\n");
		System.out.println(s);
		output.setText(s);
	}
	
	
	//show disconnect
	public void update()
	{
		System.out.println("DISCONNECTED");
		output.setText("  DISCONNECTED!");
	}
}
