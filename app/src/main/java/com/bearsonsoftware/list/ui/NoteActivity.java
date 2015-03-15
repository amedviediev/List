package com.bearsonsoftware.list.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bearsonsoftware.list.PureListApplication;
import com.bearsonsoftware.list.actions.CancelSave;
import com.bearsonsoftware.list.actions.SaveNote;
import com.bearsonsoftware.list.R;
import com.bearsonsoftware.list.billing.BillingManager;
import com.bearsonsoftware.list.database.DatabaseHelper;
import com.bearsonsoftware.list.database.NoteManager;
import com.bearsonsoftware.list.database.SaveNoteStatus;
import com.bearsonsoftware.list.database.SavePositions;
import com.bearsonsoftware.list.datatypes.Note;
import com.bearsonsoftware.list.settings.ThemeManager;
import com.bearsonsoftware.list.ui.widget.ListWidget;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.OnItemMovedListener;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;


import java.util.ArrayList;

/**
 * Activity shows all notes in specific note list (passed in the intent)
 */
public class NoteActivity extends Activity {

    public static final String LAUNCH_NOTE_ACTIVITY = "launchNoteActivity";
    private NoteManager noteManager;
    private ArrayList<Note> notes;
    private NoteAdapter adapter;
    private DynamicListView listView;
    private long listId;
    private View viewNew;
    private Button reminderButton;
    private EditText noteEditText;
    private AdView adView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check if user purchased no-ads option, and apply appropriate layout
        if (BillingManager.isAdsHidden()){
            setContentView(R.layout.activity_note_noads);
        } else {
            setContentView(R.layout.activity_note);
            adView = (AdView) this.findViewById(R.id.adView);

            // Request for Ads
            AdRequest adRequest = new AdRequest.Builder()
                    //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    //.addTestDevice("B268D54F89406426D3A70E6458B6142C")
                    //.addTestDevice("703F0CB6BBDB4A45763BBD311FAD689C")
                    .build();

            // Load ads into Banner Ads
            adView.loadAd(adRequest);
        }

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        //get list name and list ID
        Intent myIntent = getIntent();
        listId = myIntent.getLongExtra("ListID", 0);
        String listName = myIntent.getStringExtra("ListName");

        final TextView listNameView = (TextView) findViewById(R.id.textViewListName);
        listNameView.setText(listName);

        noteManager = NoteManager.getInstance(this);
        noteManager.open();

        notes = noteManager.getAllNotes(listId);
        adapter = new NoteAdapter(this, notes);

        listView =(DynamicListView)findViewById(R.id.noteView);

        //set up listeners for list items
        listView.enableDragAndDrop();

