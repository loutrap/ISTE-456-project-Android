package com.louistrapani.wheretowatch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import android.support.v7.app.ActionBar;
import android.widget.Toast;


/*
 * The MainActivity class is the home screen of the app
 * as well as the main search feature. This class also
 * has the nested class Title as well as the adapter class
 * that the other classes use. Search will take the users
 * input and return a list containing that search query.
 * It will then display the Title object as ListView items.
 */
public class MainActivity extends Activity {

    // class fields
    protected ListView titleList;
    private EditText userTitleSearch;
    private RadioButton movieRadio;
    private RadioButton tvRadio;

    /*
     * onCreate will set the layout, instantiate
     * the views and check the All radio button.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        RadioButton allRadio = (RadioButton) findViewById(R.id.allRadioButton);
        allRadio.setChecked(true);
        movieRadio = (RadioButton) findViewById(R.id.movieRadioButton);
        tvRadio = (RadioButton) findViewById(R.id.tvRadioButton);
    }


    /*
     * This class handles code to dismiss keyboard on click
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    /*
     * This class handles code to dismiss keyboard on click
     */
    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    /*
     * onClick function that will check the user input
     * when the search button is clicked. It will then
     * call execute on the title task.
     */
    public void titleSearch(View v) {
        userTitleSearch = (EditText) findViewById(R.id.userTitleSearch);
        titleList = findViewById(R.id.title_list);
        if (userTitleSearch.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), "You must enter a Title to search", Toast.LENGTH_LONG).show();
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
    protected class GetTitleTask extends AsyncTask<Void, Void, List<Title>> {


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
                        if (jObject.getString("title").toLowerCase().contains(userTitleSearch.getText().toString().toLowerCase())) {


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


            final TitleAdapter adapter = new TitleAdapter(updatedTitlesList);
            if (adapter.getCount() == 0) {
                Toast.makeText(getApplicationContext(), "There are no Titles that match your search query", Toast.LENGTH_LONG).show();
            }
            titleList.setAdapter(adapter);
            titleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, TitleDescription.class);
                    Title selectedTitle = (Title) titleList.getItemAtPosition(position);
                    intent.putExtra("TitleObj", selectedTitle);
                    startActivity(intent);
                }
            });

        }
    }


    /*
     * The Title class for each item in the JSON file
     */
    protected static class Title implements Serializable {
        protected int id;
        protected String type;
        protected String title;
        protected String year;
        protected String director;
        protected String starring;
        protected String rating;
        protected String genre;
        protected String seasons;
        protected String synopsis;
        protected String imgSrc;
        protected List<String> services;
        protected boolean inWatchList;

        public Title(int id, String type, String title, String year, String director, String starring, String rating, String genre, String seasons, String synopsis, String imgSrc, List<String> services) {
            this.id = id;
            this.type = type;
            this.title = title;
            this.year = year;
            this.director = director;
            this.starring = starring;
            this.rating = rating;
            this.genre = genre;
            this.seasons = seasons;
            this.synopsis = synopsis;
            this.imgSrc = imgSrc;
            this.services = services;
            this.inWatchList = false;
        }
    }

    /*
     * The adapter class will display the Title info in the view.
     * Depending on the type of Title it will display different
     * information.
     */
    class TitleAdapter extends BaseAdapter {
        private List<Title> titles;


        public TitleAdapter(List<Title> titles) {
            this.titles = titles;
        }

        @Override
        public int getCount() {
            return titles.size();
        }

        @Override
        public Title getItem(int position) {
            return titles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item, parent, false);
            }

            Title title = getItem(position);
            TextView nameView = convertView.findViewById(R.id.title_name);
            TextView directorView = convertView.findViewById(R.id.title_director);
            TextView typeView = convertView.findViewById(R.id.title_type);
            ImageView imgView = convertView.findViewById(R.id.title_image);
            String uri = "@drawable/" + title.imgSrc;  // where myresource (without the extension) is the file

            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            Drawable res = getResources().getDrawable(imageResource);

            if (title.type.toLowerCase().equals("tv")) {
                nameView.setText(title.title);
                directorView.setText(title.seasons);
                typeView.setText(title.genre);
                imgView.setImageDrawable(res);
            } else {
                nameView.setText(title.title);
                directorView.setText(title.starring);
                typeView.setText("Rated " + title.rating);
                imgView.setImageDrawable(res);
            }


            return convertView;
        }
    }


    // Helper function
    protected static String streamToString(InputStream in) throws IOException {
        StringBuilder data = new StringBuilder();
        byte[] buffer = new byte[1000];
        int len = in.read(buffer);
        while (len != -1) {
            data.append(new String(buffer, 0, len));
            len = in.read(buffer);
        }
        in.close();
        return data.toString();
    }


    // Create Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    // Create intents when when item clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.searchOption:
                Intent homeScreenSearch =
                        new Intent(this, MainActivity.class);
                startActivity(homeScreenSearch); // start the Activity
                return true;

            case R.id.browseOption:
                Intent browseTitlesScreen =
                        new Intent(this, BrowseTitles.class);
                startActivity(browseTitlesScreen); // start the Activity
                return true;
            case R.id.watchListOption:
                Intent watchListScreen =
                        new Intent(this, WatchList.class);
                startActivity(watchListScreen); // start the Activity
                return true;
            case R.id.aboutOption:
                AboutDialog dialog = new AboutDialog();
                dialog.show(getFragmentManager(), "dialog");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

