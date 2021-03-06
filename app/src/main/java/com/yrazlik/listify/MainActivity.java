package com.yrazlik.listify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;


public class MainActivity extends Activity implements
        PlayerNotificationCallback, ConnectionStateCallback, View.OnClickListener {

    private static final int REQUEST_CODE = 1337;
    private Button buttonStart;
    private RelativeLayout parent;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        initUI();
        reportGoogleAnaytics();
    }

    private void initUI(){
        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(this);
        parent = (RelativeLayout)findViewById(R.id.parent);
    }

    private void startLoginActivity(){
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(AppConstants.CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                AppConstants.REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "playlist-read", "playlist-read-private", "streaming", "playlist-modify-private", "playlist-modify-public"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                AppConstants.ACCESS_TOKEN = response.getAccessToken();
                Intent i = new Intent(this, ListifyActivity.class);
                startActivity(i);
                finish();
            } else{
                Toast.makeText(this, getString(R.string.errorAuthenticate), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d("MainActivity", "Playback event received: " + eventType.name());
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails) {
        Log.d("MainActivity", "Playback error received: " + errorType.name());
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.buttonStart){
           startLoginActivity();
           //Utils.showFadeOutDialog(parent, getApplicationContext(), getString(R.string.playlist_created), getString(R.string.succesfully), R.drawable.tick);
        }
    }

    private void reportGoogleAnaytics(){
        try {
            ListifyApplication application = (ListifyApplication) getApplication();
            mTracker = application.getDefaultTracker();
            mTracker.setScreenName("MainScreen");
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }catch (Exception ignored){}
    }

    @Override
    protected void onDestroy() {
        if(AppData.mPlayer != null){
            AppData.mPlayer.pause();
        }
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }


}
