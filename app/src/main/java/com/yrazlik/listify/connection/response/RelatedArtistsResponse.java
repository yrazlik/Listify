package com.yrazlik.listify.connection.response;

import com.yrazlik.listify.data.Artist;

import java.util.ArrayList;

/**
 * Created by yrazlik on 9/1/15.
 */
public class RelatedArtistsResponse {

    private ArrayList<Artist> artists;

    public ArrayList<Artist> getArtists() {
        return artists;
    }

    public void setArtists(ArrayList<Artist> artists) {
        this.artists = artists;
    }
}
