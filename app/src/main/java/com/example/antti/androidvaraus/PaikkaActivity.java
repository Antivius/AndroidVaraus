package com.example.antti.androidvaraus;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;


public class PaikkaActivity extends ActionBarActivity {
    private String reservFile;
    private String[] show;
    private ArrayList<String> reservedSeats;
    private String user;
    private String pendingReservFile = "valinnat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String[] s = intent.getStringExtra(VarausActivity.EXTRA_MESSAGE3).split(":", 2);
        user = s[0];
        show = s[1].split(":", 6);

        try {
            new GetReservTask().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        new File(this.getFilesDir(), pendingReservFile);

        if (show[2].equals("Sali1")) {
            setContentView(R.layout.activity_paikka1);
            TextView textView = (TextView) findViewById(R.id.naytoksen_data1);
            textView.setText(show[0]);

            Button cancel = (Button) findViewById(R.id.keskeyta_varaus_button1);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            final GridView gridview = (GridView) findViewById(R.id.gridView1);
            gridview.setAdapter(new ButtonAdapter1(this, reservedSeats));

            Button reset = (Button) findViewById(R.id.varaus_reset_button1);
            reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gridview.setAdapter(new ButtonAdapter1(getApplicationContext(), reservedSeats));
                    deleteFile(pendingReservFile);
                    new File(getApplicationContext().getFilesDir(), pendingReservFile);
                }
            });

            Button accept = (Button) findViewById(R.id.hyvaksy_varaus_button1);
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptChoices();
                }
            });

        } else {
            setContentView(R.layout.activity_paikka2);
            TextView textView = (TextView) findViewById(R.id.naytoksen_data2);
            textView.setText(show[0]);

            Button cancel = (Button) findViewById(R.id.keskeyta_varaus_button2);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            final GridView gridview = (GridView) findViewById(R.id.gridView2);
            gridview.setAdapter(new ButtonAdapter2(this, reservedSeats));

            Button reset = (Button) findViewById(R.id.varaus_reset_button2);
            reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gridview.setAdapter(new ButtonAdapter2(getApplicationContext(), reservedSeats));
                    deleteFile("valinnat");
                    new File(getApplicationContext().getFilesDir(), pendingReservFile);
                }
            });

            Button accept = (Button) findViewById(R.id.hyvaksy_varaus_button2);
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptChoices();
                }
            });
        }
    }

    private class GetReservTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... _) {
            reservedSeats = new ArrayList<>();
            String showId = show[0];

            try {
                reservFile = Network.download(new URL(Network.RESERV_URL));
                for (String line : reservFile.split("\n")) {
                    String[] reservation = line.split(":");
                    if (reservation[0].equals(showId)) {
                        reservedSeats.addAll(Arrays.asList(reservation).subList(2, reservation.length));
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private void acceptChoices() {
        StringBuilder stringBuilder = new StringBuilder();
        String[] choices = null;

        try {
            FileInputStream in = openFileInput(pendingReservFile);
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader buffreader = new BufferedReader(isr);


            String readString = buffreader.readLine();
            while (readString != null) {
                stringBuilder.append(readString);
                readString = buffreader.readLine();
            }

            isr.close();
            deleteFile(pendingReservFile);
            choices = stringBuilder.toString().split(":");


        } catch (IOException e) {
            e.printStackTrace();
        }

        new WriteReservTask().execute(choices);
        Toast.makeText(this, R.string.reservation_accepted, Toast.LENGTH_SHORT).show();
        finish();
    }

    private class WriteReservTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... strings) {
            if (strings.length < 1) {
                return null;
            }

            StringBuilder reservation = new StringBuilder(show[0] + ":" + user);
            for (String seat : strings) {
                reservation.append(":");
                reservation.append(seat);
            }

            if (reservFile.endsWith("\n")) {
                reservFile += reservation.toString();
            } else {
                reservFile += "\n" + reservation.toString();
            }

            Network.upload(reservFile, "varaukset.txt");
            return null;
        }
    }
}
