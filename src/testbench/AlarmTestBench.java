/**
*Class:             AlarmTestBench.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    18/02/2017                                              
*Version:           1.0.0                                         
*                                                                                   
*Purpose:           Testbench for Alarm class
*					
* 
*Update Log			v1.0.0
*						- some methods added
*/
package testbench;

import java.time.DateTimeException;

import io.json.JsonException;
import server.datatypes.Alarm;

public class AlarmTestBench extends TestBench 
{
	//test variables
	Alarm alarm;

	public AlarmTestBench(String name) 
	{
		super(name);
	}
	

	@Override
	protected void setUp() throws Exception 
	{
		super.setUp();
		alarm  = new Alarm();
	}

	
	@Override
	protected void tearDown() throws Exception 
	{
		super.tearDown();
		alarm = null;
	}
	
	
	public void testSetHour()
	{
		printHeader("Testing setHour() method...");
		boolean e;
		
		//test lower range 1
		try
		{
			println("Setting hour to 0...");
			alarm.setHour(0);
			println("Result: " + alarm.getHour());
		}
		catch (DateTimeException da)
		{
			println("EXCEPTION CAUGHT");
		}
		e = 0 == alarm.getHour();
		printTest(e);
		assertTrue("Test lower range", e);
		println();
		
		//test upper range 1 
		try
		{
			println("Setting hour to 23...");
			alarm.setHour(23);
			println("Result: " + alarm.getHour());
		}
		catch (DateTimeException da)
		{
			println("EXCEPTION CAUGHT");
		}
		e = 23 == alarm.getHour();
		printTest(e);
		assertTrue("Test upper range", e);
		println();
		
		//test upper range 2 
		try
		{
			e = false;
			println("Setting hour to 24...");
			alarm.setHour(24);
			println("Result: " + alarm.getHour());
		}
		catch (DateTimeException da)
		{
			println("EXCEPTION CAUGHT");
			e = true;
		}
		printTest(e);
		assertTrue("Test upper range", e);
		println();
		
		//test lower range 2 
		try
		{
			e = false;
			println("Setting hour to -1...");
			alarm.setHour(-1);
			println("Result: " + alarm.getHour());
		}
		catch (DateTimeException da)
		{
			println("EXCEPTION CAUGHT");
			e = true;
		}
		printTest(e);
		assertTrue("Test lower range", e);
		println();
	}
	
	
	public void testSetMinute()
	{
		printHeader("Testing setMinute() method...");
		boolean e;
		
		//test lower range 1
		try
		{
			println("Setting minute to 0...");
			alarm.setMinute(0);
			println("Result: " + alarm.getMinute());
		}
		catch (DateTimeException da)
		{
			println("EXCEPTION CAUGHT");
		}
		e = 0 == alarm.getMinute();
		printTest(e);
		assertTrue("Test lower range", e);
		println();
		
		//test upper range 1 
		try
		{
			println("Setting minute to 59...");
			alarm.setMinute(59);
			println("Result: " + alarm.getMinute());
		}
		catch (DateTimeException da)
		{
			println("EXCEPTION CAUGHT");
		}
		e = 59 == alarm.getMinute();
		printTest(e);
		assertTrue("Test upper range", e);
		println();
		
		//test upper range 2 
		try
		{
			e = false;
			println("Setting minute to 60...");
			alarm.setMinute(60);
			println("Result: " + alarm.getMinute());
		}
		catch (DateTimeException da)
		{
			println("EXCEPTION CAUGHT");
			e = true;
		}
		printTest(e);
		assertTrue("Test upper range", e);
		println();
		
		//test lower range 2 
		try
		{
			e = false;
			println("Setting minute to -1...");
			alarm.setMinute(-1);
			println("Result: " + alarm.getMinute());
		}
		catch (DateTimeException da)
		{
			println("EXCEPTION CAUGHT");
			e = true;
		}
		printTest(e);
		assertTrue("Test lower range", e);
		println();
	}
	
	
	public void testJson()
	{
		boolean e;
		printHeader("Testing toJson(...) & fromJson(...) ...");
		println("generating alarm at 11:00, Sat and Sun...");
		
		//convert to json
		println("converting to json...");
		alarm = new Alarm(11,59,new boolean[]{false,true,false,true,false,true,false},"Sample Alarm");
		println("Result:");
		String json = alarm.toJSON("").toString();
		println("-------------json start-------------");
		println(json);
		println("--------------json end--------------");
		
		//convert from json
		println("converting from json");
		Alarm alarm2 = new Alarm();
		try 
		{
			alarm2.fromJSON(json);
		} 
		catch (JsonException je) 
		{
			je.printStackTrace();
		}
		println("Result:");
		String json2 = alarm2.toJSON("").toString();
		println("-------------json start-------------");
		println(json2);
		println("--------------json end--------------");
		
		//check if the same
		e = (json2.equals(json));
		printTest(e);
		assertTrue("alarm-->json-->alarm-->jason convertion", e);
		
		
		
	}
}
