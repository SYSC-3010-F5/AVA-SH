package f5.ava_sh;

import android.content.Context;
import android.content.SharedPreferences;

import android.preference.PreferenceManager;
import android.widget.TextView;


import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import datatypes.WeatherData;
import network.DataChannel;
import network.NetworkException;
import network.PacketWrapper;



/**
 *Class:             ConnectionHelper.java
 *Project:           AVA Smart Home
 *Author:            Nathaniel Charlebois
 *Date of Update:    23/02/2017
 *Version:           1.0.1
 *Git:               https://github.com/SYSC-3010-F5/AVA-SH
 *
 *Purpose:           A thread that handles connection details and displays results in a custom
 *                      alertDialog. An instance of `ConnectionHelper` his held by `CommandHelper`
 *                      which delegates tasks based on the command provided.
 *
 *                   While there does appear to be "duplicate code" each method requires unique
 *                      error handling and particular details
 *
 *
 *
 *Update Log		v1.0.1
 *						- Adding parsed method functions
 *					v1.0.0
 *				        -Default template created
 *
 */

public class ConnectionHelper implements Runnable {

    private AlertBuilder alertBuilder;

    private DataChannel dataChannel;
    private static final int RETRY_QUANTUM = 5;

    private String 		defaultDeviceName;
    private InetAddress defaultServerAddress;
    private int 		defaultServerPort;
    private String serverIP;
    private TextView et;

    public ConnectionHelper(Context c){
        alertBuilder = new AlertBuilder(c);
        et = alertBuilder.getTextView();

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(c);


        defaultDeviceName = "i\\"+ SP.getString("interface", "i\\app");
        defaultServerPort = Integer.parseInt(SP.getString("serverPort", "3010"));
        serverIP = SP.getString("serverIP", "192.168.0.101");

        try {
            defaultServerAddress = InetAddress.getByName(serverIP);
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
                    et.append("Connection established @ " + address.toString() + ":" + port + " under name \"" + name + "\""+ "\n");
                }
                else
                {
                    et.append("Connection could not be established!" + "\n" + "Please restart the app!" + "\n");
                }
            }
            catch (NetworkException e)
            {
                et.append(e.getMessage());
            }
        }
        else
        {
            et.append("Already connected!\nPlease disconnect first" + "\n");
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
                et.append("Unexpected packet received!" + "\n");
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
        alertBuilder.clear();
        try
        {
            dataChannel.sendCmd("req current weather");
            PacketWrapper wrapper = dataChannel.receivePacket();
            WeatherData weather = new WeatherData(wrapper.info());

            String[] weatherData = weather.getWeatherData();
            et.append("Weather data for " + weatherData[WeatherData.CITY] + "," + weatherData[WeatherData.COUNTRY] + "\n");
            et.append("Current temperature: " + weatherData[WeatherData.TEMPERATURE] + " degrees Celsius" + "\n");
            et.append("Current humidity: " + weatherData[WeatherData.HUMIDITY] + "%" + "\n" );
            et.append("Current weather: " + weatherData[WeatherData.WEATHER_TYPE] + ": " + weatherData[WeatherData.WEATHER_DESCRIPTION] + "\n" );
        }
        catch (NetworkException e)
        {
            et.append(e.getMessage());

        }
        alertBuilder.showAlert();

    }

    public void sendCmd(String cmd){
        alertBuilder.clear();
        try{
            dataChannel.sendCmd(cmd);
            et.append("`"+cmd+"`"+" sent!\n");
        } catch(NetworkException e){
            et.append(e.getMessage());
            alertBuilder.showAlert();

        }
        alertBuilder.showAlert();
    }

    public void sendCmd(String cmd,String str){
        alertBuilder.clear();
        try{
            dataChannel.sendCmd(cmd,str);
            et.append("`"+cmd+": " + str +"`"+" sent!\n");
        } catch(NetworkException e){
            et.append(e.getMessage());
            alertBuilder.showAlert();

        }
        alertBuilder.showAlert();
    }

    public void getTime(){
        alertBuilder.clear();
        try
        {
            dataChannel.sendCmd("req time");
            PacketWrapper wrapper = dataChannel.receivePacket(5000);
            et.append(wrapper.info());
        }
        catch (NetworkException e)
        {
            et.append(e.getMessage());
        }
        catch (SocketException e)
        {
            et.append(e.getMessage());
        }
        alertBuilder.showAlert();
    }

    public void getEvents(String cmd){
        alertBuilder.clear();
        try
        {
            dataChannel.sendCmd(cmd);
            PacketWrapper wrapper = dataChannel.receivePacket();
            et.append(wrapper.info());
        }
        catch (NetworkException e)
        {
            et.append(e.getMessage());
        }
        alertBuilder.showAlert();
    }

    public void delEvent(String cmd,String name){
        alertBuilder.clear();
        try
        {
            //send and wait for response
            dataChannel.sendCmd(cmd, name);
            PacketWrapper response = dataChannel.receivePacket();

            //parse response
            if(response.type == DataChannel.TYPE_INFO)
            {
                et.append("\"" + name + "\" removed!" + "\n");
            }
            else if (response.type == DataChannel.TYPE_ERR)
            {
                et.append(response.errorMessage() + "\n");
            }
            else
            {
                et.append("Unknown response from server!\n"+response.toString() + "\n");
            }
        }
        catch (NetworkException e)
        {
            et.append(e.getMessage() + "\n");
        }
        alertBuilder.showAlert();
    }

    public void sendTimer(int hour, int minute, String name){
        alertBuilder.clear();

        String jsonBuild = buildTimerJson(hour,minute,name);

        try{
            dataChannel.sendCmd("set timer",jsonBuild);
            PacketWrapper response = dataChannel.receivePacket();

            if(response.type == DataChannel.TYPE_INFO){
                et.append(name + " added!");
            }
            else if(response.type == DataChannel.TYPE_ERR){
                et.append(response.errorMessage());
            }
            else{
                et.append("Unknown response from server! \n" + response.toString() + "\n");
            }

        }catch(NetworkException e){
            et.append(e.getMessage());
        }
        alertBuilder.showAlert();
    }

    public void reqIP(String name){
        alertBuilder.clear();
        try
        {
            //send and wait for response
            dataChannel.sendCmd("req ip", name);
            PacketWrapper response = dataChannel.receivePacket();

            //parse response
            if(response.type == DataChannel.TYPE_INFO)
            {
                et.append(name+": " + response.toString() +"\n");
            }
            else if (response.type == DataChannel.TYPE_ERR)
            {
                et.append(response.errorMessage() + "\n");
            }
            else
            {
                et.append("Unknown response from server!\n"+response.toString() + "\n");
            }
        }
        catch (NetworkException e)
        {
            et.append(e.getMessage() + "\n");
        }
        alertBuilder.showAlert();
    }

    public void getEventDetails(String cmd,String name){
        alertBuilder.clear();
        try
        {
            dataChannel.sendCmd(cmd,name);
            PacketWrapper wrapper = dataChannel.receivePacket();
            et.append(wrapper.info());
        }
        catch (NetworkException e)
        {
            et.append(e.getMessage());
        }
        alertBuilder.showAlert();
    }



    private String buildTimerJson(int hour, int minute,String name){
        return "{\n\t\"name\" : \"" + name + "\"\n\t\"timeUntilTrigger\" : " + getTimeInSeconds(hour,minute) + "\n}";
    }

    private int getTimeInSeconds(int hour, int minute){
        return ((hour*60*60) + (minute*60));
    }

    public DataChannel getDataChannel(){
        return dataChannel;
    }
}
