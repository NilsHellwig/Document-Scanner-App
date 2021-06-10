package com.example.unihub;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.GregorianCalendar;
import java.util.Locale;

@Entity
public class ListEntry {

    private String entryName;
    private String lectureName;
    private String date;

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int entryId;
    private String docType;
    private int entryNumber;
    private int imageNumber;


    // Constructor for the ListEntry Objects, used by the activities
    public ListEntry(String entryName, String lectureName, String date, String docType, int entryNumber, int imageNumber) {
        this.entryName = entryName;
        this.lectureName = lectureName;
        this.date = date;
        this.docType = docType;
        this.entryNumber = entryNumber;
        this.imageNumber = imageNumber;
    }

    //Constructor for the database
    public ListEntry() {

    }

    //Taken from the Exercise 05_CustomAdapter_Solution
    //Converts a String into a GregorianCalender object
    //If conversion fails uses the current date
    private GregorianCalendar getDateFromString(String date) {
        GregorianCalendar cal = new GregorianCalendar();
        try {
            DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY);
            cal.setTime(df.parse(date));
        } catch (ParseException e) {
            //When parsing fails uses the current date
            e.printStackTrace();
        }
        return cal;
    }

    //Formats the date from a GregorianCalender to a String Object
    public String getFormattedDate() {
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY);
        return df.format(getDateFromString(date).getTime());
    }

    //----- GETTER AND SETTER -----//

    //mainly needed for the database functionality

    @NonNull
    public String getEntryName() {
        return entryName;
    }

    @NonNull
    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    @NonNull
    public String getLectureName() {
        return lectureName;
    }

    @NonNull
    public void setLectureName(String lectureName) {
        this.lectureName = lectureName;
    }

    @NonNull
    public String getDocType() {
        return docType;
    }

    @NonNull
    public void setDocType(String docType) {
        this.docType = docType;
    }

    @NonNull
    public void setDate(String date) {
        this.date = date;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    @NonNull
    public int getEntryId() {
        return entryId;
    }

    @NonNull
    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    @NonNull
    public int getEntryNumber() {
        return entryNumber;
    }

    @NonNull
    public void setEntryNumber(int entryNumber) {
        this.entryNumber = entryNumber;
    }

    @NonNull
    public int getImageNumber() {
        return imageNumber;
    }

    @NonNull
    public void setImageNumber(int imageNumber) {
        this.imageNumber = imageNumber;
    }

}