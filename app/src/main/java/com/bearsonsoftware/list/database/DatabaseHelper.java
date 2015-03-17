package com.bearsonsoftware.list.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bearsonsoftware.list.R;

/**
 * Handle tasks related to creating and updating db
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "listDB.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_LISTS = "lists";
    public static final String LIST_ID = "listID";
    public static final String LIST_NAME = "listName";
    public static final String LIST_CHILDREN = "listChildren";
    public static final String LIST_POSITION = "listPosition";

    public static final String TABLE_NOTES = "notes";
    public static final String NOTE_ID = "noteID";
    public static final String NOTE_LIST_ID = "listID";
    public static final String NOTE_NAME = "noteName";
    public static final String NOTE_POSITION = "position";
    public static final String NOTE_ISACTIVE = "isActive";
    public static final String NOTE_REMINDER = "reminder";
    public static final String NOTE_REMINDER_ID = "reminderID";

    public static final String DATABASE_CREATE_LISTS = "create table " +
            TABLE_LISTS + "(" + LIST_ID + " integer primary key autoincrement, " +
            LIST_NAME + " text not null, " +
            LIST_CHILDREN + " integer not null, " +
            LIST_POSITION + " integer not null);";

    public static final String DATABASE_CREATE_ITEMS = "create table " +
            TABLE_NOTES + "(" + NOTE_ID + " integer primary key autoincrement, " +
            NOTE_LIST_ID + " integer not null, " +
            NOTE_NAME + " text not null, " +
            NOTE_POSITION + " integer not null, " +
            NOTE_ISACTIVE + " integer not null, " +
            NOTE_REMINDER + " text, " +
            NOTE_REMINDER_ID + " text, " +
            " foreign key (" + NOTE_LIST_ID + ") references " + TABLE_LISTS + "(" + LIST_ID + "));";

    private final Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_LISTS);
        db.execSQL(DATABASE_CREATE_ITEMS);

        //enter default lists and tasks to database
        ContentValues valuesDefault = new ContentValues();
        valuesDefault.put(LIST_NAME, context.getString(R.string.default_list_name));
        valuesDefault.put(LIST_CHILDREN, 0);
        valuesDefault.put(LIST_POSITION, 1);

        ContentValues valuesTips = new ContentValues();
        valuesTips.put(LIST_NAME, context.getString(R.string.tips_list_name));
        valuesTips.put(LIST_CHILDREN, 5); //number of tips
        valuesTips.put(LIST_POSITION, 2);

        long defaultId = db.insert(TABLE_LISTS, null, valuesDefault);
        long tipsId = db.insert(TABLE_LISTS, null, valuesTips);

        ContentValues valuesTipAdd = new ContentValues();
        valuesTipAdd.put(DatabaseHelper.NOTE_NAME, context.getString(R.string.tip_add));
        valuesTipAdd.put(DatabaseHelper.NOTE_LIST_ID, tipsId);
        valuesTipAdd.put(DatabaseHelper.NOTE_POSITION, 1);
        valuesTipAdd.put(DatabaseHelper.NOTE_ISACTIVE, 1); //1 = active note, 0 = done note

        ContentValues valuesTipEdit = new ContentValues();
        valuesTipEdit.put(DatabaseHelper.NOTE_NAME, context.getString(R.string.tip_edit));
        valuesTipEdit.put(DatabaseHelper.NOTE_LIST_ID, tipsId);
        valuesTipEdit.put(DatabaseHelper.NOTE_POSITION, 2);
        valuesTipEdit.put(DatabaseHelper.NOTE_ISACTIVE, 1); //1 = active note, 0 = done note

        ContentValues valuesTipDelete = new ContentValues();
        valuesTipDelete.put(DatabaseHelper.NOTE_NAME, context.getString(R.string.tip_delete));
        valuesTipDelete.put(DatabaseHelper.NOTE_LIST_ID, tipsId);
        valuesTipDelete.put(DatabaseHelper.NOTE_POSITION, 3);
        valuesTipDelete.put(DatabaseHelper.NOTE_ISACTIVE, 1); //1 = active note, 0 = done note

        ContentValues valuesTipComplete = new ContentValues();
        valuesTipComplete.put(DatabaseHelper.NOTE_NAME, context.getString(R.string.tip_complete));
        valuesTipComplete.put(DatabaseHelper.NOTE_LIST_ID, tipsId);
        valuesTipComplete.put(DatabaseHelper.NOTE_POSITION, 4);
        valuesTipComplete.put(DatabaseHelper.NOTE_ISACTIVE, 1); //1 = active note, 0 = done note

        ContentValues valuesTipMove = new ContentValues();
        valuesTipMove.put(DatabaseHelper.NOTE_NAME, context.getString(R.string.tip_move));
        valuesTipMove.put(DatabaseHelper.NOTE_LIST_ID, tipsId);
        valuesTipMove.put(DatabaseHelper.NOTE_POSITION, 5);
        valuesTipMove.put(DatabaseHelper.NOTE_ISACTIVE, 1); //1 = active note, 0 = done note

        ContentValues valuesTipReminder = new ContentValues();
        valuesTipReminder.put(DatabaseHelper.NOTE_NAME, context.getString(R.string.tip_reminder));
        valuesTipReminder.put(DatabaseHelper.NOTE_LIST_ID, tipsId);
        valuesTipReminder.put(DatabaseHelper.NOTE_POSITION, 6);
        valuesTipReminder.put(DatabaseHelper.NOTE_ISACTIVE, 1); //1 = active note, 0 = done note

        db.insert(TABLE_NOTES, null, valuesTipAdd);
        db.insert(TABLE_NOTES, null, valuesTipEdit);
        db.insert(TABLE_NOTES, null, valuesTipDelete);
        db.insert(TABLE_NOTES, null, valuesTipComplete);
        db.insert(TABLE_NOTES, null, valuesTipMove);
        db.insert(TABLE_NOTES, null, valuesTipReminder);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
