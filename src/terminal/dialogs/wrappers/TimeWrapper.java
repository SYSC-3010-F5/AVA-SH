/**
*Class:             TimeWrapper.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven
*Date of Update:    03/04/2017
*Version:           1.0.0
*
*Purpose:           Simple wrapper containing a String and some int
*
*
*Update Log			v1.0.0
*						- null
*/
package terminal.dialogs.wrappers;

public class TimeWrapper
{
	//declaring local instance variables
	public final int hours;
	public final int minutes;
	public final int seconds;
	public final String name;
	
	
	//generic constructors
	public TimeWrapper(int[] hhmmss, String name)
	{
		this(hhmmss[2], hhmmss[1], hhmmss[0], name);
	}
	public TimeWrapper(int seconds, int minutes, int hours, String name)
	{
		this.seconds = seconds;
		this.minutes = minutes;
		this.hours = hours;
		this.name = name;
	}
	
	
	//get time in seconds
	public int getTimeInSec()
	{
		return ((hours*60*60) + (minutes*60) + seconds);
	}
	
	
	@Override
	//toString
	public String toString()
	{
		return ("Name: " + name + ", Time(hh:mm:ss): " + hours + ":" + minutes + ":" + seconds);
	}
}
