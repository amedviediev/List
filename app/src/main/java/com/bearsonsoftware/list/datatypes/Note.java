package com.bearsonsoftware.list.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The note data object. Implements parcelable so that it can be passed in intents
 */
public class Note{

    private long noteID;
    private long listID;
    private String noteName;
    private int notePosition;
    private int noteIsActive;
    private String noteReminder;
    private String noteReminderID;
    private boolean isCreated = true;

    public boolean isCreated() {
        return isCreated;
    }

    public void setCreated(boolean isCreated) {
        this.isCreated = isCreated;
    }

    public long getNoteID() {
        return noteID;
    }

    public void setNoteID(long noteID) {
        this.noteID = noteID;
    }

    public long getListID() {
        return listID;
    }

    public void setListID(long listID) {
        this.listID = listID;
    }

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    public Integer getNotePosition() {
        return notePosition;
    }

    public void setNotePosition(int notePosition) {
        this.notePosition = notePosition;
    }

    public int getNoteIsActive() {
        return noteIsActive;
    }

    public void setNoteIsActive(int noteIsActive) {
        this.noteIsActive = noteIsActive;
    }

    public String getNoteReminder() {
        return noteReminder;
    }

    public void setNoteReminder(String noteReminder) {
        this.noteReminder = noteReminder;
    }

    public String getNoteReminderID() {
        return noteReminderID;
    }

    public void setNoteReminderID(String noteReminderID) {
        this.noteReminderID = noteReminderID;
    }

    public String toString(){
        return noteName;
    }

}
