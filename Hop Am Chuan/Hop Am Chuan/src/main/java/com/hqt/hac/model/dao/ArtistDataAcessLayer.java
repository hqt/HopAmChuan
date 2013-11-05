package com.hqt.hac.model.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.hqt.hac.model.Artist;
import com.hqt.hac.model.Song;
import com.hqt.hac.provider.HopAmChuanDBContract;
import com.hqt.hac.provider.HopAmChuanDBContract.SongsAuthors;
import com.hqt.hac.provider.HopAmChuanDBContract.SongsSingers;

import java.util.ArrayList;
import java.util.List;

import static com.hqt.hac.provider.helper.Query.Projections;
import static com.hqt.hac.utils.LogUtils.LOGD;
import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class ArtistDataAcessLayer {

    private static final String TAG = makeLogTag(ArtistDataAcessLayer.class);

    public static String insertArtist(Context context, Artist artist) {
        LOGD(TAG, "Adding an artist");

        ContentValues cv = new ContentValues();
        cv.put(HopAmChuanDBContract.Artists.ARTIST_ID, artist.artistId);
        cv.put(HopAmChuanDBContract.Artists.ARTIST_NAME, artist.artistName);
        cv.put(HopAmChuanDBContract.Artists.ARTIST_ASCII, artist.artistAscii);

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Artists.CONTENT_URI;
        Uri insertedUri = resolver.insert(uri, cv);
        LOGD(TAG, "inserted uri: " + insertedUri);
        return insertedUri.toString();
    }

    public static void insertListOfArtists(Context context, List<Artist> artists) {
        for (Artist artist : artists) {
            insertArtist(context, artist);
        }
    }

    public static void removeArtistByid(Context context, int artistId) {
        LOGD(TAG, "Delete Artist");

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Artists.CONTENT_URI;
        Uri deleteUri = Uri.withAppendedPath(uri, artistId + "");
        resolver.delete(deleteUri, null, null);
    }


    public static Artist getArtistById(Context context, int artistId) {
        LOGD(TAG, "Get Artist By Id");
        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Artists.CONTENT_URI;
        Uri artistUri = Uri.withAppendedPath(uri, artistId + "");

        Cursor c = resolver.query(artistUri,
                                Projections.ARTIST_PROJECTION,    // projection
                                null,                             // selection string
                                null,                             // selection args of strings
                                null);                            //  sort order

        int idCol = c.getColumnIndex(HopAmChuanDBContract.Artists._ID);
        int artistidCol = c.getColumnIndex(HopAmChuanDBContract.Artists.ARTIST_ID);
        int nameCol = c.getColumnIndex(HopAmChuanDBContract.Artists.ARTIST_NAME);
        int asciiCol = c.getColumnIndex(HopAmChuanDBContract.Artists.ARTIST_ASCII);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            int id = c.getInt(idCol);
            int ArtistId = c.getInt(artistidCol);
            String name = c.getString(nameCol);
            String ascii = c.getString(asciiCol);
            if (c != null) {
                c.close();
            }
            return new Artist(id, ArtistId, name, ascii);
        }
        if (c != null) {
            c.close();
        }
        return null;
    }

    public static List<Song> findAllSongsByAuthor(Context context, int ArtistId) {
        LOGD(TAG, "Get All Songs by Author");

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Artists.CONTENT_URI;
        Uri artistUri = Uri.withAppendedPath(uri, "author/songs/" + ArtistId + "");
        Cursor c = resolver.query(artistUri,
                Projections.SONGAUTHOR_PROJECTION,    // projection
                null,                           // selection string
                null,                           // selection args of strings
                null);                          //  sort order

        List<Song> songs = new ArrayList<Song>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            try {
                int id = c.getInt(c.getColumnIndex(SongsAuthors._ID));
                int songId = c.getInt(c.getColumnIndex(SongsAuthors.SONG_ID));
                int artistId = c.getInt(c.getColumnIndex(SongsAuthors.ARTIST_ID));

                songs.add(SongDataAccessLayer.getSongById(context, songId));
            }
            catch(Exception e) {
                LOGE(TAG, "error when parse song " + e.getMessage());
            }
        }
        c.close();
        return songs;
    }

    public static List<Song> findAllSongsBySinger(Context context, int ArtistId) {
        LOGD(TAG, "Get All Songs by Singer");

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Artists.CONTENT_URI;
        Uri artistUri = Uri.withAppendedPath(uri, "singer/songs/" + ArtistId + "");
        Cursor c = resolver.query(artistUri,
                Projections.SONGSINGER_PROJECTION,    // projection
                null,                           // selection string
                null,                           // selection args of strings
                null);                          //  sort order


        List<Song> songs = new ArrayList<Song>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            try {
                int id = c.getInt(c.getColumnIndex(SongsSingers._ID));
                int songId = c.getInt(c.getColumnIndex(SongsSingers.SONG_ID));
                int artistId = c.getInt(c.getColumnIndex(SongsSingers.ARTIST_ID));

                songs.add(SongDataAccessLayer.getSongById(context, songId));
            }
            catch(Exception e) {
                LOGE(TAG, "error when parse song " + e.getMessage());
            }
        }
        c.close();
        return songs;

    }

    public static List<Song> getRandomSongsByAuthor(Context context, int artistId, int limit) {
        throw new UnsupportedOperationException();
    }

    public static List<Song> getRandomSongsBySinger(Context context, int artistId, int limit) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get all songs, can be authors or singers
     * TODO : Later
     */
    public static List<Song> getRandomSongsByArtist(Context context, int artistId, int limit) {
        throw new UnsupportedOperationException();
    }

    /**
     * Just query all AuthorsSongs Table
     * TODO: in future
     */
    public static List<Artist> getAllAuthors(Context context, int authorId) {
        throw new UnsupportedOperationException();
    }

    /**
     * Just query all SingersSongs Table
     * TODO: in future
     */
    public static List<Artist> getAllSingers(Context context, int singerId) {
        throw new UnsupportedOperationException();
    }

}
