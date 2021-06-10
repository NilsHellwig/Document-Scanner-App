package com.example.unihub.database;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.unihub.ListEntry;

@Dao
public interface DaoAccess {

    @Insert
    void insertEntry(ListEntry listEntry);

    @Query("Select * from listEntry where entryId = :id")
    ListEntry fetch(int id);

    @Query("Select max(entryId) from listEntry")
    int getMaxId();

    @Query("Select imageNumber from listEntry where entryId = :id")
    int getImageNumberById(int id);

    @Query("Select max(imageNumber) from listEntry")
    int getMaxImageNumber();

    @Query("Select entryNumber from listEntry where entryId = :id")
    int getEntryNumberById(int id);

    @Query("Select max(entryNumber) from listEntry")
    int getMaxEntryNumber();

    @Query(("Delete from listEntry where entryNumber =:entryNumber"))
    int delete(int entryNumber);



}