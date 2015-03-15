package com.bearsonsoftware.list.database;

import android.content.Context;
import android.os.AsyncTask;

import com.bearsonsoftware.list.datatypes.Note;

/**
 * Save note status (active or not active) in async thread
 */
public class SaveNoteStatus extends AsyncTask<Void, Void, Void> {

    private final Context context;
    private final Note note;

    public SaveNoteStatus(Context context, Note note){
        super();
        this.context = context;
        this.note = note;
    }

    @Override
    protected Void doInBackground(Void... params) {
        NoteManager manager = NoteManager.getInstance(context);
        manager.updateStatus(note);
        return null;
    }
}
