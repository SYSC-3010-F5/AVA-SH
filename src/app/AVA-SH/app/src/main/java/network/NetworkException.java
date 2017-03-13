/**
*Class:             NetworkException.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    04/02/2017                                              
*Version:           1.0.0                                         
*                                                                                   
*Purpose:           Generic home-grown organic exception, nothing fancy.
*					Ripped out of NodeVisualizer project
*					
* 
*Update Log			v1.0.0
*						- null
*/
package network;

public class NetworkException extends Exception
{
	//declaring local instance variables
	public final boolean critical;
	
	
	//generic constructor
	public NetworkException(String msg, boolean critical)
	{
		super(msg);
		this.critical = critical;
	}
	//shorthand constructor
	public NetworkException(String msg)
	{
		this(msg, false);
	}
}
