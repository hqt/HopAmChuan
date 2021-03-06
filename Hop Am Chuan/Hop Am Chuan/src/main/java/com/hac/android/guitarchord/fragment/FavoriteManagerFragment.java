package com.hac.android.guitarchord.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;

import com.hac.android.config.Config;
import com.hac.android.guitarchord.MainActivity;
import com.hac.android.helper.adapter.IContextMenu;
import com.hac.android.helper.adapter.SongListAdapter;
import com.hac.android.helper.widget.InfinityListView;
import com.hac.android.helper.widget.SongListRightMenuHandler;
import com.hac.android.model.Song;
import com.hac.android.model.dal.FavoriteDataAccessLayer;
import com.hac.android.provider.HopAmChuanDBContract;
import com.hac.android.utils.DialogUtils;
import com.hac.android.utils.LogUtils;
import com.hac.android.utils.NetworkUtils;
import com.hac.android.guitarchord.R;

import java.util.ArrayList;
import java.util.List;

public class FavoriteManagerFragment extends CustomFragment implements
        AdapterView.OnItemSelectedListener,
        InfinityListView.ILoaderContent {

    private static String TAG = LogUtils.makeLogTag(FavoriteManagerFragment.class);

    public int titleRes = R.string.title_activity_my_favorite_fragment;

    /** Main Activity for reference */
    private MainActivity activity;

    /** ListView : contains all items of this fragment */
    private InfinityListView mListView;

    /** List of All songs in favorite */
    private List<Song> songs;

    /** Adapter for this fragment */
    private SongListAdapter mAdapter;

    /** One popup menu for all items **/
    private PopupWindow popupWindow = null;

    /** spinner of this fragment
     * use for user select display setting
     */
    Spinner spinner;
    private String orderMode = HopAmChuanDBContract.Songs.SONG_ISFAVORITE;

//    private ComponentLoadHandler mHandler;
    private View rootView;
    private LayoutInflater inflater;

    public FavoriteManagerFragment() {
    }


    @Override
    public int getTitle() {
        return titleRes;
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_myfavorite, container, false);
        this.inflater = inflater;

        /** Spinner configure */
        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> choices = ArrayAdapter.
                createFromResource(getActivity().getApplicationContext(),
                        R.array.favorite_sort_method, R.layout.custom_spinner_item);
        // Specify the layout to use when the list of choices appears
        choices.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinner.setAdapter(choices);    // Apply the mAdapter to the spinner
        spinner.setOnItemSelectedListener(this);   // because this fragment has implemented method

        // Load component with a delay to reduce lag
//        mHandler = new ComponentLoadHandler();
//        Thread componentLoad = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(Config.LOADING_SMOOTHING_DELAY);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                mHandler.sendMessage(mHandler.obtainMessage());
//            }
//        });
//        UIUtils.setOrientationLock(getActivity());
//        componentLoad.start();
        setUpComponents();
        return rootView;
    }

    private void setUpComponents() {
        songs = new ArrayList<Song>();
        mAdapter = new SongListAdapter(getActivity(), songs);
        mListView = (InfinityListView) rootView.findViewById(R.id.list_view);

        // Event for right menu click
        popupWindow = DialogUtils.createPopup(inflater, R.layout.popup_songlist_menu);
        SongListRightMenuHandler.setRightMenuEvents(activity, popupWindow);

        // Event received from mAdapter.
        mAdapter.contextMenuDelegate = new IContextMenu() {
            @Override
            public void onMenuClick(View view, Song song, ImageView theStar) {
                // Show the popup menu and set selectedSong, theStar
                SongListRightMenuHandler.openPopupMenu(view, song, theStar);
            }
        };

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SongDetailFragment fragment = new SongDetailFragment();
                Bundle arguments = new Bundle();
                arguments.putParcelable("song", songs.get(position));
                fragment.setArguments(arguments);
                activity.switchFragmentNormal(fragment);
                activity.changeTitleBar(songs.get(position).title);
            }
        });
    }

    /** if user click. the list will be sorted again base on choice */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        LogUtils.LOGE(TAG, "On Item Selected  : " + position);
        try {
            switch(position) {
                case 0:
                    // sort by times
                    orderMode = HopAmChuanDBContract.Songs.SONG_ISFAVORITE + " DESC";
                    break;
                case 1:
                    // sort by ABC
                    orderMode = HopAmChuanDBContract.Songs.SONG_TITLE_ASCII;
                    break;
                default:
                    // do nothing
                    break;
            }
            songs = new ArrayList<Song>();
            mAdapter.setSongs(songs);
            if (position == 0) {
                mListView.ignoreIgnoreFirstChange = true;
            }
            // refresh ListView
            reloadInfListView();
            // mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reloadInfListView() {
        /** config mode for this ListView.
         *  this ListView is full rich function. See document for more detail
         */
        /** ListView Configure */
        InfinityListView.ListViewProperty property = new InfinityListView.ListViewProperty();
        property.Loader(this).FirstProcessLoading(true).LoadingView(R.layout.list_item_loading)
                .NumPerLoading(Config.DEFAULT_SONG_NUM_PER_LOAD).RunningBackground(true);
        mListView.setListViewProperty(property);
        mListView.setEmptyView(rootView.findViewById(R.id.empty));


        mListView.resetListView(mAdapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public List load(int offset, int count) {
        NetworkUtils.stimulateNetwork(Config.LOADING_SMOOTHING_DELAY);
        return FavoriteDataAccessLayer.getSongsFromFavorite(
                getActivity().getApplicationContext(),
                orderMode,
                offset,
                count);
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////////////// METHOD FOR ENDLESS LOADING //////////////////////////

    /////////////////
    //
    /////////////////
//    private class ComponentLoadHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            setUpComponents();
//            UIUtils.releaseOrientationLock(getActivity());
//        }
//    }
}