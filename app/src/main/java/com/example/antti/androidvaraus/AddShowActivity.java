package com.example.antti.androidvaraus;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class AddShowActivity extends ActionBarActivity {
    private EditText pickDate;
    private EditText pickTime;
    private Spinner theaterSpinner;
    private Spinner auditoriumSpinner;
    private Spinner addMovieSpinner;
    private Spinner removeMovieSpinner;

    private Calendar myCalendar = Calendar.getInstance();
    private ArrayList<String> movies;
    private ArrayAdapter<String> movieAdapter;

    // Map<elokuvan nimi, Map<näytöksen UI-teksti, näytöksen data palvelimella>>
    private Map<String, Map<String, String>> shows;
    private String showsFile;
    private ArrayAdapter<String> showAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_show);
        // Näytöksen lisäys
        pickDate = (EditText) findViewById(R.id.pickDate);
        pickTime = (EditText) findViewById(R.id.pickTime);

        theaterSpinner = (Spinner) findViewById(R.id.valitseTeatteri);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Teatteri1");
        arrayList.add("Teatteri2");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        theaterSpinner.setAdapter(adapter);

        auditoriumSpinner = (Spinner) findViewById(R.id.valitseSali);
        arrayList = new ArrayList<>();
        arrayList.add("Sali1");
        arrayList.add("Sali2");
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        auditoriumSpinner.setAdapter(adapter2);

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

        pickDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new DatePickerDialog(AddShowActivity.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });
        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddShowActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        pickTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    addTime();
                }
            }
        });
        pickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTime();
            }
        });

        addMovieSpinner = (Spinner) findViewById(R.id.admin_pick_movie);
        movieAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        movieAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addMovieSpinner.setAdapter(movieAdapter);

        Button addButton = (Button) findViewById(R.id.admin_add_show);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addShow();
            }
        });

        // ****************
        // Näytöksen poisto
        final Spinner showSpinner = (Spinner) findViewById(R.id.admin_delete_show_spinner);
        showAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        showAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        showSpinner.setAdapter(showAdapter);

        removeMovieSpinner = (Spinner) findViewById(R.id.admin_delete_show_movie_spinner);
        removeMovieSpinner.setAdapter(movieAdapter);
        removeMovieSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = removeMovieSpinner.getItemAtPosition(position);
                updateShowAdapter(listItem.toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        Button removeButton = (Button) findViewById(R.id.admin_delete_show_button);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object item = showSpinner.getSelectedItem();
                if (item != null) {
                    removeShow(item.toString());
                }
            }
        });

        try {
            new GetMoviesTask().execute(new URL(Network.MOVIE_URL));
            new GetShowsTask().execute(new URL(Network.SHOW_URL));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void addShow() {
        StringBuilder sb = new StringBuilder();
        String[] show = new String[]{
                String.valueOf(System.currentTimeMillis()),
                theaterSpinner.getSelectedItem().toString(),
                auditoriumSpinner.getSelectedItem().toString(),
                pickDate.getText().toString(),
                pickTime.getText().toString(),
                addMovieSpinner.getSelectedItem().toString()};

        sb.append(show[0]);
        for (int i = 1; i < show.length; ++i) {
            if (show[i] == null || show[i].length() == 0) {
                // Kaikkia kenttiä ei täytetty
                Toast toast = Toast.makeText(getApplicationContext(), R.string.fill_all_fields, Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            sb.append(":");
            sb.append(show[i]);
        }

        new AddShowTask().execute(sb.toString());
        String uiString = show[3] + " " + show[4] + " " + show[1] + " " + show[2];
        shows.get(show[5]).put(uiString, sb.toString());
        Toast toast = Toast.makeText(getApplicationContext(), R.string.show_added, Toast.LENGTH_SHORT);
        toast.show();
        pickDate.setText("");
        pickTime.setText("");
        updateShowAdapter(removeMovieSpinner.getSelectedItem().toString());
        findViewById(R.id.addShowLayout).requestFocus();
    }

    private void updateLabel() {
        String myFormat = "dd.MM.yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        pickDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void addTime(){
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        TimePickerDialog timePicker;
        timePicker = new TimePickerDialog(AddShowActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                pickTime.setText(selectedHour + "." + String.format("%02d", selectedMinute));
            }
        }, hour, minute, true);
        timePicker.setTitle(R.string.time_picker_title);
        timePicker.show();
    }

    private void updateShowAdapter(String movie) {
        showAdapter.clear();
        showAdapter.addAll(shows.get(movie).keySet());
    }

    private void removeShow(String showUiString) {
        String movie = removeMovieSpinner.getSelectedItem().toString();
        new RemoveShowTask().execute(shows.get(movie).get(showUiString));
        shows.get(movie).remove(showUiString);
        showAdapter.remove(showUiString);
        Toast toast = Toast.makeText(getApplicationContext(), R.string.show_removed, Toast.LENGTH_SHORT);
        toast.show();
    }

    private class GetMoviesTask extends AsyncTask<URL, Void, Void> {
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

    private class GetShowsTask extends AsyncTask<URL, Void, Void> {
        protected Void doInBackground(URL... urls) {
            shows = new HashMap<>();
            for (String movie : movies) {
                shows.put(movie, new HashMap<String, String>());
            }

            if (urls.length != 1) {
                return null;
            }

            showsFile = Network.download(urls[0]);
            for (String line : showsFile.split("\n")) {
                if (line.length() > 0) {
                    String[] show = line.split(":", 6);
                    String uiString = show[3] + " " + show[4] + " " + show[1] + " " + show[2];
                    shows.get(show[5]).put(uiString, line);
                }
            }

            return null;
        }
    }

    private class AddShowTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... strings) {
            if (strings.length != 1) {
                return null;
            }

            if (showsFile.endsWith("\n")) {
                showsFile += strings[0];
            } else {
                showsFile += "\n" + strings[0];
            }
            Network.upload(showsFile, "naytokset.txt");
            return null;
        }
    }

    private class RemoveShowTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... strings) {
            if (strings.length != 1 || strings[0] == null) {
                return null;
            }

            StringBuilder sb = new StringBuilder(showsFile.length());
            for (String line : showsFile.split("\n")) {
                if (!line.equals(strings[0]) && !line.isEmpty()) {
                    sb.append(line);
                    sb.append("\n");
                }
            }

            showsFile = sb.toString();
            Network.upload(showsFile, "naytokset.txt");
            // Poista kaikki näytökseen liittyvät varaukset
            try {
                String reservations = Network.download(new URL(Network.RESERV_URL));
                sb = new StringBuilder(reservations.length());
                for (String line : reservations.split("\n")) {
                    if (line.isEmpty()) {
                        continue;
                    }
                    String reservationShowId = line.split(":", 2)[0];
                    String showId = strings[0].split(":", 2)[0];
                    if (!reservationShowId.equals(showId)) {
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
