package f5.ava_sh;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.widget.EditText;
import android.widget.TextView;

import static android.R.attr.value;

/**
 * Created by Slate on 2017-03-26.
 */

public class TextInputFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private AlertDialog.Builder alertDialogBuilder;
    private EditText et;
    private AlertDialog alertDialog;
    private String result;
    private String defaultTimerName = "DefaultTimerName";
    private OnTextSetListener mListener;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        alertDialogBuilder = new AlertDialog.Builder(getActivity());
        et = new EditText(getActivity());
        et.setEnabled(true);
        et.setTextColor(Color.parseColor("#000000"));
        et.setHint("EggTimer");

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(et);




        alertDialogBuilder.setPositiveButton("Select Name", this);


        // create alert dialog
        alertDialog = alertDialogBuilder.create();

        try {
            mListener = (OnTextSetListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnTextSetListener");
        }

        return alertDialog;

    }


    @Override
    public void onClick(DialogInterface dialog, int id) {
        Editable value = et.getText();
        result = value.toString();
        if(result.equals("")){
            result = defaultTimerName;
        }
        mListener.onTextSet(0,result);


    }
}
