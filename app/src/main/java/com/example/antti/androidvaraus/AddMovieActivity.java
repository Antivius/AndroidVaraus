package com.example.antti.androidvaraus;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

        Button addButton = (Button) findViewById(R.id.addMovieButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMovie();
            }
        });

        Button deleteButton = (Button) findViewById(R.id.deleteMovieButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMovie(spinner);
            }
        });

        try {
            new DownloadTask().execute(new URL(Network.MOVIE_URL));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void addMovie() {
        String movieName = addMovieTextView.getText().toString();
        if (movieName != null && !movieName.isEmpty()) {
            if (movies.contains(movieName)) {
                addMovieTextView.setError(getString(R.string.error_movie_name_in_use));
                addMovieTextView.requestFocus();
                return;
            }
            new AddMovieTask().execute(movieName);

            Context context = getApplicationContext();
            CharSequence text = "Lisätty: " + movieName;
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            addMovieTextView.setText("");
        }
    }

    private void deleteMovie(Spinner spinner) {
        String movie = spinner.getSelectedItem().toString();
        new DeleteMovieTask().execute(movie);

        String message = "Poistettu: " + movie;
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private class DownloadTask extends AsyncTask<URL, Void, Void> {
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





