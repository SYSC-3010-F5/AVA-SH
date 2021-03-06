/**
*Class:             MirrorController.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    31/03/2017                                              
*Version:           1.0.1
*                                                                                   
*Purpose:           Controller for the magic mirror
*					Built to mess with another group who's doing a magic mirror.
*					Also to demonstrate modularity of the AVA-SH system
*
*					Attempts connection to server (varies name if already taken).
*					Periodically checks the weather.
*
*					Build in about 4 hours to mess with the F1 group.
*
*					Will try to connect to server indefinitely, if it cannot due to another device
*					being registered under its name, it will try again with a new name. Wash rinse
*					and repeat until you connect.
*					Updates weather every 15min.
*					If Server disconnects during runtime, it will enter disconnected state within
*					5 timeout cycles (25 seconds). It will then try to reconnect indefinitely.
*					
* 
*Update Log			v1.0.1
*						- config for changing server IP added for use in .jar 
*						- config for changing fullscreen/window added
*					v1.0.0
*						- null
*
*/
package magicmirror;


//import external libraries
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

//import packages
import network.DataChannel;
import network.NetworkException;
import network.PacketWrapper;
import server.datatypes.WeatherData;



public class MirrorController implements Runnable
{
	//declaring class constants
	private static final String SERVER_ADDRESS_INIT = "192.168.2.100";
	private static final int SERVER_PORT_INIT = 3010;
	private static final String WINDOW_TITLE = "Magic Mirror v1.0.0";
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
	
	
	
	//general use constructor w/o ip config
	public MirrorController(boolean fullscreen) throws SocketException, UnknownHostException
	{
		this(fullscreen, SERVER_ADDRESS_INIT);
	}
	//generic constructor
	public MirrorController(boolean fullscreen, String serverIP) throws SocketException, UnknownHostException
	{
		//init
		SERVER_ADDRESS = InetAddress.getByName(serverIP);
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
			PacketWrapper wrapper;
			while(timeout < RETRY_QUANTUM)
			{
				try 
				{
					//get weather (block for max 5s)
					dataChannel.sendCmd("req current weather");
					wrapper = dataChannel.receivePacket(BLOCK_TIME_MS);
					WeatherData weather = new WeatherData(wrapper.info());
					
					//update mirror and sleep
					boolean dayFlag = true;
					try
					{
						dataChannel.sendCmd("req time");
						wrapper = dataChannel.receivePacket(BLOCK_TIME_MS);
						int hour = Integer.parseInt(wrapper.info().split(":")[0]);
						dayFlag = (hour < 17 && hour > 6);
					}
					catch (Exception e) {}				//squash exception, and assume day if bad packet
					mirror.update(weather, dayFlag);
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
			
			//disconnect and try to reconnect
			try 
			{
				dataChannel.disconnect("loss of connection");
			} 
			catch (NetworkException e) 						//if this happens not good
			{
				e.printStackTrace();
			}
		}
	}
	
	
	//main method
	public static void main(String[] config) 
	{
		try 
		{
			//config fullscreen
			boolean fullscreen = true;
			if (config.length >= 1)
			{
				if (config[0].equals("false") || config[0].equals("0") || config[0].equals("windowed"))
				{
					fullscreen = false;
				}
			}
			
			//new mirror with non-standard ip
			if (config.length >= 2)
			{
				new MirrorController(fullscreen, config[1]).run();
			}
			//mirror with standard ip
			else
			{
				new MirrorController(fullscreen).run();
			}
		} 
		catch (SocketException | UnknownHostException e) 
		{
			JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "Critical Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
}
