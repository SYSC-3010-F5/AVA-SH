package f5.ava_sh;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Slate on 2017-03-19.
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
