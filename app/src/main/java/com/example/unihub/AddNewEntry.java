package com.example.unihub;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static com.example.unihub.Config.RESULT_LOAD_IMAGE;

public class AddNewEntry extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Button takePicture;
    private Button fromGalery;

    private EditText inputName;
    private EditText inputLecture;
    private EditText inputDate;

    private CheckBox isFormular;
    private CheckBox isNote;
    private CheckBox isWorksheet;

    private DocumentType docType;
    private String documentType;
    private String currentPhotoPath;
    private ListView pictureListView;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> pictureArrayList;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private int amountOfImages = 0;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // All Activities except the ImageViewer are only made to be used in portrait mode.
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.add_new_entry_layout);
        setupViews();
        // The ImageTransitSave images will be deleted this way, so if you start this activity because you want to
        // create a new Entry, it's possible that there are still some images in the ImageTransitSave.images .
        setupActionBar();

        initViews();
        initListView();
    }

    private void setupViews() {
        pictureListView = findViewById(R.id.list_of_images);
        pictureArrayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.pictures_list_layout, pictureArrayList);
        pictureListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        // ImageTransitSave.images need to delete images from the last usage of the AddNewEntry.
        ImageTransitSave.images.clear();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_new_entry_action_bar_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.done_button) {
            String newDocumentName = inputName.getText().toString();
            // The user doesn't need to enter a newlectureName. Because
            // you might have a form - and forms might not have something to do
            // with a lecture
            String newLectureName = inputLecture.getText().toString();
            String newDate = inputDate.getText().toString();
            boolean singleChoiceTest = singleChoiceTest();
            if (newDocumentName.length() != 0 && newDate.length() != 0 && singleChoiceTest && ImageTransitSave.images.size() > 0) {
                // playSuccessSound will play a little sound, to notify the user also with an acoustic signal that the entry was added.
                playSuccessSound();
                createNewEntry(newDocumentName, newLectureName, newDate);
                createIconForEntry();
            } else {
                // playErrorSound will play a little error sound, so the user will also hear (not just see the toast) that something isn't correct so far
                playErrorSound();
                createIndividualEntryToast(singleChoiceTest, ImageTransitSave.images.size(), newDate, newDocumentName);
            }
        }

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void createIconForEntry() {
        // There will be icons in the list items (in the MainActivity)
        // The filesize of the images for the ImageViewer (to see it in fullscreen mode) are way too big.
        // This method will always save a smaller one (100x100). It's 100 times smaller when it comes to the filesize.
        // This way it's much easier for the MainActivity to load many images
        // It will save one image as an image for icon usage (Always the first Image of ImageTransitSave.images(0)).
        // Bitmap.createScaledBitmap will compress this image to a 100x100 pixel bitmap
        Bitmap imageForIconSave = ImageTransitSave.images.get(0);
        Bitmap finalImageForIcon;
        finalImageForIcon = Bitmap.createScaledBitmap(imageForIconSave, Config.IMAGE_ICON_WIDTH_AND_HEIGHT, Config.IMAGE_ICON_WIDTH_AND_HEIGHT, Config.IMAGE_ICON_SCALED_BITMAP_FILTER);
        ImageTransitSave.iconForNewEntry = finalImageForIcon;
    }

    private void setupActionBar() {
        // setDisplayHomeAsUpEnabled(true); will enable the functionality that
        // you will get to the MainActivity if you press the back button in the actionbar,
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Config.addNewEntryTitle);
    }

    private void createIndividualEntryToast(boolean singleChoiceTest, int amountOfImages, String date, String newDocumentName) {
        // The App will show you an individual Toast if there's just one missing Entry of the whole Entry
        // The App will show you a simple toast which says that there are several ones missing if there's more than one wrong entry.
        if (checkIfMoreThanOneEntryIsMissing(singleChoiceTest, amountOfImages, date, newDocumentName)) {
            showToastForTooManyMissingEntries();
        } else {
            createToastForOneMissingEntry(singleChoiceTest, amountOfImages, date);
        }
    }

    private boolean checkIfMoreThanOneEntryIsMissing(boolean singleChoiceTest, int amountOfImages, String date, String newDocumentName) {
        int missingEntriesCounter = 0;
        if (!singleChoiceTest) {
            missingEntriesCounter++;
        }
        if (amountOfImages == 0) {
            missingEntriesCounter++;
        }
        if (date.equals("")) {
            missingEntriesCounter++;
        }
        if (newDocumentName.equals("")) {
            missingEntriesCounter++;
        }
        // missingEntries will count the amount of missing entries in the whole Entry
        // it will return true (which means more than one's missing)
        // else it will return false
        if (missingEntriesCounter > 1) {
            return true;
        }
        return false;
    }

    // If this method get called we already know that there's exactly one entry missing
    // and we want to create an individual toast for that szenario. So this method checks which entry is
    // missing.
    private void createToastForOneMissingEntry(boolean singleChoiceTest, int amountOfImages, String date) {
        if (!singleChoiceTest) {
            createErrorToastForMissingEntry(Config.TOAST_WRONG_CHOICE_ERROR);
        } else if (amountOfImages == 0) {
            createErrorToastForMissingEntry(Config.TOAST_NO_IMAGES_ERROR);
        } else if (date.equals("")) {
            createErrorToastForMissingEntry(Config.TOAST_NO_DATE_ERROR);
        } else {
            createErrorToastForMissingEntry(Config.TOAST_NO_NAME_ERROR);
        }
    }

    // This method will just print an individual Toast for the entry which is
    // missing to notify the user what's missing.
    private void createErrorToastForMissingEntry(String errorText) {
        Toast toast = Toast.makeText(this, errorText, Toast.LENGTH_LONG);
        toast.show();
    }

    private void showToastForTooManyMissingEntries() {
        Toast toast = Toast.makeText(this, Config.TOAST_MORE_THAN_TWO_ENTRIES_MISSING, Toast.LENGTH_LONG);
        toast.show();
    }

    private void createNewEntry(String newDocumentName, String newLectureName, String newDate) {
        Intent output = new Intent();
        // all date except the images will be transfered via an Intent
        // Intents aren't able to transfer images (only very very small ones with a small amount of pixels)
        // so things like the name of the Entry, the date, the name of the lecture will be stored
        // in the internal storage to send it to the MainActivity
        output.putExtra(Config.MAIN_ACTIVITY_ITEM_NAME, newDocumentName);
        output.putExtra(Config.MAIN_ACTIVITY_ITEM_LECTURE, newLectureName);
        output.putExtra(Config.MAIN_ACTIVITY_ITEM_DATE, newDate);
        output.putExtra(Config.MAIN_ACTIVITY_DOCUMENT_TYPE, documentType);
        setResult(RESULT_OK, output);
        // finally you will get back to the MainActivity by calling finish();
        finish();
    }

    // This method will check how many tickboxes are selected
    // and if there's not exactly one tickbox marked it will return false.
    private boolean singleChoiceTest() {
        int singleChoice = 0;

        docType = DocumentType.NONE;
        documentType = "";
        if (isWorksheet.isChecked()) {
            docType = DocumentType.WORKSHEET;
            documentType = Config.DOCUMENT_TYPE_PRINTMEDIEN;
            singleChoice++;
        }
        if (isNote.isChecked()) {
            docType = DocumentType.NOTE;
            documentType = Config.DOCUMENT_TYPE_MITSCHRIFT;
            singleChoice++;
        }
        if (isFormular.isChecked()) {
            docType = DocumentType.FORMULAR;
            documentType = Config.DOCUMENT_TYPE_FORMULAR;
            singleChoice++;
        }
        // This if-clause will check if there's exactly one
        // tickbox marked
        if (singleChoice != 1) {
            return false;
        } else {
            return true;
        }
    }

    private void playErrorSound() {
        class ErrorSoundTask extends AsyncTask<Context, Void, MediaPlayer> {

            @Override
            protected MediaPlayer doInBackground(Context... context) {
                // First we setup the audioplayer
                MediaPlayer errorPlayer = MediaPlayer.create(getApplicationContext(), R.raw.output_new_entry_failed);
                return errorPlayer;
            }

            @Override
            protected void onPostExecute(MediaPlayer errorPlayer) {
                // onPostExecute will get the an instance of the player ("errorPlayer") to play the audio
                errorPlayer.start();
            }
        }

        ErrorSoundTask errorSoundTask = new ErrorSoundTask();
        errorSoundTask.execute();
    }

    public void playSuccessSound() {

        class SuccessSoundTask extends AsyncTask<Context, Void, MediaPlayer> {

            @Override
            protected MediaPlayer doInBackground(Context... context) {
                // First we setup the audioplayer
                MediaPlayer successPlayer = MediaPlayer.create(getApplicationContext(), R.raw.output_new_entry_succeded);
                return successPlayer;
            }

            @Override
            protected void onPostExecute(MediaPlayer successPlayer) {
                // onPostExecute will get the an instance of the player ("successPlayer") to play the audio
                successPlayer.start();
            }
        }

        SuccessSoundTask successSoundTask = new SuccessSoundTask();
        successSoundTask.execute();
    }

    private void initViews() {
        isFormular = findViewById(R.id.check_box_form);
        isNote = findViewById(R.id.check_box_notes);
        isWorksheet = findViewById(R.id.check_box_print_media);
        inputLecture = findViewById(R.id.name_of_event_edit);
        inputName = findViewById(R.id.name_of_document_edit);
        initDate();
        initButtons();
    }


    private void initButtons() {
        // This method will setup the clickListeners for the two buttons on
        // this Activity to select an image. It will also check if the permissions where granted
        fromGalery = findViewById(R.id.button_gallery);
        fromGalery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyStoragePermissions(AddNewEntry.this);
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        takePicture = findViewById(R.id.button_take_a_picture);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyStoragePermissions(AddNewEntry.this);
                takePicture();
            }
        });

    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    private void initListView() {
        pictureListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteImageFromListAndTransitsave(position);
                createToastToConfirmDeletion();
                return true;
            }
        });

        pictureListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getImageFromTransitSaveAndStartImageIntent(position);
            }
        });
    }

    private void createToastToConfirmDeletion() {
        // This toast will notify the user if he deleted an item from the listView in the AddNewEntry-Activity
        Toast toast = Toast.makeText(this, Config.TOAST_CONFIRM_DELETION_TEXT, Toast.LENGTH_LONG);
        toast.show();
    }

    private void deleteImageFromListAndTransitsave(int position) {
        // This Method will delete the images you added in the AddNewEntry.
        // It will delete the listView item which represents a specific image
        // and also the image from the ImageTransitSave which is an ArrayList where we storage
        // all the images that were added in the AddNewEntry-Activity
        pictureArrayList.remove(position);
        ImageTransitSave.images.remove(position);
        adapter.notifyDataSetChanged();
    }

    private void getImageFromTransitSaveAndStartImageIntent(int position) {
        Intent imageIntent = new Intent(AddNewEntry.this, ImageViewer.class);
        imageIntent.putExtra(Config.KEY_FOLDER_TO_IMAGE_VIEWER_SELCTED_IMAGE_ID, position);
        imageIntent.putExtra(Config.KEY_FOLDER_TO_IMAGE_VIEWER_INTENTFROM, Config.KEY_FOLDER_TO_IMAGE_VIEWER_INTENTFROM_FOLDER_ADD_NEW_ENTRY);
        startActivity(imageIntent);
    }


    /**
     * The functionality of the date picker was mostly taken from the exercise project 05_CustomAdapter_Solution
     */
    private void initDate() {
        // By clicking on the EditText for the Date, you will get a
        // DatePickerDialog to pick the date when a document was created / printed etc.
        inputDate = findViewById(R.id.date_edit);
        inputDate.setFocusable(false);
        inputDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDatePickerDialog().show();
            }
        });
    }

    private DatePickerDialog createDatePickerDialog() {
        GregorianCalendar today = new GregorianCalendar();
        // These integers will be to define the first date you will
        // see in the DatePickerDialog
        int day = today.get(Calendar.DAY_OF_MONTH);
        int month = today.get(Calendar.MONTH);
        int year = today.get(Calendar.YEAR);

        return new DatePickerDialog(this, this, year, month, day);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        GregorianCalendar date = new GregorianCalendar(year, month, dayOfMonth);
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT,
                Locale.GERMANY);
        String dateString = df.format(date.getTime());
        inputDate.setText(dateString);
    }

    // Callback, der aufgerufen wird, sobald startActivityResult abgeschlossen ist.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Kamera-Applikation wird aufgerufen
        if (requestCode == Config.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Bitmap takenPicture = BitmapFactory.decodeFile(currentPhotoPath);
            ImageTransitSave.images.add(takenPicture);
            amountOfImages++;
            pictureArrayList.add(Config.LIST_ITEM_CUSTOM_TEXT + amountOfImages);
            adapter.notifyDataSetChanged();
        }

        //Mit Hilfe der Cursor Klasse wird der Pfad des ausgewählten Images ermittelt
        else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap pictureFromGallery = BitmapFactory.decodeFile(picturePath);
            addImageToListViewAndImageTransitSave(pictureFromGallery);
        }

    }

    // This method will add the Image to the TransitSave, so it's possible for the MainActivity to get this Image
    // Normal Intents aren't able to transfer huge images
    // You're also able to view the picture, if you click an item in the List on this Activity.
    // The ImageViewer Activity will also access the ImageTransitSave Class, to get the images.

    private void addImageToListViewAndImageTransitSave(Bitmap pictureFromGallery) {
        ImageTransitSave.images.add(pictureFromGallery);
        amountOfImages++;
        pictureArrayList.add(Config.LIST_ITEM_CUSTOM_TEXT + amountOfImages);
        adapter.notifyDataSetChanged();
    }

    private void takePicture() {

        //Impliziter Intent für Kameraaufnahme
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Überprüfen ob der Intent aufgelöst werden kann
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Erzeugen der Datei, in welche die Kameraaufnahme geschrieben wird
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Nur wenn Datei erfolgreich erzeugt wurde, wird der eigentliche Intent gestartet
            if (photoFile != null) {
                //Erzeugen der URI für die Datei mit Hilfe des File Providers, um die Datei für andere Apps zugänglich zu machen bzw. zu übergeben.
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), Config.AUTHORITY, photoFile);
                // Neben der URI erwartet der Intent noch das EXTRA_OUTPUT, um das Bild in Originalgöße speichern zu können
                // Ohne EXTRA_OUTPUT wird das Bild lediglich als Thumbnail zurückgegeben
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, Config.REQUEST_TAKE_PHOTO);

            }
        }

    }

    private File createImageFile() throws IOException {
        // Erzeugen eines Dateinames
        String timeStamp = new SimpleDateFormat(Config.TIME_STAMP_DATE_FORMAT_PATTERN).format(new Date());
        String imageFileName = Config.JPEG_IMAGE_NAME_PREFIX + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, Config.JPEG_FILE_SUFFIX, storageDir);
        // Speichern des absoluten Bildpfades
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}