package com.bearsonsoftware.list.database;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

/**
 * Save positions in DB for all notes or note lists after an item has been moved
 */
public class SavePositions extends AsyncTask<Void, Void, Void> {

    private final Context context;
    private final String tableName;
    private final ArrayList list;
    private AbstractDatabaseManager manager;

    public SavePositions(Context context, String tableName,
                         ArrayList list){
        super();
        this.context = context;
        this.tableName = tableName;
        this.list = list;
    }

    @Override
    protected Void doInBackground(Void... params) {
        switch (tableName){
            case DatabaseHelper.TABLE_LISTS:
                manager = NoteListManager.getInstance(context);
                break;
            case DatabaseHelper.TABLE_NOTES:
                manager = NoteManager.getInstance(context);
                break;
        }

        manager.updatePositions(list);
        return null;
    }
}
