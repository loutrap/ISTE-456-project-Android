package com.louistrapani.wheretowatch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/*
 * WatchList class displays Titles from WatchList list
 * Which can be selected and viewed
 */
public class WatchList extends MainActivity {

    // class fields
    private ListView watchList;
    protected List<Title> watchListTitles;

    // onCreate sets views and executes asynctask
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watch_list);
        watchListTitles = TitleDescription.addedToWatchList;
        watchList = findViewById(R.id.watch_list_listview);
        new GetTitleTask().execute();
        watchList = (ListView) findViewById(R.id.watch_list_listview);
    }


    // reloads data
    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        new GetTitleTask().execute();
    }


    // Displays the Title items in a ListView
    @SuppressLint("StaticFieldLeak")
    private class GetTitleTask extends AsyncTask<Void, Void, List<Title>> {

        @Override
        protected List<Title> doInBackground(Void... voids) {
            List<Title> titles = new ArrayList<>();
            return watchListTitles;
        }

        @Override
        protected void onPostExecute(List<Title> titles) {
            final MainActivity.TitleAdapter adapter = new MainActivity.TitleAdapter(titles);
            for (Title t : watchListTitles) {
                System.out.println(t.title);
            }
            if (watchListTitles.size() == 0) {
                TextView emptyListTV = (TextView) findViewById(R.id.empty_watch_list_tv);
                emptyListTV.setText(R.string.empty_watch_list);
            }
            watchList.setAdapter(adapter);
            watchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(WatchList.this, TitleDescription.class);
                    Title selectedTitle = (Title) watchList.getItemAtPosition(position);
                    intent.putExtra("TitleObj", selectedTitle);
                    startActivity(intent);
                }
            });


        }
    }
}
