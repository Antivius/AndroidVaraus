package com.example.antti.androidvaraus;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class OmatVarauksetActivity extends ActionBarActivity {

//    public final static String EXTRA_MESSAGE3 = "com.example.antti.androidvaraus.MESSAGE";
    private static final String VARAUS_URL = "http://woodcomb.aleksib.fi/files/varaukset.txt";
    private static final String NAYTOS_URL = "http://woodcomb.aleksib.fi/files/naytokset.txt";
    private String email;
    private Map<String, String> varaukset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omat_varaukset);
        Intent intent = getIntent();
        email = intent.getStringExtra(MainActivity.EXTRA_MESSAGE2);

        final ListView listView = (ListView) findViewById(R.id.varaukset);
        final TextView poistettavaVaraus = (TextView) findViewById(R.id.poistettavaVaraus);

        final ArrayAdapter<String> listadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(listadapter);
        new HaeVarauksetTask().execute(listadapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = listView.getItemAtPosition(position);
                poistettavaVaraus.setText(listItem.toString());
            }
        });

        Button peruVaraus = (Button) findViewById(R.id.peru_varaus_button);
        peruVaraus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poistaVaraus(poistettavaVaraus, listadapter);
            }
        });
    }

    private class HaeVarauksetTask extends AsyncTask<ArrayAdapter<String>, Void, ArrayAdapter<String>> {
        protected ArrayAdapter<String> doInBackground(ArrayAdapter<String>... adapters) {
            varaukset = new HashMap<>();

            if (adapters.length != 1) {
                return null;
            }

            try {
                String varausFile = Network.download(new URL(VARAUS_URL));
                String naytosFile = Network.download(new URL(NAYTOS_URL));

                for (String varausRivi : varausFile.split("\n")) {
                    String[] varaus = varausRivi.split(":");
                    if (varaus[1].equals(email)) {
                        String naytosId = varaus[0];
                        for (String naytosRivi : naytosFile.split("\n")) {
                            String[] naytos = naytosRivi.split(":", 6);
                            if (naytos[0].equals(naytosId)) {
                                StringBuilder sb = new StringBuilder();
                                sb.append(naytos[3]); // Pvm
                                sb.append(" ");
                                sb.append(naytos[4]); // Klo
                                sb.append(" ");
                                sb.append(naytos[1]); // Teatteri
                                sb.append(" ");
                                sb.append(naytos[2]); // Sali
                                sb.append(" ");
                                sb.append(naytos[5]); // Nimi
                                sb.append(" Paikat:");
                                for(int i = 2; i < varaus.length; i++){
                                    sb.append(" ");
                                    sb.append(varaus[i]);

                                }

                                varaukset.put(sb.toString(), varausRivi);
                            }
                        }
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return adapters[0];
        }

        protected void onPostExecute(ArrayAdapter<String> adapter) {
            adapter.addAll(varaukset.keySet());
        }
    }

    public void poistaVaraus(TextView view, ArrayAdapter<String> adapter) {
        String varaus = view.getText().toString();
        String varausRivi = varaukset.get(varaus);
        adapter.remove(varaus);
        varaukset.remove(varaus);
        new PoistaVarausTask().execute(varausRivi);
        Toast.makeText(this, R.string.varaus_peruttu, Toast.LENGTH_SHORT).show();
    }

    private class PoistaVarausTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... strings) {
            if (strings.length != 1) {
                return null;
            }

            try {
                String varausFile = Network.download(new URL(VARAUS_URL));
                StringBuilder sb = new StringBuilder(varausFile.length());

                for (String varausRivi : varausFile.split("\n")) {
                    if (!varausRivi.equals(strings[0])) {
                        sb.append(varausRivi);
                        sb.append("\n");
                    }
                }

                Network.upload(sb.toString(), "varaukset.txt");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return null;
        }
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
