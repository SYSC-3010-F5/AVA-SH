/**
*Class:             ServerEventTestBench.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    02/03/2017                                              
*Version:           1.0.0                                         
*                                                                                   
*Purpose:           Test manual triggering and subsequent firing of ServerEvents
*					
* 
*Update Log			v1.0.0
*						- null
*/
package testbench;


//import externals
import java.net.SocketException;
import java.net.UnknownHostException;

import io.json.JsonException;
import io.json.JsonFile;
//import packages
import network.DataChannel;
import network.PacketWrapper;
import server.MainServer;
import server.datatypes.ServerEvent;
import server.datatypes.TimeAndDate;



public class SeverEventTestBench extends TestBench 
{
	//declaring local instance variables
	ServerEvent event;
	
	//generic constructor
	public SeverEventTestBench(String name)
	{
		super(name);
	}
	
	
	@Override
	protected void setUp() throws Exception 
	{
		super.setUp();
	}

	
	@Override
	protected void tearDown() throws Exception 
	{
		super.tearDown();
		event = null;
	}
	
	
	//test toJson for event with simple commands (no json for cmd fields)
	public void testJSONSimple() throws JsonException
	{
		printHeader("Testing toJson convertion for ServerEvent...");
		JsonFile json1, json2;
		ServerEvent builtEvent = new ServerEvent();

		println("Creating event...");
		TimeAndDate trigger = new TimeAndDate(17, 45, new boolean[]{false,true,true,true,true,true,false});
		PacketWrapper[] commands = new PacketWrapper[3];
		commands[0] = new PacketWrapper(DataChannel.TYPE_CMD, "ping", "", null);
		commands[1] = new PacketWrapper(DataChannel.TYPE_CMD, "req time", "", null);
		commands[2] = new PacketWrapper(DataChannel.TYPE_CMD, "ping", "", null);
		event = new ServerEvent("Make Evening Coffee", commands, trigger);
		
		println("Result: "+event.toString());
		println("Commands:");
		for(PacketWrapper cmd : commands)
		{
			println(cmd.toString());
		}
		
		println("Converting to JSON...");
		json1 = event.toJSON("");
		println("Result:\n-------------------\n"+json1.toString()+"\n-------------------");
		
		println("Converting back to ServerEvent...");
		builtEvent.fromJSON(json1.toString());
		println("Result:" + builtEvent.toString());
		
		println("Converting back to JSON...");
		json2 = builtEvent.toJSON("");
		println("Result:\n-------------------\n"+json2.toString()+"\n-------------------");
		
		boolean pass = json1.toString().equals(json2.toString());
		printTest(pass);
		assertTrue(" with simple commands JSON", pass);
	}
	
	
	//test toJson for event with simple commands (with json for cmd fields)					//TODO Why does this fail?????
	public void testJSONAdv() throws JsonException
	{
		printHeader("Testing toJson convertion for ServerEvent with JSON formated for cmd fields...");
		JsonFile json1, json2;
		ServerEvent builtEvent = new ServerEvent();

		println("Creating event...");
		TimeAndDate trigger = new TimeAndDate(12, 34, new boolean[]{false,true,true,true,true,true,false});
		PacketWrapper[] commands = new PacketWrapper[2];
		commands[0] = new PacketWrapper(DataChannel.TYPE_CMD, "holiday", "{\n\"name\" : Leif Erikson\n\"type\" : holiday\n\"day\" : 9th October\n}", null);
		commands[1] = new PacketWrapper(DataChannel.TYPE_CMD, "comp", "{\n\"name\" : {\n\t\"name\" : \"componded\"\n}\n}", null);
		event = new ServerEvent("Arbitrary", commands, trigger);
		
		println("Result: "+event.toString());
		println("Commands:");
		for(PacketWrapper cmd : commands)
		{
			println(cmd.toString());
		}
		
		println("Converting to JSON...");
		json1 = event.toJSON("");
		println("Result:\n-------------------\n"+json1.toString()+"\n-------------------");
		
		println("Converting back to ServerEvent...");
		builtEvent.fromJSON(json1.toString());
		println("Result:" + builtEvent.toString());
		for(PacketWrapper w : builtEvent.getCommands())
		{
			println(w.toString());
		}
		
		println("Converting back to JSON...");
		json2 = builtEvent.toJSON("");
		println("Result:\n-------------------\n"+json2.toString()+"\n-------------------");
		
		boolean pass = json1.toString().equals(json2.toString());
		printTest(pass);
		assertTrue(" with simple commands JSON", pass);
		
	}
	
	
	
