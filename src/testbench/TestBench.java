/**
*Class:             DataChannelTestBench.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    04/02/2017                                              
*Version:           1.0.0                                         
*                                                                                   
*Purpose:           Master test bench all other test benchs should inherit from.
*					Makes use of JUnitTests for regression tests, as well as printing out detailed 
*					information for manual debugging of programs and detailed test information.
*					More or less a collection of common methods.
*					
* 
*Update Log			v1.0.0
*						- some methods added
*/

package testbench;

import junit.framework.TestCase;

public abstract class TestBench extends TestCase
{
	//declaring public class constants
	public static final String DIV = "\n==================================================================================================================";
	
	//generic constructor
	public TestBench(String name)
	{
		super(name);
	}
	
	
	//print a new test header
	public void printHeader(String header)
	{
		this.println(header + DIV);
	}
	
	
	//print the end status of a test
	public void printTest(boolean e)
	{
		if(e)
		{
			this.println("Test: PASSED");
		}
		else
		{
			this.println("Test: FAILED");
		}
	}
	
	
	//print string
	public void println(String msg)
	{
		System.out.println(msg);
	}
	
	
	//print a line
	public void println()
	{
		this.println("");
	}
	
	
	//print a byte array
	public void println(byte[] arr)
	{
		String printable = "{";
		for(byte b : arr)
		{
			printable = printable + "0x" + (String.format("%02x", b)).toUpperCase() + " ";
		}
		this.println(printable + "}");
	}
}
