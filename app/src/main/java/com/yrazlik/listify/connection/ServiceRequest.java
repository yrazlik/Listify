package com.yrazlik.listify.connection;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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

    public void makeGetArtistIdRequest() {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = "https://api.spotify.com/v1/artists/43ZHCT0cAZBISjO8DG9PnE/related-artists";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       String res = response;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String err = "err";
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
