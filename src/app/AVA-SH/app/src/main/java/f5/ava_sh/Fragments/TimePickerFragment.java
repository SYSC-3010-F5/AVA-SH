package f5.ava_sh.Fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import f5.ava_sh.Listeners.OnTimeSetListener;

/**
 *Class:             TextInputFragment.java
 *Project:           AVA Smart Home
 *Author:            Nathaniel Charlebois
 *Date of Update:    23/02/2017
 *Version:           1.0.1
 *Git:               https://github.com/SYSC-3010-F5/AVA-SH
 *
 *Purpose:          -A fragment that prompts user input in the form of time
 *                  -Implements a call-back interface to pass those values to the suited activity
 *
 *
 *
 *Update Log		v1.0.1
 *						- Adding parsed method functions
 *					v1.0.0
 *				        -Default template created
 *
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private int defaultHour = 0;
    private int defaultMinute = 0;
    private String timerTitle = "Set Timer Duration:";

    private int[] timeWrapper;
    OnTimeSetListener mListener;
    TimePickerDialog tpDialog;



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        // Create a new instance of TimePickerDialog and return it
        tpDialog = new TimePickerDialog(getActivity(), this, defaultHour,
                defaultMinute,
                DateFormat.is24HourFormat(getActivity()));

        tpDialog.setTitle(timerTitle);

        try {
            mListener = (OnTimeSetListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnTimeSetListener");
        }


        return tpDialog;
    }


    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        timeWrapper = new int[2];
        timeWrapper[0] = hour;
        timeWrapper[1] = minute;
        mListener.onTimeSet(timeWrapper);
    }
}
