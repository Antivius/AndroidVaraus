package com.example.antti.androidvaraus;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * Created by Tuomas on 19.3.2015.
 */
public class ButtonAdapter2 extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> varaukset;

    public ButtonAdapter2(Context c, ArrayList<String> varaukset) {
        mContext = c;
        this.varaukset = varaukset;


    }

    public int getCount() {
        return mButtonIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new Button for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        final Button button;
        final int pos;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes

            pos = position;
            button = new Button(mContext);
            button.setLayoutParams(new GridView.LayoutParams(250, 250));
            button.setPadding(8, 8, 8, 8);
            button.setBackgroundColor(Color.GREEN);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    button.setBackgroundColor(Color.YELLOW);
                    button.setClickable(false);
                }
            });
            for(String s : varaukset){
                if(s.equals(Integer.toString(position + 1))){
                    button.setBackgroundColor(Color.RED);
                    button.setClickable(false);
                }
            }
        } else {
            button = (Button) convertView;
        }

        button.findViewById(mButtonIds[position]);
        return button;
    }

    // references to buttons
    private Integer[] mButtonIds = {
            R.id.paikka2_button1, R.id.paikka2_button2,
            R.id.paikka2_button3, R.id.paikka2_button4,
            R.id.paikka2_button5, R.id.paikka2_button6,
            R.id.paikka2_button7, R.id.paikka2_button8,
            R.id.paikka2_button9, R.id.paikka2_button10,
            R.id.paikka2_button11, R.id.paikka2_button12,
            R.id.paikka2_button13, R.id.paikka2_button14,
            R.id.paikka2_button15
    };
}
