/**
*Class:             DataChannelTestBench.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    22/02/2017                                              
*Version:           1.0.0                                         
*                                                                                   
*Purpose:           Test bench for methods in DataChannel.
*					
* 
*Update Log			v1.0.0
*						- test for toByteArray(...) method implemented
*						- test for fromByteArray(..) method implemented
*						- test for moving between byte[]-->int and int-->byte[] implemented
*/
package testbench;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
//import external libraries
import java.util.Arrays;

//import packages
import network.DataChannel;
import network.NetworkException;
import network.PacketWrapper;



public class DataChannelTestBench extends TestBench 
{
	//test variables
	DataChannel channel;

	
	public DataChannelTestBench(String name) 
	{
		super(name);
	}

	
	@Override
	protected void setUp() throws Exception 
	{
		super.setUp();
		channel = new DataChannel();
	}

	
	@Override
	protected void tearDown() throws Exception 
	{
		super.tearDown();
		channel = null;
	}

	
	//test unpacking a improper packet
	public void testUnpackBadPacket() throws UnknownHostException
	{
		printHeader("Testing unpack(...) method for bad packet (type 0xAD)...");
		//local test variables
		DatagramPacket packet;
		byte[] packetData;
		boolean e;

		//test 1
		//make packet
		byte[] infoString = "Rock. Robot rock".getBytes();
		println("Creating garbage packet with \"Rock. Robot rock\" as data...");
		packetData = new byte[infoString.length + 1];
		packetData[0] = (byte)0xAD;
		for(int i=0; i< infoString.length; i++)
		{
			packetData[i+1] = infoString[i];
		}
		packet = new DatagramPacket(packetData, packetData.length, InetAddress.getLocalHost(), 1234);
		println("Packet contents:");
		println(packet.getData());
		
		//unpack packet (or i guess you could say unpack-it)
		println("Unpacking packet...");
		try 
		{
			PacketWrapper wrapper = channel.unpack(packet);
			println("Result:" + wrapper.toString());
			printTest(false);
			assertTrue(false);
		} 
		catch (NetworkException e1) 
		{
			println("EXCEPTION >> " + e1.getMessage());
			printTest(true);
			assertTrue(true);
		}
	}
	
	
	//test unpacking a command packet
	public void testUnpackCommand() throws UnknownHostException
	{
		printHeader("Testing unpack(...) method for command packet...");
		//local test variables
		DatagramPacket packet;
		byte[] packetData;
		boolean e;
		int i=0;

		//test 1
		//make packet
		String commandKey = "new alarm";
		String extraInfo = "{hour: 7, minute: 00, name: wake_up}";
		byte[] keyBytes = commandKey.getBytes();
		byte[] infoBytes = extraInfo.getBytes();
		println("Creating command packet with \"" + commandKey + "\" as command key, \"" + extraInfo + "\" as extra info...");
		packetData = new byte[keyBytes.length + 3 + infoBytes.length];
		packetData[0] = DataChannel.TYPE_CMD;
		i=1;
		for(byte b : keyBytes)
		{
			packetData[i] = b;
			i++;
		}
		packetData[i] = (byte)0x00;
		i++;
		for(byte b : infoBytes)
		{
			packetData[i] = b;
			i++;
		}
		packetData[i] = (byte)0x00;
		packet = new DatagramPacket(packetData, packetData.length, InetAddress.getLocalHost(), 1234);
		println("Packet contents:");
		println(packet.getData());
		
		//unpack packet (or i guess you could say unpack-it)
		println("Unpacking packet...");
		try 
		{
			PacketWrapper wrapper = channel.unpack(packet);
			println("Result:" + wrapper.toString());
			
			e = commandKey.equals(wrapper.commandKey()) && extraInfo.equals(wrapper.extraInfo()) && DataChannel.TYPE_CMD == wrapper.type();
			printTest(e);
			assertTrue(e);
		} 
		catch (NetworkException e1) 
		{
			println("EXCEPTION >> " + e1.getMessage());
			e1.printStackTrace();
			printTest(false);
			assertTrue(false);
		}
		println();
		
		
		
		//test 2
		//make packet
		commandKey = "toggle light";
		keyBytes = commandKey.getBytes();
		println("Creating command packet with \"" + commandKey + "\" as command key, and no extra info...");
		packetData = new byte[keyBytes.length + 3];
		packetData[0] = DataChannel.TYPE_CMD;
		i=1;
		for(byte b : keyBytes)
		{
			packetData[i] = b;
			i++;
		}
		packetData[i] = (byte)0x00;
		i++;
		packetData[i] = (byte)0x00;
		packet = new DatagramPacket(packetData, packetData.length, InetAddress.getLocalHost(), 1234);
		println("Packet contents:");
		println(packet.getData());
		
		//unpack packet (or i guess you could say unpack-it)
		println("Unpacking packet...");
		try 
		{
			PacketWrapper wrapper = channel.unpack(packet);
			println("Result:" + wrapper.toString());
			
			e = commandKey.equals(wrapper.commandKey()) && "".equals(wrapper.extraInfo()) && DataChannel.TYPE_CMD == wrapper.type();
			printTest(e);
			assertTrue(e);
		} 
		catch (NetworkException e1) 
		{
			println("EXCEPTION >> " + e1.getMessage());
			e1.printStackTrace();
			printTest(false);
			assertTrue(false);
		}
	}
	
	
	//test unpacking an error packet
	public void testUnpackError() throws UnknownHostException
	{
		printHeader("Testing unpack(...) method for error packet...");
		//local test variables
		DatagramPacket packet;
		byte[] packetData;
		boolean e;

		//test 1
		//make packet
		String errorMsg = "this is an error message you've broken something. Sound about right.";
		byte[] msgByte = errorMsg.getBytes();
		println("Creating info packet with \"" + errorMsg + "\" as message field...");
		packetData = new byte[msgByte.length + 1];
		packetData[0] = DataChannel.TYPE_ERR;
		for(int i=0; i< msgByte.length; i++)
		{
			packetData[i+1] = msgByte[i];
		}
		packet = new DatagramPacket(packetData, packetData.length, InetAddress.getLocalHost(), 1234);
		println("Packet contents:");
		println(packet.getData());
		
		//unpack packet (or i guess you could say unpack-it)
		println("Unpacking packet...");
		try 
		{
			PacketWrapper wrapper = channel.unpack(packet);
			println("Result:" + wrapper.toString());
			
			e = errorMsg.equals(wrapper.errorMessage()) && DataChannel.TYPE_ERR == wrapper.type();
			printTest(e);
			assertTrue(e);
		} 
		catch (NetworkException e1) 
		{
			println("EXCEPTION >> " + e1.getMessage());
			e1.printStackTrace();
			printTest(false);
			assertTrue(false);
		}
		println();
		
		
		
		//test 2
		//make packet
		msgByte = "".getBytes();
		println("Creating info packet with blank string as message field...");
		packetData = new byte[msgByte.length + 1];
		packetData[0] = DataChannel.TYPE_ERR;
		for(int i=0; i< msgByte.length; i++)
		{
			packetData[i+1] = msgByte[i];
		}
		packet = new DatagramPacket(packetData, packetData.length, InetAddress.getLocalHost(), 1234);
		println("Packet contents:");
		println(packet.getData());
		
		//unpack packet (or i guess you could say unpack-it)
		println("Unpacking packet...");
		try 
		{
			PacketWrapper wrapper = channel.unpack(packet);
			println("Result:" + wrapper.toString());
			
			e = "".equals(wrapper.errorMessage()) && DataChannel.TYPE_ERR == wrapper.type();
			printTest(e);
			assertTrue(e);
		} 
		catch (NetworkException e1) 
		{
			println("EXCEPTION >> " + e1.getMessage());
			e1.printStackTrace();
			printTest(false);
			assertTrue(false);
		}
	}
	
	
	
