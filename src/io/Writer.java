/**
*Class:             Writer.java
*Project:           AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    23/01/2017                                              
*Version:           1.0.0                                         
*                                                                                   
*Purpose:           Saves a string or byte array to a file.
* 
* 
*Update Log			v1.0.0
*						- structured as completely static class
*/
package io;


//imports
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public abstract class Writer extends BasicIO
{
	//write a string to a particular location on the disk
	public static void write(String data, File file) throws IOException
	{
		Writer.write(data.getBytes(), file);
	}
	
	
	//write a string to the disk, get location
	public static void write(String data) throws IOException 
	{
		Writer.write(data.getBytes());
	}
	
	
	//write raw bytes to the disk
	public static void write(byte[] data) throws IOException 
	{
		//get directory path and write to
		File file = Writer.getFile("Please select a file");
		Writer.write(data, file);
	}
	
	
	//write to a file on the disk
	public static void write(byte[] data, File file) throws IOException
	{
		//check for valid file
		if(file != null)
		{
			//prep datastream for output
			FileOutputStream output = new FileOutputStream(file,false);
			
			//write
			output.write(data);
			
			//close stream
			output.close();
		}
	}
}
