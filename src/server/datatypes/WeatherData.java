package server.datatypes;

//class to automatically parse the raw JSON from Open Weather Map 

import org.json.JSONArray;
import org.json.JSONObject;

public class WeatherData 
{
	private float temperature;
	private String weatherType;
	private String weatherDescription;
	private float humidity;
	private static final float KELVIN_TO_CELSIUS_OFFSET = (float) 273.15;
	public static final int TEMPERATURE = 0;
	public static final int WEATHER_TYPE = 1;
	public static final int WEATHER_DESCRIPTION = 2;
	public static final int HUMIDITY = 3;
	
	//rawData should be the raw JSON returned by OpenWeatherMap
	public WeatherData(JSONObject rawData)
	{
		JSONObject init = rawData.getJSONArray("weather").getJSONObject(0);
		weatherType = init.getString("main");
		weatherDescription = init.getString("description");
		init = rawData.getJSONObject("main");
		//OpenWeatherMap by default returns temperature in kelvin
		temperature = (float) init.getDouble("temp") - KELVIN_TO_CELSIUS_OFFSET;
		humidity = (float) init.getDouble("humidity");
	}
	
	//rawData should be the raw JSON returned by OpenWeatherMap
	public WeatherData(String rawData)
	{
		JSONObject data = new JSONObject(rawData);
		JSONObject init = data.getJSONArray("weather").getJSONObject(0);
		weatherType = init.getString("main");
		weatherDescription = init.getString("description");
		init = data.getJSONObject("main");
		//OpenWeatherMap by default returns temperature in kelvin
		temperature = (float) init.getDouble("temp") - KELVIN_TO_CELSIUS_OFFSET;
		humidity = (float) init.getDouble("humidity");
	}
	
	public String[] getWeatherData()
	{
		String str[] = new String[4];
		str[0] = String.format("%.2f", temperature);
		str[1] = weatherType;
		str[2] = weatherDescription;
		str[3] = String.format("%.2f", humidity);
				
		return str;
	}
}