	//test unpacking an info packet
	public void testUnpackInfo() throws UnknownHostException
	{
		printHeader("Testing unpack(...) method for info packet...");
		//local test variables
		DatagramPacket packet;
		byte[] packetData;
		boolean e;

		//test 1
		//make packet
		byte[] infoString = "some info goes here".getBytes();
		println("Creating info packet with \"some info goes here\" as info field...");
		packetData = new byte[infoString.length + 1];
		packetData[0] = DataChannel.TYPE_INFO;
		for(int i=0; i< infoString.length; i++)
		{
			packetData[i+1] = infoString[i];
		}
		packet = new DatagramPacket(packetData, packetData.length, InetAddress.getLocalHost(), 1234);
		println("Packet contents:");
		println(packet.getData());
		
		//unpack packet (or i guess you could say unpack-it)
		println("Unpacking packet...");
		try 
		{
			PacketWrapper wrapper = channel.unpack(packet);
			println("Result:" + wrapper.toString());
			
			e = "some info goes here".equals(wrapper.info()) && DataChannel.TYPE_INFO == wrapper.type();
			printTest(e);
			assertTrue(e);
		} 
		catch (NetworkException e1) 
		{
			println("EXCEPTION >> " + e1.getMessage());
			e1.printStackTrace();
			printTest(false);
			assertTrue(false);
		}
		println();
		
		
		
		//test 2
		//make packet
		infoString = "".getBytes();
		println("Creating info packet with blank string as info field...");
		packetData = new byte[infoString.length + 1];
		packetData[0] = DataChannel.TYPE_INFO;
		for(int i=0; i< infoString.length; i++)
		{
			packetData[i+1] = infoString[i];
		}
		packet = new DatagramPacket(packetData, packetData.length, InetAddress.getLocalHost(), 1234);
		println("Packet contents:");
		println(packet.getData());
		
		//unpack packet (or i guess you could say unpack-it)
		println("Unpacking packet...");
		try 
		{
			PacketWrapper wrapper = channel.unpack(packet);
			println("Result:" + wrapper.toString());
			
			e = "".equals(wrapper.info()) && DataChannel.TYPE_INFO == wrapper.type();
			printTest(e);
			assertTrue(e);
		} 
		catch (NetworkException e1) 
		{
			println("EXCEPTION >> " + e1.getMessage());
			e1.printStackTrace();
			printTest(false);
			assertTrue(false);
		}
	}
	
	
	//test the handshake
	public void testHandshake() throws NetworkException, IOException
	{
		printHeader("Testing unpack(...) method for handshake packet...");
		//local test variables
		DatagramPacket packet;
		byte[] packetData;
		boolean e;
		int i=0;

		//test 1
		//make packet
		String handshake = "Handshake Test";
		String deviceName = "Terminal";
		byte[] handShakeByte = handshake.getBytes();
		byte[] nameBytes = deviceName.getBytes();
		println("Creating handshake packet with \"" + handshake + "\" as handshake, \"" + deviceName + "\" as device name...");
		packetData = new byte[handShakeByte.length + 3 + nameBytes.length];
		packetData[0] = DataChannel.TYPE_HANDSHAKE;
		i=1;
		for(byte b : handShakeByte)
		{
			packetData[i] = b;
			i++;
		}
		packetData[i] = (byte)0x00;
		i++;
		for(byte b : nameBytes)
		{
			packetData[i] = b;
			i++;
		}
		packetData[i] = (byte)0x00;
		packet = new DatagramPacket(packetData, packetData.length, InetAddress.getLocalHost(), 1234);
		println("Packet contents:");
		println(packet.getData());
		
		//unpack packet (or i guess you could say unpack-it)
		println("Unpacking packet...");
		try 
		{
			PacketWrapper wrapper = channel.unpack(packet);
			println("Result:" + wrapper.toString());
			
			e = handshake.equals(wrapper.handshakeKey()) && deviceName.equals(wrapper.deviceName()) && DataChannel.TYPE_HANDSHAKE == wrapper.type();
			printTest(e);
			assertTrue(e);
		} 
		catch (NetworkException e1) 
		{
			println("EXCEPTION >> " + e1.getMessage());
			e1.printStackTrace();
			printTest(false);
			assertTrue(false);
		}
		println();
		
		
		
		//test 2
		//make packet
		handshake = "";
		deviceName = "";
		handShakeByte = handshake.getBytes();
		nameBytes = deviceName.getBytes();
		println("Creating handshake packet with empty handshake/device name fields...");
		packetData = new byte[handShakeByte.length + 3 + nameBytes.length];
		packetData[0] = DataChannel.TYPE_HANDSHAKE;
		i=1;
		for(byte b : handShakeByte)
		{
			packetData[i] = b;
			i++;
		}
		packetData[i] = (byte)0x00;
		i++;
		for(byte b : nameBytes)
		{
			packetData[i] = b;
			i++;
		}
		packetData[i] = (byte)0x00;
		packet = new DatagramPacket(packetData, packetData.length, InetAddress.getLocalHost(), 1234);
		println("Packet contents:");
		println(packet.getData());
		
		//unpack packet (or i guess you could say unpack-it)
		println("Unpacking packet...");
		try 
		{
			PacketWrapper wrapper = channel.unpack(packet);
			println("Result:" + wrapper.toString());
			
			e = handshake.equals(wrapper.handshakeKey()) && deviceName.equals(wrapper.deviceName()) && DataChannel.TYPE_HANDSHAKE == wrapper.type();
			printTest(e);
			assertTrue(e);
		} 
		catch (NetworkException e1) 
		{
			println("EXCEPTION >> " + e1.getMessage());
			e1.printStackTrace();
			printTest(false);
			assertTrue(false);
		}
		println();
	}
	
	
	
	
	
	
	
