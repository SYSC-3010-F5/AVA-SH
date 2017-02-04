package f5.ava_sh;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;



public class Terminal extends AppCompatActivity {


    TextView terminalOutput;
    EditText terminalInput;

    OnEditorActionListener inputTerminalListener;

    InputMethodManager inputManager;







    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);
        inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


        initViews(this);

    }

    public void initViews(Context c){
        terminalOutput = (TextView) findViewById(R.id.terminal_output);
        terminalInput = (EditText) findViewById(R.id.terminal_input);


        inputTerminalListener = new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    parseInput();
                    terminalInput.getText().clear();
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    handled = true;
                }
                return handled;
            }
        };

        terminalInput.setOnEditorActionListener(inputTerminalListener);


    }

    private void parseInput(){
        terminalOutput.append(">"terminalInput.getText().toString()+"\n");

    }



}
