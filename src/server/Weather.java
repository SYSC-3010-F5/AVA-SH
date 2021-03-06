package server;

import server.database.CrudeDatabase;
import server.datatypes.WeatherData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Reader;
import java.io.StringWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class Weather 
{
	public static final CrudeDatabase db = new CrudeDatabase();
	public static final int OTTAWA_OPENWEATHER_ID = 6094817;
	public static final String DEFAULT_APP_ID = "222b4b01c634f66b93422eb43f8ed354";
	public static final String APPID_HEADER = "x-api-key";
	private static final String BASE_OWM_URL = "http://api.openweathermap.org/data/2.5/";
	
	private HttpClient httpClient;
	private String appID;
	
	public Weather()
	{
		httpClient = HttpClientBuilder.create().build();
		appID = DEFAULT_APP_ID;
	}
	
	public void setAppID(String appid)
	{
		appID = appid;
	}
	
	
	//get the current weather of a city + country
	public JSONObject currentWeatherAtCity(String city, String country) throws IOException, JSONException
	{
		Integer code = db.query(city, country);
		if(code != null)
		{
			return currentWeatherAtCity(code);
		}
		else
		{
			return null;
		}
	}
	//get the current weather of a city using default country
	public JSONObject currentWeatherAtCity(String city) throws IOException, JSONException
	{
		Integer code = db.query(city);
		if(code != null)
		{
			return currentWeatherAtCity(code);
		}
		else
		{
			return null;
		}
	}
	
	
	//get the current weather of the indicated city, by their city ID
	public JSONObject currentWeatherAtCity(int cityID) throws IOException, JSONException
	{
		String subURL = String.format("weather?id=%d", cityID);
		JSONObject response = doQuery(subURL);
		return response;
	}
	
	
	private JSONObject doQuery(String subUrl) throws JSONException, IOException
	{
		String responseBody = null;
		HttpGet httpget = new HttpGet(BASE_OWM_URL + subUrl);
		if(appID != null)
		{
			httpget.addHeader(APPID_HEADER, appID);
		}

		HttpResponse response = httpClient.execute(httpget);
		InputStream input = null;
		
		try
		{
			StatusLine statusLine = response.getStatusLine();
			if(statusLine == null)
				throw new IOException("Unable to get a response from OWM server");
			
			int statusCode = statusLine.getStatusCode();
			//check the HTTP request was successful. If not, statusCode will be >=300
			if(statusCode >= 300)
				throw new IOException(String.format("OWM server responded with status code %d: %s", statusCode, statusLine));
			
			//read response content
			HttpEntity responseEntity = response.getEntity();
			input = responseEntity.getContent();
			Reader isReader = new InputStreamReader(input);
			int length = (int) responseEntity.getContentLength();
			if(length < 0)
				length = 8*1024;
			
			StringWriter strWriter = new StringWriter(length);
			char[] buffer = new char[8*1024];
			int n = 0;
			while((n = isReader.read(buffer)) != -1)
				strWriter.write(buffer, 0, n);
			
			responseBody = strWriter.toString();
			input.close();
		}
		catch (IOException e)
		{
			throw e;
		}
		catch (RuntimeException re)
		{
			httpget.abort();
			throw re;
		}
		finally
		{
			//make sure the InputStream is closed
			if(input != null)
				input.close();
		}
		return new JSONObject(responseBody);
	}
	
	//quick test code to get the raw JSON of Ottawa's current weather
	public static void main(String[] args)
	{
		Weather weather = new Weather();
		JSONObject ottawaWeather = null;
		try { 
			ottawaWeather = weather.currentWeatherAtCity(Weather.OTTAWA_OPENWEATHER_ID);
		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(ottawaWeather.toString());
		WeatherData data = new WeatherData(ottawaWeather);
		String str[] = data.getWeatherData();
		System.out.println(str[0]);
		System.out.println(str[1]);
		System.out.println(str[2]);
		System.out.println(str[3]);
	}
}
