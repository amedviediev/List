package com.bearsonsoftware.list.ui.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.widget.RemoteViewsService;

import com.bearsonsoftware.list.database.NoteManager;
import com.bearsonsoftware.list.datatypes.Note;

import java.util.ArrayList;

/**
 * Widget service, define listview adapter as widgetlistprovider
 */
public class WidgetService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        int appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        long listId = intent.getLongExtra("listid", 0);

        NoteManager noteManager = NoteManager.getInstance(getApplicationContext());
        noteManager.open();
        ArrayList<Note> notes = noteManager.getAllNotes(listId);
        return new WidgetListProvider(this.getApplicationContext(), intent, notes, listId);
    }

}
