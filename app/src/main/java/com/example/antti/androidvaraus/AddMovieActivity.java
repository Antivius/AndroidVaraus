package com.example.antti.androidvaraus;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class AddMovieActivity extends ActionBarActivity {

    private EditText addMovieTextView;
    private static final String MOVIE_URL = "http://woodcomb.aleksib.fi/files/elokuvat.txt";
    ArrayList<String> movies;
    ArrayAdapter<String> movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);
        addMovieTextView = (EditText) findViewById(R.id.add_movie_text);

        final Spinner spinner = (Spinner) findViewById(R.id.admin_delete_movie_spinner);
        movieAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        movieAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(movieAdapter);

        Button aButton = (Button) findViewById(R.id.addMovieButton);
        aButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMovie();
            }
        });

        Button deleteButton = (Button) findViewById(R.id.deleteMovieButton);
        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                deleteMovie(spinner);
            }
        });

        try {
            new DownloadTask().execute(new URL(MOVIE_URL));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void addMovie() {
        String movieName = addMovieTextView.getText().toString();
             if(movieName != null && !movieName.isEmpty()) {
                 new AddMovieTask().execute(movieName);

                 /*
                  * Ilmoitus lisäyksestä
                  */
                 Context context = getApplicationContext();
                 CharSequence text = "Lisätty: " + movieName;
                 int duration = Toast.LENGTH_SHORT;
                 Toast toast = Toast.makeText(context, text, duration);
                 toast.show();
             }
    }

    private void deleteMovie(Spinner spinner){
        String movie = spinner.getSelectedItem().toString();
        new DeleteMovieTask().execute(movie);

        Toast toast = Toast.makeText(getApplicationContext(), movie, Toast.LENGTH_SHORT);
        toast.show();
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

    private class DownloadTask extends AsyncTask<URL, Void, Void> {

        /**
         * Downloads and parses movie list. Modifies the 'movies' field.
         * @param urls URL where movie info is found and
         * @return Returns the Adapter to be used later
         */
        protected Void doInBackground(URL... urls) {
            movies = new ArrayList<>();
            if (urls.length != 1) {
                return null;
            }

            String moviesFile = Network.download(urls[0]);
            for (String movie : moviesFile.split("\n")) {
                if (movie.length() > 0) {
                    movies.add(movie);
                }
            }

            return null;
        }

        protected void onPostExecute(Void _) {
            movieAdapter.addAll(movies);
        }
    }

    private class AddMovieTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... strings) {
            if (strings.length != 1) {
                return null;
            }

            StringBuilder sb = new StringBuilder();
            for (String movie : movies) {
                sb.append(movie);
                sb.append("\n");
            }

            sb.append(strings[0]);
            Network.upload(sb.toString(), "elokuvat.txt");
            return strings[0];
        }

        protected void onPostExecute(String movieName) {
            movies.add(movieName);
            movieAdapter.add(movieName);
        }
    }

    private class DeleteMovieTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... strings) {
            if (strings.length != 1) {
                return null;
            }

            movies.remove(strings[0]);
            StringBuilder sb = new StringBuilder();
            for (String movie : movies) {
                sb.append(movie);
                sb.append("\n");
            }

            Network.upload(sb.toString(), "elokuvat.txt");
            return strings[0];
        }

        protected void onPostExecute(String movieName) {
            movieAdapter.remove(movieName);
        }
    }
}





