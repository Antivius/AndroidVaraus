package com.example.antti.androidvaraus;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class VarausActivity extends ActionBarActivity {
    public final static String EXTRA_MESSAGE3 = "com.example.antti.androidvaraus.MESSAGE3";
    private String elokuva;
    private String kayttaja;
    private Map<String, String> naytokset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_varaus);
        Intent intent = getIntent();
        String[] osat = intent.getStringExtra(MainActivity.EXTRA_MESSAGE).split(":", 2);
        kayttaja = osat[0];
        elokuva = osat[1];
        TextView naytos = (TextView) findViewById(R.id.naytos_teksti);
        if(elokuva.equals("Kaikki")){
            naytos.setText("Kaikki näytökset");
        }else {
            naytos.setText(elokuva + " näytökset");
        }

        final ListView naytoslista = (ListView) findViewById(R.id.naytokset);
        ArrayAdapter<String> listadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        naytoslista.setAdapter(listadapter);
        try {
            new NaytosTask().execute(new Pair<>(new URL(Network.SHOW_URL), listadapter));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        naytoslista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = naytoslista.getItemAtPosition(position);
                String s = listItem.toString();
                String viesti = kayttaja + ":" + naytokset.get(s);

                Intent intent = new Intent(getApplicationContext(), PaikkaActivity.class);
                intent.putExtra(EXTRA_MESSAGE3, viesti);
                startActivity(intent);
                finish();
            }
        });
    }

    private class NaytosTask extends AsyncTask<Pair<URL, ArrayAdapter<String>>, Void, ArrayAdapter<String>> {
        @SafeVarargs
        protected final ArrayAdapter<String> doInBackground(Pair<URL, ArrayAdapter<String>>... pairs) {
            naytokset = new HashMap<>();

            if (pairs.length != 1) {
                return null;
            }

            String lines = Network.download(pairs[0].first);
            for (String line : lines.split("\n")) {
                //formaatti: ID:Teatteri:Sali:Pvm:Klo:Nimi
                //001:Teatteri1:Sali1:18.03.2015:18.00:Interstellar
                String[] osat = line.split(":", 6);
                if (elokuva.equals("Kaikki")) {
                    String naytos = osat[3] + " " + osat[4] + " " + osat[5] + " " + osat[1] + " " + osat[2];
                    naytokset.put(naytos, line);
                } else {
                    if (osat[5].equals(elokuva)) {
                        String naytos = osat[3] + " " + osat[4] + " " + osat[1] + " " + osat[2];
                        naytokset.put(naytos, line);
                    }
                }
            }

            return pairs[0].second;
        }

        protected void onPostExecute(ArrayAdapter<String> adapter) {
            adapter.addAll(naytokset.keySet());
        }
    }
}
