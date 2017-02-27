package f5.ava_sh;

import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 *Class:                TerminalActionListener.java
 *Project:              AVA Smart Home
 *Author:               Nathaniel Charlebois
 *Date of Update:       04/02/2017
 *Version:              0.0.0
 *
 *Purpose:          An ActionListener Controller bound to the TerminalActivity view
 *
 *
 *
 *Update Log			v.0.0.0
 *                          -Initial class creation
 *                          -Refactoring to reflect the View-Controller-Model schematic
 *
 */

public class TerminalActionListener implements TextView.OnEditorActionListener {


    TerminalActivity view;

    public TerminalActionListener(TerminalActivity terminal){
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
