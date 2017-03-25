/**
*Class:             Scheduler.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    23/03/2017                                              
*Version:           1.0.1                                         
*                                                                                   
*Purpose:           Real time is hard.
*					
* 
*Update Log			v1.0.1
*						- periodic events are now only added once to the list of periodic events
*						  (fixes bug with printing)
*					v1.0.0
*						- Periodic scheduling working
*					v0.2.2
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
	//returns -1 if the given trigger doesn't have a trigger day (i.e. there are no days set to true)
	public long computeDelay(TimeAndDate trigger)
	{
		boolean[] days = trigger.getDays();
		
		//get the day of the trigger
		int i = 0;
		boolean noTrigger = true;
		for(;i < 7; i++)
		{
			if(days[i])
			{
				noTrigger = false;
				break;
			}
			
		}
		
		//ensure that the TimeAndDate given actually has a trigger day
		if(noTrigger)
		{
			//no trigger day given, return -1 as no delay can be calculated
			return -1;
		}
		//get 2 Calendar objects set to the current time and time zone
		Calendar now = Calendar.getInstance();
		Calendar triggerDate = Calendar.getInstance();
		
		//Set the day of the trigger date. Calendars use SUNDAY = 1, MONDAY = 2, TUESDAY = 3 etc.
		triggerDate.set(Calendar.DAY_OF_WEEK, i+1);
		triggerDate.set(Calendar.WEEK_OF_MONTH, now.get(Calendar.WEEK_OF_MONTH));
		//this sets the week of the month to today's week of the month.
		//this means that if today is friday, and the trigger date is a sunday, the computed delay will end up being negative
		//checking if the delay is negative and adding the milliseconds in a week should fix this, which is done lower down
		
		//Set the hour, minute, second and millisecond of the trigger
		triggerDate.set(Calendar.MILLISECOND, 0);
		triggerDate.set(Calendar.SECOND, 0);
		triggerDate.set(Calendar.MINUTE, trigger.getMinute());
		triggerDate.set(Calendar.HOUR_OF_DAY, trigger.getHour());
		
		//find the difference in milliseconds
		long delay = triggerDate.getTimeInMillis() - now.getTimeInMillis();
		
		//if the delay is negative, then that means the Calendar object was set to the weekday before, not the weekday after today
		if(delay < 0)
		{
			//add the period of a week to advance to the weekday after today
			delay += MS_WEEK;
		}
		return delay;
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
				long delay = computeDelay(trigger);
				
				//due to the logic in computeDelay, the delay returned will be to the next Sunday, at the trigger time
				//to reduce the delay to the next trigger time (which could be either today or tomorrow if the time has passed), take the delay modulus the period of 1 day
				
				delay = delay % MS_DAY;
				scheduler.scheduleAtFixedRate(event, delay, MS_DAY);
				periodicEvents.add(event);
				return true;
			}
			//event occurs on less than 7 days a week
			else
			{
				for(int i = 0; i < 7; i++)
				{
					if(days[i])
					{
						ServerEvent scheduleEvent = new ServerEvent(event.getEventName(), event.getCommands(), event.getTrigger());
						boolean[] triggerDay = new boolean[]{false,false,false,false,false,false,false};
						triggerDay[i] = true;
						TimeAndDate trigger = scheduleEvent.getTrigger();
						trigger.setDays(triggerDay);
						scheduler.scheduleAtFixedRate(scheduleEvent, computeDelay(trigger), MS_WEEK);
					}
					periodicEvents.add(event);
				}
				return true;
			}
		}
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
