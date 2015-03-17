package com.bearsonsoftware.list.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.bearsonsoftware.list.datatypes.NoteList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Fetch and write data regarding note lists
 */
public class NoteListManager extends AbstractDatabaseManager{

    private static NoteListManager mInstance;

    // Database fields
    private SQLiteDatabase database;
    private final DatabaseHelper dbHelper;
    private final String[] allColumns = { DatabaseHelper.LIST_ID,
            DatabaseHelper.LIST_NAME,
            DatabaseHelper.LIST_CHILDREN,
            DatabaseHelper.LIST_POSITION
    };

    public static NoteListManager getInstance(Context context){
        if(mInstance == null){
            mInstance = new NoteListManager(context);
        }
        return mInstance;
    }

    private NoteListManager (Context context){
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    @Override
    public void updatePositions(ArrayList list) {
        for (NoteList a: (ArrayList<NoteList>)list){
            String updateMovedItem = "update " + DatabaseHelper.TABLE_LISTS + " set " +
                    DatabaseHelper.LIST_POSITION + " = " + a.getListPosition() + " where " +
                    DatabaseHelper.LIST_ID + " = " + a.getListID();
            database.execSQL(updateMovedItem);
        }

    }

    public NoteList createNoteList(String listName){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.LIST_NAME, listName);
        values.put(DatabaseHelper.LIST_CHILDREN, 0);
        values.put(DatabaseHelper.LIST_POSITION, 1);

        //increment all positions and add a new note list to position 1
        String updatePositions = "update " + DatabaseHelper.TABLE_LISTS + " set " +
                DatabaseHelper.LIST_POSITION + " = " + DatabaseHelper.LIST_POSITION + " + 1";
        database.execSQL(updatePositions);
        long insertId = database.insert(DatabaseHelper.TABLE_LISTS, null, values);

        Cursor cursor = database.query(DatabaseHelper.TABLE_LISTS,
                allColumns, DatabaseHelper.LIST_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        NoteList newNoteList = cursorToNoteList(cursor);
        cursor.close();
        return newNoteList;
    }

    public void editNoteList(NoteList noteList){
        String updateNoteList = "update " + DatabaseHelper.TABLE_LISTS + " set " +
                DatabaseHelper.LIST_NAME + " = '" + noteList.getListName() + "' where " +
                DatabaseHelper.LIST_ID + " = " + noteList.getListID();
        database.execSQL(updateNoteList);
    }

    public void deleteNoteList(NoteList noteList) {
        long id = noteList.getListID();
        System.out.println("Notelist deleted with id: " + id);
        String deleteChildren = "delete from " + DatabaseHelper.TABLE_NOTES + " where " +
                DatabaseHelper.NOTE_LIST_ID + " = " + id;
        database.execSQL(deleteChildren);
        database.delete(DatabaseHelper.TABLE_LISTS, DatabaseHelper.NOTE_LIST_ID
                + " = " + id, null);
    }

    public ArrayList<NoteList> getAllNoteLists() {
        ArrayList<NoteList> noteLists = new ArrayList<>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_LISTS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            NoteList noteList = cursorToNoteList(cursor);
            noteLists.add(noteList);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();

        Collections.sort(noteLists, new Comparator<NoteList>() {
            @Override
            public int compare(NoteList list1, NoteList list2) {
                return list1.getListPosition().compareTo(list2.getListPosition());
            }
        });
        return noteLists;
    }

    private NoteList cursorToNoteList(Cursor cursor) {
        NoteList noteList = new NoteList();
        noteList.setListID(cursor.getLong(0));
        noteList.setListName(cursor.getString(1));
        noteList.setListChildren(cursor.getInt(2));
        noteList.setListPosition(cursor.getInt(3));
        return noteList;
    }
}
