package com.example.antti.androidvaraus;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;


public class AddMovieActivity extends ActionBarActivity {

    private EditText addMovieTextView;
    private String MOVIE_URL = "http://woodcomb.aleksib.fi/files/elokuvat.txt";
    ArrayList<String> movies;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);
        addMovieTextView = (EditText) findViewById(R.id.add_movie_text);

        Button aButton = (Button) findViewById(R.id.addMovieButton);
        aButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMovie(findViewById(R.id.manageMoviesButton));
            }
        });

        Button testButton = (Button) findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testiNappi(findViewById(R.id.manageMoviesButton));
            }
        });

        Button deleteButton = (Button) findViewById(R.id.deleteMovieButton);
        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                deleteMovie(v);
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.admin_delete_movie_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);

        try {
            // Start a background task to download the available movies
            new DownloadTask().execute(new Pair<>(new URL(MOVIE_URL), adapter));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //lukutesti = Network.download(elokuvat);
        /**
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
         */
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


    }

    private void addMovie(View view) {
        String movieName = addMovieTextView.getText().toString();
             if(movieName != null && !movieName.isEmpty()) {
                    //Network.upload(movieName, "elokuvatesti.txt");
                    //TODO: Lisää elokuva serverin listalle.

                    /**
                    *Ilmoitus lisäyksestä
                    */
                    Context context = getApplicationContext();
                    CharSequence text = "Lisätty: " + movieName;
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                 /**
                  * Päivitä spinneri
                  */

                    Spinner spinner = (Spinner) findViewById(R.id.admin_delete_movie_spinner);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);

                     try {
                      // Start a background task to download the available movies
                        new DownloadTask().execute(new Pair<>(new URL(MOVIE_URL), adapter));
                      } catch (MalformedURLException e) {
                        e.printStackTrace();
                      }

                 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                 spinner.setAdapter(adapter);

                 TextView tv = (TextView) findViewById(R.id.textView1);
                 tv.setText(movieName);


             }


    }

    private void deleteMovie(View view){
        Spinner spinner = (Spinner) findViewById(R.id.admin_delete_movie_spinner);
        String movie = spinner.getSelectedItem().toString();
        ArrayList<String> arrayList = new ArrayList<String>();

        //TODO: hae lista, tee uusi lista ja vertaile poistettavaan, lähetä uusi lista takaisin

        try {

            AssetManager am = getAssets();
            BufferedReader in = null;
            in = new BufferedReader(new InputStreamReader(am.open("elokuvat.txt")));
            String line;
            while((line = in.readLine()) != null){
                if(!line.equals(movie)){
                    arrayList.add(line);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Context context = getApplicationContext();
        CharSequence text = movie;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setText(arrayList.toString());
    }






    private void testiNappi(View view){

        /**
        new Thread() {
            @Override
            public void run() {
                HttpGet httppost = new HttpGet("http://woodcomb.aleksib.fi/files/elokuvat.txt");
                HttpResponse response = null;
                try {
                    response = httpclient.execute(httppost);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                HttpEntity ht = response.getEntity();

                BufferedHttpEntity buf = null;
                try {
                    buf = new BufferedHttpEntity(ht);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                InputStream is = null;
                try {
                    is = buf.getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }



                BufferedReader r = new BufferedReader(new InputStreamReader(is));

                StringBuilder total = new StringBuilder();
                String line;
                try {
                    while ((line = r.readLine()) != null) {
                        elista.add(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();

         */
    /**
        Context context = getApplicationContext();
        CharSequence text = lukutesti;
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

     */
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_movie, menu);
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

    private class DownloadTask extends AsyncTask<Pair<URL, ArrayAdapter<String>>, Void, ArrayAdapter<String>> {

        /**
         * Downloads and parses movie list. Modifies the 'movies' field.
         * @param pairs URL where movie info is found and
         *              ArrayAdapter which is used to forward the information to the UI
         * @return Returns the Adapter to be used later
         */
        protected ArrayAdapter<String> doInBackground(Pair<URL, ArrayAdapter<String>>... pairs) {
            movies = new ArrayList<>();
            if (pairs.length != 1) {
                return null;
            }

            String moviesFile = Network.download(pairs[0].first);
            for (String movie : moviesFile.split("\n")) {
                if (movie.length() > 0) {
                    movies.add(movie);
                }
            }

            return pairs[0].second;
        }

        protected void onPostExecute(ArrayAdapter<String> adapter) {
            adapter.addAll(movies);
        }
    }

}





