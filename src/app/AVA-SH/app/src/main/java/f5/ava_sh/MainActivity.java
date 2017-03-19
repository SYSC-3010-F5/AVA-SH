package f5.ava_sh;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.GridView;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

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
    private boolean connecting;


    private AlertDialog.Builder alertDialogBuilder;
    private EditText et;
    private AlertDialog alertDialog;
    private DataChannelSetup setup;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ButtonAdapter(this,this));


        alertDialogBuilder = new AlertDialog.Builder(this);
        et = new EditText(this);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }





        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(et);

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        setup = new DataChannelSetup();
        setup.execute();



        this.setTitle("AVA-SH");
    }




    public DataChannel getDataChannel(){
        return dataChannel;
    }



    private class DataChannelSetup extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params){
            try{
                dataChannel = new DataChannel();


            }
            catch (SocketException e)
            {
                e.printStackTrace();
                System.exit(0);
            }


            /*
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    establishConnection(defaultServerAddress, defaultServerPort, defaultDeviceName);
                }
            });
            */

            runOnUiThread(new ConnectionHelper(et, dataChannel));

            return null;
        }

    }

}
