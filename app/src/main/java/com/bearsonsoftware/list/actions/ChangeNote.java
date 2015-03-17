package com.bearsonsoftware.list.actions;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.bearsonsoftware.list.R;
import com.bearsonsoftware.list.alarms.CalendarReminderManager;
import com.bearsonsoftware.list.alarms.DeleteCalenderEvent;
import com.bearsonsoftware.list.database.NoteManager;
import com.bearsonsoftware.list.datatypes.Note;
import com.bearsonsoftware.list.ui.NoteAdapter;

/**
 * Changes note entry in database, removes edit view and returns
 */
public class ChangeNote {

    public static void makeChange(View view, NoteAdapter adapter, NoteManager noteManager,
                                  Note noteToChange){
        //clear focus from item that will no longer exist
        view.clearFocus();

        EditText mEdit = (EditText) view.findViewById(R.id.noteChangeEditText);
        String name = mEdit.getText().toString();
        noteToChange.setNoteName(name);

        long reminderID;
        if(noteToChange.getNoteReminderID() != null) {
            DeleteCalenderEvent.delete(view.getContext(), noteToChange.getNoteReminderID());
        }

        //set calendar event if necessary
        if(noteToChange.getNoteReminder() != null){
            reminderID = new CalendarReminderManager().addReminder(view.getContext(), noteToChange.getNoteReminder(), name);
            noteToChange.setNoteReminderID(String.valueOf(reminderID));
        }

        noteManager.editNote(noteToChange);
        //remove the edittext row - currently at position 1
        Note toDelete = adapter.getItem(0);
        adapter.remove(toDelete);

        //hide keyboard
        InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEdit.getWindowToken(), 0);


    }
}
