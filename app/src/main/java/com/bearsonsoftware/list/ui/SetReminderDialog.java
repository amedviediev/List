package com.bearsonsoftware.list.ui;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.bearsonsoftware.list.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Show date picker to set reminder for note
 */
public class SetReminderDialog extends DialogFragment{

    private DatePicker datePicker;
    private TimePicker timePicker;

    public SetReminderDialog(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.note_reminder_dialog, container);
        datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        Drawable d = new ColorDrawable(Color.BLACK);
        d.setAlpha(130);
        getDialog().getWindow().setBackgroundDrawable(d);

        Button buttonSave = (Button) view.findViewById(R.id.buttonSaveReminder);
        buttonSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int   day  = datePicker.getDayOfMonth();
                int   month= datePicker.getMonth();
                int   year = datePicker.getYear();

                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm");
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());

                String formattedDate = sdf.format(calendar.getTime());
                NoteActivity activity = (NoteActivity) getActivity();
                activity.getResultsFromFragment(formattedDate);
                getDialog().dismiss();
            }

        });

        Button buttonCancel = (Button) view.findViewById(R.id.buttonCancelReminder);
        buttonCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }

        });

        return view;
    }

}
