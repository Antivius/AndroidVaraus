package com.example.antti.androidvaraus;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;


public class PaikkaActivity extends ActionBarActivity {


    private String naytos;
    private String[] naytosdata;
    private ArrayList<String> varatutPaikat;
    private String kayttaja;
    private Integer[] buttonIdt1;
    private Integer[] buttonIdt2;
    private String valinnatTiedosto;
    private File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();
        String[] s = intent.getStringExtra(VarausActivity.EXTRA_MESSAGE3).split(":", 2);
        kayttaja = s[0];
        naytos = s[1];
        naytosdata = naytos.split(":", 6);
        varatutPaikat = haeVarauksetLista();
        buttonIdt1 = haeButtonIdt("Sali1");
        buttonIdt2 = haeButtonIdt("Sali2");
        valinnatTiedosto = "valinnat";
        File file = new File(this.getFilesDir(), valinnatTiedosto);
        this.file = file;

        if(naytosdata[2].equals("Sali1")){
            setContentView(R.layout.activity_paikka1);
            TextView textView = (TextView) findViewById(R.id.naytoksen_data1);
            textView.setText(naytosdata[0]);



            Button keskeytaVaraus = (Button)  findViewById(R.id.keskeyta_varaus_button1);

            keskeytaVaraus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            final GridView gridview = (GridView) findViewById(R.id.gridView1);
            gridview.setAdapter(new ButtonAdapter1(this, varatutPaikat));

            Button resetValinnat = (Button) findViewById(R.id.varaus_reset_button1);
            resetValinnat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gridview.setAdapter(new ButtonAdapter1(getApplicationContext(), varatutPaikat));
                    deleteFile("valinnat");
                    File file = new File(getApplicationContext().getFilesDir(), valinnatTiedosto);
                }
            });

            Button hyvaksyValinnatButton = (Button) findViewById(R.id.hyvaksy_varaus_button1);
            hyvaksyValinnatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hyvaksyValinnat();
                }
            });

        } else{
            setContentView(R.layout.activity_paikka2);
            TextView textView = (TextView) findViewById(R.id.naytoksen_data2);
            textView.setText(naytosdata[0]);



            Button keskeytaVaraus = (Button)  findViewById(R.id.keskeyta_varaus_button2);

            keskeytaVaraus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            final GridView gridview = (GridView) findViewById(R.id.gridView2);
            gridview.setAdapter(new ButtonAdapter2(this, varatutPaikat));

            Button resetValinnat = (Button) findViewById(R.id.varaus_reset_button2);
            resetValinnat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gridview.setAdapter(new ButtonAdapter2(getApplicationContext(), varatutPaikat));
                    deleteFile("valinnat");
                    File file = new File(getApplicationContext().getFilesDir(), valinnatTiedosto);
                }
            });

            Button hyvaksyValinnatButton = (Button) findViewById(R.id.hyvaksy_varaus_button2);
            hyvaksyValinnatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hyvaksyValinnat();
                }
            });
            }




        }

//    private class NaytosTask extends AsyncTask<Pair<URL, ArrayAdapter<String>>, Void, Void> {
//        protected Void doInBackground(Pair<URL, ArrayAdapter<String>>... pairs) {
//            if (pairs.length != 1) {
//                return null;
//            }
//
//            String naytokset = Network.download(pairs[0].first);
//            for (String line : naytokset.split("\n")) {
//                //formaatti: ID:Teatteri:Sali:Pvm:Klo:Nimi
//                //001:Teatteri1:Sali1:18.03.2015:18.00:Interstellar
//                String[] osat = line.split(":", 6);
//                if (elokuva.equals("Kaikki")) {
//                    String naytos = osat[3] + " " + osat[4] + " " + osat[5] + " " + osat[1] + " " + osat[2] + " id: " + osat[0] ;
//                    pairs[0].second.add(naytos);
//                } else {
//                    if (osat[5].equals(elokuva)) {
//                        String naytos = osat[3] + " " + osat[4] + " " + osat[5] + " " + osat[1] + " " + osat[2] + " id: " + osat[0] ;
//                        pairs[0].second.add(naytos);
//                    }
//                }
//            }
//
//            return null;
//        }
//    }

    private ArrayList<String> haeVarauksetLista(){
        ArrayList<String> lista = new ArrayList<String>();
        String naytos = this.naytosdata[0];
        try {

            AssetManager am = getAssets();
            BufferedReader in1 = null;

            in1 = new BufferedReader(new InputStreamReader(am.open("varaukset.txt")));

            String line;
            while((line = in1.readLine()) != null){
                String[] osat;
                osat = line.split(":");
                String id;
                if(osat[0].equals(naytos)){
                    for(int i = 2; i < osat.length; i++){
                        lista.add(osat[i]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private Integer[] haeButtonIdt(String sali){

        if(sali.equals("Sali1")){
            Integer[] buttonit = {
                    R.id.paikka1_button1, R.id.paikka1_button2,
                    R.id.paikka1_button3, R.id.paikka1_button4,
                    R.id.paikka1_button5, R.id.paikka1_button6,
                    R.id.paikka1_button7, R.id.paikka1_button8,
                    R.id.paikka1_button9, R.id.paikka1_button10
            };
            return buttonit;

        }else{
            Integer[] buttonit = {
                    R.id.paikka2_button1, R.id.paikka2_button2,
                    R.id.paikka2_button3, R.id.paikka2_button4,
                    R.id.paikka2_button5, R.id.paikka2_button6,
                    R.id.paikka2_button7, R.id.paikka2_button8,
                    R.id.paikka2_button9, R.id.paikka2_button10,
                    R.id.paikka2_button11, R.id.paikka2_button12,
                    R.id.paikka2_button13, R.id.paikka2_button14,
                    R.id.paikka2_button15
            };
            return buttonit;
        }

    }




    private void hyvaksyValinnat(){
        /**
        Button button = (Button) findViewById(R.id.paikka1_button1);
        PaintDrawable buttonColor = (PaintDrawable) button.getBackground();
        if(buttonColor.getPaint().getColor() == Color.GREEN){
            Toast.makeText(getApplicationContext(), "green", Toast.LENGTH_SHORT).show();
        }
         **/
        StringBuffer datax = new StringBuffer("");
        String s;
        String[] valinnat = null;

            try{
                FileInputStream in = openFileInput("valinnat");
                InputStreamReader isr = new InputStreamReader(in);
                BufferedReader buffreader = new BufferedReader(isr) ;


                String readString = buffreader.readLine ( ) ;
                while(readString != null){
                    datax.append(readString);
                    readString = buffreader.readLine() ;
                }

                isr.close ( ) ;
                deleteFile("valinnat");
                s = datax.toString();
                valinnat = s.split(":");

                Toast.makeText(this,datax,Toast.LENGTH_SHORT).show();
            }catch(IOException e) {
                e.printStackTrace();
            }

        kirjoitaVaraus(valinnat);
    }

    private void kirjoitaVaraus(String[] valinnat){
        //TODO: luo oikeanlainen varausrivi (esim. 001:tuomas:1:2:3) ja kirjoita serverillä olevaan tiedostoon

//        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//        intent.putExtra("kutsuja", "PaikkaActivity");
//        startActivity(intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_paikka, menu);
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
