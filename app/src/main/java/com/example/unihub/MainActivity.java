package com.example.unihub;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;

import com.example.unihub.database.ListEntryDatabase;
import com.example.unihub.database.EntryNumberDatabaseInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, EntryNumberDatabaseInterface {

    //Attribute
    private ListView listView;
    private ListEntryAdapter adapter;
    private EntryArrayList listEntries;

    private ListEntryDatabase listEntryDatabase;
    private ArrayList<ListEntry> dataBaseAdd;
    private ArrayList<ListEntry> dataBaseDel;
    private int newEntryNumberSave;


    //+++++++ Overridden Methods +++++++//

    //*******Activity Methods *******//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // All activities except the ImageViewer are only made to be used in portrait mode.
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Declaration of the used layout xml-file
        setContentView(R.layout.activity_main);
        findViews();
        setupViews();
        setupDatabase();

        //updating the listView
        updateFromDatabase();
    }

    //Creates custom Actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_action_bar_layout, menu);
        return true;
    }

    //Click handler for the actionbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.add_document_button:
                Intent addDocumentIntent = new Intent(this, AddNewEntry.class);
                startActivityForResult(addDocumentIntent, Config.ADD_NEW_ENTRY_REQUEST_CODE);
                break;
            case R.id.app_settings_button:
                Intent appSettingsIntent = new Intent(this, AppSettings.class);
                startActivity(appSettingsIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();

        //Ensures the database is updated when the process leaves the activity or ends it
        updateDatabase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Ensures the database is updated when the process leaves the activity or ends it
        updateDatabase();
    }

    @Override
    public void onBackPressed() {
        //Disables the functionality of the backButton
    }


    //Callback from the intent with the values for the object in the extras
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //ensures the callback happens from a AddNewEntry activity
        if (requestCode == Config.ADD_NEW_ENTRY_REQUEST_CODE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            //reading the intent Extras from the Bundle
            String newName = extras.get(Config.MAIN_ACTIVITY_ITEM_NAME).toString();
            String newLecture = extras.get(Config.MAIN_ACTIVITY_ITEM_LECTURE).toString();
            String newDate = extras.get(Config.MAIN_ACTIVITY_ITEM_DATE).toString();
            String newDocType = extras.get(Config.MAIN_ACTIVITY_DOCUMENT_TYPE).toString();

            //kicks off the process of updating the ui and data
            addNewEntryByParams(newName, newLecture, newDate, newDocType);
        }
    }


    //******* INTERFACE METHODS *******//

    //======= EntryNumberDatabaseInterface =======//

    //setting the newEntryNumber variable, which is used by some AsyncTasks
    @Override
    public void setNewEntryNumber(Integer newNumber) {
        newEntryNumberSave = newNumber;
    }


    //Saves preload images in the ImageTransitSave and starts the process to save the new images in ImageTransitSave in internal storage
    @Override
    public void saveImages(int newEntryNumber) {
        //saves preload images in ImageTransitSave
        ImageTransitSave.preLoadedListIcons.put(newEntryNumber + Config.ICON_FILE_NAME_SUFFIX, ImageTransitSave.iconForNewEntry);
        for (int i = 0; i < getImageNumber(); i++) {
            ImageTransitSave.preLoadedImages.put(newEntryNumber + "_" + i, ImageTransitSave.images.get(i));
        }

        //Saves images in ImageTransitSave to the internal Storage
        for (int i = 0; i < getImageNumber(); i++) {
            saveToInternalStorage(ImageTransitSave.images.get(i), newEntryNumber + "_" + i);
        }
        saveToInternalStorage(ImageTransitSave.iconForNewEntry, newEntryNumber + Config.ICON_FILE_NAME_SUFFIX);
    }


    //Generates a new ListEntry item from its parameters and starts the process to save it to the database
    @Override
    public void interfaceAddMethod(String newName, String newLecture, String newDate, String newDocType) {
        //Generate item
        ListEntry newEntry = new ListEntry(newName, newLecture, newDate, newDocType, newEntryNumberSave, getImageNumber());

        //ensureAdapterCorrect is called multiple times, to avoid eventual collisions with other tasks
        ensureAdapterCorrect();
        addToDatabase(newEntry);
        ensureAdapterCorrect();

        //Adapter notified to display the new item in the ListView
        adapter.notifyDataSetChanged();

    }

    //Updates the list that is displayed in the UI
    @Override
    public void addUpdatedEntryToList(ListEntry listEntry) {
        listEntries.add(listEntry);
        adapter.notifyDataSetChanged();
    }

    //******* DatePickerDialog ********//
    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        //needed to
    }

    // \\\\\\ END OF OVERRIDDEN METHODS \\\\\\\ //

    //+++++++ ADD METHODS +++++++//

    //starts the process to add, save and calibrate the entries based on the parameters from the AddNewEntry activity
    private void addNewEntryByParams(final String newName, final String newLecture, final String newDate, final String newDocType) {

        class AddTask extends AsyncTask<Void, Void, Integer> {
            EntryNumberDatabaseInterface item;

            //item equals the main activity, is handed over to be accessed from the async task via interface
            public AddTask(EntryNumberDatabaseInterface interfaceInstance) {
                item = interfaceInstance;
            }

            //returns the new entryNumber that is needed for the further process
            @Override
            protected Integer doInBackground(Void... oid) {
                int i = listEntryDatabase.daoAccess().getMaxEntryNumber();
                return i + 1;
            }

            //starts the process of saving and displaying the entry that is wished to be added
            @Override
            protected void onPostExecute(Integer newEntryNumber) {
                super.onPostExecute(newEntryNumber);
                item.setNewEntryNumber(newEntryNumber);
                item.interfaceAddMethod(newName, newLecture, newDate, newDocType);
                item.saveImages(newEntryNumber);
            }
        }

        //execution of the async task
        AddTask addTask = new AddTask(this);
        addTask.execute();
    }

    //resets the adapter and list connection to ensure they are correct, to avoid complication with async tasks
    private void ensureAdapterCorrect() {
        adapter = new ListEntryAdapter(this, listEntries.getList());
        listView.setAdapter(adapter);
    }

    //+++++++ SETUP +++++++//

    //Setting up the views and the toDoList from the layout-xml-files
    private void findViews() {
        listView = findViewById(R.id.folder_list);
        listEntries = new EntryArrayList();
    }

    //Calls the initialisation for the Buttons and the Views
    private void setupViews() {
        initListView();
    }

    //initializes database and the database handler lists
    private void setupDatabase() {
        ImageTransitSave.images = new ArrayList<>();
        listEntryDatabase = Room.databaseBuilder(getApplicationContext(), ListEntryDatabase.class, Config.DATABASE_NAME).fallbackToDestructiveMigration().build();

        dataBaseDel = new ArrayList<>();
        dataBaseAdd = new ArrayList<>();
    }

    private void initListView() {
        listEntries.resetList();
        adapter = new ListEntryAdapter(this, listEntries.getList());
        listView.setAdapter(adapter);

        //remove on item long click with Dialog
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                //dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(Config.REMOVE_DIALOG_MESSAGE);
                builder.setTitle(Config.REMOVE_DIALOG_TITLE);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        deleteEntry(position);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        return;
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });

        //Load FolderView on normal click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                loadFolderView(position);
            }
        });
    }

    //===== FolderView ====//
    //prepares and starts the folder view for the listEntry on the selected position
    private void loadFolderView(int position) {

        //generating new intent
        Intent folderIntent = new Intent(MainActivity.this, ImagesFolder.class);

        //data used for the intent extras
        ListEntry selectedEntry = listEntries.get(position);
        int entryNumber = selectedEntry.getEntryNumber();
        int imageNumber = selectedEntry.getImageNumber();

        //Extras for the intent
        folderIntent.putExtra(Config.KEY_MAIN_ACTIVITY_TO_FOLDER_IMAGE_NUMBER, entryNumber);
        folderIntent.putExtra(Config.KEY_MAIN_ACTIVITY_TO_FOLDER_AMOUNT_OF_IMAGES, imageNumber);
        folderIntent.putExtra(Config.KEY_MAIN_ACTIVITY_TO_FOLDER_ENTRY_NAME, selectedEntry.getEntryName());
        folderIntent.putExtra(Config.KEY_MAIN_ACTIVITY_TO_FOLDER_LECTURE_NAME, selectedEntry.getLectureName());

        startActivity(folderIntent);
    }

    //+++++++ DATABASE +++++++//

    //removes the entry at the position in the list and adds it to the list of items that will be deleted on database updates
    private void deleteEntry(int position) {

        ListEntry entryToDel = listEntries.get(position);

        listEntries.remove(position);
        adapter.notifyDataSetChanged();

        dataBaseDel.add(entryToDel);

        deleteImagesFromInternalStorageAndTransit(entryToDel);
        updateDatabase();
    }

    //adds the new ListEntry to the display list and to the Add-list which will be saved on database updates
    private void addToDatabase(ListEntry listEntry) {
        listEntries.add(listEntry);
        adapter.notifyDataSetChanged();
        dataBaseAdd.add(listEntry);
    }

    //used to update the database corresponding to the dataBaseAdd and dataBaseDel lists
    private void updateDatabase() {
        ensureAdapterCorrect();
        addEntries();
        deleteEntries();
    }

    //adds the ListEntry objects in the dataBaseAdd list to the database using a new Thread
    private void addEntries() {

        //iterates the dataBaseAddList
        for (int i = 0; i < dataBaseAdd.size(); i++) {
            if (dataBaseAdd.size() > 0) {

                //final to be accessed from within the thread
                final ListEntry entryToAdd = dataBaseAdd.get(i);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //adds the entry to the database
                        listEntryDatabase.daoAccess().insertEntry(entryToAdd);

                        //resets the dataBaseAdd list
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dataBaseAdd = new ArrayList<>();
                            }
                        });
                    }
                }).start();
            }
        }
    }

    //Deletes all entries in the dataBaseDel list from the database
    private void deleteEntries() {

        //Async task for database access
        class DeleteEntriesTask extends AsyncTask<ListEntry, Void, Void> {

            //Deletes the entries in the database sorted by entryNumber
            @Override
            protected Void doInBackground(ListEntry... entriesToDelete) {
                for (int i = 0; i < entriesToDelete.length; i++) {
                    ListEntry nextDeleteEntry = entriesToDelete[i];
                    int entryNumber = nextDeleteEntry.getEntryNumber();
                    listEntryDatabase.daoAccess().delete(entryNumber);
                }
                return null;
            }
        }

        //fills the Array with the content of the dataBaseDel list, so it can be handed to the async task as parameter
        ListEntry[] deletingEntries = new ListEntry[dataBaseDel.size()];
        for (int i = 0; i < dataBaseDel.size(); i++) {
            deletingEntries[i] = dataBaseDel.get(i);
        }

        //execution of the async task
        DeleteEntriesTask deleteEntriesTask = new DeleteEntriesTask();
        deleteEntriesTask.execute(deletingEntries);
    }

    //Loads entries which are saved in the database into the ui list
    private void updateFromDatabase() {
        listEntries.resetList();

        class UpdateTask extends AsyncTask<Void, Void, ArrayList<ListEntry>> {
            EntryNumberDatabaseInterface item;

            public UpdateTask(EntryNumberDatabaseInterface interfaceInstance) {
                item = interfaceInstance;
            }

            //Loads all entries in the database into a ArrayList and returns it
            @Override
            protected ArrayList<ListEntry> doInBackground(Void... oid) {
                ArrayList<ListEntry> entryList = new ArrayList<>();

                //iteration of the database
                for (int i = 0; i <= getBiggestId(); i++) {
                    ListEntry newEntry = listEntryDatabase.daoAccess().fetch(i);
                    if (newEntry != null) {
                        entryList.add(newEntry);
                    }
                }
                return entryList;
            }

            //Saves and displays the ArrayList from the doInBackground into the MainActivity and the UI
            @Override
            protected void onPostExecute(ArrayList<ListEntry> list) {
                //reset of the listView List
                listEntries.resetList();
                super.onPostExecute(list);

                //Filling the list with the List from the parameters
                for (int i = 0; i < list.size(); i++) {
                    item.addUpdatedEntryToList(list.get(i));
                }

                //notifying the adapter to display the list in the UI
                ensureAdapterCorrect();
                adapter.notifyDataSetChanged();
            }
        }

        //execution of the AsyncTask
        UpdateTask updateTask = new UpdateTask(this);
        updateTask.execute();
    }

    //Returns the biggest id-value that is saved in the Database
    //IMPORTANT: must only be called in a non-UI-thread because of database access
    private int getBiggestId() {
        return listEntryDatabase.daoAccess().getMaxId();
    }

    //+++++++ INTERNAL STORAGE +++++++//

    //This method will save a bitmap to the internal storage.
    //It also needs a name for the jpg-name
    private void saveToInternalStorage(Bitmap bitmapImage, String bitmapFileName) {

        //asyncTask for the save procedure to avoid blockading the UI-thread
        class SaveToInternalStorageTask extends AsyncTask<Void, Void, Void> {
            Bitmap bitmapImage;
            String bitmapFileName;

            public SaveToInternalStorageTask(Bitmap bitmapImage, String bitmapFileName) {
                this.bitmapImage = bitmapImage;
                this.bitmapFileName = bitmapFileName;
            }

            //saves the image with the name, which both are parameters for the task, to a file in the internal device storage
            @Override
            protected Void doInBackground(Void... voi) {
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                // path to /data/data/yourapp/app_data/imageDir
                File directory = cw.getDir(Config.CUSTOM_DIR_FOR_IMAGES, Context.MODE_PRIVATE);
                // Create imageDir
                File mypath = new File(directory, bitmapFileName);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(mypath);
                    // Use the compress method on the BitMap object to write image to the OutputStream
                    bitmapImage.compress(Bitmap.CompressFormat.JPEG, Config.JPEG_COMPRESS_QUALITY, fos);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void oid) {
                super.onPostExecute(oid);
                adapter.notifyDataSetChanged();
            }
        }

        //execution of the async task
        SaveToInternalStorageTask saveToInternalStorageTask = new SaveToInternalStorageTask(bitmapImage, bitmapFileName);
        saveToInternalStorageTask.execute();

    }

    // This method will get the name of the image and will
    // delete it from the internal storage
    private void deleteImageFromStorage(String jpgName) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir(Config.CUSTOM_DIR_FOR_IMAGES, Context.MODE_PRIVATE);
        File file = new File(directory, jpgName);
        file.delete();
    }

    //Images from a new Entry are always in the ImageTransitSave class. This method will
    // return the number of the image.
    private int getImageNumber() {
        return ImageTransitSave.images.size();
    }

    //deletes the images that are associated with the listEntry from the storage and the ImageTransitSave
    private void deleteImagesFromInternalStorageAndTransit(ListEntry listEntry) {

        //deletes from the imageTransitSave
        ImageTransitSave.preLoadedListIcons.remove(listEntry.getEntryNumber() + Config.ICON_FILE_NAME_SUFFIX);

        //deletes all images from the Storage that have the associated suffix
        for (int i = 0; i < listEntry.getImageNumber(); i++) {
            String jpgName = "" + listEntry.getEntryNumber() + "_" + i;
            deleteImageFromStorage(jpgName);
            ImageTransitSave.preLoadedImages.remove(jpgName);
        }
    }
}