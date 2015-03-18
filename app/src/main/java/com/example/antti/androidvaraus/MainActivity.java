package com.example.antti.androidvaraus;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    public final static String EXTRA_MESSAGE2 = "com.example.antti.androidvaraus.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String nimi = intent.getStringExtra(LoginActivity.EXTRA_MESSAGE1);
        String[] osat = nimi.split("@");
        TextView textView = (TextView) findViewById(R.id.tervetuloa);
        textView.setText("Hei " + osat[0] + "!");


        Spinner spinner = (Spinner) findViewById(R.id.esitykset);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.elokuva_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Button logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogin(v);
            }
        }
        );

        Button varaa = (Button) findViewById(R.id.varaus_button);
        varaa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVaraus(v);
            }
        });
    }

    private void openLogin(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void openVaraus(View view){
        Intent intent = new Intent(this, VarausActivity.class);
        Spinner spinner = (Spinner) findViewById(R.id.esitykset);
        String message = spinner.getSelectedItem().toString();
        intent.putExtra(EXTRA_MESSAGE2, message);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
