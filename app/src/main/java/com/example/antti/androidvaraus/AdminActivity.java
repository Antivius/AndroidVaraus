package com.example.antti.androidvaraus;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;


public class AdminActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Button aButton = (Button) findViewById(R.id.manageMoviesButton);
        aButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageMovies(findViewById(R.id.manageMoviesButton));
            }
        });

        Button manageUsersbutton = (Button) findViewById(R.id.manageUsersButton);
        manageUsersbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageUsers(findViewById(R.id.manageMoviesButton));
            }
        });

        final Button manageShowbutton = (Button) findViewById(R.id.manageShowButton);
        manageShowbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageShow(findViewById(R.id.manageShowButton));
            }
        });

        Button aLogout = (Button) findViewById(R.id.aLogout);
        aLogout.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          openLogin(v);
                                      }
                                  }
        );


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin, menu);
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




    public  void manageMovies(View view){
        Intent intent = new Intent(this,AddMovieActivity.class);
        startActivity(intent);
    }

    public  void manageUsers(View view){
        Intent intent = new Intent(this,AddUserActivity.class);
        startActivity(intent);
    }

    public void manageShow(View view){
        Intent intent = new Intent(this,AddShowActivity.class);
        startActivity(intent);
    }


    /**
     * Kirjoitustesti, ei taida toimia
     */

    public void writeStuff(){
        TextView tv = (TextView) findViewById(R.id.adminText);
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/folder");
        myDir.mkdirs();
        String fname = "testFile";
        File file = new File (myDir, fname);
        tv.setText("done");
    }

    private void openLogin(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }





}
