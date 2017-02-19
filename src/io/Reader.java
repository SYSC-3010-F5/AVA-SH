/**
*Class:             Reader.java
*Project:           AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    24/01/2017                                              
*Version:           1.0.1                                         
*                                                                                   
*Purpose:           Reads the raw Byte array from a file on disk.
*					Loads said Bytes into memory.
* 
* 
*Update Log			v1.0.1
*						- resource leak in scanner fixed (closed properly now)
*						- method that takes allows specified File directly in method call added
*					v1.0.0
*						- structured as completely static class
*/
package io;


//imports
import java.io.*;
import java.util.*;


public abstract class Reader extends BasicIO
{
	//prompts user to select a file, returns file data encoded as a string
	public static String readAsString(String msg) throws FileNotFoundException
	{
		File file = Reader.getFile(msg);
		return Reader.readAsString(file);
	}
	
	
	//returns a file's data as a string
	public static String readAsString(File file) throws FileNotFoundException
	{
		if (file != null)
		{
			Scanner scanner = new Scanner(file);
			String content = scanner.useDelimiter("\\Z").next();
			scanner.close();
			return content;
		}
		else
		{
			return null;
		}
	}
}