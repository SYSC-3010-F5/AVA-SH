/**
*Class:             ServerTimer.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven
*Date of Update:    12/03/2017
*Version:           1.0.0
*
*Purpose:           Specialized case of ServerEvent.
*					Triggered once to alarm user of something
*
*
* 
*Update Log			v1.1.0
*						- actually triggers an alarm now
*					v1.0.0
*						- null
*/
package server.datatypes;


//import externals
import java.util.Calendar;

//import packages
import network.PacketWrapper;
import server.Scheduler;




public class ServerTimer extends ServerEvent 
{
	//declaring instance variable
	private final long timeOfCreation;
	private int secondsUntilTrigger;
	private Scheduler scheduler;
	boolean executed;
	
	//generic constructor
	public ServerTimer(String eventName, int triggerDelaySec, Scheduler scheduler)
	{
		super(eventName, null, new TimeAndDate());		//because java has a fit if I call super() on not the first line
		this.commands = new PacketWrapper[]{
				new PacketWrapper(PacketWrapper.TYPE_CMD, "alarm on", "", null),
				new PacketWrapper(PacketWrapper.TYPE_INFO, eventName,"", null)};
		secondsUntilTrigger = triggerDelaySec;
		timeOfCreation = Calendar.getInstance().getTimeInMillis();
		this.scheduler = scheduler;
		executed = false;
	}
	
	
	//generic getters
	public long getTimeOfCreation()
	{
		return timeOfCreation;
	}
	public long getSecondsUntilTrigger()
	{
		return secondsUntilTrigger;
	}
	
	
	@Override
	public void run()
	{
		super.run();
		executed = true;
		if(scheduler != null)
		{
			scheduler.removeNonPeriodic(this.eventName);
		}
	}
	
	
	@Override
	//marks the ServerEvent to not trigger
	public boolean cancel()
	{
		if(executed)
		{
			return super.cancel(false);
		}
		return super.cancel(true);
	}
	
	
	@Override
	//string representation
	public String toString()
	{
		//compute time until trigger
		int[] time = {0,0,0};
		long timeToTrig = (timeOfCreation + secondsUntilTrigger*1000) - Calendar.getInstance().getTimeInMillis();
		String printTime;
		if(timeToTrig >= 1000)
		{
			int triggerTime = (int)timeToTrig/1000;
			time[0] = triggerTime/(60*60);
			time[1] = (triggerTime%(60*60))/60;
			time[2] = (triggerTime%(60*60))%60;
			printTime = time[0]+":"+time[1]+":"+time[2];
		}
		else
		{
			printTime = "0:0:0";
		}
		String s = "\"" + eventName + "\" in " + printTime;
		return s;
	}
}
