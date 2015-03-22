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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class VarausActivity extends ActionBarActivity {
    public final static String EXTRA_MESSAGE3 = "com.example.antti.androidvaraus.MESSAGE3";
    private String movie;
    private String user;
    private Map<String, String> shows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_varaus);
        Intent intent = getIntent();
        String[] parts = intent.getStringExtra(MainActivity.EXTRA_MESSAGE).split(":", 2);
        user = parts[0];
        movie = parts[1];
        TextView show = (TextView) findViewById(R.id.naytos_teksti);
        if(movie.equals("Kaikki")){
            show.setText("Kaikki näytökset");
        }else {
            show.setText(movie + " näytökset");
        }

        final ListView showList = (ListView) findViewById(R.id.naytokset);
        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        showList.setAdapter(listAdapter);
        try {
            new NaytosTask().execute(new Pair<>(new URL(Network.SHOW_URL), listAdapter));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        showList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String listItem = showList.getItemAtPosition(position).toString();
                String message = user + ":" + shows.get(listItem);

                Intent intent = new Intent(getApplicationContext(), PaikkaActivity.class);
                intent.putExtra(EXTRA_MESSAGE3, message);
                startActivity(intent);
                finish();
            }
        });
    }

    private class NaytosTask extends AsyncTask<Pair<URL, ArrayAdapter<String>>, Void, Void> {
        private ArrayAdapter<String> adapter;
        private String showsFile;
        private ArrayList<String> showsToRemove;

        @SafeVarargs
        protected final Void doInBackground(Pair<URL, ArrayAdapter<String>>... pairs) {
            shows = new HashMap<>();
            showsToRemove = new ArrayList<>();

            if (pairs.length != 1) {
                return null;
            }

            adapter = pairs[0].second;

            showsFile = Network.download(pairs[0].first);
            for (String line : showsFile.split("\n")) {
                if (line.isEmpty()) {
                    continue;
                }
                //formaatti: ID:Teatteri:Sali:Pvm:Klo:Nimi
                //001:Teatteri1:Sali1:18.03.2015:18.00:Interstellar
                String[] parts = line.split(":", 6);
                if (movie.equals("Kaikki")) {
                    String show = parts[3] + " " + parts[4] + " " + parts[5] + " " + parts[1] + " " + parts[2];
                    shows.put(show, line);
                } else {
                    if (parts[5].equals(movie)) {
                        // Poista kaikki jo menneet näytökset
                        Date currentTime = new Date();
                        int date = Integer.parseInt(parts[3].substring(0, 2));
                        int month = Integer.parseInt(parts[3].substring(3, 5));
                        int year = Integer.parseInt(parts[3].substring(6));
                        int hrs = Integer.parseInt(parts[4].substring(0, 2));
                        int min = Integer.parseInt(parts[4].substring(3));
                        Calendar c = Calendar.getInstance(); c.set(year, month - 1, date, hrs, min);
                        Date showTime = c.getTime();
                        if (currentTime.compareTo(showTime) <= 0) {
                            String show = parts[3] + " " + parts[4] + " " + parts[1] + " " + parts[2];
                            shows.put(show, line);
                        } else {
                            showsToRemove.add(line);
                        }
                    }
                }
            }

            removeShows();

            return null;
        }

        private void removeShows() {
            if (showsToRemove.size() == 0) {
                return;
            }

            StringBuilder sb = new StringBuilder(showsFile.length());

            try {
                String reservations = Network.download(new URL(Network.RESERV_URL));
                ArrayList<String> removedShowIds = new ArrayList<>();

                for (String line : showsFile.split("\n")) {
                    if (!showsToRemove.contains(line) && !line.isEmpty()) {
                        sb.append(line);
                        sb.append("\n");
                    } else {
                        removedShowIds.add(line.split(":", 2)[0]);
                    }
                }

                showsFile = sb.toString();
                Network.upload(showsFile, "naytokset.txt");
                sb = new StringBuilder(reservations.length());

                for (String line : reservations.split("\n")) {
                    if (line.isEmpty()) {
                        continue;
                    }
                    String reservationShowId = line.split(":", 2)[0];
                    if (!removedShowIds.contains(reservationShowId)) {
                        sb.append(line);
                        sb.append("\n");
                    }
                }

                Network.upload(sb.toString(), "varaukset.txt");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        protected void onPostExecute(Void _) {
            adapter.addAll(shows.keySet());
        }
    }
}
