package com.yrazlik.listify.connection;

/**
 * Created by SUUSER on 31.08.2015.
 */
public interface ResponseListener {

    public void onSuccess(int requestId, Object response);
    public void onFailure();
}
