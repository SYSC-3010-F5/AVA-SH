/**
*Class:             ToJSONFile.java
*Project:           AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    20/01/2016                                              
*Version:           1.1.0                                                      
*                                                                                   
*Purpose:           Helper class to assemble a .io.json file as a String
*					Basically a controller to help structure a .io.json file.
* 
*Update Log:		v1.1.1
*						- added field for boolean array
*					v1.1.0
*						- minor fix with bracket positioning
*						- added method to add field --> addField(...)
*						- tab support added
*						- export as byte array added
*						- new method to support non-quote additions in form --> [i, "string"]
*					v1.0.0
*						- null
*/

package io.json;

public class JsonFile
{
	//declaring local instance variables
	private String file;
	private String offset;
	private final String base;
	
	
	//constructor given # of tabs for offset
	public JsonFile(int tabOffset)
	{
		//compute offset and invoke generic constructor
		this(computeOffset(tabOffset));
	}
	
	
	//generic constructor
	public JsonFile(String startingOffset)
	{
		file = "";
		offset = "";
		if (startingOffset == null)
		{
			startingOffset = "";
		}
		base = startingOffset;
	}
	
	
	//helper function for constructor, return empty string if no offset
	private static String computeOffset(int tabOffset)
	{
		String startingOffset = "";
		for (int i=0; i < tabOffset; i++)
		{
			startingOffset += "\t";
		}
		return startingOffset;
	}
	
	
	//generic accessors
	public String getNetOffset()
	{
		return (base + offset);
	}
	public String getOffset()
	{
		return (offset + "");
	}
	public String getBase()
	{
		return (base + "");
	}
	
	
	//add a named field to current block, field is of type String
	//fieldValue = "" to denote the value will be in a new block
	public void addField(String fieldName, String fieldValue)
	{
		if (fieldValue != "")
		{
			fieldValue ="\"" + fieldValue +"\"\n";
		}

		this.add("\"" + fieldName + "\" : " + fieldValue);
	}
	
	
	//add a field for an array of booleans
	public void addField(String fieldName, boolean[] fieldValues)
	{
		String string = "\"" + fieldName + "\" : [";
		for(int i=0; i<fieldValues.length; i++)
		{
			string += fieldValues[i];
			if (i != fieldValues.length-1)
			{
				string += ", ";
			}
		}
		this.add(string+"]\n");
	}
	
	
	//add a integer field into current block
	public void addField(String fieldName, int fieldValue)
	{
		this.add("\"" + fieldName + "\" : " + fieldValue + "\n");
	}
	
	
	//add a boolean field into current block
	public void addField(String fieldName, boolean fieldValue)
	{
		this.add("\"" + fieldName + "\" : " + fieldValue + "\n");
	}
	
	
	//add an integer-string element pair as a line
	public void addPairing(int numbericID, String value)
	{
		this.add(numbericID + ", \"" + value + "\"\n");
	}
	
	
	//uncontrolled add. Use for adding another pre-formatted .io.json file
	public void addField(String fieldName, JsonFile subFile)
	{
		this.add("\"" + fieldName + "\" : " + subFile.toString());
	}
	
	
	//controlled raw new line add
	public void add(String toAdd)
	{
		file += base + offset + toAdd;
	}
	
	
	//add a cosmetic spacer
	public void addln()
	{
		file += "\n";
	}
	
	
	//uncontrolled raw concatination
	public void addRaw(String toAdd)
	{
		file += toAdd;
	}
	
	
	//new block
	public void newBlock()
	{
		file += "{\n";
		offset += "\t";
	}
	
	
	//new block, but with field name
	public void newBlock(String fieldName)
	{
		file += "\"" + fieldName + "\" : {\n";
		offset += "\t";
	}
	
	
	//end block
	public void endBlock()
	{
		offset = offset.replaceFirst("\t", "");
		this.add("}\n");
	}
	
	
	//return the JsonFile as a raw array of bytes
	public byte[] toByteArray()
	{
		return file.getBytes();
	}
	
	
	@Override
	//return the JsonFile as a plain String
	public String toString()
	{
		return file;
	}
}
