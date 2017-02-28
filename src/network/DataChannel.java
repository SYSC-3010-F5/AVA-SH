/**
*Class:             DataChannel.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    28/02/2017                                              
*Version:           1.1.0                                         
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
*Update Log			v1.1.0
*						- uses revision 2.1.1 of coms protocol
*						- disconnect functionality added
*						- disconnect packet packing/unpacking added
*					v1.0.1
*						- added registered name field
*					v1.0.0
*						- all functionality added and tested
*						- error handling added to handshaking
*						- sendHandshake(...) method renamed to connect(...)
*					v0.3.1
*						- added automated unpacking of handshake packets
*						- bug where handshake packet's device_name field being cut off patched
*						- added timeout option for receiving
*						- timeout for handshake reduced
*					v0.3.0
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
*						- respond handshake added
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
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import io.json.JsonFile;



public class DataChannel implements ComsProtocol
{
	//declaring static class constants
	public static final byte TYPE_HANDSHAKE = 0;
	public static final byte TYPE_CMD = 1;
	public static final byte TYPE_INFO = 2;
	public static final byte TYPE_ERR = 3;
	public static final byte TYPE_DISCONNECT = 4;
	public static final int MAX_PACKET_SIZE = 1024;
	
	protected static final int TIMEOUT_MS = 4000;
	protected static final byte[] HANDSHAKE = "1: A robot may not injure a human being or, through inaction, allow a human being to come to harm.".getBytes();
	
	//declaring local instance variables
	protected boolean connected;
	protected DatagramSocket gpSocket;
	protected InetAddress pairedAddress;
	protected int pairedPort;
	protected String registeredName;
	
	
	//generic constructor
	public DataChannel() throws SocketException
	{
		//initialize things
		connected = false;
		pairedAddress = null;
		pairedPort = -1;
		gpSocket = new DatagramSocket();
		registeredName = null;
	}
	
	
	//generic accessors
	public String getPairedAddress()
	{
		if(connected)
		{
			return pairedAddress.toString();
		}
		else
		{
			return "disconnected";
		}
	}
	public int getPairedPort()
	{
		return pairedPort;
	}
	public boolean getConnected()
	{
		return connected;
	}
	public int getLocalPort()
	{
		return gpSocket.getLocalPort();
	}
	public String getLocalAddress()
	{
		try 
		{
			return InetAddress.getLocalHost().toString();
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
			return "UnknownHostException";
		}
	}
	public String getRegisteredName()
	{
		return registeredName;
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
	
	
	//close this instance of DataChannel permanently
	public void close()
	{
		gpSocket.close();
		connected=false;
	}
	
	
	//generic send to paired
	private void sendPacket(byte[] toSend) throws NetworkException
	{
		//attempt to send if connection is established
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
			DatagramPacket packet = null;
			
			//create empty packet
			packet = new DatagramPacket(new byte[MAX_PACKET_SIZE], MAX_PACKET_SIZE);
			
			//block indefinitely waiting on packet
			try 
			{
				gpSocket.receive(packet);
			} 
			catch (IOException e) 
			{
				throw new NetworkException("Socket timeout");
			}
			
			//unpack it and return
			return unpack(packet);
		}
		else
		{
			throw new NetworkException("Cannot receive packet -- DataChannel not paired");
		}
	}
	
	
	//receive for only set time
	public PacketWrapper receivePacket(int timeout) throws NetworkException, SocketException
	{
		//set timeout
		gpSocket.setSoTimeout(timeout);
		PacketWrapper wrapper = this.receivePacket();
		//reset timeout
		gpSocket.setSoTimeout(0);
		return wrapper;
	}
	
	
	//process packet (added as separate method for debugging & testing)
	public PacketWrapper unpack(DatagramPacket packet) throws NetworkException
	{
		//determine packet type and parse accordingly
		byte[] rawData = packet.getData();
		int index=0;
		switch(rawData[0])
		{
			//HANDSHAKE PACKET
			case(TYPE_HANDSHAKE):
				ArrayList<Byte> handshakeKeyBuilder = new ArrayList<Byte>();
				ArrayList<Byte> deviceNameBuilder = new ArrayList<Byte>();
				byte[] handshakeKey;
				byte[] deviceName;
				index=1;
				
				//parse out handshake
				for(; rawData[index] != 0x00; index++)
				{
					handshakeKeyBuilder.add(rawData[index]);
				}
				handshakeKey = new byte[handshakeKeyBuilder.size()];
				for(int b=0; b < handshakeKeyBuilder.size(); b++)
				{
					handshakeKey[b] = handshakeKeyBuilder.get(b).byteValue();
				}
				index++;
				
				//parse out device name
				for(; rawData[index] != 0x00; index++)
				{
					deviceNameBuilder.add(rawData[index]);
				}
				deviceName = new byte[deviceNameBuilder.size()];
				for(int b=0; b < deviceNameBuilder.size(); b++)
				{
					deviceName[b] = deviceNameBuilder.get(b).byteValue();
				}
				
				return new PacketWrapper(
						TYPE_HANDSHAKE, 
						new String(handshakeKey), 
						new String(deviceName), 
						new InetSocketAddress(packet.getAddress(), packet.getPort())
						);
				
			
			//COMMAND PACKET
			case(TYPE_CMD):
				ArrayList<Byte> commandKeyBuilder = new ArrayList<Byte>();
				ArrayList<Byte> extraInfoBuilder = new ArrayList<Byte>();
				byte[] cmdKey;
				byte[] extraInfo;
				index =1;
				
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
				
				return new PacketWrapper(
						TYPE_CMD, 
						new String(cmdKey), 
						new String(extraInfo), 
						new InetSocketAddress(packet.getAddress(), packet.getPort())
						);
			
				
			//INFO PACKET
			case(TYPE_INFO):
				//parse the info out
				byte[] infoMsg = new byte[packet.getLength()-1];
				for(int i=1; i<packet.getLength(); i++)
				{
					infoMsg[i-1] = rawData[i];
				}
				return new PacketWrapper(
						TYPE_INFO, 
						new String(infoMsg), 
						null,
						new InetSocketAddress(packet.getAddress(), packet.getPort())
						);
			
				
			//ERROR PACKET
			case(TYPE_ERR):
				//parse the error message out
				byte[] errMsg = new byte[packet.getLength()-1];
				for(int i=1; i<packet.getLength(); i++)
				{
					errMsg[i-1] = rawData[i];
				}
				return new PacketWrapper(
						TYPE_ERR, 
						new String(errMsg), 
						null,
						new InetSocketAddress(packet.getAddress(), packet.getPort())
						);
			
			//DISCONNECT PACKET
			case(TYPE_DISCONNECT):
				//parse the reason for disconnect out
				byte[] disMsg = new byte[packet.getLength()-1];
				for(int i=1; i<packet.getLength(); i++)
				{
					disMsg[i-1] = rawData[i];
				}
				return new PacketWrapper(
						TYPE_DISCONNECT,
						new String(disMsg),
						null,
						new InetSocketAddress(packet.getAddress(), packet.getPort())
						);
				
					
			default:
				throw new NetworkException("Unknown packet format: " + rawData[0]);
		}
	}


	@Override
	public void connect(InetAddress toPair, int listeningPort, String deviceName) throws NetworkException, IOException
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
			i++;
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
		DatagramPacket response = new DatagramPacket(new byte[MAX_PACKET_SIZE], MAX_PACKET_SIZE);
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
			throw e;
		}
		
		//reset timeout
		try 
		{
			gpSocket.setSoTimeout(0);
		} 
		catch (SocketException e1) {e1.printStackTrace();}
		
		
		byte[] data = response.getData();
		//valid handshake response
		if(data[0] == TYPE_HANDSHAKE && data[1] == 0x00)
		{
			//save information
			pairedPort = response.getPort();
			pairedAddress = response.getAddress();
			connected = true;
			registeredName = deviceName;
		}
		//error packet
		else if (data[0] == TYPE_ERR)
		{
			throw new NetworkException(unpack(response).errorMessage());
		}
		//unexpected packet
		else
		{
			throw new NetworkException("Invalid response to handshake");
		}
	}
	
	
	@Override
	public void disconnect(String reason) throws NetworkException
	{
		//create packet contents
		byte[] stringBytes = reason.getBytes();
		byte[] rawData = new byte[1+stringBytes.length];
		rawData[0] = TYPE_DISCONNECT;
		for(int i=0; i<stringBytes.length; i++)
		{
			rawData[i+1] = stringBytes[i];
		}
		
		//send packet and set fields
		sendPacket(rawData);
		pairedPort = -1;
		pairedAddress = null;
		connected = false;
		registeredName = null;
	}


	@Override
	public void respondHandshake(InetAddress toPair, int listeningPort) throws NetworkException 
	{
		connected = true;
		pairedAddress = toPair;
		pairedPort = listeningPort;
		
		//send packet
		byte[] data = new byte[2];
		data[0] = TYPE_HANDSHAKE;
		data[1] = 0x00;
		sendPacket(data);
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





