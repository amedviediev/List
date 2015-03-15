package com.bearsonsoftware.list.actions;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;


/**
 * Cancel Add action and remove the created empty note/notelist
 */
public class CancelSave {
    public static void cancel(View view, ArrayAdapter adapter){
        //clear focus from item that will no longer exist
        view.clearFocus();
        Object toDelete = adapter.getItem(0);
        adapter.remove(toDelete);

        //hide keyboard
        InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
