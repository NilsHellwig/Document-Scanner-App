package com.example.unihub;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ImagesFolder extends AppCompatActivity {

    private ListView folderList;
    private ArrayList<String> folderItemStrings;
    private ArrayAdapter<String> folderAdapter;
    private int entryNumber;
    private int amountOfImages;
    private String entryName;
    private String lectureName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // All Activities except the ImageViewer are only made to be used in portrait mode.
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.folder_layout);
        getIntentValuesAndSaveThem();
        setupActionBar(entryName, lectureName);
        setupFolderList();
        inputImages();
        setupClickListener();
    }

    // This onOptionsItemSelected method is just for the back button.
    // There's no other icon in the Action Bar.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // the most important element of the ImageFolder Activity is the list which will show
    // you all the images in a list
    private void setupFolderList() {
        folderList = findViewById(R.id.folder_list);
        folderItemStrings = new ArrayList<>();
        folderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, folderItemStrings);
        folderList.setAdapter(folderAdapter);
    }

    // The ImagesFolder Activity will get a lot of informations from the MainActivity
    // about the Entry
    private void getIntentValuesAndSaveThem() {
        Intent intentFromMainActivity = getIntent();
        Bundle infosAboutImages = intentFromMainActivity.getExtras();
        entryNumber = infosAboutImages.getInt(Config.KEY_MAIN_ACTIVITY_TO_FOLDER_IMAGE_NUMBER);
        amountOfImages = infosAboutImages.getInt(Config.KEY_MAIN_ACTIVITY_TO_FOLDER_AMOUNT_OF_IMAGES);
        entryName = infosAboutImages.getString(Config.KEY_MAIN_ACTIVITY_TO_FOLDER_ENTRY_NAME);
        lectureName = infosAboutImages.getString(Config.KEY_MAIN_ACTIVITY_TO_FOLDER_LECTURE_NAME);
    }

    // The ActionBar of this Activity will show informations about
    // the selected Entry like the name of the entry or the name of the lecture
    private void setupActionBar(String entryName, String lectureName) {
        // setDisplayHomeAsUpEnabled(true); will enable the functionality that
        // you will get to the MainActivity if you press the back button in the actionbar,
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(entryName);
        getSupportActionBar().setSubtitle(lectureName);
    }

    // inputImages will add the Strings to the ListView / ArrayList of the ListView
    // the amount of images is the same as the amount of listView Elements
    private void inputImages() {
        for (int i = 0; i < amountOfImages; i++) {
            folderItemStrings.add(Config.LIST_IMAGES_DEFAULT_TITLE_TEXT + (i + 1));
        }
        folderAdapter.notifyDataSetChanged();
    }

    private void setupClickListener() {
        folderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                createIntentToShowImageInImageView(position);
            }
        });
    }

    // This method will share informations about the selected Image in the folder
    private void createIntentToShowImageInImageView(int position) {
        Intent showImageIntent = new Intent(ImagesFolder.this, ImageViewer.class);
        showImageIntent.putExtra(Config.KEY_FOLDER_TO_IMAGE_VIEWER_SELCTED_IMAGE_ID, position);
        // this .putExtra() call is used to share which Class made the intent. The ImageViewer Class is also able to get an Intent from the AddNewEntry.class
        showImageIntent.putExtra(Config.KEY_FOLDER_TO_IMAGE_VIEWER_INTENTFROM, Config.KEY_FOLDER_TO_IMAGE_VIEWER_INTENTFROM_FOLDER_MAIN_ACTIVITY);
        // The ImageView might load images from the internal storage. The entryNum is used to get the filepath of the selected image
        showImageIntent.putExtra(Config.KEY_FOLDER_TO_IMAGE_VIEWER_ENTRY_NUMBER, entryNumber);
        startActivity(showImageIntent);
    }
}