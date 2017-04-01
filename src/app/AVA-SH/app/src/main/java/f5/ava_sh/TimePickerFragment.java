package f5.ava_sh;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.util.Calendar;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by Slate on 2017-03-19.
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
