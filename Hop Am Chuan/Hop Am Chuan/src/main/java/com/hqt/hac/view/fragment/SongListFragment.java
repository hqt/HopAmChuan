package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;

import com.hqt.hac.helper.adapter.SongListAdapter;
import com.hqt.hac.helper.widget.DialogFactory;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dao.SongDataAccessLayer;
import com.hqt.hac.helper.widget.SongListRightMenuHandler;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

import java.util.List;

/**
 * Fragment uses for viewing songs as categories
 */
public class SongListFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    /** Main Activity for reference */
    MainActivity activity;
    ListView mListView;
    List<Song> songs;

    /** One popup menu for all items **/
    PopupWindow popupWindow = null;

    /** Adapter for this fragment */
    SongListAdapter songlistAdapter;

    public SongListFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_song_list, container, false);

        /** Spinner : create mAdapter for Spinner */
        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner_method_list);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.
                createFromResource(getActivity().getApplicationContext(),
                        R.array.song_list_method, R.layout.custom_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinner.setAdapter(adapter);    // Apply the mAdapter to the spinner
        spinner.setOnItemSelectedListener(this);    // because this fragment has implemented method


        /** Default song list **/
        songs = SongDataAccessLayer.getRecentSongs(activity.getApplicationContext(), 10);

        /** ListView Configure */
        mListView = (ListView) rootView.findViewById(R.id.list_view);
        songlistAdapter = new SongListAdapter(activity, songs);

        // Event for right menu click
        popupWindow = DialogFactory.createPopup(inflater, R.layout.popup_songlist_menu);
        SongListRightMenuHandler.setRightMenuEvents(activity, popupWindow);

        // Event received from mAdapter.
        songlistAdapter.rightMenuClick = new SongListAdapter.RightMenuClick() {
            @Override
            public void onRightMenuClick(View view, Song song) {
                // Show the popup menu and set selectedSong
                /** Store the song that user clicked on the right menu (the star) **/
                SongListRightMenuHandler.selectedSong = song;
                popupWindow.showAsDropDown(view);
            }
        };

        mListView.setAdapter(songlistAdapter);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SongDetailFragment fragment = new SongDetailFragment();
                Bundle arguments = new Bundle();
                arguments.putSerializable("song", songs.get(position));
                fragment.setArguments(arguments);
                activity.switchFragment(fragment);
            }
        });

        return rootView;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position) {
            // Moi xem gan day
            case 0:
                songlistAdapter.setSongs(SongDataAccessLayer.getRecentSongs(activity.getApplicationContext(), 10));
                break;
            // Moi cap nhat
            case 1:
                songlistAdapter.setSongs(SongDataAccessLayer.getNewSongs(activity.getApplicationContext(), 10));
                break;
            case 2:
            // Bai hat ngau nhien
                songlistAdapter.setSongs(SongDataAccessLayer.getRandSongs(activity.getApplicationContext(), 10));
                break;
            default:
                // do nothing
        }

        songlistAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
