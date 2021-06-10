package com.example.unihub.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.unihub.ListEntry;

@Database(entities = {ListEntry.class}, version = 1, exportSchema = false)
public abstract class ListEntryDatabase extends RoomDatabase {

    public abstract DaoAccess daoAccess();
}