        listView.enableSwipeToDismiss(true,
                new OnDismissCallback() {
                    @Override
                    public void onDismiss(final ViewGroup mListView, final int[] reverseSortedPositions,
                                          final boolean toTheRight ) {
                        if(toTheRight){
                            for (int position : reverseSortedPositions){
                                Note currentNote = notes.get(position);
                                if (currentNote.getNoteIsActive() == 1){
                                    currentNote.setNoteIsActive(0);
                                } else {
                                    currentNote.setNoteIsActive(1);
                                }

                                new SaveNoteStatus(getApplicationContext(), currentNote).execute();
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            for (int position : reverseSortedPositions) {
                                noteManager.deleteNote(notes.get(position));
                                adapter.remove(notes.get(position));
                            }
                        }
                    }
                }
        );

        listView.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(final AdapterView<?> parent, final View view,
                                                   final int position, final long id) {
                        if (listView != null) {
                            listView.startDragging(position - listView.getHeaderViewsCount());
                        }
                        return true;
                    }
                }
        );

        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(notes.get(position).getNotePosition());
            }
        });

        listView.setOnItemMovedListener(new OnItemMovedListener() {
            @Override
            public void onItemMoved(int originalPosition, int newPosition) {
                new SavePositions(getApplicationContext(),
                        DatabaseHelper.TABLE_NOTES,
                        adapter.getNotes()).execute();
                adapter.recountIds();
            }
        });

        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        //init hidden view for adding new items
        viewNew = findViewById(R.id.noteNewItem);
        reminderButton = (Button) findViewById(R.id.setReminderButton);

        //init edit text (used for creating new items)
        noteEditText = (EditText) findViewById(R.id.noteEditText);
        noteEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    SaveNote.save(viewNew, adapter, noteManager, listId, notes.get(0).getNoteReminder());
                    viewNew.setVisibility(View.GONE);
                    viewNew.requestFocus();
                    return true;
                }
                return false;
            }
        });

        //set up google analytics
        //Get a Tracker (should auto-report)
        ((PureListApplication) getApplication()).getTracker(PureListApplication.TrackerName.APP_TRACKER);
    }


    @Override
    protected void onStart(){
        super.onStart();
        //Get an Analytics tracker to report app starts and uncaught exceptions etc.
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop(){
        super.onStop();
        //Stop the analytics tracking
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        new ThemeManager(this).applyTheme();
        noteManager.open();

        if(adView != null) adView.resume();
    }

    @Override
    protected void onPause() {

        //update widget
        Intent intent = new Intent(this, ListWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance
                (getApplication()).getAppWidgetIds(new ComponentName(getApplication(), ListWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);


        //pause ads
        if(adView != null) adView.pause();

        super.onPause();
    }

    public void onClick(View view) {
        @SuppressWarnings("unchecked")
        Note note;
        switch (view.getId()) {
            case R.id.buttonAddNote:
                note = new Note();
                note.setCreated(false); //code to make adapter insert edittext
                //make sure we do not create multiple empty items
                if(notes.isEmpty() || notes.get(0).isCreated()){
                    //Jelly Bean does not support smooth scroll
                    if(android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.JELLY_BEAN){
                        adapter.add(0, note);
                    } else listView.insert(0, note);
                }

                listView.smoothScrollToPosition(0);

                //smoothly bring up view with edit text
                listView.postDelayed(new Runnable() {
                    public void run() {
                        View viewBg = findViewById(R.id.noteNewBackground);

                        noteEditText.setText("");
                        reminderButton.setText(getString(R.string.set_reminder_time));

                        viewBg.setBackgroundColor(Color.parseColor("#90000000"));
                        viewNew.setVisibility(View.VISIBLE);
                        viewNew.post(new Runnable() {
                            public void run() {
                                noteEditText.requestFocusFromTouch();
                                showKeyboard(viewNew);
                            }
                        });
                    }
                }, 400);  //400ms = time it takes for listview insert animation to finish
                break;
            case R.id.buttonBackNote:
                onBackPressed();
                break;
            case R.id.noteSaveButton:
                SaveNote.save(viewNew, adapter, noteManager, listId, notes.get(0).getNoteReminder());
                viewNew.setVisibility(View.GONE);
                break;
            case R.id.setReminderButton:
                viewNew.post(new Runnable() {
                    public void run() {
                        hideKeyboard(viewNew);
                    }
                });
                showSetReminderDialog();
                break;
            case R.id.noteNewBackground:
                CancelSave.cancel(viewNew, adapter);
                viewNew.setVisibility(View.GONE);
                break;
        }
        adapter.notifyDataSetChanged();
    }

    protected void showKeyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(view.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

    }

    protected void hideKeyboard(View view) {
        InputMethodManager in = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed(){
        if (viewNew.getVisibility() == View.VISIBLE) {
            CancelSave.cancel(viewNew, adapter);
            viewNew.setVisibility(View.GONE);
            viewNew.requestFocus();
        } else {
            Intent resultIntent;
            resultIntent = new Intent();
            setResult(Activity.RESULT_OK, resultIntent);
            this.finish();
            overridePendingTransition(R.anim.back_slide_in, R.anim.back_slide_out);
        }
    }

    //display dialogfragment for users to pick reminder date
    private void showSetReminderDialog(){
        FragmentManager fragmentManager = getFragmentManager();
        SetReminderDialog setReminderDialog = new SetReminderDialog();
        setReminderDialog.show(fragmentManager, "fragment_set_reminder");
    }

    //upon successful selection of reminder date, work with results
    public void getResultsFromFragment(String reminderDate){
        Note note = notes.get(0);
        note.setNoteReminder(reminderDate);
        adapter.notifyDataSetChanged();
        reminderButton.setText("Reminder on: " + reminderDate);
        viewNew.post(new Runnable() {
            public void run() {
                noteEditText.requestFocusFromTouch();
                showKeyboard(viewNew);
            }
        });
    }


}
