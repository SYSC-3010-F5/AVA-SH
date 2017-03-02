/**
*Class:             ServerEvent.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven
*Date of Update:    02/03/2017
*Version:           1.0.0
*
*Purpose:           "Sends" packets to server at certain time over a certain period
*					Can be used to schedule commands, info, or errors.
*
*					Should only be used for commands
*
*
* 
*Update Log			v1.0.0
*						- null
*/
package server;


import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
//import libraries
import java.util.TimerTask;

import network.DataChannel;
import network.DataMultiChannel;
import network.NetworkException;
//import packages
import network.PacketWrapper;



public class ServerEvent extends TimerTask 
{
	//static class constants
	private static final int PORT = MainServer.PORT;
	
	//declaring local instance variables
	private PacketWrapper[] commands;
	

	//generic constructor
	public ServerEvent(PacketWrapper[] commands)
	{
		super();
		this.commands = commands;
	}
	
	@Override
	public void run() 
	{
		//instantiate a DataChannel to send with
		//get local IP
		InetAddress localAddress = null;
		DataMultiChannel channel = null;
		try 
		{
			localAddress = InetAddress.getLocalHost();
			channel = new DataMultiChannel();
		}
		catch (SocketException e) {e.printStackTrace();} 
		catch (UnknownHostException e) {e.printStackTrace();}
		
		//send each packet data to the server
		//packets that are not COMMAND, INFO, or ERROR not supported for scheduling and are ignored
		try
		{
			channel.hijackChannel(localAddress, PORT);
			for(PacketWrapper wrapper : commands)
			{
				switch(wrapper.type)
				{
					//send cmd packet
					case(DataChannel.TYPE_CMD):
						channel.sendCmd(wrapper.commandKey(), wrapper.extraInfo());
						break;
					
					//send info packet
					case(DataChannel.TYPE_INFO):
						channel.sendInfo(wrapper.info());
						break;
					
					//send error packet
					case(DataChannel.TYPE_ERR):
						channel.sendErr(wrapper.errorMessage());
						break;
				}
			}
		}
		catch (NetworkException e){e.printStackTrace();}
		
	}
}
