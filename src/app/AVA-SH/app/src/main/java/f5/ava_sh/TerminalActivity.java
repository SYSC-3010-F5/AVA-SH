package f5.ava_sh;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import f5.ava_sh.Listeners.TerminalActionListener;

/**
 *Class:                TerminalActivity.java
 *Project:          	AVA Smart Home
 *Author:               Nathaniel Charlebois
 *Date of Update:       04/02/2017
 *Version:              0.0.0
 *
 *Purpose:              The general terminalActivity view/model responsible for the main segment of
 *                          the app.
 *
 *                      Currently, the terminal is a glorified dataChannel reset
 *
 *
 *
 *Update Log			v.0.0.0
 *                          -Initial class creation
 *                          -Refactoring to reflect the View-Controller-Model schematic
 *
 *
 */


public class TerminalActivity extends AppCompatActivity {


    TextView terminalOutput;
    EditText terminalInput;

    OnEditorActionListener inputTerminalListener;

    InputMethodManager inputManager;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);
        this.setTitle("Terminal");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //Uses depreciated resources, for a non-essential function
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.upNav_arrow), PorterDuff.Mode.SRC_ATOP);

        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        initViews();

    }

    public void initViews(){
        terminalOutput = (TextView) findViewById(R.id.terminal_output);
        terminalInput = (EditText) findViewById(R.id.terminal_input);

        inputTerminalListener = new TerminalActionListener(this);
        terminalInput.setOnEditorActionListener(inputTerminalListener);
    }

    public TextView getTerminalOutput(){
        return terminalOutput;
    }

    public EditText getTerminalInput(){
        return terminalInput;
    }

    public void closeKeyboard(){
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }


}
