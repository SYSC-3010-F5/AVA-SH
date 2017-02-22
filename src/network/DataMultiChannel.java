package network;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class DataMultiChannel extends DataChannel 
{
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
