/**
*Class:             Alarm.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    23/03/2017                                              
*Version:           2.0.0                                         
*                                                                                   
*Purpose:           Save information on basic alarm.
*					Occurs once per day, at a certain time.
*					
* 
*Update Log			v2.0.0
*						- rewritten
*						- most logic moved into superclass ServerEvent
*						- Alarm now just contains pre-set commands to trigger at ServerEvent
*					v1.0.1
*						- toString method added for debugging (quicker to read than the JSON!)
*					v1.0.0
*						- getters/setter added
*						- each getter checks values to make sure preconditions met
*						- DateTimeException high-jacked for exception type
*						- toJSON functionality added
*						- fromJSON functionality added
*/
package server.datatypes;


//external imports
import java.time.DateTimeException;

//import packages
import network.PacketWrapper;
import io.json.JsonException;
import io.json.JsonFile;



public class Alarm extends ServerEvent
{
	//empty constructor
	public Alarm()
	{
		this("", null);
	}
	//genetic constructor
	public Alarm(String eventName, TimeAndDate trigger)
	{
		super(eventName, null, trigger);		//because java has a fit if I call super() on not the first line
		this.commands = generateCommands(eventName);
	}
	
	
	//get commands
	private PacketWrapper[] generateCommands(String eventName)
	{
		return new PacketWrapper[]{
				new PacketWrapper(PacketWrapper.TYPE_CMD, "alarm on", "", null),
				new PacketWrapper(PacketWrapper.TYPE_INFO, eventName,"", null)};
	}
}
