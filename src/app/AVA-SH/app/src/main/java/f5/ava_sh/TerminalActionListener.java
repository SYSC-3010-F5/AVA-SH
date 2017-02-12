package f5.ava_sh;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

/**
 *Class:             Terminal.java
 *Project:          	AVA Smart Home
 *Author:            Nathaniel Charlebois
 *Date of Update:    04/02/2017
 *Version:           0.2.0
 *
 *Purpose:          An ActionListener Controller bound to the Terminal view
 *
 *
 *
 *Update Log			v.0.0.0
 *                          -Initial class creation
 *                          -Refactoring to reflect the View-Controller-Model schematic
 *
 */

public class TerminalActionListener implements TextView.OnEditorActionListener {


    Terminal view;

    public TerminalActionListener(Terminal terminal){
        view = terminal;
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId,
                                  KeyEvent event) {
        boolean handled = false;
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            parseInput();
            view.closeKeyboard();
            handled = true;
        }
        return handled;
    }

    private void parseInput(){
        view.getTerminalOutput().append(">"+view.getTerminalInput().getText().toString()+"\n");
        view.getTerminalInput().getText().clear();

    }

}
