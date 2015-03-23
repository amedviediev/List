package com.bearsonsoftware.list.alarms;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.provider.CalendarContract;

/**
 * Delete event from calendar
 */
public class DeleteCalenderEvent {

    /**
     * @param context
     * @param eventIdString - String containg event ID in calendar
     */
    public static void delete(Context context, String eventIdString){
        Uri deleteUri;
        try{
            if(!eventIdString.equals(null)){
                long eventID = Long.parseLong(eventIdString);
                deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
                context.getContentResolver().delete(deleteUri, null, null);
            }
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}

