/**
*Class:             ComsProtocol.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    20/02/2017                                              
*Version:           2.0.0                                         
*                                                                                   
*Purpose:           Outlines what anything following the outlined Coms Protocol
*					must be able to do.
*					
* 
*Update Log			v2.0.0
*						- revised version
*					v1.0.0
*						- null
*/
package network;


//import packages
import java.net.InetAddress;
import io.json.JsonFile;



public interface ComsProtocol 
{
	//receive
	public PacketWrapper receivePacket() throws NetworkException;
	
	//contact paired connection via handshake protocol
	public void sendHandshake(InetAddress toPair, int listeningPort, String deviceName) throws NetworkException;
	
	//responds to a handshake, finalizing the connection
	public void respondHandshake(InetAddress toPair, int listeningPort) throws NetworkException;

	//send a command to the server
	public void sendCmd(String cmdKey) throws NetworkException;		//send with empty EXTRA_INFO field
	public void sendCmd(String cmdKey, String extraInfo) throws NetworkException;
	public void sendCmd(String cmdKey, JsonFile extraInfo) throws NetworkException;
	public void sendCmd(byte[] cmdKey, byte[] extraInfo)throws NetworkException;
	
	//send an info packet to the server
	public void sendInfo(JsonFile info) throws NetworkException;
	public void sendInfo(String info) throws NetworkException;
	public void sendInfo(byte[] info) throws NetworkException;
	
	//send an error packet to the server
	public void sendErr(String errMsg) throws NetworkException;
	public void sendErr(byte[] errMsg) throws NetworkException;
}
