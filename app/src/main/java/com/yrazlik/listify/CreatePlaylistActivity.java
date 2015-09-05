package com.yrazlik.listify;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
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
import java.util.Collections;
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
    private Button buttonNew, buttonSave, buttonOpenInSpotify;
    private Track nowPlayingTrack;
    private RelativeLayout loadingLayout;
    private ImageView dot1, dot2, dot3;
    private boolean showDots = true;
    private TextView percentage;
    private ProgressBar progress;
    private RelativeLayout parent;
    private boolean backButtonCanBeUsed = false;
    private ServiceRequest topTracksRequest;
    private boolean saved = false;

    int time = 0;
    private Tracker mTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        selectedTracks = new ArrayList<Track>();
        if(getIntent() != null && getIntent().getExtras() != null){
            Bundle b = getIntent().getExtras();
            relatedArtists = (ArrayList<String>)b.get(EXTRA_RELATED_ARTISTS);
        }
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        ListifyApplication application = (ListifyApplication) getApplication();
        mTracker = application.getDefaultTracker();
        reportGoogleAnaytics();
        initUI();
    }

    private void initUI(){
        parent = (RelativeLayout)findViewById(R.id.parent);
        progress = (ProgressBar)findViewById(R.id.progress);
        percentage = (TextView)findViewById(R.id.percentage);
        loadingLayout = (RelativeLayout)findViewById(R.id.loadingLayout);
        dot1 = (ImageView)findViewById(R.id.dot1);
        dot2 = (ImageView)findViewById(R.id.dot2);
        dot3 = (ImageView)findViewById(R.id.dot3);
        dot2.setVisibility(View.INVISIBLE);
        dot3.setVisibility(View.INVISIBLE);

        CountDownTimer t = new CountDownTimer(4800, 600) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(time%3 == 0){
                    dot1.setVisibility(View.VISIBLE);
                    dot2.setVisibility(View.INVISIBLE);
                    dot3.setVisibility(View.INVISIBLE);
                }else if(time%3 == 1){
                    dot1.setVisibility(View.VISIBLE);
                    dot2.setVisibility(View.VISIBLE);
                    dot3.setVisibility(View.INVISIBLE);
                }else{
                    dot1.setVisibility(View.VISIBLE);
                    dot2.setVisibility(View.VISIBLE);
                    dot3.setVisibility(View.VISIBLE);
                }

                time++;

            }

            @Override
            public void onFinish() {
                topTracksRequest.cancelAllTopTracksRequests();
                if(selectedTracks.size() >= 20){
                    loadingLayout.setVisibility(View.GONE);
                    Collections.shuffle(selectedTracks);
                    playListAdapter.notifyDataSetChanged();
                    backButtonCanBeUsed = true;
                }else {
                    getTopTracks();
                    start();
                }
            }
        };


        t.start();

        playList = (ListView)findViewById(R.id.playList);
        buttonNew = (Button)findViewById(R.id.buttonNew);
        buttonSave = (Button)findViewById(R.id.buttonSave);
        buttonOpenInSpotify = (Button)findViewById(R.id.buttonOpenInSpotify);
        buttonNew.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
        buttonOpenInSpotify.setOnClickListener(this);

        playList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
                final Track t = (Track)parent.getAdapter().getItem(position);
                final ImageView playButton = (ImageView)parent.findViewById(R.id.playButton);
                if(t != null){
                    Config playerConfig = new Config(getApplicationContext(), AppConstants.ACCESS_TOKEN, AppConstants.CLIENT_ID);

                    if(AppData.mPlayer == null) {
                        AppData.mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                            @Override
                            public void onInitialized(Player player) {
                                if (t.isPlaying) {
                                    for (int i = 0; i < parent.getAdapter().getCount(); i++) {
                                        Track track = (Track) parent.getAdapter().getItem(i);
                                        track.isPlaying = false;
                                    }
                                    AppData.mPlayer.pause();
                                    ((PlayListAdapter) parent.getAdapter()).notifyDataSetChanged();
                                } else {
                                    for (int i = 0; i < parent.getAdapter().getCount(); i++) {
                                        Track track = (Track) parent.getAdapter().getItem(i);
                                        track.isPlaying = false;
                                    }
                                    if (nowPlayingTrack != null && nowPlayingTrack.getUri().equalsIgnoreCase(t.getUri())) {
                                        AppData.mPlayer.resume();
                                        t.isPlaying = true;
                                        playButton.setBackgroundResource(R.drawable.pause);
                                        ((PlayListAdapter) parent.getAdapter()).notifyDataSetChanged();
                                        nowPlayingTrack = t;
                                    } else {
                                        AppData.mPlayer.play(t.getUri());
                                        t.isPlaying = true;
                                        playButton.setBackgroundResource(R.drawable.pause);
                                        nowPlayingTrack = t;
                                        ((PlayListAdapter) parent.getAdapter()).notifyDataSetChanged();
                                    }

                                }

                            }

                            @Override
                            public void onError(Throwable throwable) {
                            }
                        });
                    }else {
                        if (t.isPlaying) {
                            for (int i = 0; i < parent.getAdapter().getCount(); i++) {
                                Track track = (Track) parent.getAdapter().getItem(i);
                                track.isPlaying = false;
                            }
                            AppData.mPlayer.pause();
                            ((PlayListAdapter) parent.getAdapter()).notifyDataSetChanged();
                        } else {
                            for (int i = 0; i < parent.getAdapter().getCount(); i++) {
                                Track track = (Track) parent.getAdapter().getItem(i);
                                track.isPlaying = false;
                            }
                            if (nowPlayingTrack != null && nowPlayingTrack.getUri().equalsIgnoreCase(t.getUri())) {
                                AppData.mPlayer.resume();
                                t.isPlaying = true;
                                playButton.setBackgroundResource(R.drawable.pause);
                                ((PlayListAdapter) parent.getAdapter()).notifyDataSetChanged();
                                nowPlayingTrack = t;
                            } else {
                                AppData.mPlayer.play(t.getUri());
                                t.isPlaying = true;
                                playButton.setBackgroundResource(R.drawable.pause);
                                nowPlayingTrack = t;
                                ((PlayListAdapter) parent.getAdapter()).notifyDataSetChanged();
                            }

                        }
                    }
                }
            }
        });
        playListAdapter = new PlayListAdapter(this, R.layout.list_row_playlist, selectedTracks);
        playList.setAdapter(playListAdapter);
        getTopTracks();
    }

    private void getTopTracks(){
        topTracksRequest = new ServiceRequest(getBaseContext(), listener);
        for(String id : relatedArtists){
            topTracksRequest.makeGetArtistsTopTracksRequest(Request.getTopTracks, id);
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
                if(t.getArtists() != null && t.getArtists().size() > 0) {
                    t.setArtist(t.getArtists().get(0).getName());
                }
                if(selectedTracks == null){
                    selectedTracks = new ArrayList<Track>();
                }

                if(selectedTracks.size() < 20){
                    selectedTracks.add(t);
                    if(selectedTracks.size()*5 <= 100) {
                        percentage.setText("%" + selectedTracks.size() * 5 + " completed");
                    }
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
            progress.setVisibility(View.GONE);
            saved = true;
            Utils.showFadeOutDialog(parent, CreatePlaylistActivity.this, getString(R.string.playlist_saved), getString(R.string.succesfully), R.drawable.tick);
            buttonSave.setEnabled(true);
        }
    }

    @Override
    public void onFailure() {
        progress.setVisibility(View.GONE);
        if(progress != null){
            progress.setVisibility(View.GONE);
        }
        buttonSave.setEnabled(true);
        Utils.showFadeOutDialog(parent, CreatePlaylistActivity.this, getString(R.string.an_error_occured), getString(R.string.please_try_again), R.drawable.error);
    }




    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.buttonNew){
            finish();
        }else if(v.getId() == R.id.buttonSave){
            //TODO: save playlist on spotify
            //First get user's id:
            buttonSave.setEnabled(false);
            progress.setVisibility(View.VISIBLE);
            ServiceRequest request = new ServiceRequest(this, listener);
            request.makeGetUserProfileRequest(Request.getUserProfile);
            try {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Save")
                        .build());
            }catch (Exception ignored){}

        }else if(v.getId() == R.id.buttonOpenInSpotify){
            try {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("OpenInSpotify")
                        .build());
            }catch (Exception ignored){}
            if(saved) {
                try {
                    Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
                    intent.setData(Uri.parse(
                            savedPlaylistResponse.getNameValuePairs().getUri()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }catch (Exception e){
                    Utils.showFadeOutDialog(parent, CreatePlaylistActivity.this, getString(R.string.spotify_not), getString(R.string.installed), R.drawable.error);
                }
            }else {
                Utils.showFadeOutDialog(parent, CreatePlaylistActivity.this, getString(R.string.you_need_to), getString(R.string.save_playlist_first), R.drawable.error);
            }
        }
    }

    @Override
    public void onNoConnection() {
        buttonSave.setEnabled(true);
        progress.setVisibility(View.GONE);
        try{
            Utils.showFadeOutDialog(parent, CreatePlaylistActivity.this, getString(R.string.please_check_your), getString(R.string.internet_connection), R.drawable.error);
        }catch (Exception ignored){}
    }

    @Override
    public void onBackPressed() {
        if(!backButtonCanBeUsed){

        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void reportGoogleAnaytics(){
        try {
            mTracker.setScreenName("PlaylistPage");
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }catch (Exception ignored){}
    }
}