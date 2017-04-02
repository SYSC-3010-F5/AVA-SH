/**
*Class:             SettingsWrapper.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven
*Date of Update:    02/04/2017
*Version:           1.0.0
*
*Purpose:           Simple wrapper containing InetAddress, int, and string
*
*
*Update Log			v1.0.0
*						- null
*/
package terminal.dialogs.wrappers;


//import libraries
import java.net.InetAddress;



public class SettingsWrapper
{
	//local instance constants
	public final InetAddress address;
	public final int port;
	public final String name;
	public final int closeMode;
	
	
	//generic constructor
	public SettingsWrapper(InetAddress address, int port, String name, int closeMode)
	{
		//init
		this.address = address;
		this.port = port;
		this.name = name;
		this.closeMode = closeMode;
	}
	
	
	@Override
	//mostly for debug
	public String toString()
	{
		String[] params = new String[2];
		
		if(address == null)		params[0] = "null";
		else					params[0] = address.toString();
		
		if(name == null)		params[1] = "null";
		else					params[1] = name;
		
		return ("address: " + params[0] + ", port: " + port + ", name:" + params[1]);
	}
}
