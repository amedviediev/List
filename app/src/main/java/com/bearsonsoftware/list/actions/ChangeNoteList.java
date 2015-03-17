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
 *Changes notelist entry in database, removes edit view
 */
public class ChangeNoteList {
    public static void makeChange(View view, NoteListAdapter adapter, NoteListManager noteListManager,
                                  NoteList noteListToChange){
        //clear focus from item that will no longer exist
        view.clearFocus();

        EditText mEdit = (EditText) view.findViewById(R.id.noteListChangeEditText);
        String name = mEdit.getText().toString();
        noteListToChange.setListName(name);

        noteListManager.editNoteList(noteListToChange);
        //remove the edittext row - currently at position 1
        NoteList toDelete = adapter.getItem(0);
        adapter.remove(toDelete);

        //hide keyboard
        InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEdit.getWindowToken(), 0);

    }
}
