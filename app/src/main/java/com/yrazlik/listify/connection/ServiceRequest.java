package com.yrazlik.listify.connection;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.yrazlik.listify.AppConstants;
import com.yrazlik.listify.connection.response.ArtistsResponse;
import com.yrazlik.listify.connection.response.RelatedArtistsResponse;
import com.yrazlik.listify.connection.response.TopTracksResponse;
import com.yrazlik.listify.connection.response.UserProfileResponse;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SUUSER on 31.08.2015.
 */
public class ServiceRequest {

    private ServiceRequest instance;
    private Context mContext;
    private ResponseListener mListener;

    public ServiceRequest(Context context, ResponseListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    public void makeSuggestArtistNameRequest(final int requestId, String artistName) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        artistName = artistName.trim().replaceAll(" +", " ");
        artistName = artistName.replaceAll(" ", "+");

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
                mListener.onFailure();
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
                mListener.onFailure();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void makeGetArtistsTopTracksRequest(final int requestId, String artistId) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
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
                mListener.onFailure();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
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
                        mListener.onFailure();
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
        String url = AppConstants.API_BASE_URL + "users/yrazlik/playlists";

        // Request a string response from the provided URL.

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
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
                        mListener.onFailure();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Authorization", "Bearer " + AppConstants.ACCESS_TOKEN);
                params.put("Content-Type", "application/json");
                return params;
            }

            @Override
            public byte[] getPostBody() throws AuthFailureError {
                try {
                    return jsonData.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
            }

            @Override
            public byte[] getBody() {
                try {
                    return jsonData.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
            }
        };


        queue.add(request);
    }
}
