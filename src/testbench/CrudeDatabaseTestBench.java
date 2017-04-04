/**
*Class:             CrudeDatabaseTestBench.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    03/04/2017                                              
*Version:           1.0.0                                         
*                                                                                   
*Purpose:           Test bench for methods in CrudeDatabase.
*					
* 
*Update Log			v1.0.0
*						- null
*/
package testbench;


import server.database.CrudeDatabase;



public class CrudeDatabaseTestBench extends TestBench
{
	//declaring test variables
	CrudeDatabase db;
	
	
	//generic constructor
	public CrudeDatabaseTestBench(String name) 
	{
		super(name);
	}
	
	
	@Override
	protected void setUp() throws Exception 
	{
		super.setUp();
		db = new CrudeDatabase();
	}
	
	
	@Override
	protected void tearDown() throws Exception 
	{
		super.tearDown();
	}
	
	
	public void testQuery1() throws Exception
	{
		printHeader("Test query with only city");
		
		int expected = 6094817;
		println("Query with city: \"Ottawa\"");
		int actual = db.query("Ottawa");
		println("Expected: " + expected + "\n"
			   +"Actual  : " + actual);
		boolean e = (expected == actual);
		printTest(e);
		assertTrue(e);
	}
	
	
	public void testQuery2() throws Exception
	{
		printHeader("Test query with only city, invalid");
		
		println("Query with city: \"Lumbridge\"");
		Integer actual = db.query("Lumbridge");
		if(actual == null)
		{
			println("Expected: null\n"
				   +"Actual  : null");
			printTest(true);
			assertTrue(true);
		}
		else
		{
			println("Expected: null\n"
				   +"Actual  : " + actual);
			printTest(false);
			assertTrue(false);
		}
	}
	
	
	public void testQuery3() throws Exception
	{
		printHeader("Test query with city and county");
		
		int expected = 5128638;
		println("Query with city: \"New York\", country code: \"US\"");
		int actual = db.query("New York", "US");
		println("Expected: " + expected + "\n"
			   +"Actual  : " + actual);
		boolean e = (expected == actual);
		printTest(e);
		assertTrue(e);
	}
	
	
	public void testQuery4() throws Exception
	{
		printHeader("Test query with city and country, invalid");
		
		println("Query with city: \"Ottawa\", country code: \"AU\"");
		Integer actual = db.query("Ottawa", "AU");
		if(actual == null)
		{
			println("Expected: null\n"
				   +"Actual  : null");
			printTest(true);
			assertTrue(true);
		}
		else
		{
			println("Expected: null\n"
				   +"Actual  : " + actual);
			printTest(false);
			assertTrue(false);
		}
	}

}
