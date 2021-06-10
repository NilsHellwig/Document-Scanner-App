package com.example.unihub;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageViewer extends AppCompatActivity {

    private ImageView imageView;
    private Bitmap image;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_viewer_layout);
        // The user's able to change the orientation when it comes to viewing the picture, so there's no "setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)"!
        setupImageView();
        // AddNewEntry and ImagesFolder are both able to make an intent to this class named ImageViewer
        // so getIntentAndCheckOrigin will check this because afterwards it's not the same procedure with these two classes.
        getIntentAndCheckOrigin();
        setupActionBar();

    }

    private void getIntentAndCheckOrigin() {
        Intent imageIntent = getIntent();
        Bundle imageIntentBundle = imageIntent.getExtras();
        int selectedImageNumber = imageIntentBundle.getInt(Config.KEY_FOLDER_TO_IMAGE_VIEWER_SELCTED_IMAGE_ID);
        String intentFrom = imageIntentBundle.getString(Config.KEY_FOLDER_TO_IMAGE_VIEWER_INTENTFROM);
        int entryNumber = imageIntentBundle.getInt(Config.KEY_FOLDER_TO_IMAGE_VIEWER_ENTRY_NUMBER);
        checkOrigin(selectedImageNumber, intentFrom, entryNumber);
    }

    private void checkOrigin(int selectedImageNumber, String intentFrom, int entryNumber) {
        checkIfIntentIsFromMainActivity(selectedImageNumber, intentFrom, entryNumber);
        checkIfIntentIsFromAddNewEntry(selectedImageNumber, intentFrom, entryNumber);
    }

    private void checkIfIntentIsFromMainActivity(int selectedImageNumber, String intentFrom, int entryNumber) {
        if (intentFrom.equals(Config.KEY_FOLDER_TO_IMAGE_VIEWER_INTENTFROM_FOLDER_MAIN_ACTIVITY)) {
            // new images will be saved to the ImageTransitSave because sometimes these images take some time
            // to be saved in the internal storage. It's also possible that they're only available in the internal storage
            // the following if-clause will check if it's available in the ImageTransitSave-preLoadedImages
            if (ImageTransitSave.preLoadedImages.containsKey(entryNumber + "_" + selectedImageNumber)) {
                image = ImageTransitSave.preLoadedImages.get(entryNumber + "_" + selectedImageNumber);
            } else {
                image = loadImageFromStorage(entryNumber + "_" + selectedImageNumber);
            }
            imageView.setImageBitmap(image);
            getSupportActionBar().setTitle(Config.LIST_IMAGES_DEFAULT_TITLE_TEXT + (selectedImageNumber + 1));
        }
    }

    // If the intent was created in the AddNewEntry class, this class will get the image from the ImageTransitSave
    private void checkIfIntentIsFromAddNewEntry(int selectedImageNumber, String intentFrom, int entryNumber) {
        if (intentFrom.equals(Config.KEY_FOLDER_TO_IMAGE_VIEWER_INTENTFROM_FOLDER_ADD_NEW_ENTRY)) {
            image = ImageTransitSave.images.get(selectedImageNumber);
            imageView.setImageBitmap(image);
            getSupportActionBar().setTitle(Config.LIST_IMAGES_DEFAULT_TITLE_TEXT + (selectedImageNumber + 1));
        }
    }

    private void setupActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupImageView() {
        imageView = findViewById(R.id.imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_viewer_action_bar_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.share_button:
                shareImage();
                break;
            case android.R.id.home:
                onBackPressed();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareImage() {
        try {
            File cachePath = new File(this.getCacheDir(), Config.SHARE_FILE_PATH_CHILD_FOLDER);
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + Config.SHARE_FILE_PATH_CHILD_PNG_DIR); // overwrites this image every time
            image.compress(Bitmap.CompressFormat.JPEG, Config.JPEG_COMPRESS_QUALITY, stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File imagePath = new File(this.getCacheDir(), Config.SHARE_FILE_PATH_CHILD_FOLDER);
        File newFile = new File(imagePath, Config.SHARE_FILE_PATH_CHILD_PNG);
        Uri contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + Config.SHARE_FILE_PATH_PROVIDER, newFile);
        if (contentUri != null) {
            createShareIntent(contentUri);
        }
    }

    private void createShareIntent(Uri contentUri) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
        shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        shareIntent.setType(Config.SHARE_IMAGE_FILE_TYPE);
        startActivity(Intent.createChooser(shareIntent, Config.SHARE_POP_UP_TEXT));
    }

    // this method is able to load an image from the internal storage just
    // by using the name of the name of the file
    // both the method which is able to load images from the internal storage
    // and the method to save images to the internal storage are using the
    // same filepath on every device
    private Bitmap loadImageFromStorage(String jpgName) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir(Config.CUSTOM_DIR_FOR_IMAGES, Context.MODE_PRIVATE);
        try {
            File f = new File(directory, jpgName);
            Bitmap loadedImage = BitmapFactory.decodeStream(new FileInputStream(f));
            return loadedImage;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}