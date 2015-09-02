package com.yrazlik.listify.connection.response;

/**
 * Created by SUUSER on 02.09.2015.
 */

import com.yrazlik.listify.data.NameValuePairs;
import com.yrazlik.listify.data.Track;

import java.util.ArrayList;


import com.yrazlik.listify.data.Track;

import java.util.ArrayList;

/**
 * Created by yrazlik on 9/2/15.
 */
public class CreatePlaylistResponse {

   private NameValuePairs nameValuePairs;

    public NameValuePairs getNameValuePairs() {
        return nameValuePairs;
    }

    public void setNameValuePairs(NameValuePairs nameValuePairs) {
        this.nameValuePairs = nameValuePairs;
    }
}
