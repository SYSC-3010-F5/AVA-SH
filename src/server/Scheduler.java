/**
*Class:             Scheduler.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    12/03/2017                                              
*Version:           0.1.0                                         
*                                                                                   
*Purpose:           Real time is hard.
*					
* 
*Update Log			v0.2.2
*						- timers added using standard schedule method
*						- timers now use subclass of ServerEvent <|--- ServerTimer
*						- Scheduler instances now have special Event that runs every 30 seconds to
*						  run a purge
*					v0.2.1
*						- add timer modified so no duplicate names can exist
*					v0.2.0
*						- lists added to store all active events
*						- method for removing events implemented (based on event name)
*						- add timer method tweaked
*						- some generic accessors
*						- method to clear scheduler added
*					v0.1.0
*						- added one timer timer event
*						- added helper method to determine if event will occur later on todays date
*						- constructor, fields, and other setup stuff
*/
package server;


//import externals
import java.util.Timer;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

//import packages
import server.datatypes.ServerEvent;
import server.datatypes.ServerTimer;
import server.datatypes.TimeAndDate;



public class Scheduler
{
	//declaring static class constants
	public static final int MS_MIN =	1000*60;			// (1000ms/s)(60s/min)
	public static final int MS_DAY =	MS_MIN*60*24;		// (1000ms/s)(60s/min)(60min/hr)(24hr/day)
	public static final int MS_WEEK =	MS_DAY*7;			// (1000ms/s)(60s/min)(60min/hr)(24hr/day)(7day/week)
	public static final int DAY_SUN = Calendar.SUNDAY;
	public static final int DAY_MON = Calendar.MONDAY;
	public static final int DAY_TUE = Calendar.TUESDAY;
	public static final int DAY_WED = Calendar.WEDNESDAY;
	public static final int DAY_THU = Calendar.THURSDAY;
	public static final int DAY_FRI = Calendar.FRIDAY;
	public static final int DAY_SATURDAY = Calendar.SATURDAY;
	
	//declaring local instance variables
	public final String name;
	private Timer scheduler;
	private ArrayList<ServerEvent> periodicEvents;
	private ArrayList<ServerEvent> nonPeriodicEvents;
	
	
	//generic constructor
	public Scheduler(String name)
	{
		//create Timer thread as daemon thread
		scheduler = new Timer(name, true);
		this.name = name;
		
		//init
		periodicEvents = new ArrayList<ServerEvent>();
		nonPeriodicEvents = new ArrayList<ServerEvent>();
	}
	
	
	//generic accessors
	public ArrayList<ServerEvent> getPeriodicEvents()
	{
		return periodicEvents;
	}
	public ArrayList<ServerEvent> getNonPeriodicEvents()
	{
		return nonPeriodicEvents;
	}
	
	
	//clear all scheduled events
	public void clearAll()
	{
		scheduler.cancel();
		scheduler = new Timer(name, true);
		periodicEvents.clear();
		nonPeriodicEvents.clear();
	}
	
	//get current day of the week
	private int getTodaysDate()
	{
		return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
	}
	
	
	//return current time as an array of integers such that [hours, minutes, seconds]
	private int[] getCurrentTime() 
	{
	    String timeString = new SimpleDateFormat("HH:mm:ss").format(new Date());
	    String[] arr = timeString.split(":");
	    int[] time = new int[3];
	    for(int i=0; i<3; i++)
	    {
	    	time[i] = Integer.parseInt(arr[i]);
	    }
	    return time;
	}
	
	
	//return if hh:mm will occur later today
	public boolean occursLaterToday(int hour, int minute)
	{
		//check preconditions
		if(hour > 23 || hour < 0 || minute > 59 || minute < 0)
		{
			throw new DateTimeException("hour or minute parameter out of range");
		}
		
		//get current time and time at hour:minute
		Calendar trigger = Calendar.getInstance();
		trigger.set(
				Calendar.getInstance().get(Calendar.YEAR),
				Calendar.getInstance().get(Calendar.MONTH),
				Calendar.getInstance().get(Calendar.DATE),
				hour,
				minute,
				0);
		Calendar current = Calendar.getInstance();
		
		//check if trigger occurs at a later time than current
		return (current.getTimeInMillis() < trigger.getTimeInMillis());
	}
	
	
	//calculate the amount of milliseconds until a certain date-time
	public long computeDelay(TimeAndDate trigger)
	{
		return 5;
	}
	
	
	//schedule event to occur using internal information
	public boolean schedule(ServerEvent event)
	{
		if(event instanceof ServerTimer)
		{
			ServerTimer timerEvent = (ServerTimer)event;
			
			//check if timer with name already exists
			String eventName = timerEvent.getEventName();
			for(ServerEvent liveEvent : nonPeriodicEvents)
			{
				if(liveEvent.getEventName().equals(eventName))
				{
					return false;
				}
			}
			//schedule timer
			nonPeriodicEvents.add(event);
			scheduler.schedule(timerEvent, timerEvent.getSecondsUntilTrigger()*1000);
			return true;
		}
		else
		{
			//determine if the event will occur daily
			boolean[] days = event.getTrigger().getDays();
			boolean dailyEvent = true;
			for(boolean day : days)
			{
				dailyEvent = dailyEvent && day;
			}
			
			//special case, daily event
			if(dailyEvent)
			{
				TimeAndDate trigger = event.getTrigger();
				int[] currentTime = this.getCurrentTime();
				boolean today = false;
			}
			//event occurs on less than 7 days a week
			else
			{
				
			}
		}
		return false;//TODO Remove
	}
	
	
	//remove from a list of ServerEvents based on ServerEvent.name
	private boolean remove(ArrayList<ServerEvent> events, String toRemove)
	{
		for(ServerEvent event : events)
		{
			if(event.getEventName().equals(toRemove))
			{
				//remove event from list, mark event to not run, purge from scheduler
				events.remove(event);
				event.cancel();
				scheduler.purge();				//allows garbage collection to remove event, time of n+log(n)
				return true;
			}
		}
		return false;
	}
	
	
	//remove a single occurrence event (like a timer), based on event name
	public boolean removeNonPeriodic(String eventName)
	{
		return remove(nonPeriodicEvents, eventName);
	}
	
	
	//remove a periodic event (like an alarm), based on event name
	public boolean removePeriodic(String eventName)
	{
		return remove(periodicEvents, eventName);
	}
	
}
