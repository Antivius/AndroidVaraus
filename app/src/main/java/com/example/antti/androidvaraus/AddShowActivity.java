package com.example.antti.androidvaraus;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class AddShowActivity extends ActionBarActivity {

    private EditText showId;
    private EditText pickDate;
    private EditText pickTime;
    private Spinner spinner;
    private Spinner spinner2;
    Calendar myCalendar = Calendar.getInstance();
    ArrayList<String> movies;
    ArrayAdapter<String> movieAdapter;
    private static final String MOVIE_URL = "http://woodcomb.aleksib.fi/files/elokuvat.txt";
    private static final String SHOW_URL = "http://woodcomb.aleksib.fi/files/naytokset.txt";

    //TODO: Samat posistot ja lisäykset kuin elokuvissa ja käyttäjissä, kirjotussysteemi ei taida taipua muokkaukseen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_show);
        showId = (EditText) findViewById(R.id.showAddId);
        pickDate = (EditText) findViewById(R.id.pickDate);
        pickTime = (EditText) findViewById(R.id.pickTime);

        spinner = (Spinner) findViewById(R.id.valitseTeatteri);
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("Teatteri1");
        arrayList.add("Teatteri2");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner2 = (Spinner) findViewById(R.id.valitseSali);
        ArrayList<String> arrayListSali = new ArrayList<String>();
        arrayListSali.add("Sali1");
        arrayListSali.add("Sali2");

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayListSali);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);



        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        pickDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new DatePickerDialog(AddShowActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        pickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTime(findViewById(R.id.addUserButton));
            }
        });

        final Spinner moviespinner = (Spinner) findViewById(R.id.valitseElokuva);
        movieAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        movieAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moviespinner.setAdapter(movieAdapter);

        try {
            new DownloadTask().execute(new URL(MOVIE_URL));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        Button addShowButton = (Button) findViewById(R.id.admin_add_show);
        addShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addShow(findViewById(R.id.addUserButton),moviespinner);
            }
        });

        final Spinner showspinner = (Spinner) findViewById(R.id.showSpinner);
        ArrayList<String> arrayListShow = new ArrayList<String>();
        arrayListShow.add("fluff:fluff2");
        arrayListShow.add("fluff2:fluff");

        ArrayAdapter<String> showAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayListShow);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        showspinner.setAdapter(showAdapter);

        Button removeShow = (Button) findViewById(R.id.removeShow);
        removeShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeShow(findViewById(R.id.addUserButton), showspinner);
            }
        });


    }


    private void updateLabel() {

        String myFormat = "dd.MM.yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        pickDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void addTime(View view){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AddShowActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                pickTime.setText( selectedHour + "." + selectedMinute);
            }
        }, hour, minute, true);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void addShow(View view,Spinner mspinner){
        String id = showId.getText().toString();
        String teatteri = spinner.getSelectedItem().toString();
        String sali = spinner2.getSelectedItem().toString();
        String pvm = pickDate.getText().toString();
        String aika = pickTime.getText().toString();
        String elokuva = mspinner.getSelectedItem().toString();
        String kootut = id+":"+teatteri+":"+sali+":"+pvm+":"+aika+":"+elokuva;

        //TODO: kootut lisänä naytokset.txt

    }

    private void removeShow(View view,Spinner sp){
        String poisto = sp.getSelectedItem().toString();

        //TODO: poista ja lisää loput takaisin
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_show, menu);
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
}
