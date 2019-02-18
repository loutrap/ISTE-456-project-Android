package com.louistrapani.wheretowatch;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/*
 * The BrowseTitle class is the secondary feature of the app.
 * It will list all of the Title objects based on selected
 * filters.
 */
public class BrowseTitles extends MainActivity {

    // class fields
    private ListView browseList;
    private List<String> selectedServices = new ArrayList<String>();
    private List<String> alreadyDisplaying;
    private RadioButton allRadio;
    private RadioButton movieRadio;
    private RadioButton tvRadio;

    /*
     * onCreate sets all the views and the opacity of the
     * filter buttons
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_screen);
        browseList = findViewById(R.id.browse_list);
        allRadio = (RadioButton) findViewById(R.id.allRadioButton);
        allRadio.setChecked(true);
        movieRadio = (RadioButton) findViewById(R.id.movieRadioButton);
        tvRadio = (RadioButton) findViewById(R.id.tvRadioButton);

        final ImageView netflixButton = (ImageView) findViewById(R.id.netflixIV);
        netflixButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (netflixButton.getAlpha() < 1) {
                    netflixButton.setAlpha((float)1.0);
                    selectedServices.add("netflix");
                } else {
                    netflixButton.setAlpha((float)0.4);
                    selectedServices.remove("netflix");
                }
            }
        });

        final ImageView huluButton = (ImageView) findViewById(R.id.huluIV);
        huluButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (huluButton.getAlpha() < 1) {
                    huluButton.setAlpha((float)1.0);
                    selectedServices.add("hulu");
                } else {
                    huluButton.setAlpha((float)0.4);
                    selectedServices.remove("hulu");
                }
            }
        });

        final ImageView primeButton = (ImageView) findViewById(R.id.primeIV);
        primeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (primeButton.getAlpha() < 1) {
                    primeButton.setAlpha((float)1.0);
                    selectedServices.add("prime");

                } else {
                    primeButton.setAlpha((float)0.4);
                    selectedServices.remove("prime");

                }
            }
        });

        final ImageView hboButton = (ImageView) findViewById(R.id.hboIV);
        hboButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (hboButton.getAlpha() < 1) {
                    hboButton.setAlpha((float)1.0);
                    selectedServices.add("hbo");
                } else {
                    hboButton.setAlpha((float)0.4);
                    selectedServices.remove("hbo");
                }
            }
        });
    }

    // onResume reloads the data by calling execute again
    @Override
    public void onResume() {
        // After a pause OR at startup
        super.onResume();
        new GetTitleTask().execute();
    }

    // onClick for the browse button. Calls execute
    public void browseTitles(View v) {
        //userTitleSearch = (EditText) findViewById(R.id.userTitleSearch);
        //browseList = findViewById(R.id.browse_list);
        if(selectedServices.isEmpty()) {
            Toast.makeText(getApplicationContext(), "You must tap one or more services to browse.", Toast.LENGTH_LONG).show();

        } else {
            new GetTitleTask().execute();
        }
    }

    /*
     * This class makes the API call and receives the data
     * it then parses that data into Title objects and adds
     * them to the titleList. After that is complete the
     * type of search is checked and then the list is displayed
     * with onClicks on each list item
     */
    @SuppressLint("StaticFieldLeak")
    private class GetTitleTask extends AsyncTask<Void, Void, List<Title>> {

        @Override
        protected List<Title> doInBackground(Void... voids) {
            List<Title> titles = new ArrayList<>();

            String titleData = null;
            try {
                URL url = new URL("http://loutrapani.com/android_project/data.json");
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                try {
                    titleData = streamToString(connection.getInputStream());
                } finally {
                    connection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (titleData != null) {
                try {
                    JSONObject json = new JSONObject(titleData);
                    JSONArray titlesJSON = json.getJSONArray("titles");
                    for (int i = 0; i < titlesJSON.length(); i++) {
                        JSONObject jObject = titlesJSON.getJSONObject(i);
                        alreadyDisplaying =  new ArrayList<>();
                        for (String s : selectedServices) {
                            if(jObject.getString("services").toLowerCase().contains(s.toLowerCase())) {
                                if (!alreadyDisplaying.contains(jObject.getString("title"))) {
                                    alreadyDisplaying.add(jObject.getString("title"));
                                    ArrayList<String> list = new ArrayList<String>();
                                    JSONArray jsonArray = (JSONArray)jObject.get("services");
                                    if (jsonArray != null) {
                                        int len = jsonArray.length();
                                        for (int j=0;j<len;j++){
                                            list.add(jsonArray.get(j).toString());
                                        }
                                    }

                                    Title title = new Title(jObject.getInt("id"),
                                            jObject.getString("type"),
                                            jObject.getString("title"),
                                            jObject.getString("year"),
                                            jObject.getString("director"),
                                            jObject.getString("starring"),
                                            jObject.getString("rating"),
                                            jObject.getString("genre"),
                                            jObject.getString("seasons"),
                                            jObject.getString("synopsis"),
                                            jObject.getString("imgSrc"),
                                            list);
                                    titles.add(title);
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return titles;
        }

        @Override
        protected void onPostExecute(List<Title> titles) {
            List<Title> updatedTitlesList = new ArrayList<>();

            if (movieRadio.isChecked()) {
                for (Title t : titles) {
                    if (t.type.toLowerCase().equals("movie")) {
                        updatedTitlesList.add(t);
                    }
                }
            } else if (tvRadio.isChecked()) {
                for (Title t : titles) {
                    if (t.type.toLowerCase().equals("tv")) {
                        updatedTitlesList.add(t);
                    }
                }
            } else {
                updatedTitlesList = titles;
            }

            MainActivity.TitleAdapter adapter = new MainActivity.TitleAdapter(updatedTitlesList);
            browseList.setAdapter(adapter);
            browseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(BrowseTitles.this, TitleDescription.class);
                    Title selectedTitle = (Title) browseList.getItemAtPosition(position);
                    intent.putExtra("TitleObj", selectedTitle);
                    startActivity(intent);
                }
            });

        }
    }

}
