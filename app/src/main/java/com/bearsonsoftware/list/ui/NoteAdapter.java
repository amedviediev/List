package com.bearsonsoftware.list.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bearsonsoftware.list.R;
import com.bearsonsoftware.list.datatypes.Note;
import com.nhaarman.listviewanimations.util.Insertable;
import com.nhaarman.listviewanimations.util.Swappable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Provides the adapter from arraylist to listview for notes
 */
public class NoteAdapter extends ArrayAdapter<Note> implements Swappable, Insertable<Note> {

    private final int INVALID_ID = -1;
    private final Context context;
    private HashMap<Note, Integer> mIdMap = new HashMap<>();
    private final ArrayList<Note> notes;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_NEW = 1;
    private static final int TYPE_MAX_COUNT = TYPE_NEW + 1;

    public static class ViewHolder {
        TextView noteRowText;
    }

    public NoteAdapter (Context context, ArrayList<Note> notes){
        super(context, R.layout.note_list_row, notes);
        this.notes = notes;
        this.context = context;
        for (int i = 0; i < notes.size(); ++i) {
            mIdMap.put(notes.get(i), i);
        }
    }

    @Override
    public void add(int i, Note note) {
        notes.add(i, note);
        recountIds();
        notifyDataSetChanged();
    }

    @Override
    public void add(Note note){
        notes.add(note);
        recountIds();
        notifyDataSetChanged();
    }

    @Override
    public void remove(Note note){
        notes.remove(note);
        recountIds();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position){
        if (!notes.get(position).isCreated()){
            return TYPE_NEW;
        }
        return TYPE_ITEM;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Note note = getItem(position);

        int type = this.getItemViewType(position);
        // for both view types, heck if an existing view is being reused, otherwise inflate the view
        switch (type){
            case TYPE_ITEM:
                final ViewHolder viewHolderItem; // view lookup cache stored in tag
                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(R.layout.note_row, parent, false);

                    viewHolderItem = new ViewHolder();
                    viewHolderItem.noteRowText = (TextView) convertView.findViewById(R.id.noteRowText);

                    convertView.setTag(viewHolderItem);
                }  else {
                    viewHolderItem = (ViewHolder) convertView.getTag();
                }

                //change view depending on item status and properties
                if (notes.get(position).getNoteIsActive() == 0) {
                    viewHolderItem.noteRowText.setText(note.getNoteName());
                    viewHolderItem.noteRowText.setTextColor(Color.parseColor("#999999"));
                    //make text strike-through
                    viewHolderItem.noteRowText.setPaintFlags
                            (viewHolderItem.noteRowText.getPaintFlags()
                                    | Paint.STRIKE_THRU_TEXT_FLAG);
                    viewHolderItem.noteRowText.setBackground(null);
                } else if (notes.get(position).getNoteReminder() == null){
                    viewHolderItem.noteRowText.setText(note.getNoteName());
                    viewHolderItem.noteRowText.setBackgroundResource(R.drawable.glass_btn);
                    //remove strike-through and reset color
                    viewHolderItem.noteRowText.setPaintFlags(0);
                    viewHolderItem.noteRowText.setTextColor(Color.WHITE);
                } else{
                    viewHolderItem.noteRowText.setTextColor(Color.WHITE);
                    String text = note.getNoteName() + "<br />" +
                            "<font color=#999999><small>" + note.getNoteReminder() + "</small></font>";
                    viewHolderItem.noteRowText.setText(Html.fromHtml(text));
                    viewHolderItem.noteRowText.setBackgroundResource(R.drawable.glass_btn);
                    //remove strike-through
                    viewHolderItem.noteRowText.setPaintFlags(0);
                }
                break;

            case TYPE_NEW:
                final ViewHolder viewHolderNew; // view lookup cache stored in tag

                LayoutInflater inflater2 = LayoutInflater.from(getContext());
                convertView = inflater2.inflate(R.layout.note_edit, parent, false);

                viewHolderNew = new ViewHolder();

                convertView.setTag(viewHolderNew);

                break;
        }

        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    public long getItemId(int position) {
        if (position < 0 || position >= mIdMap.size()) {
            return INVALID_ID;
        }
        Note item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public void recountIds(){
        mIdMap = new HashMap<>();
        for (int i = 0; i < notes.size(); ++i) {
            mIdMap.put(notes.get(i), i);
            notes.get(i).setNotePosition(i + 1);
        }
    }

    public ArrayList<Note> getNotes(){
        return notes;
    }

    @Override
    public void swapItems(int originalPosition, int newPosition) {
        Object temp = notes.get(originalPosition);
        notes.set(originalPosition, notes.get(newPosition));
        notes.set(newPosition, (Note) temp);
    }
}
