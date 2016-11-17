package com.example.aghaasad.trendreader;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




    }
     //task works on background thread and result shows on UI thread
    public class DownloadTask extends AsyncTask<String, Void , String>
    {

        @Override
        protected String doInBackground(String... urls) {

            String result= "";
            URL url;
            HttpURLConnection urlConnection = null;

            try
            {
                url= new URL(urls[0]);

            }catch(Exception e)
            {
                //for identifying bugs information
               e.printStackTrace();
            }




            return null;
        }
    }
}
