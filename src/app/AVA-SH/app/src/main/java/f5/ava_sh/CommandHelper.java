package f5.ava_sh;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import f5.ava_sh.Fragments.TextInputFragment;
import f5.ava_sh.Fragments.TimePickerFragment;



/**
 *Class:             CommandHelper.java
 *Project:           AVA Smart Home
 *Author:            Nathaniel Charlebois
 *Date of Update:    23/02/2017
 *Version:           4.0.1
 *Git:               https://github.com/SYSC-3010-F5/AVA-SH
 *
 *Purpose:          Dispatches commands and instantiates fragments to receive user data
 *
 *
 *
 *Update Log		v4.0.1
 *						- Adding parsed method functions
 *
 *				    v2.0.0
 *				        -Refactored to meet MVC
 *
 *					v1.0.0
 *				        -Default template created
 *
 */

public class CommandHelper {

    public String[] buttonNames = {
            "terminal",
            "ping",
            "sch event",
            "new timer",
            "req current weather",
            "set location",
            "req time",
            "req ip",
            "req np-events",
            "req p-events",
            "details np-event",
            "details p-event",
            "del np-event",
            "led on",
            "led off",
            "led pwm",
            "alarm on",
            "alarm off",
            "shutdown server"
    };


    private MainActivity main;
    private Context c;
    private ConnectionHelper connectionHelper;
    private android.app.FragmentManager fragmentManager;




    public CommandHelper(Context c, MainActivity main){
        this.main = main;
        this.c = c;
        fragmentManager = main.getFragmentManager();
        connectionHelper = main.getConnectionHelper();


    }



    public String[] getCommands(){
        return buttonNames;
    }



    public void interpret(View view, String id){
        Bundle bundle = new Bundle();
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

                bundle.putInt("type",0);

                DialogFragment timerTextInput = new TextInputFragment();
                timerTextInput.show(fragmentManager,"timerTextInput");
                timerTextInput.setArguments(bundle);
                DialogFragment timerPicker = new TimePickerFragment();
                timerPicker.show(fragmentManager, "timerTimePicker");


                break;


            case "req current weather":
                connectionHelper.getWeather();
                break;

            case "set location":

                bundle.putInt("type",1);

                DialogFragment locationTextInput = new TextInputFragment();
                locationTextInput.setArguments(bundle);
                locationTextInput.show(fragmentManager,"locationTextInput");


            case "req time":
                connectionHelper.getTime();
                break;

            case "req ip":
                bundle.putInt("type",2);

                DialogFragment ipTextInput = new TextInputFragment();
                ipTextInput.setArguments(bundle);
                ipTextInput.show(fragmentManager,"ipTextInput");
                break;

            case "req np-events":
                connectionHelper.getEvents("req np-events");
                break;

            case "req p-events":
                connectionHelper.getEvents("req p-events");
                break;

            case "details np-event":

                bundle.putInt("type",5);

                DialogFragment detailsNP = new TextInputFragment();
                detailsNP.setArguments(bundle);
                detailsNP.show(fragmentManager,"detailsNP");
                break;


            case "details p-event":
                bundle.putInt("type",6);

                DialogFragment detailsP = new TextInputFragment();
                detailsP.setArguments(bundle);
                detailsP.show(fragmentManager,"detailsP");
                break;


            case "del np-event":
                bundle.putInt("type",3);

                DialogFragment delNpInput = new TextInputFragment();
                delNpInput.setArguments(bundle);
                delNpInput.show(fragmentManager,"delNpInput");
                break;

            case "del p-event":
                bundle.putInt("type",4);

                DialogFragment delPInput = new TextInputFragment();
                delPInput.setArguments(bundle);
                delPInput.show(fragmentManager,"delPInput");
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

            case "shutdown server":
                connectionHelper.sendCmd("shutdown server");
                break;


        }


    }




}
