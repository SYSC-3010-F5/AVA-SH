package f5.ava_sh;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;

import java.net.SocketException;

import network.DataChannel;
import network.NetworkException;
import network.PacketWrapper;

import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.TextView;

import static android.R.attr.data;
import static android.R.attr.id;

/**
 * Created by Slate on 2017-03-13.
 */

public class CommandHelper {

    public String[] buttonNames = {
            "terminal",
            "ping",
            "sch event",
            "set timer",
            "new alarm",
            "req current Weather",
            "req time",
            "req ip",
            "req np-events",
            "req p-events",
            "del np-event"
    };

    private AlertDialog.Builder alertDialogBuilder;
    private TextView et;
    private AlertDialog alertDialog;
    private DataChannel dataChannel;
    private MainActivity main;

    private Context c;
    private ConnectionHelper connectionHelper;


    public CommandHelper(Context c, MainActivity main){
        this.main = main;
        this.c = c;
        connectionHelper = main.getConnectionHelper();
    }

    public String[] getCommands(){
        return buttonNames;
    }

    public void interpret(View view, String id){

        switch(id){
            case "terminal":
                Intent myIntent = new Intent(view.getContext(), TerminalActivity.class);
                view.getContext().startActivity(myIntent);
                break;

            case "ping":
                connectionHelper.ping();
                break;

            case "sch event":
                connectionHelper.getWeather();
                break;

            case "set timer":
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(main.getFragmentManager(), "timePicker");
                break;

            case "new alarm":
                break;

            case "req current weather":
                break;

            case "req time":
                break;

            case "req ip":
                break;

            case "req np-events":
                break;

            case  "req p-events":
                break;

            case "del np-event":
                break;

        }


    }




}
