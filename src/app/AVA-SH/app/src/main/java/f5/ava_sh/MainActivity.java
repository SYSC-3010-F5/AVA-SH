package f5.ava_sh;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.TextView;

import f5.ava_sh.Listeners.ButtonAdapter;
import f5.ava_sh.Listeners.OnTextSetListener;
import f5.ava_sh.Listeners.OnTimeSetListener;

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
 */

public class MainActivity extends AppCompatActivity implements OnTimeSetListener, OnTextSetListener {


    private DataChannelSetup setup;
    private ConnectionHelper connectionHelper;
    private int[] timeWrapper;

    private AlertBuilder alertBuilder;

    private String interfaceName;
    private String location;
    private String serverIP;
    private String serverPort;




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
        timeWrapper = null;


        this.setTitle("AVA-SH");
    }


    public ConnectionHelper getConnectionHelper(){
        return connectionHelper;

    }


    @Override
    public void onTimeSet(int[] time) {
        timeWrapper = time;
        Log.d("Hour/Minute",time[0]+"/"+time[1]);
    }

    @Override
    public void onTextSet(int type, String name){
        Log.d("Type/Name",type+"/"+name);

            switch (type) {
                case 0:
                    connectionHelper.sendTimer(timeWrapper[0], timeWrapper[1], name);
                    timeWrapper = null;
                    break;
                case 1:
                    connectionHelper.sendCmd("set location",name);
                    break;
                case 2:
                    connectionHelper.reqIP(name);
                    break;
                case 3:
                    connectionHelper.delEvent("del np-event",name);
                    break;
                case 4:
                    connectionHelper.delEvent("del p-event",name);
                    break;
                case 5:
                    connectionHelper.getEventDetails("details np-event",name);
                    break;
                case 6:
                    connectionHelper.getEventDetails("details p-event",name);
                    break;
                default:
                    //ToDo: Handle improper user input
                    Log.d("ERROR","Invalid onTextSet.type set");
                    break;

            }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_settings:
                Intent i = new Intent(this, PreferencesActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        interfaceName = SP.getString("interface", "NA");
        location = SP.getString("location", "NA");
        serverIP = SP.getString("serverIP", "NA");
        serverPort = SP.getString("serverPort", "NA");

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
