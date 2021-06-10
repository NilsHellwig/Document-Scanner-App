package com.example.unihub;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.unihub.internalStorage.LoadFromStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class ListEntryAdapter extends ArrayAdapter<ListEntry> implements LoadFromStorage {

    private ArrayList<ListEntry> entryList;
    private Context context;
    private TextView listEntryName;
    private TextView listEntryLecture;
    private TextView listEntryDate;
    private TextView listEntryDocType;
    private ImageView listImage;

    public ListEntryAdapter(Context context, ArrayList<ListEntry> entryList) {
        super(context, R.layout.main_list_item_element_layout, entryList);

        this.entryList = entryList;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = layoutInflater.inflate(R.layout.main_list_item_element_layout, null);
        }

        ListEntry entryItem = entryList.get(position);

        if (entryItem != null) {
            setupViews(v);
            setTextViews(entryItem);
            setImage(entryItem);
        }
        return v;
    }

    private void setupViews(View v) {
        listEntryName = v.findViewById(R.id.list_entry_name_view);
        listEntryLecture = v.findViewById(R.id.list_entry_lecture_view);
        listEntryDate = v.findViewById(R.id.list_entry_date_view);
        listEntryDocType = v.findViewById(R.id.document_type);
        listImage = v.findViewById(R.id.list_entry_image_view);
    }

    private void setTextViews(ListEntry entryItem) {
        listEntryName.setText(entryItem.getEntryName());
        listEntryLecture.setText(entryItem.getLectureName());
        listEntryDate.setText(entryItem.getFormattedDate());
        listEntryDocType.setText(entryItem.getDocType());
    }

    // It might happen that the phone takes some time to save the images to the internal storage so
    // the device will always save them to the ImageTransitsave too, so it's always possible for the user to see the images.
    private void setImage(ListEntry entryItem) {
        if (ImageTransitSave.preLoadedListIcons.get(entryItem.getEntryNumber() + Config.ICON_FILE_NAME_SUFFIX) != null) {
            listImage.setImageBitmap(ImageTransitSave.preLoadedListIcons.get(entryItem.getEntryNumber() + Config.ICON_FILE_NAME_SUFFIX));
        } else {
            listImage.setImageBitmap(loadFromInternalStorage(entryItem.getEntryNumber() + Config.ICON_FILE_NAME_SUFFIX));
        }
    }

    // this method will get a jpg-name to load it from the
    // internal storage. The first image of an entry will also be stored as
    // a 100x100px version. This method will load it from the internal storage
    // the sizes of the original images for the ImageViewer are too big because when you start the
    // app with a lot of entries it would be to much data at once.
    private Bitmap loadFromInternalStorage(String jpgName) {


        class LoadFromInternalStorageTask extends AsyncTask<Void, Void, Bitmap> {
            String jpgName;
            LoadFromStorage item;

            public LoadFromInternalStorageTask(String jpgName, LoadFromStorage item) {
                this.jpgName = jpgName;
                this.item = item;
            }

            @Override
            protected Bitmap doInBackground(Void... voi) {
                ContextWrapper cw = new ContextWrapper(context);
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

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                item.getImageFromStorage(bitmap);
            }

        }

        LoadFromInternalStorageTask saveToInternalStorageTask = new LoadFromInternalStorageTask(jpgName, this);
        return saveToInternalStorageTask.doInBackground();

    }

    @Override
    public Bitmap getImageFromStorage(Bitmap bitmap) {
        return bitmap;
    }
}