	//test creating new events
	public void testFire() throws SocketException, UnknownHostException, InterruptedException
	{
		printHeader("Testing command firing at trigger...");
		
		println("Creating server to respond to commands...");
		MainServer server = new MainServer();
		ServerEvent.hookDSKY(server.getDKSY());
		server.start();
		println("Server created on new thread!");
		
		println("Creating event...");
		TimeAndDate trigger = new TimeAndDate(7, 30, new boolean[]{false,true,true,true,true,true,false});
		PacketWrapper[] commands = new PacketWrapper[3];
		commands[0] = new PacketWrapper(DataChannel.TYPE_CMD, "ping", "", null);
		commands[1] = new PacketWrapper(DataChannel.TYPE_CMD, "req time", "", null);
		commands[2] = new PacketWrapper(DataChannel.TYPE_CMD, "ping", "", null);
		event = new ServerEvent("TestEvent", commands, trigger);
		println("Event created as: "+event.toString());
		println("At trigger daemon thread fires packets as:\n"
				+commands[0].toString()+"\n"
				+commands[1].toString()+"\n"
				+commands[2].toString()+"\n");
		
		println("Manually triggering event in:");
		for(int i=5; i>0; i--)
		{
			println(i+"...");
			Thread.sleep(1000);
		}
		event.run();
		println("Is server response correct? (y/n)");
		String input = getInput();
		boolean pass = false;
		if(input.equals("y"))
		{
			pass = true;
		}
		
		server.shutdown();
		printTest(pass);
		assertTrue("Test event firing", pass);
	}
	
	
	
	//test firing event created from json deconstruct and build
	public void testFireFromJSON() throws SocketException, UnknownHostException, InterruptedException, JsonException
	{
		printHeader("Testing command firing at trigger...");
		
		println("Creating server to respond to commands...");
		MainServer server = new MainServer();
		ServerEvent.hookDSKY(server.getDKSY());
		server.start();
		println("Server created on new thread!");
		
		println("Creating event...");
		TimeAndDate trigger = new TimeAndDate(12, 34, new boolean[]{false,true,true,true,true,true,false});
		PacketWrapper[] commands = new PacketWrapper[2];
		commands[0] = new PacketWrapper(DataChannel.TYPE_CMD, "holiday", "{\n\"name\" : Leif Erikson\n\"type\" : holiday\n\"day\" : 9th October\n}", null);
		commands[1] = new PacketWrapper(DataChannel.TYPE_CMD, "comp", "{\n\"name\" : {\n\t\"name\" : \"componded\"\n}\n}", null);
		event = new ServerEvent("Arbitrary", commands, trigger);
		
		println("Result: "+event.toString());
		println("Commands:");
		for(PacketWrapper cmd : commands)
		{
			println(cmd.toString());
		}
		
		println("Converting to JSON...");
		JsonFile json1 = event.toJSON("");
		println("Result:\n-------------------\n"+json1.toString()+"\n-------------------");
		
		println("Converting back to ServerEvent...");
		ServerEvent builtEvent = new ServerEvent();
		builtEvent.fromJSON(json1.toString());
		println("Result:" + builtEvent.toString());
		for(PacketWrapper w : builtEvent.getCommands())
		{
			println(w.toString());
		}
		
		println("Converting back to JSON...");
		JsonFile json2 = builtEvent.toJSON("");
		println("Result:\n-------------------\n"+json2.toString()+"\n-------------------");
		
		println("Manually triggering event in:");
		for(int i=5; i>0; i--)
		{
			println(i+"...");
			Thread.sleep(1000);
		}
		builtEvent.run();
		println("Is server response correct? (y/n)");
		String input = getInput();
		boolean pass = false;
		if(input.equals("y"))
		{
			pass = true;
		}
		
		server.shutdown();
		printTest(pass);
		assertTrue("Test event firing", pass);
	}
}
