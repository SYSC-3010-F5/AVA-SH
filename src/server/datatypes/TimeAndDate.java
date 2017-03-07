/**
*Class:             TimeAndDate.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven
*Date of Update:    02/04/2017
*Version:           1.0.1
*
*Purpose:           Holds information regarding a single time (hh:mm) for a ServerEvent to trigger at.
*					Also includes a list of days to trigger to event on.
*
*					All logic ripped from server.datatypes.Alarm
*
*
* 
*Update Log			v1.1.0
*						- restructured from Alarm.java into TimeAndDate.java
*						- name field removed -- to/from JSON rewritten accordingly
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

import java.time.DateTimeException;

import io.json.JsonException;
import io.json.JsonFile;
import io.json.ToJSONFile;

public class TimeAndDate implements ToJSONFile
{
	//declaring static class constants
	public static final String[] DAY_NAMES_FULL = { "Sunday",
													"Monday",
													"Tuesday",
													"Wednesday",
													"Thursday",
													"Friday",
													"Saturday"};
	public static final String[] DAY_NAMES_SHORT = {"sun",
													"mon",
													"tue",
													"wed",
													"thu",
													"fri",
													"sat"};
	
	//declaring local instance variables
	private int hour;
	private int minute;
	private boolean[] days;
	
	//empty constructor (useful for testing)
	public TimeAndDate()
	{
		this(0, 0, new boolean[]{false,false,false,false,false,false,false});
	}
	
	//generic constructor
	public TimeAndDate(int hour, int minute, boolean[] days) throws DateTimeException
	{
		setMinute(minute);
		setHour(hour);
		setDays(days);
	}
	
	
	//controlled set for minute
	public void setMinute(int minute) throws DateTimeException
	{
		//check validity
		if (minute < 0 || minute > 59)
		{
			throw new DateTimeException("Minute must be within range of 0 to 59 (inclusive)");
		}
		this.minute = minute;
	}
	
	
	//controlled set for hour
	public void setHour(int hour) throws DateTimeException
	{
		//check validity
		if (hour < 0 || hour > 23)
		{
			throw new DateTimeException("Hour must be within range of 0 to 23 (inclusive)");
		}
		this.hour = hour;
	}
	
	
	//controlled set for days
	public void setDays(boolean[] days) throws DateTimeException
	{
		//check validity
		if (days == null)
		{
			throw new DateTimeException("Days must have a value for each of the 7 days, starting at Monday");
		}
		else
		{
			if(days.length != 7)
			{
				throw new DateTimeException("Days must have a value for each of the 7 days, starting at Monday");
			}
		}
		this.days = days;
	}
	
	
	//generic accessors
	public int getMinute()
	{
		return minute;
	}
	public int getHour()
	{
		return hour;
	}
	public boolean[] getDays()
	{
		return days;
	}
	
	
	@Override
	//convert into json formated
	public JsonFile toJSON(String baseOffset) 
	{
		//create empty json file and start prime block
		JsonFile json = new JsonFile(baseOffset);
		json.newBlock();
		
		//add info
		json.addField("hour", hour);
		json.addField("minute", minute);
		json.addField("days", days);
		
		//end prime block and return
		json.endBlock();
		return json;
	}
	
	
	@Override
	//create new alarm from json
	public void fromJSON(String jsonFile) throws JsonException 
	{
		try
		{
			//declaring method variables
			int line = 0;
			
			/*split at newlines, remove all tabs, remove spaces
			 * keep one copy of tab-space filtered, separated at \n		fileLine
			 * keep one copy of tab filtered, separated at \n			fileLineSpace
			 */
			String intermediate = jsonFile.replaceAll("\t", "");
			String[] fileLineSpace = intermediate.split("\n");
			intermediate = intermediate.replaceAll(" ", "");
			String[] fileLine = intermediate.split("\n");
			intermediate = null;
			
			//make sure there is a starting block
			if (!fileLine[line].equals("{"))
			{
				throw new JsonException("No starting block", JsonException.ERR_FORMAT);
			}
			line++;
			
			//check for hour field
			if (!fileLine[line].contains("\"hour\":"))
			{
				throw new JsonException("\"hour\" field not found", JsonException.ERR_BAD_FIELD);
			}
			//extract data from hour field
			String hours = fileLine[line].split(":")[1];
			try
			{
				this.setHour(Integer.parseInt(hours));
			}
			catch (NumberFormatException e)
			{
				throw new JsonException("hour field must be a valid 32bit integer", JsonException.ERR_BAD_VALUE);
			}
			catch (DateTimeException e)
			{
				throw new JsonException(e.getMessage(), JsonException.ERR_BAD_VALUE);
			}
			line++;
			
			//check for minute field
			if (!fileLine[line].contains("\"minute\":"))
			{
				throw new JsonException("\"minute\" field not found", JsonException.ERR_BAD_FIELD);
			}
			//extract data from minute field
			String minute = fileLine[line].split(":")[1];
			try
			{
				this.setMinute(Integer.parseInt(minute));
			}
			catch (NumberFormatException e)
			{
				throw new JsonException("minute field must be a valid 32bit integer", JsonException.ERR_BAD_VALUE);
			}
			catch (DateTimeException e)
			{
				throw new JsonException(e.getMessage(), JsonException.ERR_BAD_VALUE);
			}
			line++;
			
			//check for days field
			if (!fileLine[line].contains("\"days\""))
			{
				throw new JsonException("\"days\" field not found", JsonException.ERR_BAD_FIELD);
			}
			//check for format on array in field
			String arrayString = fileLine[line].split(":")[1];
			if(!arrayString.contains("[") || !arrayString.contains("]"))
			{
				throw new JsonException("Invalid format on \"days\" array", JsonException.ERR_BAD_VALUE);
			}
			arrayString = arrayString.replaceAll("\\[|\\]", "");
			//extract data from field
			boolean[] days = new boolean[7];
			String[] elements = arrayString.split(",");
			if(elements.length != 7)
			{
				throw new JsonException("\"days\" array must contain exactly 7 elements", JsonException.ERR_BAD_VALUE);
			}
			for(int i=0; i<7; i++)
			{
				switch(elements[i])
				{
					case("true"):
						days[i] = true;
						break;
					case("false"):
						days[i] = false;
						break;
					default:
						throw new JsonException("array elements must be of type boolean", JsonException.ERR_BAD_VALUE);
				}
			}
			this.setDays(days);
			line++;
			
			//check for terminating block
			if(!fileLine[line].equals("}"))
			{
				throw new JsonException("File does not terminate with \"}\"", JsonException.ERR_FORMAT);
			}
		}
		catch (Exception e)
		{
			throw new JsonException("Unknown build error: \"" + e.getMessage() + "\"", JsonException.ERR_COULD_NOT_BUILD);
		}
	}
	
	
	@Override
	//create new alarm from json
	public void fromJSON(byte[] jsonFile) throws JsonException 
	{
		/* TODO there must be a better way to do this
		 * using char array so I don't need to make a new string each char add (each string is immutable)
		 */

		//cast each byte as a char
		char[] brokenString = new char[jsonFile.length];
		for(int i=0; i < jsonFile.length; i++)
		{
			brokenString[i] = (char)jsonFile[i];
		}
		
		//generate object from json file, now in String form
		fromJSON(new String(brokenString));
	}
	
	
	@Override
	//show as a string
	public String toString()
	{
		String string = hour + ":" + minute + " on [";
		boolean first = true;
		for(int i=0; i<days.length; i++)
		{
			if(days[i])
			{
				if(first)
				{
					first = false;
				}
				else
				{
					string += ", ";
				}
				string += DAY_NAMES_SHORT[i];
			}
		}
		return (string + "]");
	}
}
