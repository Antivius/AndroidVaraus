package com.example.antti.androidvaraus;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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
    private String email;
    private Map<String, String> reservations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omat_varaukset);
        Intent intent = getIntent();
        email = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        final ListView listView = (ListView) findViewById(R.id.varaukset);
        final TextView pendingRemove = (TextView) findViewById(R.id.poistettavaVaraus);

        final ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(listAdapter);
        new GetReservTask().execute(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = listView.getItemAtPosition(position);
                pendingRemove.setText(listItem.toString());
            }
        });

        Button removeButton = (Button) findViewById(R.id.peru_varaus_button);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeReservation(pendingRemove, listAdapter);
            }
        });
    }

    private class GetReservTask extends AsyncTask<ArrayAdapter<String>, Void, Void> {
        private ArrayAdapter<String> adapter;

        @SafeVarargs
        protected final Void doInBackground(ArrayAdapter<String>... adapters) {
            reservations = new HashMap<>();

            if (adapters.length != 1) {
                return null;
            }

            adapter = adapters[0];

            try {
                String reservFile = Network.download(new URL(Network.RESERV_URL));
                String showFile = Network.download(new URL(Network.SHOW_URL));

                for (String reservLine : reservFile.split("\n")) {
                    if (reservLine.isEmpty()) {
                        continue;
                    }
                    String[] reservation = reservLine.split(":");
                    if (reservation[1].equals(email)) {
                        String showId = reservation[0];
                        for (String showLine : showFile.split("\n")) {
                            if (showLine.isEmpty()) {
                                continue;
                            }
                            String[] show = showLine.split(":", 6);
                            if (show[0].equals(showId)) {
                                StringBuilder sb = new StringBuilder();
                                sb.append(show[3]); // Pvm
                                sb.append(" ");
                                sb.append(show[4]); // Klo
                                sb.append(" ");
                                sb.append(show[1]); // Teatteri
                                sb.append(" ");
                                sb.append(show[2]); // Sali
                                sb.append(" ");
                                sb.append(show[5]); // Nimi
                                sb.append(" Paikat:");
                                for (int i = 2; i < reservation.length; i++) {
                                    sb.append(" ");
                                    sb.append(reservation[i]);

                                }

                                reservations.put(sb.toString(), reservLine);
                            }
                        }
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void _) {
            adapter.addAll(reservations.keySet());
        }
    }

    public void removeReservation(TextView view, ArrayAdapter<String> adapter) {
        String reservation = view.getText().toString();
        String line = reservations.get(reservation);
        adapter.remove(reservation);
        reservations.remove(reservation);
        new RemoveReservTask().execute(line);
        Toast.makeText(this, R.string.reservation_cancelled, Toast.LENGTH_SHORT).show();
    }

    private class RemoveReservTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... strings) {
            if (strings.length != 1) {
                return null;
            }

            try {
                String reservFile = Network.download(new URL(Network.RESERV_URL));
                StringBuilder sb = new StringBuilder(reservFile.length());

                for (String line : reservFile.split("\n")) {
                    if (!line.equals(strings[0]) && !line.isEmpty()) {
                        sb.append(line);
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
}
