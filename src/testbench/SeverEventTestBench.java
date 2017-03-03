/**
*Class:             TestBench.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    02/03/2017                                              
*Version:           1.0.0                                         
*                                                                                   
*Purpose:           Master test bench all other test benchs should inherit from.
*					Makes use of JUnitTests for regression tests, as well as printing out detailed 
*					information for manual debugging of programs and detailed test information.
*					More or less a collection of common methods.
*					
* 
*Update Log			v1.0.1
*						- added method for user input (manual testing)
*					v1.0.0
*						- some methods added
*/
package testbench;


//import externals
import java.net.SocketException;
import java.net.UnknownHostException;

import network.DataChannel;
import network.PacketWrapper;
import server.MainServer;
import server.ServerEvent;
import server.datatypes.Alarm;

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
		
		println("Creating event...");
		PacketWrapper[] commands = new PacketWrapper[3];
		commands[0] = new PacketWrapper(DataChannel.TYPE_CMD, "ping", "", null);
		commands[1] = new PacketWrapper(DataChannel.TYPE_CMD, "req time", "", null);
		commands[2] = new PacketWrapper(DataChannel.TYPE_CMD, "ping", "", null);
		event = new ServerEvent(commands);
		println("Event created. At trigger daemon thread fires packets as:\n"
				+commands[0].toString()+"\n"
				+commands[1].toString()+"\n"
				+commands[2].toString()+"\n");
		
		println("Creating server to respond to commands...");
		MainServer server = new MainServer();
		server.start();
		println("Server created on new thread!");
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
