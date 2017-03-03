package server;


//import externals
import java.util.Timer;



public class DayScheduler 
{
	//declaring static class constants
	public static final int PERIOD = 1000*60*60*24;		// (1000ms/s)(60s/min)(60min/hr)(24hr/day)
	
	//declaring local instance variables
	public final String name;
	private Timer scheduler;
	
	
	//generic constructor
	public DayScheduler(String name)
	{
		//create Timer thread as daemon thread
		scheduler = new Timer(name, true);
		this.name = name;
	}
	
	
	
}
