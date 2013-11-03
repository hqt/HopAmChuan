package com.hqt.hac.helper;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.hqt.hac.model.dao.SongArtistDataAccessLayer;
import com.hqt.hac.utils.ParserUtils;
import com.hqt.hac.model.Artist;
import com.hqt.hac.model.Chord;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dao.ArtistDataAcessLayer;
import com.hqt.hac.model.dao.ChordDataAccessLayer;
import com.hqt.hac.model.dao.SongDataAccessLayer;

import java.util.Date;
import java.util.List;

public class Helper {

    /**
     * Use this method for easier development phrase
     */
    public static void prepareLocalDatabase(Context context) {
        // create song database
        List<Song> songs = ParserUtils.getAllSongsFromResource(context);
        SongDataAccessLayer.insertListOfSongs(context, songs);

        // create artist database
        List<Artist> artists = ParserUtils.getAllArtistsFromRescource(context);
        ArtistDataAcessLayer.insertListOfArtists(context, artists);

        // create chord database
        List<Chord> chords = ParserUtils.getAllChordsFromResource(context);
        ChordDataAccessLayer.insertListOfChords(context, chords);
    }

    public static void prepareLocalDatabaseByHand(Context context) {
        // create three artist
        Artist a1 = new Artist(1, "Huynh Quang Thao", "Huynh Quang Thao");
        Artist a2 = new Artist(2, "Dinh Quang Trung", "Dinh Quang Tring");
        Artist a3 = new Artist(3, "Pham Thi Thu Hoa", "Pham Thi Thu Hoa");
        ArtistDataAcessLayer.insertArtist(context, a1);
        ArtistDataAcessLayer.insertArtist(context, a2);
        ArtistDataAcessLayer.insertArtist(context, a3);

        // create two songs
        Song s1 = new Song(1, "Chau Len ba", "www.google.com", "chau len ba chau vo mau giao", "chau len ba", new Date());
        Song s2 = new Song(2, "Lang toi", "www.microsoft.com", "lang toi xanh bong tre", "lang toi", new Date());
        Song s3 = new Song(3, "Quoc Ca", "www.echip.com.vn", "doan quan Viet Nam di", "doan quan Viet Nam", new Date());
        SongDataAccessLayer.insertSong(context, s1);
        SongDataAccessLayer.insertSong(context, s2);
        SongDataAccessLayer.insertSong(context, s3);

        // create author
        // thao : author of two songs
        SongArtistDataAccessLayer.insertSong_Author(context, 1, 1);
        SongArtistDataAccessLayer.insertSong_Author(context, 2, 1);

        // create singer
        //  Trung and Hoa sing Chau Len Ba (1)
        // Hoa sings Quoc Ca (3)
        SongArtistDataAccessLayer.insertSong_Singer(context, 1, 1);
        SongArtistDataAccessLayer.insertSong_Singer(context, 1, 3);
        SongArtistDataAccessLayer.insertSong_Singer(context, 3, 3);
    }

    public static Drawable getDrawableFromResId(Context context, int id) {
        return context.getResources().getDrawable(id);
    }

}
