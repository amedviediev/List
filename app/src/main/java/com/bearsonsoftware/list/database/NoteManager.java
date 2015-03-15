package com.bearsonsoftware.list.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.bearsonsoftware.list.datatypes.Note;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Fetch and write data regarding notes
 */
public class NoteManager extends AbstractDatabaseManager{

    private static NoteManager mInstance;

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private final String[] allColumns = { DatabaseHelper.NOTE_ID,
            DatabaseHelper.NOTE_LIST_ID,
            DatabaseHelper.NOTE_NAME,
            DatabaseHelper.NOTE_POSITION,
            DatabaseHelper.NOTE_ISACTIVE,
            DatabaseHelper.NOTE_REMINDER,
            DatabaseHelper.NOTE_REMINDER_ID
    };

    public static NoteManager getInstance(Context context){
        if(mInstance == null){
            mInstance = new NoteManager(context);
        }
        return mInstance;
    }

    private NoteManager (Context context){
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
        for (Note a: (ArrayList<Note>)list){
            String updateItemPosition = "update " + DatabaseHelper.TABLE_NOTES + " set " +
                    DatabaseHelper.NOTE_POSITION + " = " + a.getNotePosition() + " where " +
                    DatabaseHelper.NOTE_ID + " = " + a.getNoteID();
            database.execSQL(updateItemPosition);
        }
    }

    public void updateStatus(Note note){
        String updateItemStatus = "update " + DatabaseHelper.TABLE_NOTES + " set " +
                DatabaseHelper.NOTE_ISACTIVE + " = " + note.getNoteIsActive() + " where " +
                DatabaseHelper.NOTE_ID + " = " + note.getNoteID();
        database.execSQL(updateItemStatus);
    }

    public Note createNote(String noteName, long listId, String reminderTime){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.NOTE_NAME, noteName);
        values.put(DatabaseHelper.NOTE_LIST_ID, listId);
        values.put(DatabaseHelper.NOTE_POSITION, 1);
        values.put(DatabaseHelper.NOTE_ISACTIVE, 1); //1 = active note, 0 = done note
        values.put(DatabaseHelper.NOTE_REMINDER, reminderTime);

        //increment all positions and add a new note to position 1
        String updatePositions = "update " + DatabaseHelper.TABLE_NOTES + " set " +
                DatabaseHelper.NOTE_POSITION + " = " + DatabaseHelper.NOTE_POSITION + " + 1 where " +
                DatabaseHelper.NOTE_LIST_ID + " = " + listId;
        database.execSQL(updatePositions);

        String updateList = "update " + DatabaseHelper.TABLE_LISTS + " set " +
                DatabaseHelper.LIST_CHILDREN + " = " + DatabaseHelper.LIST_CHILDREN + " + 1 where " +
                DatabaseHelper.LIST_ID + " = " + listId;
        database.execSQL(updateList);
        long insertId = database.insert(DatabaseHelper.TABLE_NOTES, null, values);


        Cursor cursor = database.query(DatabaseHelper.TABLE_NOTES,
                allColumns, DatabaseHelper.NOTE_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Note newNote = cursorToNote(cursor);
        cursor.close();
        return newNote;
    }

    public void deleteNote(Note note) {
        long id = note.getNoteID();
        long listID = note.getListID();
        System.out.println("Note deleted with id: " + id);
        String updatePositions = "update " + DatabaseHelper.TABLE_NOTES + " set " +
                DatabaseHelper.NOTE_POSITION + " = " + DatabaseHelper.NOTE_POSITION + " -- 1 where " +
                DatabaseHelper.NOTE_POSITION + " > " + note.getNotePosition() + " and " +
                DatabaseHelper.NOTE_LIST_ID + " = " + listID;
        database.execSQL(updatePositions);
        String updateList = "update " + DatabaseHelper.TABLE_LISTS + " set " +
                DatabaseHelper.LIST_CHILDREN + " = " + DatabaseHelper.LIST_CHILDREN + " - 1 where " +
                DatabaseHelper.LIST_ID + " = " + note.getListID();
        database.execSQL(updateList);
        database.delete(DatabaseHelper.TABLE_NOTES, DatabaseHelper.NOTE_ID
                + " = " + id, null);
    }

    public ArrayList<Note> getAllNotes(long listId) {
        ArrayList<Note> notes = new ArrayList<>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_NOTES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            Note note = cursorToNote(cursor);
            if(note.getListID() == listId) notes.add(note);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();

        Collections.sort(notes, new Comparator<Note>() {
            @Override
            public int compare(Note list1, Note list2) {
                return list1.getNotePosition().compareTo(list2.getNotePosition());
            }
        });
        return notes;
    }

    private Note cursorToNote(Cursor cursor) {
        Note note = new Note();
        note.setNoteID(cursor.getLong(0));
        note.setListID(cursor.getLong(1));
        note.setNoteName(cursor.getString(2));
        note.setNotePosition(cursor.getInt(3));
        note.setNoteIsActive(cursor.getInt(4));
        note.setNoteReminder(cursor.getString(5));
        return note;
    }
}
