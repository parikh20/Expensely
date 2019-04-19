package team16.cs307.expensetracker;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class IDTracker {
    private ArrayList<String> IDs;
    private int lastID;

    public IDTracker(ArrayList<String> ids, int lastID) {
        this.IDs = ids;
        this.lastID = lastID;
    }
    public IDTracker (String[] strs, int lastID) {
        IDs = new ArrayList<>();
        for (String s : strs) {
            IDs.add(s);
        }
        this.lastID = lastID;

    }

    public IDTracker() {

    }
    public IDTracker(String s) {
        IDs = new ArrayList<>();
        lastID = 0;
        IDs.add(s);
    }


    public int getLastID() {
        return lastID;
    }

    public ArrayList<String> getIDs() {
        return IDs;
    }

    public void setIDs(ArrayList<String> IDs) {
        this.IDs = IDs;
    }

    public void addID (String s){
        IDs.add(s);
        lastID++;
    }
    public void remove(String s) {
        IDs.remove(s);

    }
    public int IDindexOf(String s ) {
        return IDs.indexOf(s);
    }


}
