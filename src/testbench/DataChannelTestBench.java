/**
*Class:             DataChannelTestBench.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    04/02/2017                                              
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


//import external libraries
import junit.framework.TestCase;
import java.util.Arrays;

//import packages
import network.DataChannel;


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
}