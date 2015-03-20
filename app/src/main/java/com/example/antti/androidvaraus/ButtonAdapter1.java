package com.example.antti.androidvaraus;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by Tuomas on 19.3.2015.
 */
public class ButtonAdapter1 extends BaseAdapter{
    private Context mContext;
    private ArrayList<String> varaukset;

    public ButtonAdapter1(Context c, ArrayList<String> varaukset) {
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
        final String pos = Integer.toString(position + 1) + ":";
        if (convertView == null) {

            // if it's not recycled, initialize some attributes

            button = new Button(mContext);
            button.setLayoutParams(new GridLayout.LayoutParams());
            button.setPadding(8, 8, 8, 8);
            button.setBackgroundColor(Color.GREEN);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    button.setBackgroundColor(Color.YELLOW);
                    button.setClickable(false);

                    try {
                        FileOutputStream fos = mContext.openFileOutput("valinnat", Context.MODE_APPEND);
                        OutputStreamWriter osw = new OutputStreamWriter(fos);
                        osw.write (pos);
                        osw.flush();
                        osw.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
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
            R.id.paikka1_button1, R.id.paikka1_button2,
            R.id.paikka1_button3, R.id.paikka1_button4,
            R.id.paikka1_button5, R.id.paikka1_button6,
            R.id.paikka1_button7, R.id.paikka1_button8,
            R.id.paikka1_button9, R.id.paikka1_button10
    };
}
