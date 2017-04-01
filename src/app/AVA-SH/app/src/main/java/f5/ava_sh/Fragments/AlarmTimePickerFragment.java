package f5.ava_sh.Fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 *Class:             AlarmTimePickerFragment.java
 *Project:          	AVA Smart Home
 *Author:            Nathaniel Charlebois
 *Date of Update:    19/02/2017
 *Version:           1.0.1
 *
 *Purpose:           Save information on basic alarm.
 *					Occurs once per day, at a certain time.
 *
 *
 *Update Log			v1.0.1
 *						- toString method added for debugging (quicker to read than the JSON!)
 *					v1.0.0
 *						- getters/setter added
 *						- each getter checks values to make sure preconditions met
 *						- DateTimeException high-jacked for exception type
 *						- toJSON functionality added
 *						- fromJSON functionality added
 */

public class AlarmTimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private int defaultHour;
    private int defaultMinute;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Calendar c = Calendar.getInstance();
        defaultHour = c.get(Calendar.HOUR_OF_DAY);
        defaultMinute = c.get(Calendar.MINUTE);


        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, defaultHour, defaultMinute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }
}
