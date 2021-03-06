package com.hac.android.helper.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hac.android.guitarchord.R;

import java.util.HashMap;
import java.util.List;

public class FindByChordAdapter extends ArrayAdapter<String> {

    Context mContext;

    /** delegate is the callback to fragment / mActivity */
    IFindByChordAdapter delegate;

    private List<String> chords;

    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

    private View.OnTouchListener mTouchListener;

    public FindByChordAdapter(Context context, IFindByChordAdapter delegate, List<String> chords) {
        super(context, R.layout.list_item_chord_search,chords);
        this.mContext = context.getApplicationContext();
        this.delegate = delegate;
        this.chords = chords;
        // building stable id
        buildIdMap();
    }

    public void setChords(List<String> chords) {
        this.chords = chords;
        buildIdMap();
    }

    public String getChords() {
        if (chords.size() == 0) return "";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chords.size() - 1; ++i) {
            builder.append(chords.get(i) + ",");
        }
        builder.append(chords.get(chords.size() - 1));
        return builder.toString();
    }

    public boolean addChord(String chord) {
        if (!isDuplicatedChord(chord)) {
            chords.add(chord);
            buildIdMap();
            return true;
        } else {
            return false;
        }

    }

    public boolean isDuplicatedChord(String chord) {
        for (int i = 0; i < chords.size(); i++) {
            if (chords.get(i).equals(chord)) return true;
        }
        return false;
    }

    public void removeChord(int position) {
        chords.remove(position);
        buildIdMap();
    }

    private void buildIdMap() {
        mIdMap = new HashMap<String, Integer>();
        for (int i = 0; i < chords.size(); ++i) {
            mIdMap.put(chords.get(i), i);
        }
    }

    public void setTouchListener(View.OnTouchListener mTouchListener) {
        this.mTouchListener = mTouchListener;
    }

    @Override
    public int getCount() {
        return chords.size();
    }

    @Override
    public long getItemId(int position) {
        // String item = getItem(position);
        // return mIdMap.get(item);
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        View row = convertView;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (row == null) {
            row = inflater.inflate(R.layout.list_item_chord_search, null);
            holder = new ViewHolder();
            holder.chordTextView = (TextView) row.findViewById(R.id.text_view);
            holder.removeImageView = (ImageView) row.findViewById(R.id.image_view);
            row.setTag(holder);
            row.setOnTouchListener(mTouchListener);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        holder.chordTextView.setText(chords.get(position));
        holder.removeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.removeChordFromList(position);
            }
        });

        row.setOnTouchListener(mTouchListener);

        return row;
    }

    public static class ViewHolder {
        public TextView chordTextView;
        public ImageView removeImageView;
    }

    public static interface IFindByChordAdapter {
        public void removeChordFromList(int position);
    }
}
