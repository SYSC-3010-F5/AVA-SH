/**
*Class:             DataChannelTestBench.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    15/03/2017                                              
*Version:           1.1.0                                         
*                                                                                   
*Purpose:           Test bench for methods in DataChannel.
*					
* 
*Update Log			v1.1.0
*						- 10 new tests added testing JUST packing packets
*						- 2 new tests for testing unpacking disconnect packets added
*					v1.0.0
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
import java.util.LinkedList;

//import packages
import network.DataChannel;
import network.NetworkException;
import network.PacketWrapper;



public class DataChannelTestBench extends TestBench 
{
	//test variables
	DataChannel channel;
	boolean e;

	
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
	
	
	public void testSend() throws Exception
	{
		network.DataMultiChannel dmc = new network.DataMultiChannel();
		dmc.hijackChannel(InetAddress.getLocalHost(), 9999);
		dmc.sendPacket(new byte[]{(byte)0});
		dmc.sendPacket(new byte[]{(byte)1});
		dmc.sendPacket(new byte[]{(byte)2});
		dmc.sendPacket(new byte[]{(byte)0});
	}

	
	@Override
	protected void tearDown() throws Exception 
	{
		super.tearDown();
		channel = null;
	}
	
	
	//test packing command with no extra info
	public void testPackCommand1()
	{
		printHeader("Testing packCommand(...) method w/ extra info field... x1/2");
		
		String key = "This is the Key";
		String ex = "This is the info";
		byte[] expected = ("\u0001" + key + "\0" + ex + "\0").getBytes();
		println("Packing command packet with key: \"" + key + "\", info: \"" + ex + "\"");
		println("Expected:");
		println(expected);
		println();
		
		byte[] obtained = channel.packCmd(key.getBytes(), ex.getBytes());
		println("Actual:");
		println(obtained);
		
		e = Arrays.equals(obtained, expected);
		printTest(e);
		assertTrue(e);
	}
	
	
	//test packing command with extra info
	public void testPackCommand2()
	{
		printHeader("Testing packCommand(...) method w/o extra info field... x2/2");

		String key = "burnin\' love";
		String ex = "";
		byte[] expected = ("\u0001" + key + "\0" + ex + "\0").getBytes();
		println("Packing command packet with key: \"" + key + "\", info: \"" + ex + "\"");
		println("Expected:");
		println(expected);
		
		byte[] obtained = channel.packCmd(key.getBytes(), ex.getBytes());
		println("Actual:");
		println(obtained);
		
		e = Arrays.equals(obtained, expected);
		printTest(e);
		assertTrue(e);
	}
	
	
	//test packing info with msg
	public void testPackInfo1()
	{
		printHeader("Testing packInfo(...) method w/ message field... x1/2");

		String info = "This is a test message :)";
		byte[] expected = ("\u0002" + info).getBytes();
		println("Packing info packet with infoMsg: \"" + info + "\"");
		println("Expected:");
		println(expected);
		
		byte[] obtained = channel.packInfo(info.getBytes());
		println("Actual:");
		println(obtained);
		
		e = Arrays.equals(obtained, expected);
		printTest(e);
		assertTrue(e);
	}
	
	
	//test packing info with msg
	public void testPackInfo2()
	{
		printHeader("Testing packInfo(...) method w/o message field... x2/2");

		String info = "";
		byte[] expected = ("\u0002" + info).getBytes();
		println("Packing info packet with infoMsg: \"" + info + "\"");
		println("Expected:");
		println(expected);
		
		byte[] obtained = channel.packInfo(info.getBytes());
		println("Actual:");
		println(obtained);
		
		e = Arrays.equals(obtained, expected);
		printTest(e);
		assertTrue(e);
	}
	
	
	//test packing error with msg
	public void testPackError1()
	{
		printHeader("Testing packError(...) method w/ erorr msg... x1/2");

		String error = "404 error message not found";
		byte[] expected = ("\u0003" + error).getBytes();
		println("Packing error packet with errorMsg: \"" + error + "\"");
		println("Expected:");
		println(expected);
		
		byte[] obtained = channel.packError(error.getBytes());
		println("Actual:");
		println(obtained);
		
		e = Arrays.equals(obtained, expected);
		printTest(e);
		assertTrue(e);
	}
	
	
	//test packing error with no msg
	public void testPackError2()
	{
		printHeader("Testing packError(...) method w/o error msg... x2/2");

		String error = "";
		byte[] expected = ("\u0003" + error).getBytes();
		println("Packing error packet with errorMsg: \"" + error + "\"");
		println("Expected:");
		println(expected);
		
		byte[] obtained = channel.packError(error.getBytes());
		println("Actual:");
		println(obtained);
		
		e = Arrays.equals(obtained, expected);
		printTest(e);
		assertTrue(e);
	}
	
	
	//test packing disconnect with reason
	public void testPackDisconnect1()
	{
		printHeader("Testing packDisconnect(...) method w/ reason x1/2");

		String msg = "User Request";
		byte[] expected = ("\u0004" + msg).getBytes();
		println("Packing disconnect packet with reason: \"" + msg + "\"");
		println("Expected:");
		println(expected);
		
		byte[] obtained = channel.packDisconnect(msg.getBytes());
		println("Actual:");
		println(obtained);
		
		e = Arrays.equals(obtained, expected);
		printTest(e);
		assertTrue(e);
	}
	
	
	//test packing disconnect w/o reason
	public void testPackDisconnect2()
	{
		printHeader("Testing packDisconnect(...) method w/o reason x2/2");

		String msg = "";
		byte[] expected = ("\u0004" + msg).getBytes();
		println("Packing disconnect packet with reason: \"" + msg + "\"");
		println("Expected:");
		println(expected);
		
		byte[] obtained = channel.packDisconnect(msg.getBytes());
		println("Actual:");
		println(obtained);
		
		e = Arrays.equals(obtained, expected);
		printTest(e);
		assertTrue(e);
	}
	
	
	//test packing handshake w/ fields
	public void testPackHandshake1()
	{
		printHeader("Testing packHandshake(...) method w/ fields x1/2");

		String key = "THIS IS THE KEY";
		String name = "Some tester";
		byte[] expected = ("\u0000" + key + "\0" + name).getBytes();
		println("Packing handshake packet with key: \"" + key + "\", name: \"" + name + "\"");
		println("Expected:");
		println(expected);
		
		byte[] obtained = channel.packHandshake(key.getBytes(), name.getBytes());
		println("Actual:");
		println(obtained);
		
		e = Arrays.equals(obtained, expected);
		printTest(e);
		assertTrue(e);
	}
	
	
	//test packing handshake w/ fields
	public void testPackHandshake2()
	{
		printHeader("Testing packHandshake(...) method w/o fields x2/2");

		String key = "";
		String name = "";
		byte[] expected = ("\u0000" + key + "\0" + name).getBytes();
		println("Packing handshake packet with key: \"" + key + "\", name: \"" + name + "\"");
		println("Expected:");
		println(expected);
		
		byte[] obtained = channel.packHandshake(key.getBytes(), name.getBytes());
		println("Actual:");
		println(obtained);
		
		e = Arrays.equals(obtained, expected);
		printTest(e);
		assertTrue(e);
	}

	
	//test unpacking a improper packet
	public void testUnpackBadPacket() throws UnknownHostException
	{
		printHeader("Testing unpack(...) method for bad packet (type 0xAD)...");
		//local test variables
		DatagramPacket packet;
		byte[] packetData;

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
	
	
	//test unpacking a command packet with extra info
	public void testUnpackCommand1() throws UnknownHostException
	{
		printHeader("Testing unpack(...) method for command packet x1/2...");
		//local test variables
		DatagramPacket packet;
		byte[] packetData;
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
	}
		
		
	//test unpacking a command packet with extra info
	public void testUnpackCommand2() throws UnknownHostException
	{	
		printHeader("Testing unpack(...) method for command packet x2/2...");
		
		//make packet
		String commandKey = "toggle light";
		byte[] keyBytes = commandKey.getBytes();
		println("Creating command packet with \"" + commandKey + "\" as command key, and no extra info...");
		byte[] packetData = new byte[keyBytes.length + 3];
		packetData[0] = DataChannel.TYPE_CMD;
		int i=1;
		for(byte b : keyBytes)
		{
			packetData[i] = b;
			i++;
		}
		packetData[i] = (byte)0x00;
		i++;
		packetData[i] = (byte)0x00;
		DatagramPacket packet = new DatagramPacket(packetData, packetData.length, InetAddress.getLocalHost(), 1234);
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
	
	
	//test unpacking an error packet with msg
	public void testUnpackError1() throws UnknownHostException
	{
		printHeader("Testing unpack(...) method for error packet x1/2...");
		//local test variables
		DatagramPacket packet;
		byte[] packetData;
		 ;

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
	}
		
	//test unpack error with no msg
	public void testUnpackError2() throws UnknownHostException
	{
		printHeader("Testing unpack(...) method for error packet x2/2...");
		
		//make packet
		byte[] msgByte = "".getBytes();
		println("Creating info packet with blank string as message field...");
		byte[] packetData = new byte[msgByte.length + 1];
		packetData[0] = DataChannel.TYPE_ERR;
		for(int i=0; i< msgByte.length; i++)
		{
			packetData[i+1] = msgByte[i];
		}
		DatagramPacket packet = new DatagramPacket(packetData, packetData.length, InetAddress.getLocalHost(), 1234);
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
	
	
	
	//test unpacking an info packet with msg
	public void testUnpackInfo1() throws UnknownHostException
	{
		printHeader("Testing unpack(...) method for info packet... x1/2");
		//local test variables
		DatagramPacket packet;
		byte[] packetData;
		 ;

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
	}
		
	public void testUnpackInfo2() throws UnknownHostException
	{
		printHeader("Testing unpack(...) method for info packet... x2/2");
		
		//make packet
		byte[] infoString = "".getBytes();
		println("Creating info packet with blank string as info field...");
		byte[] packetData = new byte[infoString.length + 1];
		packetData[0] = DataChannel.TYPE_INFO;
		for(int i=0; i< infoString.length; i++)
		{
			packetData[i+1] = infoString[i];
		}
		DatagramPacket packet = new DatagramPacket(packetData, packetData.length, InetAddress.getLocalHost(), 1234);
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
	
	
	//test the handshake with fields
	public void testUnpackHandshake1() throws NetworkException, IOException
	{
		printHeader("Testing unpack(...) method for handshake packet... x1/2");
		//local test variables
		DatagramPacket packet;
		byte[] packetData;
		 ;
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
	}
		
	
	//test unpack empty handshake
	public void testUnpackHandshake2() throws UnknownHostException
	{
		printHeader("Testing unpack(...) method for handshake packet... x2/2");
		
		//make packet
		String handshake = "";
		String deviceName = "";
		byte[] handShakeByte = handshake.getBytes();
		byte[] nameBytes = deviceName.getBytes();
		println("Creating handshake packet with empty handshake/device name fields...");
		byte[] packetData = new byte[handShakeByte.length + 3 + nameBytes.length];
		packetData[0] = DataChannel.TYPE_HANDSHAKE;
		int i=1;
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
		DatagramPacket packet = new DatagramPacket(packetData, packetData.length, InetAddress.getLocalHost(), 1234);
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
	
	
	//test unpacking a disconnect packet
	public void testUnpackDisconnect1() throws UnknownHostException
	{
		printHeader("Testing unpack(...) method for disconnect packet w/ message... x1/2");
		
		String msg = "some disconnect reason here";
		byte[] data = ("\u0004"+msg).getBytes();
		DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), 1234);
		println("Expected: Type: 4, Msg: \"" + msg + "\"");
		
		//unpack packet (or i guess you could say unpack-it)
		println("Unpacking packet...");
		try 
		{
			PacketWrapper wrapper = channel.unpack(packet);
			println("Result:" + wrapper.toString());
			
			e = wrapper.disconnectMessage().equals(msg) && wrapper.type() == DataChannel.TYPE_DISCONNECT;
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
	
	
	//test unpacking a disconnect packet w/o msg
	public void testUnpackDisconnect2() throws UnknownHostException
	{
		printHeader("Testing unpack(...) method for disconnect packet w/o message... x2/2");
		
		String msg = "";
		byte[] data = ("\u0004"+msg).getBytes();
		DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), 1234);
		println("Expected: Type: 4, Msg: \"" + msg + "\"");
		
		//unpack packet (or i guess you could say unpack-it)
		println("Unpacking packet...");
		try 
		{
			PacketWrapper wrapper = channel.unpack(packet);
			println("Result:" + wrapper.toString());
			
			e = wrapper.disconnectMessage().equals(msg) && wrapper.type() == DataChannel.TYPE_DISCONNECT;
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
	
}