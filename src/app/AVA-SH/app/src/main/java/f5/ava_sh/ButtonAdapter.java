package f5.ava_sh;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Button;
import android.graphics.Color;

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
    public String[] buttonNames = {
            "Terminal",
            "Play Song",
            "Check Weather",
            "Start Morning",
            "Sample Function",
            "Sample Function",
            "Sample Function",
            "Sample Function",
            "Sample Function",
            "Sample Function",
            "Sample Function",
            "Sample Function",
            "Sample Function",
            "Sample Function",
            "Sample Function",
            "Sample Function",
            "Sample Function",
            "Sample Function",
            "Sample Function",
            "Sample Function",
            "Sample Function",
            "Sample Function",
            "Sample Function",
            "Sample Function",
            "Sample Function"
    };


    public ButtonAdapter(Context c) {
        mContext = c;
    }


    public int getCount() {
        return buttonNames.length;
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
            // if it's not recycled, initialize some attributes
            btn = new Button(mContext);
            btn.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT*2));
            btn.setPadding(25, 25, 25, 25);
        }
        else {
            btn = (Button) convertView;
        }

        btn.setText(buttonNames[position]);
        btn.setTextColor(Color.BLACK);
        btn.setBackgroundColor(Color.LTGRAY);
        btn.setId(position);
        btn.setOnClickListener(new MainActivityButtonListener(buttonNames[position]));


        return btn;

    }

}
