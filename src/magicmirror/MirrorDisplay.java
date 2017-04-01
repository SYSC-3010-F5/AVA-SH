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
import java.awt.Toolkit;



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
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(MirrorDisplay.class.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif")));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(0, 0, DEFAULT_WINDOW_X, DEFAULT_WINDOW_Y);
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
	public void update(WeatherData weather, boolean day)
	{
		//setup
		String[] weatherData = weather.getWeatherData();
		String type = weatherData[WeatherData.WEATHER_TYPE].toLowerCase();
		String s = "\n";
		
		//set ASCII art
		if(type.contains("clear"))
		{
			if(day)
			{
				s += WeatherIcons.CLEAR_SKY_DAY + "\n\n\n";
			}
			else
			{
				s += WeatherIcons.CLEAR_SKY_NIGHT + "\n\n\n";
			}
		}
		else if (type.contains("mist") || type.contains("fog") || type.contains("haze") || type.contains("ash") || type.contains("squall") || type.contains("sand"))
		{
			s += WeatherIcons.FOG_OR_MIST + "\n\n\n";
		}
		else if (type.contains("rain"))
		{
			s += WeatherIcons.RAIN + "\n\n\n";
		}
		else if (type.contains("snow") || type.contains("sleet") || type.contains("hail"))
		{
			s += WeatherIcons.SNOW + "\n\n\n";
		}
		else if (type.contains("thunder") || type.contains("storm"))
		{
			s += WeatherIcons.THUNDERSTORM + "\n\n\n";
		}
		else
		{
			s += WeatherIcons.CLOUDY + "\n\n\n";
		}
		
		
		//set info
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
		output.setText(WeatherIcons.DISCONNECTED);
	}
}
