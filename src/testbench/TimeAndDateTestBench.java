/**
*Class:             TimeAndDateTestBench.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    04/03/2017                                              
*Version:           1.0.0
*                                                                                   
*Purpose:           Testbench for TimeAndDate class
*					
* 
*Update Log			v1.0.0
*						- some methods added
*/
package testbench;

//import external
import java.time.DateTimeException;

//import packages
import io.json.JsonException;
import server.datatypes.TimeAndDate;




public class TimeAndDateTestBench extends TestBench 
{
	//test variables
	TimeAndDate trigger;

	public TimeAndDateTestBench(String name) 
	{
		super(name);
	}
	

	@Override
	protected void setUp() throws Exception 
	{
		super.setUp();
		trigger  = new TimeAndDate();
	}

	
	@Override
	protected void tearDown() throws Exception 
	{
		super.tearDown();
		trigger = null;
	}
	
	
	public void testSetHour()
	{
		printHeader("Testing setHour() method...");
		boolean e;
		
		//test lower range 1
		try
		{
			println("Setting hour to 0...");
			trigger.setHour(0);
			println("Result: " + trigger.getHour());
		}
		catch (DateTimeException da)
		{
			println("EXCEPTION CAUGHT");
		}
		e = 0 == trigger.getHour();
		printTest(e);
		assertTrue("Test lower range", e);
		println();
		
		//test upper range 1 
		try
		{
			println("Setting hour to 23...");
			trigger.setHour(23);
			println("Result: " + trigger.getHour());
		}
		catch (DateTimeException da)
		{
			println("EXCEPTION CAUGHT");
		}
		e = 23 == trigger.getHour();
		printTest(e);
		assertTrue("Test upper range", e);
		println();
		
		//test upper range 2 
		try
		{
			e = false;
			println("Setting hour to 24...");
			trigger.setHour(24);
			println("Result: " + trigger.getHour());
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
			trigger.setHour(-1);
			println("Result: " + trigger.getHour());
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
			trigger.setMinute(0);
			println("Result: " + trigger.getMinute());
		}
		catch (DateTimeException da)
		{
			println("EXCEPTION CAUGHT");
		}
		e = 0 == trigger.getMinute();
		printTest(e);
		assertTrue("Test lower range", e);
		println();
		
		//test upper range 1 
		try
		{
			println("Setting minute to 59...");
			trigger.setMinute(59);
			println("Result: " + trigger.getMinute());
		}
		catch (DateTimeException da)
		{
			println("EXCEPTION CAUGHT");
		}
		e = 59 == trigger.getMinute();
		printTest(e);
		assertTrue("Test upper range", e);
		println();
		
		//test upper range 2 
		try
		{
			e = false;
			println("Setting minute to 60...");
			trigger.setMinute(60);
			println("Result: " + trigger.getMinute());
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
			trigger.setMinute(-1);
			println("Result: " + trigger.getMinute());
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
		println("generating time&date at 11:00, Sat and Sun...");
		
		//convert to json
		println("converting to json...");
		trigger = new TimeAndDate(11,00,new boolean[]{true,false,false,true,false,false,true});
		println("Result:");
		String json = trigger.toJSON("").toString();
		println("-------------json start-------------");
		println(json);
		println("--------------json end--------------");
		
		//convert from json
		println("converting from json");
		TimeAndDate trigger2 = new TimeAndDate();
		try 
		{
			trigger2.fromJSON(json);
		} 
		catch (JsonException je) 
		{
			je.printStackTrace();
		}
		println("Result:");
		String json2 = trigger2.toJSON("").toString();
		println("-------------json start-------------");
		println(json2);
		println("--------------json end--------------");
		
		//check if the same
		e = (json2.equals(json));
		printTest(e);
		assertTrue("trigger-->json-->trigger-->jason convertion", e);
		
		
		
	}
}
