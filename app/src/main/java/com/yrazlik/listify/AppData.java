package com.yrazlik.listify;

import android.content.Context;

import com.yrazlik.listify.data.Track;

import java.util.ArrayList;

/**
 * Created by SUUSER on 01.09.2015.
 */
public class AppData {

    private static String userId;

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        AppData.userId = userId;
    }
}
