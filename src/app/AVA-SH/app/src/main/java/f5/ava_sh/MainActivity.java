package f5.ava_sh;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import network.DataChannel;
import network.NetworkException;

/**
 *Class:                MainActivity.java
 *Project:          	AVA Smart Home
 *Author:               Nathaniel Charlebois
 *Date of Update:       21/02/2017
 *Version:              0.0.0
 *
 *Purpose:              The main activity from which commands may be launched
 *
 *
 *
 *Update Log			v.0.0.0
 *                          -Initial class creation
 *
 *
 */

public class MainActivity extends AppCompatActivity {

    DataChannel dataChannel;

    //declaring local instance variables
    private String 		defaultDeviceName;
    private InetAddress defaultServerAddress;
    private int 		defaultServerPort;
    private boolean connecting;
    private static final int RETRY_QUANTUM = 5;

    private AlertDialog.Builder alertDialogBuilder;
    private EditText et;
    private AlertDialog alertDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ButtonAdapter(this,this));


        alertDialogBuilder = new AlertDialog.Builder(this);
        et = new EditText(this);



        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(et);

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        // create alert dialog
        alertDialog = alertDialogBuilder.create();



        try{
            dataChannel = new DataChannel();
            defaultServerAddress = InetAddress.getLocalHost();
            defaultDeviceName = "app";
            defaultServerPort = 3010;

        }
        catch (SocketException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
            System.exit(0);
        }

        establishConnection(defaultServerAddress, defaultServerPort, defaultDeviceName);


        this.setTitle("AVA-SH");
    }

    public DataChannel getDataChannel(){
        return dataChannel;
    }

    //connect to server
    private void establishConnection(InetAddress address, int port, String name)
    {
        if(!dataChannel.getConnected())
        {
            connecting = true;
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
                    et.append("Connection could not be established!");
                }
            }
            catch (NetworkException e)
            {
                et.append(e.getMessage());
            }
            connecting = false;
        }
        else
        {
            et.append("Already connected!\nPlease disconnect first");
        }
        alertDialog.show();
    }

}
