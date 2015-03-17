package com.bearsonsoftware.list.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

import com.bearsonsoftware.list.R;
import com.bearsonsoftware.list.database.NoteListManager;
import com.bearsonsoftware.list.datatypes.NoteList;
import com.bearsonsoftware.list.ui.NoteActivity;

import java.util.ArrayList;

/**
 * Class to display homescreen widget
 */
public class ListWidget extends AppWidgetProvider{

    public static final String ACTION_WIDGET_PREVIOUS = "ActionWidgetPrevious";
    public static final String ACTION_WIDGET_OPEN = "ActionWidgetOpen";
    public static final String ACTION_WIDGET_NEXT = "ActionWidgetNext";
    private SharedPreferences settings;
    private NoteListManager noteListManager;
    private int currentPosition;
    private long listId;
    private ArrayList<NoteList> noteLists;
    private RemoteViews widget;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            widget = updateWidgetListView(context, appWidgetIds[i]);

            appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
            super.onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();


        noteListManager = NoteListManager.getInstance(context);
        noteListManager.open();
        noteLists = noteListManager.getAllNoteLists();
        int size = noteLists.size();

        settings = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        if(settings.contains("widgetPosition")){
            currentPosition = settings.getInt("widgetPosition", 0);
        } else currentPosition = 0;

        SharedPreferences.Editor editor = settings.edit();

        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            //reset widget position to avoid any conflicts
            editor.putInt("widgetPosition", 0);
            editor.apply();
            fireAllUpdates(context, intent);

        } else if (action.equals(ACTION_WIDGET_PREVIOUS)){
            currentPosition--;
            if (currentPosition < 0) currentPosition = size - 1;
            editor.putInt("widgetPosition", currentPosition);
            editor.apply();

            fireAllUpdates(context, intent);
        } else if (action.equals(ACTION_WIDGET_OPEN)){
            Intent noteActivity = new Intent(context, NoteActivity.class);
            noteActivity.setAction(NoteActivity.LAUNCH_NOTE_ACTIVITY);
            noteActivity.putExtra("ListID", noteLists.get(currentPosition).getListID());
            noteActivity.putExtra("ListName", noteLists.get(currentPosition).getListName());
            noteActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(noteActivity);

        } else if (action.equals(ACTION_WIDGET_NEXT)){
            currentPosition++;
            if (currentPosition > size - 1) currentPosition = 0;
            editor.putInt("widgetPosition", currentPosition);
            editor.apply();

            fireAllUpdates(context, intent);
        } else {
            super.onReceive(context, intent);
        }
    }

    private void fireAllUpdates(Context context, Intent intent){
        final ComponentName cn = new ComponentName(context,
                ListWidget.class);
        final AppWidgetManager manager = AppWidgetManager.getInstance(context);
        final int[] appWidgetIds = manager.getAppWidgetIds(cn);

        final int N = appWidgetIds.length;
        for(int i = 0; i < N; i++){
            RemoteViews remoteViews = updateWidgetListView(context,
                    appWidgetIds[i]);
            manager.updateAppWidget(appWidgetIds[i], remoteViews);
        }

        manager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listViewWidget);
    }

    /**
     * @return Method to populate app widget listView and setting button click
     *         action as well
     */
    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {
        noteListManager = NoteListManager.getInstance(context);
        noteListManager.open();
        noteLists = noteListManager.getAllNoteLists();

        widget = new RemoteViews(context.getPackageName(), R.layout.widget);
        settings = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        if(settings.contains("widgetPosition")){
            currentPosition = settings.getInt("widgetPosition", 0);
        } else currentPosition = 0;

        Intent mIntent = new Intent(context, ListWidget.class);
        mIntent.setAction(ACTION_WIDGET_PREVIOUS);
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, mIntent, 0);
        widget.setOnClickPendingIntent(R.id.buttonPreviousList, actionPendingIntent);

        /*mIntent = new Intent(context, ListWidget.class);
        mIntent.setAction(ACTION_WIDGET_OPEN);
        actionPendingIntent = PendingIntent.getBroadcast(context, 0, mIntent, 0);
        widget.setOnClickPendingIntent(R.id.textViewWidgetListName, actionPendingIntent);*/

        mIntent = new Intent(context, ListWidget.class);
        mIntent.setAction(ACTION_WIDGET_NEXT);
        actionPendingIntent = PendingIntent.getBroadcast(context, 0, mIntent, 0);
        widget.setOnClickPendingIntent(R.id.buttonNextList, actionPendingIntent);

        if(!noteLists.isEmpty()){
            listId = noteLists.get(currentPosition).getListID();
            widget.setTextViewText(R.id.textViewWidgetListName, noteLists.get(currentPosition).getListName());

            mIntent = new Intent(context, WidgetService.class);
            mIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            mIntent.putExtra("listid", listId);
            mIntent.setData(Uri.parse(mIntent.toUri(Intent.URI_INTENT_SCHEME)));

            widget.setRemoteAdapter(R.id.listViewWidget, mIntent);
        } else {
            String s = context.getString(R.string.no_tasks);
            widget.setTextViewText(R.id.textViewWidgetListName, s);
        }

        return widget;
    }

}