	/* these methods were used in a previous revision, no longer needed but leaving the code in
	 * case we need it in the future
	 */
	/*
	public void testToByteArray()
	{
		printHeader("Testing toByteArray(...) method...");
		//local test variables
		byte[] retArr;
		boolean e;
		
		//test 1
		retArr = channel.toByteArray(0xFFFFFFFF);
		println("Convert 0xFFFFFFFF to byte[]...");
		println("Result: ");
		println(retArr);
		e = Arrays.equals(retArr, new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF});
		printTest(e);
		assertTrue("Convert 0xFFFFFFF to byte[]", e);
		println();
		
		//test 2
		retArr = channel.toByteArray(0x12345678);
		println("Convert 0x12345678 to byte[]...");
		println("Result: ");
		println(retArr);
		e = Arrays.equals(retArr, new byte[]{(byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78});
		printTest(e);
		assertTrue("Convert 0x12345678 to byte[]", e);
		println();
		
		//test 3
		retArr = channel.toByteArray(0);
		println("Convert 0 to byte[]...");
		println("Result: ");
		println(retArr);
		e = Arrays.equals(retArr, new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00});
		printTest(e);
		assertTrue("Convert 0 to byte[]", e);
		println();
		
		//test 4
		retArr = channel.toByteArray(3010);
		println("Convert 3010 to byte[]...");
		println("Result: ");
		println(retArr);
		e = Arrays.equals(retArr, new byte[]{(byte)0x00, (byte)0x00, (byte)0x0B, (byte)0xC2});
		printTest(e);
		assertTrue("Convert 3010 to byte[]", e);
		println("\n\n");
	}
	
	
	public void testfromByteArray()
	{
		printHeader("Testing fromByteArray(...) method...");
		
		//declaring method variables
		int retInt;
		boolean e;
		
		//test 1
		retInt = channel.fromByteArray(new byte[]{(byte)0x00, (byte)0x00, (byte)0x0B, (byte)0xC2});
		println("Convert 00-00-0B-C2 to int...");
		println("Result: ");
		println(retInt+"");
		e = (retInt == 3010);
		printTest(e);
		assertTrue("Convert 00-00-0B-C2 to int", e);
		println();
		
		//test 2
		retInt = channel.fromByteArray(new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00});
		println("Convert 00-00-00-00 to int...");
		println("Result: ");
		println(retInt+"");
		e = (retInt == 0);
		printTest(e);
		assertTrue("Convert 00-00-00-00 to int", e);
		println();
		
		//test 3
		retInt = channel.fromByteArray(new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF});
		println("Convert FF-FF-FF-FF to int...");
		println("Result: ");
		println(retInt+"");
		e = (retInt == 0xFFFFFFFF);
		printTest(e);
		assertTrue("Convert FF-FF-FF-FF to int", e);
		println();
		
		//test 4 
		retInt = channel.fromByteArray(new byte[]{(byte)0x00, (byte)0x00, (byte)0x10, (byte)0xFF});
		println("Convert 00-00-10-FF to int...");
		println("Result: ");
		println(retInt+"");
		e = (retInt == 0x10FF);
		printTest(e);
		assertTrue("Convert 00-00-10-FF to int", e);
		println("\n\n");
	}
	
	
	//test from int --> byte[], and byte[] --> int
	public void testIntByteArrConversion()
	{
		printHeader("Testing X-->[toByteArray]-->[fromByteArray]-->X\n"
				  + "        Y-->[fromByteArray]-->[toByteArray]-->Y ...");
		
		//declaring test variables
		int startInt = 1969;
		byte[] byteArr;
		int endInt;
		boolean e;
		
		//test int-->byte[]
		byteArr = channel.toByteArray(startInt);
		println("Convert " + startInt + " to byte[]...");
		println("Result:");
		println(byteArr);
		println("Converting byte[] to int...");
		endInt = channel.fromByteArray(byteArr);
		println("Result:");
		println(endInt+"");
		e = (startInt == endInt);
		printTest(e);
		assertTrue("Testing X-->[toByeArr]-->[fromByteArray]-->X", e);
		println();
		
		
		//declaring test variables
		byte[] startArr = {(byte)0xDE, (byte)0xAF, (byte)0xDA, (byte)0xD5};
		int interm;
		byte[] endArr;
		
		//test byte[] --> int
		println("Converting this from byte[]:");
		println(startArr);
		interm = channel.fromByteArray(startArr);
		println("Result:");
		println(""+interm);
		println("Converting byte[] to int...");
		endArr = channel.toByteArray(interm);
		e = (Arrays.equals(startArr, endArr));
		println("Result:");
		println(endArr);
		printTest(e);
		assertTrue("Testing Y-->[toByteArr]-->[fromByteArr]-->Y", e);
		println("\n\n");
	}
	*/
}