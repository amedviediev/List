package com.bearsonsoftware.list.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bearsonsoftware.list.PureListApplication;
import com.bearsonsoftware.list.actions.CancelSave;
import com.bearsonsoftware.list.actions.ChangeNoteList;
import com.bearsonsoftware.list.actions.SaveNoteList;
import com.bearsonsoftware.list.R;
import com.bearsonsoftware.list.billing.BillingManager;
import com.bearsonsoftware.list.billing.Keys;
import com.bearsonsoftware.list.billing.util.IabHelper;
import com.bearsonsoftware.list.billing.util.IabResult;
import com.bearsonsoftware.list.billing.util.Inventory;
import com.bearsonsoftware.list.billing.util.Purchase;
import com.bearsonsoftware.list.database.DatabaseHelper;
import com.bearsonsoftware.list.database.NoteListManager;
import com.bearsonsoftware.list.database.SavePositions;
import com.bearsonsoftware.list.datatypes.NoteList;
import com.bearsonsoftware.list.settings.ThemeManager;
import com.bearsonsoftware.list.ui.widget.ListWidget;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.OnItemMovedListener;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;

import java.util.ArrayList;

/**
 * Base activity, holds all note lists
 */
public class NoteListActivity extends Activity{

    public static final int STATIC_NOTE_REQUEST_CODE = 1;

    private NoteListManager noteListManager;
    private NoteListAdapter adapter;
    private DynamicListView listView;
    private View viewNew;
    private EditText noteListEditText;
    private ArrayList<NoteList> noteLists;

