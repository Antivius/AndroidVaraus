package com.example.antti.androidvaraus;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Handles networking requests.
 */
public class Network {
    public static final String MOVIE_URL = "http://woodcomb.aleksib.fi/files/elokuvat.txt";
    public static final String SHOW_URL = "http://woodcomb.aleksib.fi/files/naytokset.txt";
    public static final String RESERV_URL = "http://woodcomb.aleksib.fi/files/varaukset.txt";
    public static final String USERS_URL = "http://woodcomb.aleksib.fi/files/usrnamepw.txt";
    private static final String UPLOAD_URL = "http://woodcomb.aleksib.fi/up";

    private static final String POST_START =
        "-----------------------------175223208916369\r\n" +
        "Content-Disposition: form-data; name=\"file\"; filename=\"";
    private static final String POST_MIDDLE = "\"\r\nContent-Type: text/plain\r\n\r\n";
    private static final String POST_END = "\r\n-----------------------------175223208916369--\r\n\r\n";

    public static String download(URL url) {
        StringBuilder buffer = new StringBuilder(512);

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            try {
                InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                for (;;) {
                    int ret = inputStream.read();
                    if (ret != -1) {
                        buffer.append((char)ret);
                    } else {
                        inputStream.close();
                        break;
                    }
                }
            } finally {
                connection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return buffer.toString();
    }

    public static boolean upload(String data, String filename) {
        boolean status = false;
        StringBuilder buffer = new StringBuilder(256);
        buffer.append(POST_START);
        buffer.append(filename);
        buffer.append(POST_MIDDLE);
        buffer.append(data);
        buffer.append(POST_END);

        try {
            HttpURLConnection connection = (HttpURLConnection)(new URL(UPLOAD_URL)).openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setFixedLengthStreamingMode(buffer.length());
            connection.setRequestProperty(
                    "Content-Type",
                    "multipart/form-data; boundary=---------------------------175223208916369");

            try {
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(buffer.toString());
                writer.flush();
                writer.close();

                buffer = new StringBuilder();
                InputStreamReader inputStream = new InputStreamReader(connection.getInputStream());
                for (;;) {
                    int ret = inputStream.read();
                    if (ret != -1) {
                        buffer.append((char)ret);
                    } else {
                        inputStream.close();
                        break;
                    }
                }

                if (buffer.toString().contains("1")) {
                    status = true;
                }
            } finally {
                connection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return status;
    }
}
