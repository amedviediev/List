package com.bearsonsoftware.list.alarms;

import android.content.ContentValues;
import android.content.Context;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.net.Uri;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Class to set and delete events via android calendar
 */
public class CalendarReminderManager {

    public CalendarReminderManager(){

    }

    public long addReminder(Context context, String dateAndTime, String title){

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm");

        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        try {
            startTime.setTime(sdf.parse(dateAndTime));
            endTime.setTime(sdf.parse(dateAndTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long startMillis = startTime.getTimeInMillis();
        long endMillis = endTime.getTimeInMillis();

        ContentValues eventValues = new ContentValues();

        eventValues.put(Events.DTSTART, startMillis);
        eventValues.put(Events.DTEND, endMillis);
        eventValues.put(Events.CALENDAR_ID, 1);
        eventValues.put(Events.TITLE, title);
        eventValues.put(Events.DESCRIPTION, "A reminder for " + title);
        eventValues.put(Events.EVENT_TIMEZONE, String.valueOf(TimeZone.getDefault()));

        Uri eventUri = context.getContentResolver().insert(Events.CONTENT_URI, eventValues);
        long eventID = Long.parseLong(eventUri.getLastPathSegment());

        ContentValues reminderValues = new ContentValues();

        reminderValues.put(CalendarContract.Reminders.EVENT_ID, eventID);
        reminderValues.put(CalendarContract.Reminders.MINUTES, 1);
        reminderValues.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);

        Uri reminderUri = context.getContentResolver().insert(CalendarContract.Reminders.CONTENT_URI, reminderValues);

        return eventID;
    }
}
