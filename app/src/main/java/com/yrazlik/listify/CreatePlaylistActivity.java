package com.yrazlik.listify;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;
import com.yrazlik.listify.adapters.PlayListAdapter;
import com.yrazlik.listify.connection.ResponseListener;
import com.yrazlik.listify.connection.ServiceRequest;
import com.yrazlik.listify.connection.request.Request;
import com.yrazlik.listify.connection.response.TopTracksResponse;
import com.yrazlik.listify.data.Artist;
import com.yrazlik.listify.data.Track;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by SUUSER on 01.09.2015.
 */
public class CreatePlaylistActivity extends Activity implements ResponseListener{

    public static String EXTRAS = "EXTRAS";
    public static String EXTRA_RELATED_ARTISTS = "RELATED_ARTISTS";
    private ResponseListener listener = this;
    private Context mContext;
    private ListView playList;
    private PlayListAdapter playListAdapter;
    private ArrayList<String> relatedArtists;
    private ArrayList<Track> selectedTracks;
    Player mPlayer = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_playlist);
        selectedTracks = new ArrayList<Track>();
        if(getIntent() != null && getIntent().getExtras() != null){
            Bundle b = getIntent().getExtras();
            relatedArtists = (ArrayList<String>)b.get(EXTRA_RELATED_ARTISTS);
        }
        initUI();
    }

    private void initUI(){
        playList = (ListView)findViewById(R.id.playList);
        playList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Track t = (Track)parent.getAdapter().getItem(position);
                if(t != null){
                    Config playerConfig = new Config(getApplicationContext(), AppConstants.ACCESS_TOKEN, AppConstants.CLIENT_ID);

                    mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                        @Override
                        public void onInitialized(Player player) {
                            mPlayer.play(t.getUri());
                        }

                        @Override
                        public void onError(Throwable throwable) {
                        }
                    });
                }
            }
        });
        playListAdapter = new PlayListAdapter(this, R.layout.list_row_playlist, selectedTracks);
        playList.setAdapter(playListAdapter);
        getTopTracks();
    }

    private void getTopTracks(){
        for(String id : relatedArtists){
            ServiceRequest request = new ServiceRequest(getBaseContext(), listener);
            request.makeGetArtistsTopTracksRequest(Request.getTopTracks, id);

        }
    }

    @Override
    public void onSuccess(int requestId, Object response) {
        if(requestId == Request.getTopTracks){
            TopTracksResponse topTracksResponse = (TopTracksResponse)response;
            ArrayList<Track> tracks = topTracksResponse.getTracks();
            if(tracks != null && tracks.size() > 0) {
                int topTracksSize = tracks.size();
                int low = 0, high = topTracksSize;
                int rand = new Random().nextInt(high);
                Track t = tracks.get(rand);
                if(selectedTracks == null){
                    selectedTracks = new ArrayList<Track>();
                }

                if(selectedTracks.size() < 25){
                    selectedTracks.add(t);
                }
                playListAdapter.notifyDataSetChanged();

            }
        }
    }

    @Override
    public void onFailure() {

    }
}
