package com.example.unihub;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;

// ImageTransitSave is able to transfer a huge amount of images to another Activity
public class ImageTransitSave {
    // This Arraylist is saving every image the user added in a new entry
    // this ArrayList will be cleared if the AddNewEntry-Activity gets started again
    public static ArrayList<Bitmap> images = new ArrayList<>();
    // This HashMap saves every Image you took while running the app.
    // The ImageViewer might load one of these. The ImageViewer will load
    // an Image from the internal storage if an image doesn't exist in this
    // Hashmap. This is because sometimes it might some time until all
    // new images you added are in the internal storage. So by saving all of them
    // in the HashMap too this problem won't happen and the user's always able to see
    // all of the images. The Key of the images in the Hashmap is the name of the image file.
    public static HashMap<String,Bitmap> preLoadedImages = new HashMap<>();
    // This Bitmap "iconForNewEntry" is used by the AddNewEntry to send a 100x100 Bitmap of
    // the first Image of the "images" ArrayList to the MainActivity. The MainActivity will save
    // them to the internal storage
    public static Bitmap iconForNewEntry;
    // It might also take some time to save the 100x100 icons to the internal storage
    // so the ones that were added while using the app will also be saved to
    // the preLoadedListIcons-Hashmap.
    public static HashMap<String,Bitmap> preLoadedListIcons = new HashMap<>();
}