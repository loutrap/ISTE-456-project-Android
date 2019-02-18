package com.louistrapani.wheretowatch;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/*
 *  TitleDescription class display various info for a single
 *  specific Title that was selected in the ListView
 */
public class TitleDescription extends Activity {

    // class fields
    public static List<MainActivity.Title> addedToWatchList = new ArrayList<MainActivity.Title>();
    protected MainActivity.Title title;
    Button watchListButton;

    /*
     * onCreate sets all the views, iamges and buttons
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title_info);

        watchListButton = (Button) findViewById(R.id.watch_list_button);

        TextView titleName = (TextView) findViewById(R.id.title_info_name);
        TextView year = (TextView) findViewById(R.id.title_info_year);
        TextView info = (TextView) findViewById(R.id.title_info_info);
        TextView genre = (TextView) findViewById(R.id.title_info_genre);
        ImageView titleImage = (ImageView) findViewById(R.id.title_info_image);
        TextView titleDescription = (TextView) findViewById(R.id.title_info_description);
        TextView titleStarring = (TextView) findViewById(R.id.title_info_starring);
        ImageView service1 = (ImageView) findViewById(R.id.service1image);
        ImageView service2 = (ImageView) findViewById(R.id.service2image);
        ImageView service3 = (ImageView) findViewById(R.id.service3image);
        ImageView service4 = (ImageView) findViewById(R.id.service4image);
        List<ImageView> ivList = new ArrayList<>();
        ivList.add(service1);
        ivList.add(service2);
        ivList.add(service3);
        ivList.add(service4);



        title = (MainActivity.Title) getIntent().getSerializableExtra("TitleObj");

        titleName.setText(title.title);
        year.setText("Released: " + title.year);
        if (title.type.toLowerCase().equals("tv")) {
            info.setText(title.seasons);
        } else {
            info.setText("Director: " + title.director);
        }
        genre.setText("Genre: " + title.genre);

        // set the title image
        String uri = "@drawable/" + title.imgSrc;  // where myresource (without the extension) is the file
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        Drawable res = getResources().getDrawable(imageResource);

        titleImage.setImageDrawable(res);
        titleDescription.setText(title.synopsis);
        titleStarring.setText(title.starring);

        int counter = 1;

        for (int i = 0; i < title.services.size(); i++) {

            String smallUri = "@drawable/" + title.services.get(i).toLowerCase();  // where myresource (without the extension) is the file
            int smallImageResource = getResources().getIdentifier(smallUri, null, getPackageName());
            Drawable smallRes = getResources().getDrawable(smallImageResource);
            ivList.get(i).setImageDrawable(smallRes);
        }

        if (addedToWatchList.isEmpty()) {
            watchListButton.setText(R.string.add_watch_list);
        } else {
            for (int i = 0; i < addedToWatchList.size(); i++) {
                if (addedToWatchList.get(i).equals(title) || addedToWatchList.get(i).id == title.id) {
                    watchListButton.setText(R.string.remove_watch_list);
                    return;
                } else {
                    watchListButton.setText(R.string.add_watch_list);

                }
            }
        }

    }


    /*
     * this class handles the WatchList button text and functionality.
     * It will add or remove Title items from the list.
     */
    public void addToWatchList(View v) {
        if (watchListButton.getText().equals("Add to WatchList")) {
            System.out.println("ADD TO WATCH");
            if (addedToWatchList.size() == 0) {
                System.out.println("inside base case");
                addedToWatchList.add(title);
                watchListButton.setText(R.string.remove_watch_list);
                Toast.makeText(getApplicationContext(), title.title + " was added to your WatchList", Toast.LENGTH_LONG).show();
            } else {
                for (int i = 0; i < addedToWatchList.size(); i++) {
                    System.out.println("inside for loop size if " + addedToWatchList.size());
                    if (addedToWatchList.get(i).id != title.id) {
                        System.out.println("inside if, adding " + title.title);
                        addedToWatchList.add(title);
                        watchListButton.setText(R.string.remove_watch_list);
                        Toast.makeText(getApplicationContext(), title.title + " was added to your WatchList", Toast.LENGTH_LONG).show();
                        return;

                    }
                }
            }
        } else {
            System.out.println("REMOVE FROM WATCH");

            for (int i = 0; i < addedToWatchList.size(); i++) {
                if (addedToWatchList.get(i).equals(title) || addedToWatchList.get(i).id == title.id) {
                    addedToWatchList.remove(addedToWatchList.get(i));
                    watchListButton.setText(R.string.add_watch_list);
                    Toast.makeText(getApplicationContext(), title.title + " was removed from your WatchList", Toast.LENGTH_LONG).show();
                }
            }
        }

    }
}
