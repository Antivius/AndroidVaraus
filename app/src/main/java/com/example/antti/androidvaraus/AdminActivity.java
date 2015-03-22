package com.example.antti.androidvaraus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;


public class AdminActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Button aButton = (Button) findViewById(R.id.manageMoviesButton);
        aButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageMovies();
            }
        });

        Button manageUsersButton = (Button) findViewById(R.id.manageUsersButton);
        manageUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageUsers();
            }
        });

        final Button manageShowButton = (Button) findViewById(R.id.manageShowButton);
        manageShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageShow();
            }
        });

        Button logoutButton = (Button) findViewById(R.id.aLogout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogin();
            }
        });
    }

    private void manageMovies() {
        Intent intent = new Intent(this, AddMovieActivity.class);
        startActivity(intent);
    }

    private void manageUsers() {
        Intent intent = new Intent(this, AddUserActivity.class);
        startActivity(intent);
    }

    private void manageShow() {
        Intent intent = new Intent(this, AddShowActivity.class);
        startActivity(intent);
    }

    private void openLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
