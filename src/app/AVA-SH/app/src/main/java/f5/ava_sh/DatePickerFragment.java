package f5.ava_sh;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TimePicker;
import java.util.Calendar;

/**
 * Created by Slate on 2017-03-19.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private int defaultYear;
    private int defaultMonth;
    private int defaultDay;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        Calendar c = Calendar.getInstance();
        defaultYear = c.get(Calendar.YEAR);
        defaultMonth = c.get(Calendar.MONTH);
        defaultDay = c.get(Calendar.DAY_OF_MONTH);



        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(),this, defaultYear,
                defaultMonth,defaultDay);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }
}
