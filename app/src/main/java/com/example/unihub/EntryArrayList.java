package com.example.unihub;


import java.util.ArrayList;

public class EntryArrayList {
    private ArrayList<ListEntry> list;

    public EntryArrayList() {
        list = new ArrayList<>();
    }

    public void add(ListEntry newEntry) {
        if (newEntry != null) {
            ArrayList<ListEntry> newList = new ArrayList<>();
            newList.add(newEntry);
            for (int i = 0; i < list.size(); i++) {
                newList.add(list.get(i));
            }
            list = newList;
        }
    }

    public void resetList() {
        list = new ArrayList<>();
    }

    public ListEntry get(int index) {
        return list.get(index);
    }

    public void remove(int position) {
        list.remove(position);
    }

    public ArrayList<ListEntry> getList() {
        return list;
    }
}