package com.bearsonsoftware.list.actions;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.bearsonsoftware.list.R;
import com.bearsonsoftware.list.alarms.CalendarReminderManager;
import com.bearsonsoftware.list.database.NoteManager;
import com.bearsonsoftware.list.datatypes.Note;
import com.bearsonsoftware.list.ui.NoteAdapter;

/**
 * Creates note entry in database and makes all other requried adjustments
 */
public class SaveNote {
    public static void save(View view, NoteAdapter adapter, NoteManager noteManager,
                            long listId, String reminderTime){

        //clear focus from item that will no longer exist
        view.clearFocus();

        Note note;
        EditText mEdit = (EditText) view.findViewById(R.id.noteEditText);
        String name = mEdit.getText().toString();

        long reminderID = 0;
        //set calendar event if necessary
        if(reminderTime != null){
            try{
                reminderID = new CalendarReminderManager().addReminder(view.getContext(), reminderTime, name);
            } catch (SQLiteException e){
                e.printStackTrace();
            }
        }

        note = noteManager.createNote(name, listId, reminderTime, reminderID);
        adapter.add(0, note);
        //remove the edittext row - currently at position 1
        Note toDelete = adapter.getItem(1);
        adapter.remove(toDelete);


        //hide keyboard
        InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEdit.getWindowToken(), 0);
    }
}
