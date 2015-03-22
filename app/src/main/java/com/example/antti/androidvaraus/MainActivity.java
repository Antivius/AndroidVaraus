package com.example.antti.androidvaraus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    public final static String EXTRA_MESSAGE = "com.example.antti.androidvaraus.MESSAGE";
    public static final String EMAIL = "Email";
    private String email;
    ArrayList<String> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String name = null;

        if (!isTaskRoot()) {
            Intent intent = getIntent();
            String action = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action != null && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }

        // Tervetuloteksti
        Intent intent = getIntent();
        email = intent.getStringExtra(LoginActivity.EXTRA_MESSAGE);
        if(email == null){
            SharedPreferences settings = getPreferences(MODE_PRIVATE);
            email = settings.getString(EMAIL, null);

            if (email == null) {
                openLogin();
            } else {
                name = email.split("@")[0];
            }
        } else {
            name = email.split("@")[0];
        }

        TextView textView = (TextView) findViewById(R.id.tervetuloa);
        textView.setText("Hei " + name + "!");

        // Elokuvaspinneri
        Spinner spinner = (Spinner) findViewById(R.id.esitykset);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        try {
            new DownloadTask().execute(new Pair<>(new URL(Network.MOVIE_URL), adapter));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Napit
        Button logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogin();
            }
        });

        Button reserveButton = (Button) findViewById(R.id.varaus_button);
        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVaraus();
            }
        });

        Button ownReservationsButton = (Button) findViewById(R.id.omatVaraukset);
        ownReservationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOmatVaraukset();
            }
        });
    }

    private void openLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    protected void onPause(){
        super.onPause();

        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = settings.edit();
        ed.putString(EMAIL, email);
        ed.apply();
    }

    private void openVaraus(){
        Intent intent = new Intent(this, VarausActivity.class);
        Spinner spinner = (Spinner) findViewById(R.id.esitykset);
        String message = spinner.getSelectedItem().toString();
        message = email + ":" + message;
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    private void openOmatVaraukset(){
        Intent intent = new Intent(this, OmatVarauksetActivity.class);
        intent.putExtra(EXTRA_MESSAGE, email);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(EMAIL, email);
    }

    @Override
    protected void onRestart(){
        super.onRestart();
    }

    private class DownloadTask extends AsyncTask<Pair<URL, ArrayAdapter<String>>, Void, Void> {
        private ArrayAdapter<String> adapter;

        @SafeVarargs
        protected final Void doInBackground(Pair<URL, ArrayAdapter<String>>... pairs) {
            movies = new ArrayList<>();
            if (pairs.length != 1) {
                return null;
            }

            adapter = pairs[0].second;

            String moviesFile = Network.download(pairs[0].first);
            for (String movie : moviesFile.split("\n")) {
                if (movie.length() > 0) {
                    movies.add(movie);
                }
            }

            return null;
        }

        protected void onPostExecute(Void _) {
            adapter.addAll(movies);
        }
    }
}
