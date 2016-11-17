package com.example.aghaasad.trendreader;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadTask task = new DownloadTask();
        try {

            String result= task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty").get();
            JSONArray jsonArray = new JSONArray(result); // hold all json data

            for(int i=0; i < 50; i++)
            {
                Log.i("Article Id", jsonArray.getString(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public class DownloadTask extends AsyncTask<String, Void , String> // AsyncTask is helping class for UI thread
    {

        @Override
        protected String doInBackground(String... urls) {

            String result= "";
            URL url; // URL type variable
            HttpURLConnection urlConnection = null; // HttpURLConnection variable used to send and receive data over the web

            try
            {
                url= new URL(urls[0]);

                urlConnection =(HttpURLConnection) url.openConnection(); // Obtain a new HttpURLConnection

                InputStream in = urlConnection.getInputStream(); // read bytes from urlConnection source

                // convert byets stream  into character stream
                InputStreamReader reader = new InputStreamReader(in); // an object of InputStreamReader class and pass in(InputStream)as a parameter

                     /* read() method Reads a single character from
                    this reader and returns it as an integer
                    with the two higher-order bytes set  */
                     int data = reader.read();

                while(data != -1)
                {
                    char current = (char)data;// convert int(bytes) to char
                    result += current;
                    data = reader.read();
                }


            }catch(Exception e)
            {

               e.printStackTrace(); //detail about bugs if is...
            }





            return result;
        }
    }
}
