package com.example.antti.androidvaraus;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class OmatVarauksetActivity extends ActionBarActivity {

    public final static String EXTRA_MESSAGE3 = "com.example.antti.androidvaraus.MESSAGE";
    private String nimi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omat_varaukset);
        Intent intent = getIntent();
        nimi = intent.getStringExtra(MainActivity.EXTRA_MESSAGE2);

        ArrayList<String> listViewarrayList = haeVarauksetLista();
        ArrayList<String> spinnerarrayList = haeVarauksetSpinner();

        ListView listView = (ListView) findViewById(R.id.varaukset);
        Spinner spinner = (Spinner) findViewById(R.id.poistettavaVaraus);

        ArrayAdapter<String> listadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listViewarrayList);
        listView.setAdapter(listadapter);

        ArrayAdapter<String> spinneradapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerarrayList);
        spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinneradapter);

        Button peruvaraus = (Button) findViewById(R.id.peru_varaus_button);
        peruvaraus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poistaVaraus();
            }
        });
    }

    private ArrayList<String> haeVarauksetLista(){
        ArrayList<String> lista = new ArrayList<String>();
        String nimi = this.nimi;
        try {

            AssetManager am = getAssets();
            BufferedReader in1 = null;

            in1 = new BufferedReader(new InputStreamReader(am.open("varaukset.txt")));

            String line1;
            String line2;
            while((line1 = in1.readLine()) != null){
                String viesti1 = "";
                String viesti2 = "";
                String[] osat1;
                osat1 = line1.split(":");
                String id;
                if(osat1[1].equals(nimi)){
                    id = osat1[0];
                    BufferedReader in2 = null;
                    in2 = new BufferedReader(new InputStreamReader(am.open("naytokset.txt")));
                    while((line2 = in2.readLine()) != null){
                        String[] osat2;
                        osat2 = line2.split(":");
                        if(osat2[0].equals(id)){
                            viesti1 = osat2[3] + " " + osat2[4] + " " + osat2[1] + " " + osat2[2] + " " + osat2[5] + " Paikat: ";
                            for(int i = 2; i < osat1.length; i++){
                                viesti1 = viesti1.concat(" " + osat1[i]);

                            }
                            lista.add(viesti1);

                        }
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public ArrayList<String> haeVarauksetSpinner(){
        ArrayList<String> lista = new ArrayList<String>();
        String nimi = this.nimi;

        try {

            AssetManager am = getAssets();
            BufferedReader in1 = null;

            in1 = new BufferedReader(new InputStreamReader(am.open("varaukset.txt")));

            String line1;
            String line2;
            while((line1 = in1.readLine()) != null){
                String viesti2 = "";
                String[] osat1;
                osat1 = line1.split(":");
                String id;
                if(osat1[1].equals(nimi)){
                    id = osat1[0];
                    BufferedReader in2 = null;
                    in2 = new BufferedReader(new InputStreamReader(am.open("naytokset.txt")));
                    while((line2 = in2.readLine()) != null){
                        String[] osat2;
                        osat2 = line2.split(":");
                        if(osat2[0].equals(id)){
                            viesti2 = osat2[3] + " " + osat2[4] + " " + osat2[1] + " " + osat2[2] + " " + osat2[5];
                            lista.add(viesti2);
                        }
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void poistaVaraus(){
        //TODO: varauksen poisto = tekstitiedoston muokkaaminen
    }

    private String getNimi(){
        return this.nimi;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_omat_varaukset, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                                    // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
