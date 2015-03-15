package com.bearsonsoftware.list.ui.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Html;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bearsonsoftware.list.R;
import com.bearsonsoftware.list.database.NoteManager;
import com.bearsonsoftware.list.datatypes.Note;

import java.util.ArrayList;

/**
 * Adapter for widget listview
 */
public class WidgetListProvider implements RemoteViewsService.RemoteViewsFactory {
    private ArrayList<Note> notes;
    private Context context = null;
    private int appWidgetId;
    private long listId;
    private int count = 0;

    public WidgetListProvider(Context context, Intent intent, ArrayList<Note> notes, long listId) {
        this.context = context;
        this.notes = notes;
        this.listId = listId;

        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }


    @Override
    public void onCreate() {

    }

    //count to ignore first initialization
    @Override
    public void onDataSetChanged() {
        count++;
        if (count > 1) {
            NoteManager noteManager = NoteManager.getInstance(context);
            noteManager.open();
            if (notes != null) notes.clear();
            notes = noteManager.getAllNotes(listId);
        }
    }

    @Override
    public void onDestroy() {
        if (notes != null) notes.clear();
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.note_row);
        Note note;

        if(position < notes.size()) {
            note = notes.get(position);
        } else return remoteView;

        if (notes.get(position).getNoteIsActive() == 0) {
            remoteView.setTextViewText(R.id.noteRowText, note.getNoteName());
            remoteView.setTextColor(R.id.noteRowText, Color.parseColor("#999999"));
            //make text strike-through
            remoteView.setInt(R.id.noteRowText, "setPaintFlags", Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        } else if (notes.get(position).getNoteReminder() == null){
            //default text presentation
            remoteView.setTextColor(R.id.noteRowText, Color.WHITE);
            remoteView.setInt(R.id.noteRowText, "setPaintFlags", 0);
            remoteView.setTextViewText(R.id.noteRowText, note.getNoteName());
        } else {
            String text = note.getNoteName() + "<br />" +
                    "<font color=#999999><small>" + note.getNoteReminder() + "</small></font>";
            remoteView.setTextViewText(R.id.noteRowText, Html.fromHtml(text));
        }

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }
}
