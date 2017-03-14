/**
*Class:             ToJSONFile.java
*Project:           AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    20/01/2016                                              
*Version:           1.0.1                                                      
*                                                                                   
*Purpose:           Basic interface to denote an object can be written to a .io.json
*					file and can be read from a .io.json file.
* 
*Update Log:		v1.0.1
*						- added method for converting .io.json from raw bytes
*					v1.0.0
*						- null
*/
package io.json;


public interface ToJSONFile
{
	//write
	public JsonFile toJSON(String baseOffset);
	
	//read
	public void fromJSON(String jsonFile) throws JsonException;
	
	//read
	public void fromJSON(byte[] jsonFile) throws JsonException;
}