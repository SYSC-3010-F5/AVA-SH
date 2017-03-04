/**
*Class:             PacketWrapper.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    04/03/2017                                              
*Version:           1.2.0                                         
*                                                                                   
*Purpose:           Generic wrapper class that shows the components of a packet
*					in an easy, ready-to-use form.
*					
* 
*Update Log			v1.2.0
*						- to and from JSON added
*						- fields made non-final and private for json init
*						- accessors made for source and type
*					v1.1.0
*						- toString for debugging added
*						- debug type support added
*						- type changed from int to byte
*					v1.0.0
*						- null
*/
package network;


//import libraries
import java.net.InetSocketAddress;
import java.time.DateTimeException;

//import packages
import io.json.*;


public class PacketWrapper implements ToJSONFile
{
	//declaring local instance variables
	private byte type;
	private String sField1;
	private String sField2;
	private InetSocketAddress source;
	
	
	//null constructor for json files
	public PacketWrapper()
	{
		this((byte)0,"","",null);
	}
	//generic constructor
	public PacketWrapper (byte type, String sField1, String sField2, InetSocketAddress source)
	{
		this.type = type;
		this.source = source;
		
		if(sField1 != null)
		{
			this.sField1 = sField1;
		}
		else
		{
			this.sField1 = "";
		}
		
		if(sField2 != null)
		{
			this.sField2 = sField2;
		}
		else
		{
			this.sField2 = "";
		}
	}
	
	
	//accessors
	public byte type()
	{
		return type;
	}
	public InetSocketAddress source()
	{
		return source;
	}
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
		if(source != null)
		{
			s += ", FROM: " + this.source.toString();
		}
		else
		{
			s += ", FROM: null";
		}
		return s;
	}


	@Override
	/*
	 * any '\n' characters in sField1 or sField2 are converted to '$'
	 * '$' characters are converted back into '\n' upon using the .fromJSON method
	 */
	public JsonFile toJSON(String baseOffset) 
	{
		//empty jsonfile plus prime block
		JsonFile json = new JsonFile(baseOffset);
		json.newBlock();
		
		//add information
		json.addField("type", type);
		json.addField("sField1", sField1.replaceAll("\n", "\\$"));
		json.addField("sField2", sField2.replaceAll("\n", "\\$"));
		json.addField("source", "null");							//source is not needed

		//end block and return
		json.endBlock();
		return json;
	}


	@Override
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
			
			//check for type field
			if (!fileLine[line].contains("\"type\":"))
			{
				throw new JsonException("\"type\" field not found", JsonException.ERR_BAD_FIELD);
			}
			//extract data from type field
			String type = fileLine[line].split(":")[1];
			try
			{
				this.type = (Byte.parseByte(type));
			}
			catch (NumberFormatException e)
			{
				throw new JsonException("type field must be a valid nummber between 0 and 255", JsonException.ERR_BAD_VALUE);
			}
			line++;
			
			//check for sField1 field
			if (!fileLine[line].contains("\"sField1\":"))
			{
				throw new JsonException("\"sField1\" field not found", JsonException.ERR_BAD_FIELD);
			}
			String tempString = fileLineSpace[line].split(":",2)[1];
			tempString = tempString.substring(tempString.indexOf("\"")+1, tempString.length()-1);
			sField1 = tempString.replaceAll("\\$", "\n");
			line++;
			
			//check for sField2 field
			if (!fileLine[line].contains("\"sField2\":"))
			{
				throw new JsonException("\"sField2\" field not found", JsonException.ERR_BAD_FIELD);
			}
			tempString = fileLineSpace[line].split(":",2)[1];
			tempString = tempString.substring(tempString.indexOf("\"")+1, tempString.length()-1);
			sField2 = tempString.replaceAll("\\$", "\n");
			line++;
			
			//check for source field
			if (!fileLine[line].contains("\"source\":"))
			{
				throw new JsonException("\"source\" field not found", JsonException.ERR_BAD_FIELD);
			}
			tempString = fileLine[line].split(":")[1].replaceAll("\"", "");
			if(tempString.equals("null"))
			{
				source = null;
			}
			else
			{
				throw new JsonException("\"source\" field as non-null not supported", JsonException.ERR_BAD_VALUE);
			}
		}
		catch (Exception e)
		{
			throw new JsonException("Unknown build error: \"" + e.getMessage() + "\"", JsonException.ERR_COULD_NOT_BUILD);
		}
	}


	@Override
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
}
