package com.bearsonsoftware.list.actions;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.bearsonsoftware.list.R;
import com.bearsonsoftware.list.database.NoteListManager;
import com.bearsonsoftware.list.datatypes.NoteList;
import com.bearsonsoftware.list.ui.NoteListAdapter;

/**
 * Creates note list entry in database, removes edittext and replaces it with a
 * row for the created note list
 */
public class SaveNoteList {

    public static void save(View view, NoteListAdapter adapter, NoteListManager noteListManager){

        //clear focus from item that will no longer exist
        view.clearFocus();
        NoteList noteList;
        EditText mEdit   = (EditText) view.findViewById(R.id.noteListEditText);
        String name = mEdit.getText().toString();
        noteList = noteListManager.createNoteList(name);

        adapter.add(0, noteList);
        //remove the edittext row - currently at position 1
        NoteList toDelete = adapter.getItem(1);
        adapter.remove(toDelete);

        //hide keyboard
        InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEdit.getWindowToken(), 0);
    }
}
