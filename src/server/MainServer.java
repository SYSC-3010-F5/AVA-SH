/**
*Class:             MainServer.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    22/02/2017                                              
*Version:           0.1.0                                         
*                                                                                   
*Purpose:           The main controller of the AVA system
*					
* 
*Update Log			v0.1.0
*						- null
*/
package server;


//import libraries
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashMap;

//import packages
import network.DataMultiChannel;
import network.NetworkException;
import network.PacketWrapper;



public class MainServer 
{
	//declaring static class constants
	public static final int PORT = 3010;
	
	//declaring local instance variables
	HashMap<InetSocketAddress, String> registry;
	DataMultiChannel multiChannel;
	
	
	//generic constructor
	public MainServer() throws SocketException
	{
		//initialize
		multiChannel = new DataMultiChannel(PORT);
	}
	
	
	//main server input-control-wait loop
	public void serverControlCycle()
	{
		while(true)
		{
			//receive
			try 
			{
				System.out.println("Blocking...");
				PacketWrapper wrapper = multiChannel.receivePacket();
				System.out.println(wrapper.deviceName());
				System.out.println(wrapper.toString());
				multiChannel.respondHandshake(wrapper.source.getAddress(), wrapper.source.getPort());
			} 
			catch (NetworkException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	
	//main method
	public static void main(String[] args)
	{
		MainServer server = null;
		try 
		{
			server = new MainServer();
		} 
		catch (SocketException e) 
		{
			System.out.println(e.toString());
			e.printStackTrace();
		}
		server.serverControlCycle();
	}
}








