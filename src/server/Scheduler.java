/**
*Class:             Scheduler.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    06/03/2017                                              
*Version:           0.1.0                                         
*                                                                                   
*Purpose:           Real time is hard.
*					
* 
*Update Log			v1.0.0
*						- null
*/
package server;


//import externals
import java.util.Timer;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.Calendar;
import java.util.Date;

//import packages
import server.datatypes.ServerEvent;
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
	
	
	//generic constructor
	public Scheduler(String name)
	{
		//create Timer thread as daemon thread
		scheduler = new Timer(name, true);
		this.name = name;
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
	
	
	/* schedule a single event to occur after a delay (like a timer)
	 * ignores any internal time information in event
	 */
	public void scheduleTimer(ServerEvent event, int secondsUntilTrigger)
	{
		scheduler.schedule(event, secondsUntilTrigger*1000);
	}
	
	
	//schedule event to occur using internal information
	public void schedule(ServerEvent event)
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
		
		//determine if event to occur later today, or on a new day
		Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
	}
}
