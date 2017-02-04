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
		println();
	}
	
	
	public void testToInteger()
	{
		printHeader("Testing toInteger(...) method...");
		
		//declaring method variables
		int retInt;
		boolean e;
		
		//test 1
		retInt = channel.toInteger(new byte[]{(byte)0x00, (byte)0x00, (byte)0x0B, (byte)0xC2});
		println("Convert 00-00-0B-C2 to int...");
		println("Result: ");
		println(retInt+"");
		e = (retInt == 3010);
		assertTrue("Convert 00-00-0B-C2 to int", e);
		println();
		
		
	}
}