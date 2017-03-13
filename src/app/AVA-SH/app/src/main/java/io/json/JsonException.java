/**
*Class:             JsonException.java
*Project:           AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    28/12/2016                                              
*Version:           1.0.0                                                      
*                                                                                   
*Purpose:           Generic exception
* 
*Update Log:		v1.0.1
*						- toString method fixed
*					v1.0.0
*						- null
*/
package io.json;


@SuppressWarnings("serial")
public class JsonException extends Exception 
{
	//declaring static class constants
	public static final int ERR_FORMAT = 0;
	public static final int ERR_BAD_FIELD = 1;
	public static final int ERR_BAD_VALUE = 2;
	public static final int ERR_COULD_NOT_BUILD = 3;
	
	//declaring local instance variables
	private final String title;
	private final int errorCode;
	
	
	//no-title constructor
	public JsonException(String errorMsg, int errorCode)
	{
		this(errorMsg, "JsonException", errorCode);
	}
	
	//generic constructor
	public JsonException(String errorMsg, String title, int errorCode)
	{
		super(errorMsg);
		this.title = title;
		this.errorCode = errorCode;
	}
	
	
	//return a string representation
	@Override
	public String toString()
	{
		return ("Error Code: " + errorCode + ", msg = \"" + this.getMessage() + "\", title = \"" + title + "\"");
	}
}
