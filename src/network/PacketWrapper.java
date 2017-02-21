/**
*Class:             PacketWrapper.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    21/02/2017                                              
*Version:           1.1.0                                         
*                                                                                   
*Purpose:           Generic wrapper class that shows the components of a packet
*					in an easy, ready-to-use form.
*					
* 
*Update Log			v1.1.0
*						- toString for debugging added
*						- type changed from int to byte
*					v1.0.0
*						- null
*/
package network;



public class PacketWrapper
{
	//declaring local instance variables
	public final byte type;
	private final String sField1;
	private final String sField2;
	
	
	//generic constructor
	public PacketWrapper (byte type, String sField1, String sField2)
	{
		this.type = type;
		this.sField1 = sField1;
		this.sField2 = sField2;
	}
	
	
	//accessors
	public String handshakeKey()
	{
		return sField1;
	}
	public String deviceName()
	{
		return sField2;
	}
	public String commandKey()
	{
		return sField1;
	}
	public String extraInfo()
	{
		return sField2;
	}
	public String info()
	{
		return sField1;
	}
	public String errorMessage()
	{
		return sField1;
	}
	
	
	//toString method for debug
	@Override
	public String toString()
	{
		String s = "TYPE: " + "0x" + (String.format("%02x", type)).toUpperCase() + ", ";
		switch(type)
		{
			case(DataChannel.TYPE_HANDSHAKE):
				return(s += "HANDSHAKE_KEY: <" + this.handshakeKey() + ">, DEVICE_NAME: <" + this.commandKey() + ">");
			case(DataChannel.TYPE_CMD):
				return(s += "CMD_KEY: <" + this.commandKey() + "> EXTRA_INFO: <" + this.extraInfo() + ">");
			case(DataChannel.TYPE_INFO):
				return(s += "INFO: <" + this.info() + ">");
			case(DataChannel.TYPE_ERR):
				return(s += "ERR_MSG: <" + this.errorMessage() + ">");
			default:
				return(s += "FIELD1: <" + this.sField1 + "> FIELD2: <" + this.sField2 + ">");
		}
	}
}
