package com.example.antti.androidvaraus;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class AddUserActivity extends ActionBarActivity {

    private EditText add_user_text;
    private String USER_URL = "http://woodcomb.aleksib.fi/files/usrnamepw.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        add_user_text = (EditText) findViewById(R.id.add_user_text);

        Button addUserButton = (Button) findViewById(R.id.addUserButton);
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser(findViewById(R.id.addUserButton));
            }
        });

        Button deleteUserButton = (Button) findViewById(R.id.deleteUserButton);
        deleteUserButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                deleteUser(v);
            }
        });

        //TODO: hae käyttäjälista verkosta. Ei admin?
        Spinner spinner = (Spinner) findViewById(R.id.admin_delete_user_spinner);
        ArrayList<String> arrayList = new ArrayList<String>();
        try {

            AssetManager am = getAssets();
            BufferedReader in = null;
            in = new BufferedReader(new InputStreamReader(am.open("usrnamepw.txt")));
            String line;
            while((line = in.readLine()) != null){
                arrayList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    private void addUser(View view){
        String user = add_user_text.getText().toString();
        String[] check = user.split(":");

        /**
        int pituus = check.length;
        Context context = getApplicationContext();
        String message = Integer.toString(pituus);
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
         */

        if((check.length == 1) || (check.length > 2)){
            Context context = getApplicationContext();
            CharSequence text = "Erota käyttäjänimi ja salasan";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            return;
        }
        if(check[0].contains("@")){
            if(check[1].length()>4){
                //TODO: lisää user usrnamepw.txt
                Context context = getApplicationContext();
                CharSequence text = "Lisätty:"+ user;
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            else {
                Context context = getApplicationContext();
                CharSequence text = "salasana on liian lyhyt";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }
        else {
            Context context = getApplicationContext();
            CharSequence text = "@ merkki vaaditaan";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

    }

    private void deleteUser(View view){
        Spinner spinner = (Spinner) findViewById(R.id.admin_delete_user_spinner);
        String user = spinner.getSelectedItem().toString();

        if(user.equals("admin@admin:admin")){
            Context context = getApplicationContext();
            CharSequence text = "ylläpitäjää ei voi poistaa";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }
        ArrayList<String> arrayList = new ArrayList<String>();

        //TODO: hae lista, tee uusi lista ja vertaile poistettavaan, lähetä uusi lista takaisin

        try {

            AssetManager am = getAssets();
            BufferedReader in = null;
            in = new BufferedReader(new InputStreamReader(am.open("usrnamepw.txt")));
            String line;
            while((line = in.readLine()) != null){
                if(!line.equals(user)){
                    arrayList.add(line);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Context context = getApplicationContext();
        CharSequence text = "Poistettu: "+user;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
}




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_user, menu);
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
}
