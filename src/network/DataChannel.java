/**
*Class:             DataChannel.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    21/02/2017                                              
*Version:           0.3.0                                         
*                                                                                   
*Purpose:           Single channel, only designed for coms between ONE server, ONE client.
*					Will reject all packets from non-paired port/IP.
*					Send and receive data to/from paired DataChannel.
*
*					Assumes an ideal network, ie, does not account for any:
*						- packet loss
*						- packet duplication
*						- packet corruption
*						- gross packet delays
*					
* 
*Update Log			v0.3.0
*						- revision 2.0.0 of the proposed system
*						- checksums removed
*						- opcodes changed (changed to type byte as well)
*						- send start/end packet types removed (no longer exist in rev2.0.0 of protocol)
*						- receive method written
*						- packet unpacking added
*						- send command packet added
*						- send info packet added
*						- send error packet added
*						- send handshake added
*					v0.2.1
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


//import external libraries
import java.io.IOException;
import java.net.DatagramPacket;
//imports
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import io.json.JsonFile;



public class DataChannel extends Thread implements ComsProtocol
{
	//declaring static class constants
	public static final byte TYPE_HANDSHAKE = 0;
	public static final byte TYPE_CMD = 1;
	public static final byte TYPE_INFO = 2;
	public static final byte TYPE_ERR = 3;
	public static final int MAX_PACKET_SIZE = 1024;
	
	private static final int TIMEOUT_MS = 5000;
	private static final byte[] HANDSHAKE = "1: A robot may not injure a human being or, through inaction, allow a human being to come to harm.".getBytes();
	
	//declaring local instance variables
	private boolean connected;
	private DatagramSocket gpSocket;
	private InetAddress pairedAddress;
	private int pairedPort;
	
	
	//generic constructor
	public DataChannel() throws SocketException
	{
		//initialize things
		connected = false;
		this.pairedAddress = null;
		this.pairedPort = 0;
		gpSocket = new DatagramSocket();
	}
	
	
	//generic accessors
	public String getPairedAddress()
	{
		return pairedAddress.toString();
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
	private void sendPacket(byte[] toSend) throws NetworkException
	{
		if(connected)
		{
			//construct and send packet
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
		else
		{
			throw new NetworkException("Cannot send packet -- DataChannel not paired");
		}
	}


	@Override
	public PacketWrapper receivePacket() throws NetworkException 
	{
		if(connected)
		{
			//create empty packet
			DatagramPacket packet = new DatagramPacket(new byte[MAX_PACKET_SIZE], MAX_PACKET_SIZE);
			
			//wait until packet comes from paired source
			InetAddress packetAddressSource = null;
			int packetPortSource = 0;
			while( !pairedAddress.equals(packetAddressSource) && pairedPort != packetPortSource )
			{
				//block indefinitely waiting on packet
				try 
				{
					gpSocket.receive(packet);
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				
				//get packet source info
				packetAddressSource = packet.getAddress();
				packetPortSource = packet.getPort();
			}
			
			//unpack it, or should I say, un-packet
			return unpack(packet);
		}
		else
		{
			throw new NetworkException("Cannot receive packet -- DataChannel not paired");
		}
	}
	
	
	//process packet (added as separate method for debugging & testing)
	public PacketWrapper unpack(DatagramPacket packet) throws NetworkException
	{
		//determine packet type and parse accordingly
		byte[] rawData = packet.getData();
		switch(rawData[0])
		{
			//HANDSHAKE PACKET
			case(TYPE_HANDSHAKE):
				throw new NetworkException("Intial handshake packet received from paired server");
			
			//COMMAND PACKET
			case(TYPE_CMD):
				ArrayList<Byte> commandKeyBuilder = new ArrayList<Byte>();
				ArrayList<Byte> extraInfoBuilder = new ArrayList<Byte>();
				byte[] cmdKey;
				byte[] extraInfo;
				int index=1;
				
				//parse out command key
				for(; rawData[index] != 0x00; index++)
				{
					commandKeyBuilder.add(rawData[index]);
				}
				cmdKey = new byte[commandKeyBuilder.size()];
				for(int b=0; b<commandKeyBuilder.size(); b++)
				{
					cmdKey[b] = commandKeyBuilder.get(b).byteValue();
				}
				index++;
				
				//parse out extra info];
				for(; rawData[index] != 0x00; index++)
				{
					extraInfoBuilder.add(rawData[index]);
				}
				extraInfo = new byte[extraInfoBuilder.size()];
				for(int b=0; b < extraInfoBuilder.size(); b++)
				{
					extraInfo[b] = extraInfoBuilder.get(b).byteValue();
				}
				
				return new PacketWrapper(TYPE_CMD, new String(cmdKey), new String(extraInfo));
			
			//INFO PACKET
			case(TYPE_INFO):
				//parse the info out
				byte[] infoMsg = new byte[packet.getLength()-1];
				for(int i=1; i<packet.getLength(); i++)
				{
					infoMsg[i-1] = rawData[i];
				}
				return new PacketWrapper(TYPE_INFO, new String(infoMsg), null);
			
			//ERROR PACKET
			case(TYPE_ERR):
				//parse the error message out
				byte[] errMsg = new byte[packet.getLength()-1];
				for(int i=1; i<packet.getLength(); i++)
				{
					errMsg[i-1] = rawData[i];
				}
				return new PacketWrapper(TYPE_ERR, new String(errMsg), null);
					
			default:
				throw new NetworkException("Unknown packet format: " + rawData[0]);
		}
	}


	@Override
	public void sendHandshake(InetAddress toPair, int listeningPort, String deviceName) throws NetworkException
	{
		int i=1;
		//assemble empty byte array
		byte[] nameBytes = deviceName.getBytes();
		byte[] toSend = new byte[HANDSHAKE.length + nameBytes.length + 2];
		
		//add opcode
		toSend[0] = TYPE_HANDSHAKE;
		for(byte b : HANDSHAKE)
		{
			toSend[i] = b;
			i++;
		}
		
		//add terminating 0
		toSend[i] = (byte)0x00;
		i++;
		
		//add device name
		for(byte b : nameBytes)
		{
			toSend[i] = b;
		}
		
		//create & send packet
		DatagramPacket packet = new DatagramPacket(toSend, toSend.length, toPair, listeningPort);
		try 
		{
			gpSocket.send(packet);
		}
		catch (IOException e) 
		{
			throw new NetworkException("Error sending inital handshake packet");
		}
		
		//wait for response for 10 seconds
		DatagramPacket response = new DatagramPacket(new byte[1], 1);
		try 
		{
			gpSocket.setSoTimeout(TIMEOUT_MS);
			gpSocket.receive(response);
		} 
		catch (IOException e) 
		{
			//socket timeout, reset timeout and throw error
			try 
			{
				gpSocket.setSoTimeout(0);
			} 
			catch (SocketException e1) {e1.printStackTrace();}
			throw new NetworkException("Socket timeout -- no responce from server");
		}
		
		//save information
		if(response.getData()[0] == TYPE_HANDSHAKE)
		{
			pairedPort = response.getPort();
			pairedAddress = response.getAddress();
			connected = true;
		}
		else
		{
			throw new NetworkException("Invalid response to handshake");
		}
	}


	@Override
	public void respondHandshake(InetAddress toPair, int listeningPort) throws NetworkException 
	{
		// TODO Auto-generated method stub
	}


	@Override
	public void sendCmd(String cmdKey) throws NetworkException 
	{
		sendCmd(cmdKey.getBytes(), new byte[0]);
	}


	@Override
	public void sendCmd(String cmdKey, String extraInfo) throws NetworkException 
	{
		sendCmd(cmdKey.getBytes(), extraInfo.getBytes());
	}


	@Override
	public void sendCmd(String cmdKey, JsonFile extraInfo) throws NetworkException 
	{
		sendCmd(cmdKey.getBytes(), extraInfo.toByteArray());
	}


	@Override
	public void sendCmd(byte[] cmdKey, byte[] extraInfo) throws NetworkException 
	{
		//create empty byte array
		byte[] toSend = new byte[cmdKey.length + 3 + extraInfo.length];
		
		//set opcode
		toSend[0] = TYPE_CMD;
		int i = 1;
		
		//set command key
		for(byte b : cmdKey)
		{
			toSend[i] = b;
			i++;
		}
		
		//add 1st terminating 0
		toSend[i] = (byte)0x00;
		i++;
		
		//set extra info
		for(byte b : extraInfo)
		{
			toSend[i] = b;
			i++;
		}
		
		//add final termination 0
		toSend[i] = (byte)0x00;
		
		//send packet
		this.sendPacket(toSend);
	}


	@Override
	public void sendInfo(JsonFile info) throws NetworkException 
	{
		sendInfo(info.toByteArray());
	}


	@Override
	public void sendInfo(String info) throws NetworkException 
	{
		sendInfo(info.getBytes());
	}


	@Override
	public void sendInfo(byte[] info) throws NetworkException
	{
		//create an empty byte array
		byte[] toSend = new byte[info.length + 1];
		
		//add opcode
		toSend[0] = TYPE_INFO;
		
		//add info field
		for (int i=0; i<info.length; i++)
		{
			toSend[i+1] = info[i];
		}
		
		//send
		this.sendPacket(toSend);
	}


	@Override
	public void sendErr(String errMsg) throws NetworkException
	{
		sendErr(errMsg.getBytes());
	}


	@Override
	public void sendErr(byte[] errMsg) throws NetworkException 
	{
		//create an empty byte array
		byte[] toSend = new byte[errMsg.length + 1];
		
		//add opcode
		toSend[0] = TYPE_ERR;
		
		//add info field
		for (int i=0; i<errMsg.length; i++)
		{
			toSend[i+1] = errMsg[i];
		}
		
		//send
		this.sendPacket(toSend);
	}
}





