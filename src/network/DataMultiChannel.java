/**
*Class:             DataMultChannel.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    22/02/2017                                              
*Version:           1.0.0                                         
*                                                                                   
*Purpose:           Real ugly way of allowing DataChannel to send to any address.
*					Like, REALLY ugly.
*					
* 
*Update Log			v1.0.0
*						- null
*/
package network;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class DataMultiChannel extends DataChannel 
{
	//even more generic constructor
	public DataMultiChannel() throws SocketException
	{
		//initialize
		super();
		gpSocket = new DatagramSocket();
		connected = true;
	}
	//generic constructor
	public DataMultiChannel(int port) throws SocketException
	{
		//initialize
		super();
		gpSocket = new DatagramSocket(port);
		connected = true;
	}
	
	
	//override the destination IP and port
	public void hijackChannel(InetAddress address, int port)
	{
		this.pairedAddress = address;
		this.pairedPort = port;
	}
}
