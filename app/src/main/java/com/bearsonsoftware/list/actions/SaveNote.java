package com.bearsonsoftware.list.actions;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.bearsonsoftware.list.R;
import com.bearsonsoftware.list.alarms.CalendarReminderManager;
import com.bearsonsoftware.list.database.NoteManager;
import com.bearsonsoftware.list.datatypes.Note;
import com.bearsonsoftware.list.ui.NoteAdapter;

/**
 * Creates note entry in database, removes edittext and replaces it with a
 * row for the created note list
 */
public class SaveNote {
    public static void save(View view, NoteAdapter adapter, NoteManager noteListManager,
                            long listId, String reminderTime){

        //clear focus from item that will no longer exist
        view.clearFocus();
        Note note;
        EditText mEdit = (EditText) view.findViewById(R.id.noteEditText);
        String name = mEdit.getText().toString();
        note = noteListManager.createNote(name, listId, reminderTime);

        adapter.add(0, note);
        //remove the edittext row - currently at position 1
        Note toDelete = adapter.getItem(1);
        adapter.remove(toDelete);

        //set calendar event if necessary
        if(reminderTime != null){
            new CalendarReminderManager().addReminder(view.getContext(), reminderTime, name);
        }

        //hide keyboard
        InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEdit.getWindowToken(), 0);
    }
}
