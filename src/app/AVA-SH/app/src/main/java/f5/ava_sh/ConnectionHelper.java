package f5.ava_sh;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import datatypes.WeatherData;
import network.DataChannel;
import network.NetworkException;
import network.PacketWrapper;

import static android.R.attr.data;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Created by Slate on 2017-03-19.
 */

public class ConnectionHelper implements Runnable {

    private AlertBuilder alertBuilder;

    private DataChannel dataChannel;
    private static final int RETRY_QUANTUM = 5;

    private String 		defaultDeviceName;
    private InetAddress defaultServerAddress;
    private int 		defaultServerPort;
    private TextView et;

    public ConnectionHelper(Context c){
        alertBuilder = new AlertBuilder(c);
        et = alertBuilder.getTextView();

        defaultDeviceName = "i\\app";
        defaultServerPort = 3010;

        try {
            defaultServerAddress = InetAddress.getByName("192.168.0.102");
            dataChannel = new DataChannel();
        } catch(Exception e){

        }

    }

    @Override
    public void run(){
        establishConnection(defaultServerAddress, defaultServerPort,defaultDeviceName);
    }

    //connect to server
    private void establishConnection(InetAddress address, int port, String name)
    {
        alertBuilder.clear();
        if(!dataChannel.getConnected())
        {
            try
            {
                for(int i=0; i<RETRY_QUANTUM && !dataChannel.getConnected(); i++)
                {
                    et.append("Establishing connection..."+"\n");
                    try
                    {
                        dataChannel.connect(address, port, name);
                    }
                    catch (IOException e1)
                    {
                        //timeout has occurred
                    }
                }

                if(dataChannel.getConnected())
                {
                    et.append("Connection established @ " + address.toString() + ":" + port + " under name \"" + name + "\"");
                }
                else
                {
                    et.append("Connection could not be established!" + "\n" + "Please restart the app!");
                }
            }
            catch (NetworkException e)
            {
                et.append(e.getMessage());
            }
        }
        else
        {
            et.append("Already connected!\nPlease disconnect first");
        }
        alertBuilder.showAlert();
    }

    public void ping(){
        //declaring method variables
        long pre, post;
        alertBuilder.clear();
        try
        {
            //send ping
            pre = System.currentTimeMillis();
            dataChannel.sendCmd("ping");

            //wait for response
            PacketWrapper wrapper = dataChannel.receivePacket(5000);
            if(wrapper.type == DataChannel.TYPE_INFO)
            {
                post = System.currentTimeMillis();
                et.append("Response from server, delay of " + (post-pre) + "ms" + "\n");
            }
            else
            {
                et.append("Unexpected packet recieved!" + "\n");
            }
        }
        catch (NetworkException e)
        {
            et.append(e.getMessage());
        }
        catch (SocketException e)
        {
            et.append("No response" + "\n" );
        }
        alertBuilder.showAlert();

    }

    public void getWeather(){

        try
        {
            dataChannel.sendCmd("req current weather" + "\n" );
            PacketWrapper wrapper = dataChannel.receivePacket();
            WeatherData weather = new WeatherData(wrapper.info());

            String[] weatherData = weather.getWeatherData();
            et.append("Weather data for Ottawa, Ontario.");
            et.append("Current temperature: " + weatherData[WeatherData.TEMPERATURE] + " degrees Celsius");
            et.append("Current humidity: " + weatherData[WeatherData.HUMIDITY] + "%");
            et.append("Current weather: " + weatherData[WeatherData.WEATHER_TYPE] + ": " + weatherData[WeatherData.WEATHER_DESCRIPTION]);
        }
        catch (NetworkException e)
        {
            et.append(e.getMessage());

        }
        alertBuilder.showAlert();

    }

    public void sendCmd(String cmd){
        try{
            dataChannel.sendCmd(cmd);
        } catch(NetworkException e){
            et.append(e.getMessage());
            alertBuilder.showAlert();

        }

    }

    public DataChannel getDataChannel(){
        return dataChannel;
    }
}
