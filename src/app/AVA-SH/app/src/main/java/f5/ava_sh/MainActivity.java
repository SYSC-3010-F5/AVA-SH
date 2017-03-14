package f5.ava_sh;

import android.os.Bundle;
import android.app.Activity;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.net.InetAddress;

import network.DataChannel;

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




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ButtonAdapter(this,this));

        try{
            dataChannel = new DataChannel();
            defaultServerAddress = InetAddress.getLocalHost();
            defaultDeviceName = "terminal";
            defaultServerPort = 3010;

        } catch(Exception e){

        }


        this.setTitle("AVA-SH");
    }

    public DataChannel getDataChannel(){
        return dataChannel;
    }

}
