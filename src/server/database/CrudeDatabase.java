/**
*Class:             MainServer.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven
*Date of Update:    13/03/2017
*Version:           1.0.0
*
*Purpose:           Not really a proper database, more of a file-crawler.
*					Should meet the requirements for the project.
*					Gets the job done, even if its less than efficient
*					O(n) query time :(
*
* 
*Update Log			v1.0.0
*						- null
**/
package server.database;


//load libraries
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;



public class CrudeDatabase 
{
	//declaring local class constants
	public static final Path DB_LOC = Paths.get("").toAbsolutePath().resolve("src/server/database/citylist.json.butletspretenditsapdf.pdf");
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
	public Integer query(String city)
	{
		return query(city, COUNTRY_CODE);
	}
	
	
	//query database
	public Integer query(String city, String country)
	{
		Integer code = null;
		//access the database
		String target = this.formatQ(city, country);
		try (BufferedReader reader = new BufferedReader(new FileReader(DB_LOC.toFile())))
		{
		    String line;
		    while ((line = reader.readLine()) != null) 
		    {
		       if(line.contains(target))
		       {
		    	   String id = line.split(":")[1].split(",")[0];
		    	   return Integer.parseInt(id);
		       }
		    }
		}
		catch (IOException|NumberFormatException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
			return null;
		}
		return code;
	}
	
	
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
}
