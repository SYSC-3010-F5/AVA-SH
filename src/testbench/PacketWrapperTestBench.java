/**
*Class:             PacketWrapperTestBench.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    18/02/2017                                              
*Version:           1.0.0                                         
*                                                                                   
*Purpose:           Testbench for PacketWrapper class
*					
* 
*Update Log			v1.0.0
*						- some methods added
*/
package testbench;

import java.time.DateTimeException;

import io.json.JsonException;
import network.PacketWrapper;

public class PacketWrapperTestBench extends TestBench 
{
	//test variables
	PacketWrapper wrapper;

	public PacketWrapperTestBench(String name) 
	{
		super(name);
	}
	

	@Override
	protected void setUp() throws Exception 
	{
		super.setUp();
		wrapper  = new PacketWrapper();
	}

	
	@Override
	protected void tearDown() throws Exception 
	{
		super.tearDown();
		wrapper = null;
	}
	
	
	
	public void testJson() throws JsonException
	{
		boolean e;
		printHeader("Testing toJson(...) & fromJson(...) ...");
		println("generating wrapper...");
		wrapper = new PacketWrapper((byte)1, "req time", "", null);
		println("Result: " + wrapper.toString());
		
		//convert to json
		println("converting to json...");
		println("Result:");
		String json = wrapper.toJSON("").toString();
		println("-------------json start-------------");
		println(json);
		println("--------------json end--------------");
		
		//convert from json
		println("converting from json...");
		PacketWrapper wrapper2 = new PacketWrapper();
		try 
		{
			wrapper2.fromJSON(json);
		} 
		catch (JsonException je) 
		{
			je.printStackTrace();
			throw je;
		}
		println("Result:");
		String json2 = wrapper2.toJSON("").toString();
		println("-------------json start-------------");
		println(json2);
		println("--------------json end--------------");
		println("wrapper: " + wrapper2.toString());
		
		//check if the same
		e = (json2.equals(json) && wrapper.toString().equals(wrapper2.toString()));
		printTest(e);
		assertTrue("alarm-->json-->alarm-->jason convertion", e);
	}
	
	
	public void testJson2() throws JsonException
	{
		boolean e;
		printHeader("Testing toJson(...) & fromJson(...) ...");
		println("generating wrapper...");
		wrapper = new PacketWrapper((byte)1, "play song", "{\n\"artist\" : \"Metric\"\n\"trackName\" : \"Dead Disco\"\n\"album\" : \"Old World Underground, Where are You Now?\"\n}", null);
		println("Result: " + wrapper.toString());
		
		//convert to json
		println("converting to json...");
		println("Result:");
		String json = wrapper.toJSON("").toString();
		println("-------------json start-------------");
		println(json);
		println("--------------json end--------------");
		
		//convert from json
		println("converting from json...");
		PacketWrapper wrapper2 = new PacketWrapper();
		try 
		{
			wrapper2.fromJSON(json);
		} 
		catch (JsonException je) 
		{
			je.printStackTrace();
			throw je;
		}
		println("Result:");
		String json2 = wrapper2.toJSON("").toString();
		println("-------------json start-------------");
		println(json2);
		println("--------------json end--------------");
		println("wrapper: " + wrapper2.toString());
		
		//check if the same
		e = (json2.equals(json) && wrapper.toString().equals(wrapper2.toString()));
		printTest(e);
		assertTrue("alarm-->json-->alarm-->jason convertion", e);
	}
}
