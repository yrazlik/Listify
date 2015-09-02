package com.yrazlik.listify;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;
import com.yrazlik.listify.adapters.PlayListAdapter;
import com.yrazlik.listify.connection.ResponseListener;
import com.yrazlik.listify.connection.ServiceRequest;
import com.yrazlik.listify.connection.request.Request;
import com.yrazlik.listify.connection.response.AddTracksToPlaylistResponse;
import com.yrazlik.listify.connection.response.CreatePlaylistResponse;
import com.yrazlik.listify.connection.response.TopTracksResponse;
import com.yrazlik.listify.connection.response.UserProfileResponse;
import com.yrazlik.listify.data.Track;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by SUUSER on 01.09.2015.
 */
public class CreatePlaylistActivity extends Activity implements ResponseListener, View.OnClickListener{

    public static String EXTRAS = "EXTRAS";
    public static String EXTRA_RELATED_ARTISTS = "RELATED_ARTISTS";
    private ResponseListener listener = this;
    private Context mContext;
    private ListView playList;
    private PlayListAdapter playListAdapter;
    private ArrayList<String> relatedArtists;
    private ArrayList<Track> selectedTracks;
    private String newlyCreatedSnapshotId;
    private CreatePlaylistResponse savedPlaylistResponse;
    Player mPlayer = null;
    private Button buttonNew, buttonSave, buttonOpenInSpotify;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        selectedTracks = new ArrayList<Track>();
        if(getIntent() != null && getIntent().getExtras() != null){
            Bundle b = getIntent().getExtras();
            relatedArtists = (ArrayList<String>)b.get(EXTRA_RELATED_ARTISTS);
        }
        initUI();
    }

    private void initUI(){
        playList = (ListView)findViewById(R.id.playList);
        buttonNew = (Button)findViewById(R.id.buttonNew);
        buttonSave = (Button)findViewById(R.id.buttonSave);
        buttonOpenInSpotify = (Button)findViewById(R.id.buttonOpenInSpotify);
        buttonNew.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
        buttonOpenInSpotify.setOnClickListener(this);

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
        }else if(requestId == Request.getUserProfile){
            UserProfileResponse userProfileResponse = (UserProfileResponse)response;
            String userId = userProfileResponse.getId();
            AppData.setUserId(userId);
            String jsonData = "{\"name\":\"Listify Playlist\", \"public\":false}";

            ServiceRequest request = new ServiceRequest(this, listener);
            request.makeCreatePlaylistRequest(Request.createPlaylist, jsonData);
        }else if(requestId == Request.createPlaylist){
            CreatePlaylistResponse createPlaylistResponse = (CreatePlaylistResponse)response;
            savedPlaylistResponse = createPlaylistResponse;
            ServiceRequest request = new ServiceRequest(this, listener);
           String jsonData = "{\"uris\": [";
            for(Track t : selectedTracks){
                jsonData += "\"" + t.getUri() + "\"" + ",";
            }
            jsonData = jsonData.substring(0, jsonData.length()-1);
            jsonData += "]}";
            request.makeAddTracksToPlaylistRequest(Request.addTracksToPlaylist, createPlaylistResponse.getNameValuePairs().getId(), jsonData);
        }else if(requestId == Request.addTracksToPlaylist){
            AddTracksToPlaylistResponse addTracksToPlaylistResponse = new AddTracksToPlaylistResponse();
            newlyCreatedSnapshotId = addTracksToPlaylistResponse.getSnapshot_id();
            Toast.makeText(this, "Succesful", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure() {

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.buttonNew){
            finish();
        }else if(v.getId() == R.id.buttonSave){
            //TODO: save playlist on spotify
            //First get user's id:
            ServiceRequest request = new ServiceRequest(this, listener);
            request.makeGetUserProfileRequest(Request.getUserProfile);

        }else if(v.getId() == R.id.buttonOpenInSpotify){
            Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
            intent.setData(Uri.parse(
                    savedPlaylistResponse.getNameValuePairs().getUri()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
