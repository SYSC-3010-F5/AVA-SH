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
	public final String title;
	public final String msg;
	public final boolean critical;
	
	
	//generic constructor
	public NetworkException(String msg, String title, boolean critical)
	{
		super();
		this.title = title;
		this.msg = msg;					//use final instance var instead of getMessage() from super to keep all accessing of class fields the same
		this.critical = critical;
	}
	//shorthand constructor
	public NetworkException(String msg, String title)
	{
		this(msg, title, false);
	}
}
