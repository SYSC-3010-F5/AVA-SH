/**
*Class:             DataChannel.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    14/02/2017                                              
*Version:           0.2.1                                         
*                                                                                   
*Purpose:           Single channel, only designed for coms between ONE server, ONE client.
*					Will reject all packets from non-paired port/IP.
*					Send and receive data to/from paired DataChannel.
*					
* 
*Update Log			v0.2.1
*						- generic accessors added
*					v0.2.0
*						- toInt(byte[]) method implemented (and debugged)
*						- toByteArr(int) method implemented 
*						- handshake values assigned (Asimov), retry quantum specified
*						- basic send packet implementation (no retransmit)
*					v0.1.0
*						- general design added
*/
package network;


import java.io.IOException;
import java.net.DatagramPacket;
//imports
import java.net.DatagramSocket;
import java.net.InetAddress;


public class DataChannel extends Thread 
{
	//declaring static class constants
	public static final int TYPE_HANDSHAKE = 0;
	public static final int TYPE_CMD = 1;
	public static final int TYPE_INFO = 2;
	public static final int TYPE_ACK = 3;
	public static final int TYPE_ERR = 4;
	private static final int TIMEOUT_MS = 5000;
	private static final byte[] HANDSHAKE_ASIMOV1 = "1: A robot may not injure a human being or, through inaction, allow a human being to come to harm.".getBytes();
	
	//declaring local instance variables
	private boolean connected;
	private DatagramSocket gpSocket;
	private InetAddress pairedAddress;
	private int pairedPort;
	
	
	//generic constructor
	public DataChannel()
	{
		//initialize things
		connected = false;
		
		try
		{
			//initialize socket for send/receive
			gpSocket = new DatagramSocket();
			gpSocket.setSoTimeout(TIMEOUT_MS);
			
			//set IP and master port to default
			this.pairedAddress = null;
			this.pairedPort = 0;
		}
		catch (Exception e)
		{
			System.out.println("An unexpected error has occured");
			System.exit(0);
		}
	}
	
	
	//generic accessors
	public InetAddress getPairedAddress()
	{
		return pairedAddress;
	}
	public int getPairedPort()
	{
		return pairedPort;
	}
	public boolean getConnected()
	{
		return connected;
	}
	
	
	//convert 4 Byte integer to byte array
	public byte[] toByteArray(int value)
	{
		return new byte[] 
			{
	            (byte)(value >>> 24),
	            (byte)(value >>> 16),
	            (byte)(value >>> 8),
	            (byte)value
	        };
	}
	
	
	//convert 4 Byte array to integer
	public int fromByteArray(byte[] bytes)
	{
		//bit shift each index
		//low index holds most significant byte		(big Endian)
		int[] val = {0,0,0,0};
		val[0] = (int)(bytes[0] << 24);
		val[1] = (int)(bytes[1] << 16);
		val[2] = (int)(bytes[2] << 8);
		val[3] = (int)(bytes[3]);

		return (val[0] & 0xFF000000 | val[1] & 0x00FF0000 | val[2] & 0x0000FF00 | val[3] & 0x000000FF);
	}
	
	
	//generic send
	private void sendPacket(byte opcode, byte[] toSend) throws NetworkException
	{
		//construct the byte array to send, add opcode
		byte[] data = new byte[toSend.length + 5];
		data[0] = opcode;
		
		//add 4 Byte checksum
		byte[] checksum = toByteArray(toSend.hashCode());
		for(int c=0; c<4; c++)
		{
			data[c+1] = checksum[c];
		}
		
		//add all bytes from toSend
		int i = 5;
		for(byte b :  toSend)
		{
			data[i] = b;
		}
		
		//construct and send datagram
		DatagramPacket packet = new DatagramPacket(toSend, toSend.length, pairedAddress, pairedPort);
		try 
		{
			gpSocket.send(packet);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	
	//send the start packet
	private void sendStart()
	{
		//TODO
	}
	
	
	//send the end packet
	private void sendEnd()
	{
		//TODO
	}
	
	
	//receive
	public void receiveTransfer() throws NetworkException
	{
		//TODO
	}
	
	
	//contact paired connection via handshake protocol
	public void sendHandshake(InetAddress toPair, int listeningPort) throws NetworkException
	{
		//TODO
	}
	
	
	//responds to a handshake, finalizing the connection
	public void respondHandshake(InetAddress toPair, int listeningPort) throws NetworkException
	{
		//TODO
	}
	
	
	//send an ack
	public void sendAck()
	{
		
	}
	
	
	//send data, convert to bytes
	public void sendCmd(String data)
	{
		sendCmd(data.getBytes());
	}
	//send data
	public void sendCmd(byte[] data)
	{
		//TODO
	}
	
	
	//send an info packet
	public void sendInfo(String info)
	{
		sendInfo(info.getBytes());
	}
	//send info packet bytes
	public void sendInfo(byte[] info)
	{
		//TODO
	}
	
	
	//send an error packet
	public void sendErr(String errMsg)
	{
		sendErr(errMsg.getBytes());
	}
	public void sendErr(byte[] errMsg)
	{
		//TODO
	}
}





