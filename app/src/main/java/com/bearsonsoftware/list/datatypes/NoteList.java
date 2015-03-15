package com.bearsonsoftware.list.datatypes;

/**
 * The data object for note lists
 */
public class NoteList{

    private long listID;
    private String listName;
    private int listChildren;
    private int listPosition;
    private boolean isCreated = true;

    public boolean isCreated() {
        return isCreated;
    }

    public void setCreated(boolean isCreated) {
        this.isCreated = isCreated;
    }


    public long getListID() {
        return listID;
    }

    public void setListID(long listID) {
        this.listID = listID;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public Integer getListChildren() {
        return listChildren;
    }

    public void setListChildren(int listChildren) {
        this.listChildren = listChildren;
    }

    public Integer getListPosition() {
        return listPosition;
    }

    public void setListPosition(int listPosition) {
        this.listPosition = listPosition;
    }

    // Will be used by the ArrayAdapter in the ListView
    public String toString(){
        return listName;
    }
}
