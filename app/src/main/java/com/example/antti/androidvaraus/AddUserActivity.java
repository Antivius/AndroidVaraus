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
import java.util.HashMap;
import java.util.Map;


public class AddUserActivity extends ActionBarActivity {
    private EditText addUserText;
    private EditText addPasswordText;
    private ArrayAdapter<String> deleteAdapter;
    private Map<String, String> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        addUserText = (EditText) findViewById(R.id.add_user_text);
        addPasswordText = (EditText) findViewById(R.id.add_password_text);

        Button addUserButton = (Button) findViewById(R.id.addUserButton);
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();
            }
        });

        final Spinner spinner = (Spinner) findViewById(R.id.admin_delete_user_spinner);
        deleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        deleteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(deleteAdapter);

        Button deleteUserButton = (Button) findViewById(R.id.deleteUserButton);
        deleteUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser(spinner);
            }
        });

        try {
            new GetUsersTask().execute(new URL(Network.USERS_URL));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    private void addUser() {
        String email = addUserText.getText().toString();
        String password = addPasswordText.getText().toString();

        if (email.contains("@") && !email.contains(":")) {
            if (password.length() > 4) {
                if (users.keySet().contains(email)) {
                    addUserText.setError(getString(R.string.error_username_in_use));
                    addUserText.requestFocus();
                    return;
                }

                users.put(email, email + ":" + password);
                new UpdateUsersTask().execute();
                String text = email + " lisätty";
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                toast.show();

                deleteAdapter.add(email);
                addPasswordText.setText("");
                addUserText.setText("");
                addUserText.requestFocus();
            } else {
                addPasswordText.setError(getString(R.string.error_invalid_password));
                addPasswordText.requestFocus();
            }
        } else {
            if (password.length() <= 4) {
                addPasswordText.setError(getString(R.string.error_invalid_password));
            }
            addUserText.setError(getString(R.string.error_invalid_email));
            addUserText.requestFocus();
        }

    }

    private void deleteUser(Spinner spinner) {
        String user = spinner.getSelectedItem().toString();

        if (user.equals("admin@admin:admin")) {
            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context, R.string.dont_remove_admin, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        users.remove(user);
        deleteAdapter.remove(user);
        new UpdateUsersTask().execute();
        new UpdateReservTask().execute(user);

        String text = user + " poistettu";
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private class GetUsersTask extends AsyncTask<URL, Void, Void> {
        protected Void doInBackground(URL... urls) {
            users = new HashMap<>();
            if (urls.length != 1) {
                return null;
            }

            String usersFile = Network.download(urls[0]);
            for (String user : usersFile.split("\n")) {
                if (user.length() > 0 && !user.equals("admin@admin:admin")) {
                    users.put(user.split(":", 2)[0], user);
                }
            }

            return null;
        }

        protected void onPostExecute(Void _) {
            deleteAdapter.addAll(users.keySet());
        }
    }

    private class UpdateUsersTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... _) {
            StringBuilder sb = new StringBuilder("admin@admin:admin\n");
            for (String user : users.values()) {
                sb.append(user);
                sb.append("\n");
            }

            Network.upload(sb.toString(), "usrnamepw.txt");
            return null;
        }
    }

    private class UpdateReservTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... strings) {
            if (strings.length != 1) {
                return null;
            }

            try {
                String reservations = Network.download(new URL(Network.RESERV_URL));
                StringBuilder sb = new StringBuilder(reservations.length());
                for (String line : reservations.split("\n")) {
                    if (line.isEmpty()) {
                        continue;
                    }
                    String reservationShowId = line.split(":", 3)[1];
                    if (!reservationShowId.equals(strings[0])) {
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