    private IabHelper mHelper;
    private View viewChange;
    private EditText noteListChangeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.RGBA_8888);

        setContentView(R.layout.activity_note_list);

        noteListManager = NoteListManager.getInstance(this);
        noteListManager.open();

        noteLists = noteListManager.getAllNoteLists();

        adapter = new NoteListAdapter(this, noteLists);

        listView =(DynamicListView)findViewById(R.id.noteListView);

        //set up listeners for list items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg) {
                Intent noteActivity = new Intent(NoteListActivity.this, NoteActivity.class);
                noteActivity.putExtra("ListID", noteLists.get(position).getListID());
                noteActivity.putExtra("ListName", noteLists.get(position).getListName());
                startActivityForResult(noteActivity, STATIC_NOTE_REQUEST_CODE);
            }
        });

        listView.enableDragAndDrop();
        listView.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(final AdapterView<?> parent, final View view,
                                                   final int position, final long id) {
                        if (listView != null) {
                            listView.startDragging(position);
                        }
                        return true;
                    }
                }
        );

        listView.setOnItemMovedListener( new OnItemMovedListener() {
            @Override
            public void onItemMoved(int originalPosition, int newPosition) {
                new SavePositions(getApplicationContext(),
                        DatabaseHelper.TABLE_LISTS,
                        adapter.getNoteLists())
                        .execute();
                adapter.recountIds();
            }
        });

        listView.enableSwipeToDismiss(false,
                new OnDismissCallback() {
                    @Override
                    public void onDismiss(final ViewGroup listView, final int[] reverseSortedPositions,
                                          final boolean toTheRight ) {
                        for (int position : reverseSortedPositions) {
                            noteListManager.deleteNoteList(noteLists.get(position));
                            adapter.remove(noteLists.get(position));
                        }
                        noteListEditText.requestFocus();
                    }
                }
        );

        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        //init hidden view for adding new items
        viewNew = findViewById(R.id.noteListNewItem);

        //init edit text (used for creating new items)
        noteListEditText = (EditText) findViewById(R.id.noteListEditText);
        noteListEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    SaveNoteList.save(viewNew, adapter, noteListManager);
                    viewNew.setVisibility(View.GONE);
                    viewNew.requestFocus();
                    return true;
                }
                return false;
            }
        });

        //init hidden view for changing items
        viewChange = findViewById(R.id.noteListChangeItem);

        //init edit text (used for changing items)
        noteListChangeEditText = (EditText) findViewById(R.id.noteListChangeEditText);
        noteListChangeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    ChangeNoteList.makeChange(viewChange, adapter, noteListManager, noteLists.get(0));
                    refreshList();
                    viewChange.setVisibility(View.GONE);
                    viewChange.requestFocus();
                    return true;
                }
                return false;
            }
        });

        //set up google analytics
        //Get a Tracker (should auto-report)
        ((PureListApplication) getApplication()).getTracker(PureListApplication.TrackerName.APP_TRACKER);
    }

    public void onClick(View view) {
        @SuppressWarnings("unchecked")
        NoteList tempNoteList;
        switch (view.getId()) {
            case R.id.buttonAdd:
                // save the new comment to the database
                tempNoteList = new NoteList();
                tempNoteList.setCreated(false); //code to make adapter insert edittext
                //make sure we do not create multiple empty items
                if(noteLists.isEmpty() || noteLists.get(0).isCreated()){
                    //Jelly Bean does not support smooth scroll
                    if(android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.JELLY_BEAN){
                        adapter.add(0, tempNoteList);
                    } else listView.insert(0, tempNoteList);
                }
                listView.smoothScrollToPosition(0);

                //smoothly bring up view with edit text
                listView.postDelayed(new Runnable() {
                    public void run() {
                        View viewBg = findViewById(R.id.noteListNewBackground);

                        noteListEditText.setText("");

                        viewBg.setBackgroundColor(Color.parseColor("#90000000"));
                        viewNew.setVisibility(View.VISIBLE);
                        viewNew.post(new Runnable() {
                            public void run() {
                                noteListEditText.requestFocusFromTouch();
                                showKeyboard(viewNew);
                            }
                        });
                    }
                }, 350); //350ms = time it takes for listview insert animation to finish
                break;
            case R.id.buttonOptions:
                Intent optionsActivity = new Intent(NoteListActivity.this, OptionsActivity.class);
                startActivity(optionsActivity);
                break;
            case R.id.noteListSaveButton:
                SaveNoteList.save(viewNew, adapter, noteListManager);
                viewNew.setVisibility(View.GONE);
                break;
            case R.id.noteListChangeButton:
                ChangeNoteList.makeChange(viewChange, adapter, noteListManager, noteLists.get(0));
                refreshList();
                viewChange.setVisibility(View.GONE);
                viewChange.requestFocus();
                break;
            case R.id.noteListNewBackground:
                CancelSave.cancel(viewNew, adapter);
                viewNew.setVisibility(View.GONE);
                break;
            case R.id.noteListChangeBackground:
                CancelSave.cancel(viewChange, adapter);
                viewChange.setVisibility(View.GONE);
                break;
        }
        adapter.notifyDataSetChanged();
    }

    protected void showKeyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(view.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }

    public void changeItem(int position){
        final NoteList tempNoteList = new NoteList();
        final NoteList noteListToChange = noteLists.get(position);
        tempNoteList.setListID(noteListToChange.getListID());
        int noteListPosition = noteListToChange.getListPosition();

        tempNoteList.setCreated(false); //code to make adapter insert edittext
        //make sure we do not create multiple empty items
        if(noteLists.isEmpty() || noteLists.get(0).isCreated()){
            //Jelly Bean does not support smooth scroll
            if(android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.JELLY_BEAN){
                adapter.add(0, tempNoteList);
            } else listView.insert(0, tempNoteList);
        }
        tempNoteList.setListPosition(noteListPosition);
        listView.smoothScrollToPosition(0);

        //smoothly bring up view with edit text
        listView.postDelayed(new Runnable() {
            public void run() {
                View viewBg = findViewById(R.id.noteListChangeBackground);

                noteListChangeEditText.setText(noteListToChange.getListName());

                viewBg.setBackgroundColor(Color.parseColor("#90000000"));
                viewChange.setVisibility(View.VISIBLE);
                viewChange.post(new Runnable() {
                    public void run() {
                        noteListChangeEditText.requestFocusFromTouch();
                        showKeyboard(viewChange);
                    }
                });
            }
        }, 350); //350ms = time it takes for listview insert animation to finish
    }

    @Override
    public void onBackPressed(){
        if (viewNew.getVisibility() == View.VISIBLE) {
            CancelSave.cancel(viewNew, adapter);
            viewNew.setVisibility(View.GONE);
            viewNew.requestFocus();
        } else if (viewChange.getVisibility() == View.VISIBLE){
            CancelSave.cancel(viewChange, adapter);
            viewChange.setVisibility(View.GONE);
            viewChange.requestFocus();
        } else super.onBackPressed();
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
    protected void onResume() {
        super.onResume();
        new ThemeManager(this).applyTheme();

        //init internal payments and check for payment status
        mHelper = new IabHelper(this, Keys.LICENCE_KEY);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    alert("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.

                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });

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

        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // very important to dispose of billing helper
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //update notelist after changes might have been made to notes
        switch(requestCode) {
            case (STATIC_NOTE_REQUEST_CODE) : {
                if (resultCode == Activity.RESULT_OK) {
                    refreshList();
                }
            }
        }
    }

    protected void refreshList(){
        adapter.clear();
        noteLists = noteListManager.getAllNoteLists();
        adapter = new NoteListAdapter(this, noteLists);
        listView.setAdapter(adapter);
    }

    // Listener that's called when we finish querying the items and subscriptions we own
    final IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                alert("Failed to query inventory: " + result);
                return;
            }

            // Do we have the no-ads upgrade?
            Purchase adsPurchase = inventory.getPurchase(BillingManager.SKU_NO_ADS);
            BillingManager.setAdsHidden((adsPurchase != null && BillingManager.verifyDeveloperPayload(adsPurchase)));
        }
    };

    private void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        bld.create().show();
    }
}
