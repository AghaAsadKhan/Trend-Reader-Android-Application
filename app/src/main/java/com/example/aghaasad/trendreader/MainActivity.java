package com.example.aghaasad.trendreader;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    Map<Integer,String> articleUrls= new HashMap<Integer,String>();
    Map<Integer,String> articleTitles= new HashMap<Integer,String>();
    ArrayList<Integer> articleIds= new ArrayList<Integer>();
    SQLiteDatabase articlesDB;
    ArrayList<String> titles= new ArrayList<String>();
    ArrayAdapter arrayAdapter;
    ArrayList<String> urls = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView= (ListView)findViewById(R.id.listView);
        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,titles);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                intent.putExtra("articleUrl", urls.get(position));
                startActivity(intent);
            }
        });

        articlesDB = this.openOrCreateDatabase("Articles", MODE_PRIVATE, null);
        articlesDB.execSQL("CREATE TABLE IF NOT EXISTS articles (id INTEGER PRIMARY KEY, articleId INTEGER, url VARCHAR , title VARCHAR, content VARCHAR)");

        DownloadTask task = new DownloadTask();
        try {

            String result= task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty").get();
            JSONArray jsonArray = new JSONArray(result); // hold all json data
            articlesDB.execSQL("DELETE FROM articles");// delete previous record because we dont want twice data
            for(int i=0; i < 30; i++)

            {
                String articleId =jsonArray.getString(i);
                DownloadTask getArticle= new DownloadTask();
                String articleInfo = getArticle.execute("https://hacker-news.firebaseio.com/v0/item/"+ articleId + ".json?print=pretty").get();
                JSONObject jsonObject = new JSONObject(articleInfo);
                String articleTitle = jsonObject.getString("title");
                String articleUrl = jsonObject.getString("url");

                articleIds.add(Integer.valueOf(articleId));
                articleTitles.put(Integer.valueOf(articleId), articleTitle);
                articleUrls.put(Integer.valueOf(articleId), articleUrl);
                String sql = "INSERT INTO articles (articleId, url, title) VALUES (?,  ?, ?)";
                SQLiteStatement statement= articlesDB.compileStatement(sql);
                statement.bindString(1,articleId);
                statement.bindString(2,articleUrl);
                statement.bindString(3,articleTitle);
                statement.execute();


            }
            Cursor c= articlesDB.rawQuery("SELECT * FROM articles ORDER BY articleId DESC", null);

            int articleIdIndex=  c.getColumnIndex("articleId");
            int urlIndex=  c.getColumnIndex("url");
            int titleIndex=c.getColumnIndex("title");

            c.moveToFirst();
            titles.clear();
            urls.clear();

            while(c != null)
            {   titles.add(c.getString(titleIndex));
                urls.add(c.getString(urlIndex));


                c.moveToNext();

            }
           arrayAdapter.notifyDataSetChanged();
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
