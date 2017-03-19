package f5.ava_sh;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;

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




    private AlertDialog.Builder alertDialogBuilder;
    private TextView et;
    private AlertDialog alertDialog;
    private DataChannelSetup setup;
    private ConnectionHelper connectionHelper;

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


        this.setTitle("AVA-SH");
    }


    public ConnectionHelper getConnectionHelper(){
        return connectionHelper;

    }




    private class DataChannelSetup extends AsyncTask<Void,Void,Void>{

        private ConnectionHelper connectionHelper;

        public DataChannelSetup(ConnectionHelper ch){
            connectionHelper = ch;
        }

        @Override
        protected Void doInBackground(Void... params){



            /*
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    establishConnection(defaultServerAddress, defaultServerPort, defaultDeviceName);
                }
            });
            */
            runOnUiThread(connectionHelper);



            return null;
        }

    }

}
