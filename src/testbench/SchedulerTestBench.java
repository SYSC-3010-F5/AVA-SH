/**
*Class:             SchedulerTestBench.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    06/03/2017                                              
*Version:           1.0.0                                         
*                                                                                   
*Purpose:           Testing for scheduling events
*					
* 
*Update Log			v1.0.0
*						- null
*/
package testbench;


import java.net.SocketException;
import java.net.UnknownHostException;

import io.json.JsonException;
import network.DataChannel;
import network.PacketWrapper;
import server.MainServer;
//import packages
import server.Scheduler;
import server.datatypes.ServerEvent;
import server.datatypes.TimeAndDate;



public class SchedulerTestBench extends TestBench 
{
	//declaring local instance variables
	Scheduler scheduler;
	MainServer server;
	
	
	//generic constructor
	public SchedulerTestBench(String name)
	{
		super(name);
	}
	
	
	@Override
	protected void setUp() throws Exception 
	{
		super.setUp();
		scheduler = new Scheduler("Testing Scheduler");
		println("Creating server to respond to commands...");
		server = new MainServer();
		ServerEvent.hookDSKY(server.getDKSY());
		server.start();
		println("Server created on new thread!");
	}

	
	@Override
	protected void tearDown() throws Exception 
	{
		super.tearDown();
		scheduler = null;
		server.shutdown();
		server = null;
	}
	

	public void testSingleTrigger() throws SocketException, UnknownHostException
	{
		printHeader("Testing Event trigger in 10 seconds...");

		println("Creating event...");
		PacketWrapper[] commands = new PacketWrapper[1];
		commands[0] = new PacketWrapper(DataChannel.TYPE_CMD, "ping", "", null);
		ServerEvent event = new ServerEvent("Test Alarm", commands, null);
		println("Event created as: " + event.toString());
		
		println("Schedualing event to trigger in 10 seconds...");
		scheduler.scheduleTimer(event, 10);
		
		println("Is server response correct? (y/n)");
		String input = getInput();
		boolean pass = false;
		if(input.equals("y"))
		{
			pass = true;
		}
		printTest(pass);
		assertTrue("Test alarm triggering", pass);
	}
	
	
	public void testRemoveEventAndMultiTrigger() throws SocketException, UnknownHostException
	{
		printHeader("Testing removing active event...");
		
		println("Creating event...");
		PacketWrapper[] commands = new PacketWrapper[1];
		commands[0] = new PacketWrapper(DataChannel.TYPE_CMD, "ping", "", null);
		ServerEvent event1 = new ServerEvent("First Alarm", commands, null);
		ServerEvent event2 = new ServerEvent("DELETED ALARM", commands, null);
		ServerEvent event3 = new ServerEvent("Second Alarm", commands, null);
		println("Events created!");
		
		println("Schedualing event 1 to trigger in 15 seconds...");
		scheduler.scheduleTimer(event1, 15);
		println("Schedualing event 2 to trigger in 20 seconds...");
		scheduler.scheduleTimer(event2, 20);
		println("Schedualing event 3 to trigger in 25 seconds...");
		scheduler.scheduleTimer(event3, 25);
		println("Removing event 2...");
		event2.cancel();
		
		println("Is server response correct? (y/n)");
		String input = getInput();
		boolean pass = false;
		if(input.equals("y"))
		{
			pass = true;
		}
		printTest(pass);
		assertTrue("Test alarm triggering", pass);
	}
	
	
	public void testfromJSONEventTrigger() throws SocketException, UnknownHostException, JsonException
	{
		printHeader("Testing Scheduling Event from JSON-built ServerEvent...");

		println("Creating event...");
		PacketWrapper[] commands = new PacketWrapper[1];
		commands[0] = new PacketWrapper(DataChannel.TYPE_CMD, "trash", "{\n\"name\" : {\n\t\"name\" : \"componded\"\n}\n}", null);
		ServerEvent event = new ServerEvent("Test Alarm", commands);
		println("Event created as: " + event.toString());
		println("Deconstructing and rebuilding event from json...");
		ServerEvent built = new ServerEvent();
		built.fromJSON(event.toJSON("").toString());
		
		println("Schedualing original event to trigger in 10 seconds...");
		scheduler.scheduleTimer(event, 10);
		println("Schedualing rebuilt event to trigger in 15 seconds...");
		scheduler.scheduleTimer(built, 15);
		
		println("Is server response correct? (y/n)");
		String input = getInput();
		boolean pass = false;
		if(input.equals("y"))
		{
			pass = true;
		}
		printTest(pass);
		assertTrue("Test alarm triggering", pass);
	}
}
