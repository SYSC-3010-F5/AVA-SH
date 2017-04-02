package datatypes;

//class to automatically parse the raw JSON from Open Weather Map 

import org.json.JSONArray;
import org.json.JSONObject;

public class WeatherData 
{
	private float temperature;
	private String weatherType;
	private String weatherDescription;
	private String city;
	private String country;
	private float humidity;
	private static final float KELVIN_TO_CELSIUS_OFFSET = (float) 273.15;
	public static final int TEMPERATURE = 0;
	public static final int WEATHER_TYPE = 1;
	public static final int WEATHER_DESCRIPTION = 2;
	public static final int HUMIDITY = 3;
	public static final int CITY = 4;
	public static final int COUNTRY = 5;
	
	//rawData should be the raw JSON returned by OpenWeatherMap
	public WeatherData(JSONObject rawData)
	{
		try {
			JSONObject init = rawData.getJSONArray("weather").getJSONObject(0);
			weatherType = init.getString("main");
			weatherDescription = init.getString("description");
			init = rawData.getJSONObject("main");
			//OpenWeatherMap by default returns temperature in kelvin
			temperature = (float) init.getDouble("temp") - KELVIN_TO_CELSIUS_OFFSET;
			humidity = (float) init.getDouble("humidity");
			city = rawData.getString("name");
			init = rawData.getJSONObject("sys");
			country = init.getString("country");
		} catch(Exception e){

		}
	}
	
	//rawData should be the raw JSON returned by OpenWeatherMap
	public WeatherData(String rawData)
	{
		try {
			JSONObject data = new JSONObject(rawData);
			JSONObject init = data.getJSONArray("weather").getJSONObject(0);
			weatherType = init.getString("main");
			weatherDescription = init.getString("description");
			init = data.getJSONObject("main");
			//OpenWeatherMap by default returns temperature in kelvin
			temperature = (float) init.getDouble("temp") - KELVIN_TO_CELSIUS_OFFSET;
			humidity = (float) init.getDouble("humidity");
			city = data.getString("name");
			init = data.getJSONObject("sys");
			country = init.getString("country");
		} catch (Exception e){}
		
	}
	
	public String[] getWeatherData()
	{
		String str[] = new String[6];
		str[0] = String.format("%.2f", temperature);
		str[1] = weatherType;
		str[2] = weatherDescription;
		str[3] = String.format("%.2f", humidity);
		str[4] = city;
		str[5] = country;
		
		return str;
	}
}
