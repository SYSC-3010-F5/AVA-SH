/**
*Class:             Reader.java
*Project:           AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    18/01/2016                                              
*Version:           1.0.0                                         
*                                                                                   
*Purpose:           Reads the raw Byte array from a file on disk.
*					Loads said Bytes into memory.
*
*  
*Update Log			v1.0.0
*						- 4 main methods added
*						- structured as completely static class
*/
package io;


//external imports
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;


public abstract class BasicIO 
{
	//allow the user to select a file, do not force them to select
	public static File getFile(String msg)
	{
		return getFile(msg, false);
	}
	
	
	//allow to user to select a file
	public static File getFile(String msg, boolean mustSelectFile)
	{
		while(true)
		{
			//launch dialog to select file
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			FileNameExtensionFilter filter = new FileNameExtensionFilter("*");
			fileChooser.setFileFilter(filter);
			fileChooser.setDialogTitle(msg);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			
			//get file
			int result = fileChooser.showOpenDialog(fileChooser);
			
			//check results
			if (result == JFileChooser.APPROVE_OPTION)
			{
				return fileChooser.getSelectedFile();
			}
			else if (!mustSelectFile)
			{
				return null;
			}
		}
	}
}
