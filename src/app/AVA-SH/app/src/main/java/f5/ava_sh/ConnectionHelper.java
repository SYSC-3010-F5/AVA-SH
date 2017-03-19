package f5.ava_sh;

import android.widget.EditText;

import java.io.IOException;
import java.net.InetAddress;

import network.DataChannel;
import network.NetworkException;

/**
 * Created by Slate on 2017-03-19.
 */

public class ConnectionHelper implements Runnable {

    private EditText et;
    private DataChannel dataChannel;
    private static final int RETRY_QUANTUM = 5;

    private String 		defaultDeviceName;
    private InetAddress defaultServerAddress;
    private int 		defaultServerPort;

    public ConnectionHelper(EditText updateField, DataChannel dc){
        et = updateField;
        dataChannel = dc;
        try {
            defaultServerAddress = InetAddress.getByName("192.168.0.102");
        } catch(Exception e){

        }
        defaultDeviceName = "i\\app";
        defaultServerPort = 3010;
    }

    @Override
    public void run(){
        establishConnection(defaultServerAddress, defaultServerPort,defaultDeviceName);

    }

    //connect to server
    private void establishConnection(InetAddress address, int port, String name)
    {
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
    }
}
