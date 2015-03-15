package com.bearsonsoftware.list.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bearsonsoftware.list.R;
import com.bearsonsoftware.list.datatypes.NoteList;
import com.nhaarman.listviewanimations.util.Insertable;
import com.nhaarman.listviewanimations.util.Swappable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Provides the adapter from arraylist to listview for notelists
 */
public class NoteListAdapter extends ArrayAdapter<NoteList> implements Swappable, Insertable<NoteList> {

    private final int INVALID_ID = -1;
    private HashMap<NoteList, Integer> mIdMap = new HashMap<>();
    private final ArrayList<NoteList> noteLists;
    private final Context context;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_NEW = 1;
    private static final int TYPE_MAX_COUNT = TYPE_NEW + 1;

    public static class ViewHolder {
        TextView noteListRowText;
        TextView noteListRowCounter;
    }

    public NoteListAdapter (Context context, ArrayList<NoteList> noteLists){
        super(context, R.layout.note_list_row, noteLists);
        this.noteLists = noteLists;
        this.context = context;
        for (int i = 0; i < noteLists.size(); ++i) {
            mIdMap.put(noteLists.get(i), i);
        }
    }

    @Override
    public void add(int i, NoteList noteList) {
        noteLists.add(i, noteList);
        recountIds();
        notifyDataSetChanged();
    }

    @Override
    public void add(NoteList noteList){
        noteLists.add(noteList);
        recountIds();
        notifyDataSetChanged();
    }

    @Override
    public void remove(NoteList noteList){
        noteLists.remove(noteList);
        recountIds();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position){
        if (!noteLists.get(position).isCreated()) return TYPE_NEW;
        return TYPE_ITEM;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        NoteList noteList = getItem(position);

        int type = this.getItemViewType(position);
        // for both view types, heck if an existing view is being reused, otherwise inflate the view
        switch (type){
            case TYPE_ITEM:
                final ViewHolder viewHolderItem; // view lookup cache stored in tag
                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(R.layout.note_list_row, parent, false);

                    viewHolderItem = new ViewHolder();
                    viewHolderItem.noteListRowText = (TextView) convertView.findViewById(R.id.noteListRowText);
                    viewHolderItem.noteListRowCounter = (TextView) convertView.findViewById(R.id.noteListRowCounter);
                    convertView.setTag(viewHolderItem);
                }  else {
                    viewHolderItem = (ViewHolder) convertView.getTag();
                }

                viewHolderItem.noteListRowText.setText(noteList.getListName());
                viewHolderItem.noteListRowCounter.setText(noteList.getListChildren().toString());
                break;

            case TYPE_NEW:
                final ViewHolder viewHolderNew; // view lookup cache stored in tag

                LayoutInflater inflater2 = LayoutInflater.from(getContext());
                convertView = inflater2.inflate(R.layout.note_list_edit, parent, false);

                viewHolderNew = new ViewHolder();

                convertView.setTag(viewHolderNew);
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
        return noteLists.size();
    }

    public long getItemId(int position) {
        if (position < 0 || position >= mIdMap.size()) {
            return INVALID_ID;
        }
        NoteList item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public void recountIds(){
        mIdMap = new HashMap<>();
        for (int i = 0; i < noteLists.size(); ++i) {
            mIdMap.put(noteLists.get(i), i);
            noteLists.get(i).setListPosition(i + 1);
        }
    }

    public ArrayList<NoteList> getNoteLists(){
        return noteLists;
    }

    @Override
    public void swapItems(int originalPosition, int newPosition) {
        Object temp = noteLists.get(originalPosition);
        noteLists.set(originalPosition, noteLists.get(newPosition));
        noteLists.set(newPosition, (NoteList) temp);
    }

}
