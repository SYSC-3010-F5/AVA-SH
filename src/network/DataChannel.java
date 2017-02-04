/**
*Class:             DataChannel.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    31/01/2016                                              
*Version:           0.1.0                                         
*                                                                                   
*Purpose:           Single channel, only designed for coms between ONE server, ONE client.
*					Will reject all packets from non-paired port/IP.
*					Send and receive data to/from paired DataChannel.
*					
* 
*Update Log			v0.1.0
*						- general design added
*/
package network;


//imports
import java.net.DatagramSocket;
import java.net.InetAddress;


public class DataChannel extends Thread 
{
	//declaring static class constants
	public static final int TIMEOUT_MS = 5000;
	public static final int TYPE_HANDSHAKE = 0;
	public static final int TYPE_START = 1;
	public static final int TYPE_END = 2;
	public static final int TYPE_CMD = 3;
	public static final int TYPE_INFO = 4;
	public static final int TYPE_ACK = 5;
	public static final int TYPE_ERR = 6;
	
	//declaring local instance variables
	private DatagramSocket gpSocket;
	private InetAddress pairedAddress;
	private int serverMasterPort;
	
	
	//generic constructor
	public DataChannel()
	{
		try
		{
			//initialize socket for send/receive
			gpSocket = new DatagramSocket();
			gpSocket.setSoTimeout(TIMEOUT_MS);
			
			//set IP and master port to default
			this.pairedAddress = null;
			this.serverMasterPort = 0;
		}
		catch (Exception e)
		{
			System.out.println("An unexpected error has occured");
			System.exit(0);
		}
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
	public int toInteger(byte[] bytes)				//TODO fix this
	{
		//bit shift each index
		//low index holds most significant byte		(big Endian)
		int[] val = {0,0,0,0};
		val[0] = (int)(bytes[0] << 8*3);
		val[1] = (int)(bytes[1] << 8*2);
		val[2] = (int)(bytes[2] << 8*1);
		val[3] = (int)(bytes[3] << 8*0);
		System.out.println(val[0]+"");
		System.out.println(val[1]+"");
		System.out.println(val[2]+"");
		System.out.println(val[3]+"");
		
		return (val[0] + val[1] + val[2] + val[3]);
	}
	
	
	//generic send
	private void sendPacket(byte opcode, byte[] toSend)
	{
		//construct the byte array to send, add opcode
		byte[] data = new byte[toSend.length + 5];
		data[0] = opcode;
		
		//add 4 Byte checksum
		int checksum = toSend.hashCode();
		
		//construct the datagram
		//DatagramPacket packet = new DatagramPacket();
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
	public void receiveTransfer()
	{
		//TODO
	}
	
	
	//contact paired connection via handshake protocol
	public void sendHandshake(InetAddress toPair, int listeningPort)
	{
		//TODO
	}
	
	
	//responds to a handshake, finalizing the connection
	public void respondHandshake(InetAddress toPair, int listeningPort)
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

//hashmap test, hash1 == hash2
/*
byte[] arr1 = {'h','e','l','l','o'};
byte[] arr2 = new byte[5];

arr2[0] = 'h';
arr2[1] = 'e';
arr2[2] = 'l';
arr2[3] = 'l';
arr2[4] = 'o';


int hash1 = Arrays.hashCode(arr1);
int hash2 = Arrays.hashCode(arr2);
System.out.println(hash1);
System.out.println(hash2);
if(hash1 == hash2)
{
	System.out.println("true");
}
else
{
	System.out.println("false");
}
*/





