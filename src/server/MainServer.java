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

import network.DataChannel;
//import packages
import network.DataMultiChannel;
import network.NetworkException;
import network.PacketWrapper;



public class MainServer 
{
	//declaring static class constants
	public static final int PORT = 3010;
	public static final byte TYPE_HANDSHAKE = 0;
	public static final byte TYPE_CMD = 1;
	public static final byte TYPE_INFO = 2;
	public static final byte TYPE_ERR = 3;
	public static final int MAX_PACKET_SIZE = 1024;
	private static final String HANDSHAKE = "1: A robot may not injure a human being or, through inaction, allow a human being to come to harm.";

	
	//declaring local instance variables
	private HashMap<String,InetSocketAddress> registry;
	private DataMultiChannel multiChannel;
	private boolean runFlag;
	
	
	//generic constructor
	public MainServer() throws SocketException
	{
		//initialize
		registry = new HashMap<String,InetSocketAddress>();
		multiChannel = new DataMultiChannel(PORT);
		runFlag = true;
	}
	
	
	//receive packet
	private PacketWrapper receivePacket() throws NetworkException
	{
		System.out.println("Waiting for packet...");
		PacketWrapper wrapper = multiChannel.receivePacket();
		System.out.println("Packet received!");
		System.out.println("Contents: {" + wrapper.toString() + "}");
		return wrapper;
	}
	
	
	//send a ping
	private void sendPing(InetSocketAddress dest)
	{
		//send an empty info packet to act as a ping
		try 
		{
			System.out.println("Sending empty info packet...");
			multiChannel.hijackChannel(dest.getAddress(), dest.getPort());
			multiChannel.sendInfo("");
		} 
		catch (NetworkException e) {e.printStackTrace();}
	}
	
	
	//main server input-control-wait loop
	public void serverControlCycle()
	{
		while(runFlag)
		{
			//receive
			PacketWrapper packet = null;
			try 
			{
				packet = this.receivePacket();
			} 
			catch (NetworkException e) {e.printStackTrace();}
			
			//decide what to do with the packet
			switch(packet.type)
			{
				//new device for the registry
				case(TYPE_HANDSHAKE):
					System.out.println("Device attempting pairing...");
					//check handshake
					if(packet.handshakeKey().equals(HANDSHAKE))
					{
						//add to registry
						System.out.println("Device handshake correct!\nAdding to registry...");
						registry.put(packet.deviceName(), packet.source);
						System.out.println("Device added to registry under name \"" + packet.deviceName() + "\", value: \"" + packet.source.toString() + "\"");
						
						//respond to handshake with empty handshake
						try
						{
							multiChannel.respondHandshake(packet.source.getAddress(), packet.source.getPort());
						}
						catch (NetworkException e)
						{
							System.out.println("EXCEPTION >> " + e.getMessage());
						}
						
					}
					else
					{
						System.out.println("Device handshake incorrect!\nPairing FAILED");
						//TODO respond to bad handshake
					}
					break;
				
				//some command from an interface
				case(DataChannel.TYPE_CMD):
					//determine what to do based on command key
					switch(packet.commandKey())
					{
						case("ping"):
							sendPing(packet.source);
							break;
					}
					break;
				
				//we should not get these
				case(DataChannel.TYPE_INFO):
					break;
				
				//an error from one of the devices
				case(DataChannel.TYPE_ERR):
					break;
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








