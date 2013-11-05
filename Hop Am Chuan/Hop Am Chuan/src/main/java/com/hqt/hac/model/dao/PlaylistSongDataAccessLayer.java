package com.hqt.hac.model.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.hqt.hac.provider.HopAmChuanDBContract;

import static com.hqt.hac.utils.LogUtils.LOGD;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

/**
 * Created by Quang Trung on 11/4/13.
 *
 *
 * Middle table : Playlist-Song
 * Playlist and Song MUST already exists in table
 */
public class PlaylistSongDataAccessLayer {
    private static String TAG = makeLogTag(PlaylistSongDataAccessLayer.class);
    public static String insertPlaylist_Song(Context context, int playlistId, int songId) {
        LOGD(TAG, "Adding an playlist_song");

        ContentValues cv = new ContentValues();
        cv.put(HopAmChuanDBContract.PlaylistSongs.PLAYLIST_ID, playlistId);
        cv.put(HopAmChuanDBContract.PlaylistSongs.SONG_ID, songId);

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.PlaylistSongs.CONTENT_URI;
        Uri insertedUri = resolver.insert(uri, cv);
        LOGD(TAG, "inserted uri: " + insertedUri);
        return insertedUri.toString();
    }
}
