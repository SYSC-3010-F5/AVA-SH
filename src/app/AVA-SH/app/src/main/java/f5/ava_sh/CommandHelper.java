package f5.ava_sh;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import datatypes.Alarm;
import f5.ava_sh.Fragments.TextInputFragment;
import f5.ava_sh.Fragments.TimePickerFragment;
import network.DataChannel;

import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.TextView;


/**
 * Created by Slate on 2017-03-13.
 */

public class CommandHelper {

    public String[] buttonNames = {
            "terminal",
            "ping",
            "sch event",
            "new timer",
            "new event",
            "req current weather",
            "req time",
            "req ip",
            "req np-events",
            "req p-events",
            "del np-event",
            "led on",
            "led off",
            "led pwm",
            "alarm on",
            "alarm off",
            "get time"

    };


    private EditText timerText;
    private String timerName;
    private AlertDialog.Builder alertDialogBuilder;
    private TextView et;
    private AlertDialog alertDialog;
    private DataChannel dataChannel;
    private Alarm alarm;
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

                break;

            case "new timer":
                DialogFragment timerTextInput = new TextInputFragment();
                timerTextInput.show(main.getFragmentManager(),"timerTextInput");
                DialogFragment timerPicker = new TimePickerFragment();
                timerPicker.show(main.getFragmentManager(), "timerTimePicker");


                break;

            case "new event":



                break;

            case "req current weather":
                connectionHelper.getWeather();
                break;

            case "req time":
                connectionHelper.getTime();
                break;

            case "req ip":

                break;

            case "req np-events":
                connectionHelper.getNpEvents();
                break;

            case "req p-events":
                break;

            case "del np-event":

                break;

            case "led on":
                connectionHelper.sendCmd("led on");
                break;

            case "led off":
                connectionHelper.sendCmd("led off");
                break;

            case "led pwm":
                connectionHelper.sendCmd("led pwm");
                break;

            case "alarm on":
                connectionHelper.sendCmd("alarm on");
                break;

            case "alarm off":
                connectionHelper.sendCmd("alarm off");
                break;


        }


    }




}
