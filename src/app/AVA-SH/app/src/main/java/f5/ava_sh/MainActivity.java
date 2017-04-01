package f5.ava_sh;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;

import network.DataChannel;

import static android.R.attr.src;

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

public class MainActivity extends AppCompatActivity implements OnTimeSetListener, OnTextSetListener{




    private AlertDialog.Builder alertDialogBuilder;
    private TextView et;
    private AlertDialog alertDialog;
    private DataChannelSetup setup;
    private ConnectionHelper connectionHelper;
    private int[] timeWrapper;

    private AlertBuilder alertBuilder;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView gridview = (GridView) findViewById(R.id.gridview);

        alertBuilder = new AlertBuilder(this);

        connectionHelper = new ConnectionHelper(this);

        gridview.setAdapter(new ButtonAdapter(this,this));

        setup = new DataChannelSetup(connectionHelper);
        setup.execute();


        //Ensures wide network permissions
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        timeWrapper = new int[2];


        this.setTitle("AVA-SH");
    }


    public ConnectionHelper getConnectionHelper(){
        return connectionHelper;

    }



    public void setTime(int[] time){
        timeWrapper= time;
    }

    @Override
    public void onTimeSet(int[] time) {
        timeWrapper = time;
        Log.d("Hour/Minute",time[0]+"/"+time[1]);
    }

    @Override
    public void onTextSet(int type, String name){
        Log.d("Type/Name",type+"/"+name);
        switch(type){
            case 0:
                connectionHelper.sendTimer(timeWrapper[0],timeWrapper[1],name);
                break;
            case 1:
                break;
        }

    }



    private class DataChannelSetup extends AsyncTask<Void,Void,Void>{

        private ConnectionHelper connectionHelper;

        public DataChannelSetup(ConnectionHelper ch){
            connectionHelper = ch;
        }

        @Override
        protected Void doInBackground(Void... params){

            runOnUiThread(connectionHelper);

            return null;
        }

    }

}
