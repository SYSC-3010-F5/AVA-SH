package f5.ava_sh.Listeners;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Button;
import android.graphics.Color;

import f5.ava_sh.CommandHelper;
import f5.ava_sh.MainActivity;
import f5.ava_sh.Listeners.MainActivityButtonListener;



/**
 * Created by Slate on 2017-02-21.
 *//**
 *Class:                ButtonAdapter.java
 *Project:          	AVA Smart Home
 *Author:               Nathaniel Charlebois
 *Date of Update:       21/02/2017
 *Version:              0.0.0
 *
 *Purpose:              The bridge between UI components and data sources.
 *
 *
 *
 *
 *Update Log			v.0.0.0
 *                          -Initial class creation
 *
 *
 */

public class ButtonAdapter extends BaseAdapter {

    private Context mContext;
    private CommandHelper commandHelper;
    private String[] commands;
    MainActivity main;

    public ButtonAdapter(Context c, MainActivity main) {
        mContext = c;
        this.main = main;
        commandHelper = new CommandHelper(c, main);
        commands = commandHelper.getCommands();

    }


    public int getCount() {
        return commands.length;
    }

    public Object getItem(int position) {
        return null;
    }


    public long getItemId(int position) {
        return position;
    }

    // create a new Button for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        Button btn;
        if (convertView == null) {
            // if the view is not recycled, initialize some attributes
            btn = new Button(mContext);
            btn.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT*2));
            btn.setPadding(25, 25, 25, 25);
        }
        else {
            btn = (Button) convertView;
        }

        btn.setText(commands[position]);
        btn.setTextColor(Color.BLACK);
        btn.setBackgroundColor(Color.LTGRAY);
        btn.setId(position);
        btn.setOnClickListener(new MainActivityButtonListener(commands[position], mContext, main));


        return btn;

    }

}
