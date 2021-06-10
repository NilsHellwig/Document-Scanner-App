package com.example.unihub.database;

import android.content.Intent;

import com.example.unihub.ListEntry;

public interface EntryNumberDatabaseInterface {

    void setNewEntryNumber(Integer newNumber);
    void interfaceAddMethod(String newName, String newLecture, String newDate, String newDocType);
    void saveImages(int biggestEntryNumber);
    void addUpdatedEntryToList(ListEntry listEntry);
}
