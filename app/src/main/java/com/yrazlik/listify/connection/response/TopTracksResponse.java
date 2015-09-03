package com.yrazlik.listify.connection.response;

import com.yrazlik.listify.data.Artist;
import com.yrazlik.listify.data.Track;

import java.util.ArrayList;

/**
 * Created by yrazlik on 9/1/15.
 */
public class TopTracksResponse {

    private ArrayList<Track> tracks;

    public ArrayList<Track> getTracks() {
        return tracks;
    }

    public void setTracks(ArrayList<Track> tracks) {
        this.tracks = tracks;
    }
}
