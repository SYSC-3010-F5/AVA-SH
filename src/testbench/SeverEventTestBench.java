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
		printTest(pass);
		assertTrue("Test event firing", pass);
	}
}
