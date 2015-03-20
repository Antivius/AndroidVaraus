package com.example.antti.androidvaraus;

import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;


public class VarausActivity extends ActionBarActivity {

    public final static String EXTRA_MESSAGE3 = "com.example.antti.androidvaraus.MESSAGE3";
    private String elokuva;
    private String kayttaja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_varaus);
        Intent intent = getIntent();
        String[] osat = intent.getStringExtra(MainActivity.EXTRA_MESSAGE2).split(":");
        elokuva = osat[0];
        kayttaja = osat[1];
        TextView naytos = (TextView) findViewById(R.id.naytos_teksti);
        final TextView nappulaPainettu = (TextView) findViewById(R.id.nappula_painettu);
        if(elokuva.equals("Kaikki")){
            naytos.setText("Kaikki näytökset");
        }else {
            naytos.setText("Elokuvan " + elokuva + " näytökset");
        }

        ArrayList<String> listViewarrayList = haeNaytoksetLista();

        final ListView naytoslista = (ListView) findViewById(R.id.naytokset);
        ArrayAdapter<String> listadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listViewarrayList);
        naytoslista.setAdapter(listadapter);
        naytoslista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                nappulaPainettu.setText("Nappula painettu!");
                Object listItem = naytoslista.getItemAtPosition(position);
                String s = listItem.toString();
                String[] slista = s.split(" ");
                String viesti = slista[slista.length -1] + ":" + kayttaja;

                Intent intent = new Intent(getApplicationContext(), PaikkaActivity.class);
                intent.putExtra(EXTRA_MESSAGE3, viesti);
                startActivity(intent);
                finish();


            }
        });
    }

    private ArrayList<String> haeNaytoksetLista(){
        ArrayList<String> lista = new ArrayList<String>();
        String elokuva = this.elokuva;
        try {

            AssetManager am = getAssets();
            BufferedReader in1 = null;

            in1 = new BufferedReader(new InputStreamReader(am.open("naytokset.txt")));

            String line1;
            while((line1 = in1.readLine()) != null){
                String viesti1 = "";
                String[] osat1;
                osat1 = line1.split(":");
                if(elokuva.equals("Kaikki")){
                    viesti1 = osat1[3] + " " + osat1[4] + " " + osat1[5] + " " + osat1[1] + " " + osat1[2] + " id: " + osat1[0] ;
                    lista.add(viesti1);
                }else {
                    if (osat1[5].equals(elokuva)) {
                        viesti1 = osat1[3] + " " + osat1[4] + " " + osat1[5] + " " + osat1[1] + " " + osat1[2] + " id: " + osat1[0] ;
                        lista.add(viesti1);

                    }
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_varaus, menu);
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
}
