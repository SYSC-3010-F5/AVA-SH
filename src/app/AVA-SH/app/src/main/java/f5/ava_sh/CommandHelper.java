package f5.ava_sh;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.view.View;

import datatypes.Alarm;
import network.DataChannel;

import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.TextView;

import static android.R.id.input;


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
            "del np-event",
            "led on",
            "led off",
            "led pwm",
            "alarm on",
            "alarm off"

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

            case "set timer":
                DialogFragment timerPicker = new TimerTimePickerFragment();
                timerPicker.show(main.getFragmentManager(), "timerTimePicker");

                AlertDialog.Builder alert = new AlertDialog.Builder(c);

                timerText = new EditText(c);
                timerText.setEnabled(false);
                timerText.setTextColor(Color.parseColor("#000000"));

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(timerText);


                alert.setPositiveButton("Name the timer!", new DialogInterface.OnClickListener() {
                    //@Override
                    public void onClick(DialogInterface dialog, int which) {

                        Editable value = timerText.getText();
                        timerName = value.toString();

                    }
                });


                //send timer command
                String json = "{\n\t\"name\" : \"" + timerName + "\"\n\t\"timeUntilTrigger\" : " + "60" + "\n}";



                break;

            case "new alarm":

                //Need to pull master and get alarm type
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(main.getFragmentManager(), "datePicker");
                DialogFragment timePicker = new AlarmTimePickerFragment();
                timePicker.show(main.getFragmentManager(), "alarmTimePicker");


                break;

            case "req current weather":
                connectionHelper.getWeather();
                break;

            case "req time":
                break;

            case "req ip":
                break;

            case "req np-events":
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
