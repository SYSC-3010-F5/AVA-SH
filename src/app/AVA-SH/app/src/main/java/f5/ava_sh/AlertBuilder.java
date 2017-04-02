package f5.ava_sh;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Slate on 2017-03-19.
 */

public class AlertBuilder {


    private AlertDialog.Builder alertDialogBuilder;
    private TextView et;
    private AlertDialog alertDialog;

    public AlertBuilder(Context c){
        alertDialogBuilder = new AlertDialog.Builder(c);
        et = new EditText(c);
        et.setEnabled(false);
        et.setTextColor(Color.parseColor("#000000"));

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(et);

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        // create alert dialog
        alertDialog = alertDialogBuilder.create();
    }

    public void showAlert(){
        alertDialog.show();
    }

    public TextView getTextView(){
        return et;
    }

    public void clear(){
        et.setText(null);
    }
}
