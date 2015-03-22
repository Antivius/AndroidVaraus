package com.example.antti.androidvaraus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Kirjautumisruutu
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {
    private UserLoginTask mAuthTask1 = null;
    private UserRegisterTask mAuthTask2 = null;

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    public final static String EXTRA_MESSAGE = "com.example.antti.androidvaraus.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Kirjautumiskentät
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        // Napit
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        Button mEmailRegisterButton = (Button) findViewById(R.id.register_button);
        mEmailRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Avaa pääikkunan.
     */
    private void openMain(String email){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_MESSAGE, email);
        intent.putExtra("kutsuja", "LoginActivity");
        startActivity(intent);
    }

    /**
     * Avaa hallintapaneelin.
     */
    private void openAdmin(){
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
    }

    /**
     * Tarkistaa rekisteröintiä varten annetut arvot ja aloittaa UserRegisterTaskin,
     * jos kaikki on kunnossa.
     */
    private void register() {
        if (mAuthTask2 != null) {
            return;
        }

        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // Virheen sattuessa siirrä focus virheelliseen kenttään.
            focusView.requestFocus();
        } else {
            // Näytä latausspinneri ja lähetä tieto uudesta käyttäjästä
            showProgress(true);
            mAuthTask2 = new UserRegisterTask(email, password);
            mAuthTask2.execute((Void) null);
        }

    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * Yrittää kirjautua annetuilla arvoilla. Tarkistaa arvojen perusvaatimukset,
     * kuten rekisteröinnissä.
     */
    public void attemptLogin() {
        if (mAuthTask1 != null) {
            return;
        }

        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask1 = new UserLoginTask(email, password);
            mAuthTask1.execute((Void) null);
        }
    }

    /**
     * Yksinkertainen perustesti, onko sähköpostiosoite validi.
     * @param email testattava sähköposti
     * @return boolean, onko sähköpostiosoite kunnollinen
     */
    private boolean isEmailValid(String email) {
        return email.contains("@") && !email.contains(":");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Näytä latausspinneri kirjautumisen sijaan.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // Animoi transitio, jos API >= HONEYCOMB_MR2
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // Muuten vain piilota kirjautuminen ja näytä spinneri
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Hae käyttäjän tallennetut sähköpostiosoitteet.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    /**
     * Laita tallennetut sähköpostiosoitteet kenttään automaattista täyttöä varten.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    /**
     * Lataa käyttäjätiedot verkosta, ja kirjaudu sisään, jos käyttäjä löytyy.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private final String mEmail;
        private final String mPassword;
        private String admin = "admin@admin";

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                String userInfo = Network.download(new URL(Network.USERS_URL));

                for (String line : userInfo.split("\n")) {
                    if (line.isEmpty()) {
                        continue;
                    }
                    String[] s = line.split(":");
                    if(s[0].equals(mEmail) && s[1].equals(mPassword)){
                        return true;
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask1 = null;
            showProgress(false);

            if (success) {
                if(mEmail.equals(admin)){
                    openAdmin();
                    finish();
                }
                else{
                    openMain(mEmail);
                    finish();
                }

            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask1 = null;
            showProgress(false);
        }
    }

    /**
     * Lataa käyttäjätiedot verkosta, tarkista ettei samaa käyttäjää ole, lähetä uusi
     * käyttäjälista, joka sisältää uuden käyttäjän, takaisin.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserRegisterTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String userInfo;

            try {
                userInfo = Network.download(new URL(Network.USERS_URL));
                for (String line : userInfo.split("\n")) {
                    if (line.isEmpty()) {
                        continue;
                    }
                    String[] s = line.split(":");
                    if(s[0].equals(mEmail)){
                        return false;
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return false;
            }

            return Network.upload(userInfo + "\n" + mEmail + ":" + mPassword, "usrnamepw.txt");
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask2 = null;
            showProgress(false);

            if (success) {
                attemptLogin();
            } else {
                mEmailView.setError(getString(R.string.error_username_in_use));
                mEmailView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask2 = null;
            showProgress(false);
        }
    }
}



