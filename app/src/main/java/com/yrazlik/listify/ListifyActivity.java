package com.yrazlik.listify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.yrazlik.listify.adapters.SuggestedArtistsAdapter;
import com.yrazlik.listify.connection.ResponseListener;
import com.yrazlik.listify.connection.ServiceRequest;
import com.yrazlik.listify.connection.request.Request;
import com.yrazlik.listify.connection.response.ArtistsResponse;
import com.yrazlik.listify.connection.response.RelatedArtistsResponse;
import com.yrazlik.listify.connection.response.TopTracksResponse;
import com.yrazlik.listify.data.Artist;
import com.yrazlik.listify.data.Track;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by SUUSER on 31.08.2015.
 */
public class ListifyActivity extends Activity implements View.OnClickListener, ResponseListener{

    private EditText edittextArtist;
    private Button buttonCreateListify;
    final ResponseListener listener = this;
    private ArrayList<Artist> suggestedArtists;
    private ListView suggestedArtistLV;
    private RelativeLayout suggestedArtistRL, parent;
    private LinearLayout images;
    private SuggestedArtistsAdapter suggestedArtistsAdapter;
    private boolean listItemClicked = false;
    private Artist searchedArtist;
    private String searchedArtistName;
    private boolean openPlayListDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listify);
        initUI();
    }

    private void initUI(){
        parent = (RelativeLayout)findViewById(R.id.parent);
        images = (LinearLayout)findViewById(R.id.images);
        parent.setOnClickListener(this);
        images.setOnClickListener(this);
        edittextArtist = (EditText)findViewById(R.id.edittextArtist);
        buttonCreateListify = (Button)findViewById(R.id.buttonCreateListify);
        suggestedArtistRL = (RelativeLayout)findViewById(R.id.suggestedArtistsRL);
        suggestedArtistLV = (ListView)findViewById(R.id.suggestedArtistLV);
        buttonCreateListify.setOnClickListener(this);

        suggestedArtistLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listItemClicked = true;
                Artist artist = (Artist) parent.getAdapter().getItem(position);
                if (artist != null) {
                    String artistName = artist.getName();
                    if (artistName != null) {
                        edittextArtist.setText(artistName);
                        suggestedArtistRL.setVisibility(View.GONE);
                    }
                }
            }
        });

        edittextArtist.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    suggestedArtistLV.setVisibility(View.GONE);
                }
            }
        });

        edittextArtist.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s != null && s.toString().length() >= 3){
                    ServiceRequest request = new ServiceRequest(getBaseContext(), listener);
                    request.makeSuggestArtistNameRequest(Request.getSuggestedArtists, s.toString());
                }else if(s != null && s.length() < 3){
                    suggestedArtistRL.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.buttonCreateListify){
            String artistName = edittextArtist.getText().toString();
            if(artistName.length() > 0){
                artistName = artistName.trim();
                searchedArtistName = artistName;
                ServiceRequest request = new ServiceRequest(getBaseContext(), this);
                request.makeSuggestArtistNameRequest(Request.getArtist, artistName);

            }else{
                //TODO:show error
            }
        }else if(v.getId() == R.id.parent || v.getId() == R.id.images){
            suggestedArtistRL.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSuccess(int requestId, Object response) {
        if(requestId == Request.getSuggestedArtists){
            if(suggestedArtists == null){
                suggestedArtists = new ArrayList<Artist>();
            }else{
                suggestedArtists.clear();
            }
            ArtistsResponse artistsResponse = (ArtistsResponse) response;
            ArrayList<Artist> artists = artistsResponse.getArtists().getItems();
            if(artists != null && artists.size() > 4){
                for(int i = 0; i < 4; i++){
                    suggestedArtists.add(artists.get(i));
                }
            }else if(artists != null && artists.size() <= 4){
                for(Artist a : artists){
                    suggestedArtists.add(a);
                }
            }

            if(suggestedArtists.size() > 0){
                if(!listItemClicked) {
                    suggestedArtistRL.setVisibility(View.VISIBLE);
                }
            }else{
                suggestedArtistRL.setVisibility(View.GONE);
            }
            listItemClicked = false;

            if(suggestedArtistsAdapter == null){
                suggestedArtistsAdapter = new SuggestedArtistsAdapter(this, R.layout.list_row_suggested_artists, suggestedArtists);
                suggestedArtistLV.setAdapter(suggestedArtistsAdapter);
            }else{
                suggestedArtistsAdapter.notifyDataSetChanged();
            }


            //TODO: set adapter
        }else if(requestId == Request.getArtist){
            //first we get the id of the searched artist, the we use that id to get related artists
            searchedArtist = new Artist();
            ArtistsResponse artistsResponse = (ArtistsResponse) response;
            boolean foundSomeResultsButNameNotEqualsIgnoreCase = false;
            if(artistsResponse != null && artistsResponse.getArtists() != null && artistsResponse.getArtists().getItems() != null && artistsResponse.getArtists().getItems().size() > 0){


                for(Artist a : artistsResponse.getArtists().getItems()){
                    foundSomeResultsButNameNotEqualsIgnoreCase = true;
                    String aName = a.getName();
                    if(aName != null){
                        aName = aName.trim();
                        if (aName.equalsIgnoreCase(searchedArtistName)){
                            searchedArtist = a;
                            break;
                        }
                    }
                }
            }else{
                Toast.makeText(this, getString(R.string.no_results_found), Toast.LENGTH_SHORT).show();
            }

            if(searchedArtist != null && searchedArtist.getId() != null && searchedArtist.getId().length() > 0){
                ServiceRequest request = new ServiceRequest(getBaseContext(), listener);
                request.makeGetRelatedArtistsRequest(Request.getRelatedArtists, searchedArtist.getId());
            }else if(foundSomeResultsButNameNotEqualsIgnoreCase){
                if(artistsResponse.getArtists() != null && artistsResponse.getArtists().getItems() != null && artistsResponse.getArtists().getItems().size() > 0){
                    searchedArtist = artistsResponse.getArtists().getItems().get(0);
                    ServiceRequest request = new ServiceRequest(getBaseContext(), listener);
                    if(searchedArtist != null && searchedArtist.getId() != null && searchedArtist.getId().length() > 0) {
                        request.makeGetRelatedArtistsRequest(Request.getRelatedArtists, searchedArtist.getId());
                    }else {
                        Toast.makeText(this, getString(R.string.no_results_found), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }else if(requestId == Request.getRelatedArtists){
            RelatedArtistsResponse relatedArtistsResponse = (RelatedArtistsResponse)response;
            if(relatedArtistsResponse != null){
                if(relatedArtistsResponse.getArtists() != null && relatedArtistsResponse.getArtists().size() > 0){
                    ArrayList<String> relatedArtistIds = new ArrayList<String>();
                    for(Artist a : relatedArtistsResponse.getArtists()){
                        if(a.getId() != null) {
                            relatedArtistIds.add(a.getId());
                        }
                    }
                    Intent i = new Intent(this, CreatePlaylistActivity.class);
                    i.putStringArrayListExtra(CreatePlaylistActivity.EXTRA_RELATED_ARTISTS, relatedArtistIds);
                    startActivity(i);
                }else {
                    Toast.makeText(this, getString(R.string.no_results_found), Toast.LENGTH_SHORT).show();

                }
            }else {
                Toast.makeText(this, getString(R.string.no_results_found), Toast.LENGTH_SHORT).show();

            }
        }

    }

    @Override
    public void onFailure() {
        suggestedArtistRL.setVisibility(View.GONE);
    }
}