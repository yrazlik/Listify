package com.yrazlik.listify.connection;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.yrazlik.listify.AppConstants;
import com.yrazlik.listify.AppData;
import com.yrazlik.listify.connection.response.AddTracksToPlaylistResponse;
import com.yrazlik.listify.connection.response.ArtistsResponse;
import com.yrazlik.listify.connection.response.CreatePlaylistResponse;
import com.yrazlik.listify.connection.response.RelatedArtistsResponse;
import com.yrazlik.listify.connection.response.TopTracksResponse;
import com.yrazlik.listify.connection.response.UserProfileResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SUUSER on 31.08.2015.
 */
public class ServiceRequest {

    private ServiceRequest instance;
    private Context mContext;
    private ResponseListener mListener;
    private RequestQueue topTracksRequestQueue;
    public static String TAG_TOP_TRACKS = "TAG_TOP_TRACKS";

    public void cancelAllTopTracksRequests(){
        topTracksRequestQueue.cancelAll(TAG_TOP_TRACKS);
    }

    public ServiceRequest(Context context, ResponseListener listener) {
        this.mContext = context;
        this.mListener = listener;
        topTracksRequestQueue = Volley.newRequestQueue(mContext);
    }

    public void makeSuggestArtistNameRequest(final int requestId, String artistName) {
        RequestQueue queue = Volley.newRequestQueue(mContext);


        artistName = artistName.trim().replaceAll(" +", " ");
        String [] values = artistName.split(" ");
        ArrayList<String> nameParts = new ArrayList<String>();
        if(values != null && values.length > 0){
            for(String s : values){
                String value = null;
                try {
                    value = URLEncoder.encode(s, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if(value != null) {
                    nameParts.add(value);
                }
            }
        }

        artistName = "";
        if(nameParts.size() > 0){
            for(String s : nameParts){
                artistName = artistName + "+" + s;
            }
            artistName = artistName.substring(1);
        }


        String url = AppConstants.API_BASE_URL + AppConstants.SEARCH_BASE_URL + "?q=" + artistName + "&type=artist";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            Gson gson = new Gson();
                            ArtistsResponse artistsResponse = gson.fromJson(response, ArtistsResponse.class);
                            mListener.onSuccess(requestId, artistsResponse);
                        }catch (Exception e){
                            //TODO: implement parse error case
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NoConnectionError){
                    mListener.onNoConnection();
                }else {
                    mListener.onFailure();
                }
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void makeGetRelatedArtistsRequest(final int requestId, String artistId) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = AppConstants.API_BASE_URL + AppConstants.ARTISTS_BASE_URL + artistId + "/related-artists";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            Gson gson = new Gson();
                            RelatedArtistsResponse artistsResponse = gson.fromJson(response, RelatedArtistsResponse.class);
                            mListener.onSuccess(requestId, artistsResponse);
                        }catch (Exception e){
                            //TODO: implement parse error case
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NoConnectionError){
                    mListener.onNoConnection();
                }else {
                    mListener.onFailure();
                }
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void makeGetArtistsTopTracksRequest(final int requestId, String artistId) {

        String url = AppConstants.API_BASE_URL + AppConstants.ARTISTS_BASE_URL + artistId + "/top-tracks?country=US";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            Gson gson = new Gson();
                            TopTracksResponse artistsResponse = gson.fromJson(response, TopTracksResponse.class);
                            mListener.onSuccess(requestId, artistsResponse);
                        }catch (Exception e){
                            //TODO: implement parse error case
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NoConnectionError){
                    mListener.onNoConnection();
                }else {
                    mListener.onFailure();
                }
            }
        });
        stringRequest.setTag(TAG_TOP_TRACKS);
        // Add the request to the RequestQueue.
        topTracksRequestQueue.add(stringRequest);
    }

    public void makeGetUserProfileRequest(final int requestId) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = AppConstants.API_BASE_URL + AppConstants.USER_PROFILE_BASE_URL;

        // Request a string response from the provided URL.

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                try{
                    Gson gson = new Gson();
                    UserProfileResponse userProfileResponse = gson.fromJson(response, UserProfileResponse.class);
                    mListener.onSuccess(requestId, userProfileResponse);
                }catch (Exception e){
                    //TODO: implement parse error case
                }
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error instanceof NoConnectionError){
                            mListener.onNoConnection();
                        }else {
                            mListener.onFailure();
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Authorization", "Bearer " + AppConstants.ACCESS_TOKEN);

                return params;
            }
        };


        queue.add(request);
    }


    public void makeCreatePlaylistRequest(final int requestId, final String jsonData) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = AppConstants.API_BASE_URL + AppConstants.USERS_BASE_URL + AppData.getUserId() + "/" + AppConstants.PLAYLISTS_BASE_URL;
        JSONObject object = null;
        try {
            object = new JSONObject(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Request a string response from the provided URL.


        JsonObjectRequest request = new JsonObjectRequest(url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                String res  = gson.toJson(response);
                Gson gson2 = new Gson();
                CreatePlaylistResponse createPlaylistResponse = gson2.fromJson(res, CreatePlaylistResponse.class);
                mListener.onSuccess(requestId, createPlaylistResponse);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NoConnectionError){
                    mListener.onNoConnection();
                }else {
                    mListener.onFailure();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Authorization", "Bearer " + AppConstants.ACCESS_TOKEN);
                params.put("Content-Type", "application/json");
                return params;
            }
        };


        queue.add(request);
    }

    public void makeAddTracksToPlaylistRequest(final int requestId, String playlistId, String jsonData) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = AppConstants.API_BASE_URL + AppConstants.USERS_BASE_URL + AppData.getUserId() + "/" + AppConstants.PLAYLISTS_BASE_URL + playlistId + "/tracks";

        // Request a string response from the provided URL.

        JSONObject object = null;
        try {
            object = new JSONObject(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Request a string response from the provided URL.


        JsonObjectRequest request = new JsonObjectRequest(url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                String res  = gson.toJson(response);
                Gson gson2 = new Gson();
                AddTracksToPlaylistResponse addTracksToPlaylistResponse = gson2.fromJson(res, AddTracksToPlaylistResponse.class);
                mListener.onSuccess(requestId, addTracksToPlaylistResponse);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NoConnectionError){
                    mListener.onNoConnection();
                }else {
                    mListener.onFailure();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Authorization", "Bearer " + AppConstants.ACCESS_TOKEN);
                return params;
            }
        };



        queue.add(request);
    }
}
