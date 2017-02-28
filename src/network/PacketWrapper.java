/**
*Class:             PacketWrapper.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    22/02/2017                                              
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


//import libraries
import java.net.InetSocketAddress;




public class PacketWrapper
{
	//declaring local instance variables
	public final byte type;
	private final String sField1;
	private final String sField2;
	public final InetSocketAddress source;
	
	
	//generic constructor
	public PacketWrapper (byte type, String sField1, String sField2, InetSocketAddress source)
	{
		this.type = type;
		this.sField1 = sField1;
		this.sField2 = sField2;
		this.source = source;
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
	public String disconnectMessage()
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
				s += "HANDSHAKE_KEY: <" + this.handshakeKey() + ">, DEVICE_NAME: <" + this.deviceName() + ">";
				break;
			case(DataChannel.TYPE_CMD):
				s += "CMD_KEY: <" + this.commandKey() + "> EXTRA_INFO: <" + this.extraInfo() + ">";
				break;
			case(DataChannel.TYPE_INFO):
				s += "INFO: <" + this.info() + ">";
				break;
			case(DataChannel.TYPE_ERR):
				s += "ERR_MSG: <" + this.errorMessage() + ">";
				break;
			case(DataChannel.TYPE_DISCONNECT):
				s += "DISCONNECT_MSG: <" + this.disconnectMessage() + ">";
				break;
			default:
				s += "FIELD1: <" + this.sField1 + "> FIELD2: <" + this.sField2 + ">";
				break;
		}
		s += ", FROM: " + this.source.toString();
		return s;
	}
}
