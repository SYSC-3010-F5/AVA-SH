/**
*Class:             CrudeDatabase.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven
*Date of Update:    21/03/2017
*Version:           1.1.0
*
*Purpose:           Not really a proper database, more of a file-crawler.
*					Should meet the requirements for the project.
*					Gets the job done, even if its less than efficient
*					O(n) query time :(
*
* 
*Update Log			v1.1.0
*						- gracefully handles exceptions
*						- will actually tell you if the file doesn't exist
*						- resource leak with BufferedReader patched 
*						- no longer crashes if database corrupt/dirty/contains garbage
*					v1.0.0
*						- null
**/
package server.database;


//load libraries
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;



public class CrudeDatabase 
{
	//declaring local class constants
	public static final Path DB_LOC = Paths.get("").toAbsolutePath().resolve("resources/citylist.json.butletspretenditsapdf.pdf");
	public static final String COUNTRY_CODE = "CA";

	
	//generic constructor
	public CrudeDatabase(){}
	
	
	//format city and country into JSON format for query
	private String formatQ(String city, String country)
	{
		//format:	"name":"Etten-Leur","country":"NL"
		return ("\"name\":\"" + city + "\",\"country\":\"" + country.toUpperCase() + "\"");
	}
	
	
	//query database with default country
	public Integer query(String city) throws FileNotFoundException
	{
		return query(city, COUNTRY_CODE);
	}
	
	
	//query database
	public Integer query(String city, String country) throws FileNotFoundException
	{
		Integer code = null;
		
		//access the database
		String target = this.formatQ(city, country);
		BufferedReader reader = new BufferedReader(new FileReader(DB_LOC.toFile()));
		
		//search database for target
	    String line;
	    boolean readFlag = true;
	    while (readFlag) 
	    {
	    	/*
	    	 * read line and set flag
	    	 * 
	    	 * It should be noted this was originally the direct condition for the while,
	    	 * i.e. -->  while((line = reader.readLine()) != null)
	    	 * with the try-catch block outside the while
	    	 * 
	    	 * HOWEVER
	    	 * 
	    	 * if database becomes dirty or corrupt, we could quite easily get a IOException or NFE
	    	 * which would cause us to exit the while loop, not reading the rest of the database due
	    	 * to a single corrupt entry
	    	 * 
	    	 * This, while taking an extra flag, lets us read the ENTIRE database every time, even in
	    	 * the event that some entries are corrupt/not proper format/dirty
	    	 */
	    	try
	    	{
	    		//read line and set flag
		    	readFlag = (line = reader.readLine()) != null;
		    	if(readFlag)
		    	{
			    	//check for query hit
			    	if(line.contains(target))
			    	{
			    		//parse and exit while ID
			    		String id = line.split(":")[1].split(",")[0];
			    		code = Integer.parseInt(id);
			    		break;
			    	}
		    	}
	    	}
	    	//current entry is dirty, ignore it and check next
	    	catch (IOException | NumberFormatException e){}
	    }
		

	    //return close reader and return query result
	    try {reader.close();}
	    catch (IOException e){}
		return code;
	}
	
	
	/*
	//tests
	public static void main(String[] args)
	{
		CrudeDatabase db = new CrudeDatabase();
		System.out.println(DB_LOC.toString());
		System.out.println("start...");
		System.out.println(db.query("Ottawa"));
		System.out.println(db.query("jasonville", "jasonland"));
		System.out.println(db.query("Toronto", "CA"));
		System.out.println(db.query("New York", "US"));
	}
	*/
}
