package f5.ava_sh.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.widget.EditText;

import f5.ava_sh.Listeners.OnTextSetListener;

/**
 *Class:             TextInputFragment.java
 *Project:           AVA Smart Home
 *Author:            Nathaniel Charlebois
 *Date of Update:    23/02/2017
 *Version:           1.0.1
 *Git:               https://github.com/SYSC-3010-F5/AVA-SH
 *
 *Purpose:          -A fragment that prompts user input in the form of a text
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

public class TextInputFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private AlertDialog.Builder alertDialogBuilder;
    private EditText et;
    private AlertDialog alertDialog;
    private String result;

    private String DEFAULT_NAME = "DefaultName";
    private String DEFAULT_HINT = "Your Preferred Name";
    private String DEFAULT_TEXT_COLOR = "#000000";
    private String POS_BUTTON = "Okay";

    private OnTextSetListener mListener;
    private int type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt("type");
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        alertDialogBuilder = new AlertDialog.Builder(getActivity());
        et = new EditText(getActivity());
        et.setEnabled(true);
        et.setTextColor(Color.parseColor(DEFAULT_TEXT_COLOR));

        switch(type){
            case 0:
                DEFAULT_HINT = "Your Preferred Name";
                break;
            case 1:
                DEFAULT_HINT = "Ottawa";
                break;
            case 2:
                DEFAULT_HINT = "terminal";
                break;
            case 3:
                DEFAULT_HINT = "Egg Timer";
                break;
            case 4:
                DEFAULT_HINT = "Morning Coffee";
                break;
            case 5:
                DEFAULT_HINT = "Egg Timer";
                break;
            case 6:
                DEFAULT_HINT = "Morning Coffee";
                break;
            default:
                DEFAULT_HINT = "Your Preferred Name";
                break;
        }

        et.setHint(DEFAULT_HINT);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(et);




        alertDialogBuilder.setPositiveButton(POS_BUTTON, this);


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
            result = DEFAULT_NAME;
        }
        mListener.onTextSet(type,result);


    }
}
