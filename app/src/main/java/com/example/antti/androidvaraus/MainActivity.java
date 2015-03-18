package com.example.antti.androidvaraus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    public final static String EXTRA_MESSAGE2 = "com.example.antti.androidvaraus.MESSAGE";
    public static final String NIMI = "nimi";
    private String nimi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (!isTaskRoot()) {
            Intent intent = getIntent();
            String action = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action != null && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }

        Intent intent = getIntent();
        nimi = intent.getStringExtra(LoginActivity.EXTRA_MESSAGE1);
        if(nimi == null){
            SharedPreferences settings = getPreferences(MODE_PRIVATE);
            nimi = settings.getString("Nimi", nimi);

        }

        TextView textView = (TextView) findViewById(R.id.tervetuloa);
        textView.setText("Hei " + nimi + "!");


        Spinner spinner = (Spinner) findViewById(R.id.esitykset);
        ArrayList<String> arrayList = new ArrayList<String>();
        try {

            AssetManager am = getAssets();
            BufferedReader in = null;
            in = new BufferedReader(new InputStreamReader(am.open("elokuvat.txt")));
            String line;
            while((line = in.readLine()) != null){
                arrayList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Button logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogin(v);
            }
        }
        );

        Button varaa = (Button) findViewById(R.id.varaus_button);
        varaa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVaraus(v);
            }
        });

        Button omatVaraukset = (Button) findViewById(R.id.omatVaraukset);
        omatVaraukset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOmatVaraukset(v);
            }
        });
    }

    private void setNimi(String nimi){
        this.nimi = nimi;
    }

    private void openLogin(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    protected void onPause(){
        super.onPause();

        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = settings.edit();
        ed.putString("Nimi", nimi);
        ed.commit();
    }

    private void openVaraus(View view){
        Intent intent = new Intent(this, VarausActivity.class);
        Spinner spinner = (Spinner) findViewById(R.id.esitykset);
        String message = spinner.getSelectedItem().toString();
        intent.putExtra(EXTRA_MESSAGE2, message);
        startActivity(intent);
    }

    private void openOmatVaraukset(View view){
        Intent intent = new Intent(this, OmatVarauksetActivity.class);
        String message = nimi;
        intent.putExtra(EXTRA_MESSAGE2, message);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(NIMI, nimi);

    }

    @Override
    protected void onRestart(){
        super.onRestart();

        setNimi(LoginActivity.EXTRA_MESSAGE1);
    }
}
