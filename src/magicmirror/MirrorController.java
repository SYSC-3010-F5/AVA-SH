/**
*Class:             MirrorController.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    31/03/2017                                              
*Version:           1.0.0
*                                                                                   
*Purpose:           Controller for the magic mirror
*					Built to mess with another group who's doing a magic mirror.
*					Also to demonstrate modularity of the AVA-SH system
*
*					Attempts connection to server (varies name if already taken).
*					Periodically checks the weather.
*
*					Coded in <4 hours to mess with Dave.
*					
* 
*Update Log			v1.0.0
*						- null
*
*/
package magicmirror;

import java.io.IOException;
//import external libraries
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import network.DataChannel;
import network.NetworkException;
import network.PacketWrapper;
import server.datatypes.WeatherData;



public class MirrorController implements Runnable
{
	//declaring class constants
	private static final String SERVER_ADDRESS_INIT = "192.168.2.34";
	private static final int SERVER_PORT_INIT = 3010;
	private static final String WINDOW_TITLE = "Magic Mirror";
	private static final String REGISTRY_NAME = "g\\magic mirror";
	private static final int RETRY_QUANTUM = 5;
	private static final int BLOCK_TIME_MS = 5*1000;
	private static final int UPDATE_PERIOD = 15*60*1000;
	
	//declaring instance constants (java wont let me have a static InetAddress, so guess we're using instance constants
	private final InetAddress SERVER_ADDRESS;
	private final int SERVER_PORT;
	
	//declaring local instance variables
	private DataChannel dataChannel;
	private MirrorDisplay mirror;
	
	
	//generic constructor
	public MirrorController(boolean fullscreen) throws SocketException, UnknownHostException
	{
		//init
		SERVER_ADDRESS = InetAddress.getByName(SERVER_ADDRESS_INIT);
		SERVER_PORT = SERVER_PORT_INIT;
		dataChannel = new DataChannel();
		mirror = new MirrorDisplay(fullscreen, WINDOW_TITLE);
		mirror.update();
	}
	
	
	@Override
	//run mirror controller
	public void run()
	{
		//declaring control variables
		String registry;
		int number;
		int timeout;
		
		//main run loop
		while(true)
		{
			//init control variables
			number = 1;
			timeout = 0;
			registry = REGISTRY_NAME;
			
			//attempt to register
			while(!dataChannel.getConnected())
			{
				//try to connect
				try 
				{
					dataChannel.connect(SERVER_ADDRESS, SERVER_PORT, registry);
				} 
				//name already registered
				catch (NetworkException e) 
				{
					mirror.update();
					registry = REGISTRY_NAME + number;
					number++;
				} 
				//timeout, try again
				catch (IOException e)
				{
					mirror.update();
				}
			}

			//get weather data every 15 minutes
			while(timeout < RETRY_QUANTUM)
			{
				try 
				{
					//get weather (block for max 5s)
					dataChannel.sendCmd("req current weather");
					PacketWrapper wrapper = dataChannel.receivePacket(BLOCK_TIME_MS);
					WeatherData weather = new WeatherData(wrapper.info());
					
					//update mirror and sleep
					mirror.update(weather);
					Thread.sleep(UPDATE_PERIOD);
				}
				catch (NetworkException e) 
				{
					//inc timeout count
					timeout++;
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private void getWeatherLoop()
	{
		
	}
	
	
	//main method
	public static void main(String[] args) 
	{
		try 
		{
			new MirrorController(false).run();
		} 
		catch (SocketException | UnknownHostException e) 
		{
			JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "Critical Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
}
