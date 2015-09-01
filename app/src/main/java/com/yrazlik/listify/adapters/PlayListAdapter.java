package com.yrazlik.listify.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yrazlik.listify.R;
import com.yrazlik.listify.data.Artist;
import com.yrazlik.listify.data.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SUUSER on 01.09.2015.
 */
public class PlayListAdapter extends ArrayAdapter<Track>{

    private Context mContext;
    private ArrayList<Track> tracks;
    private int layoutId;

    public PlayListAdapter(Context context, int resource, ArrayList<Track> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.layoutId = resource;
        this.tracks = objects;
    }

    @Override
    public int getCount() {
        if(tracks == null){
            return 0;
        }
        return tracks.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutId, parent, false);
            holder = new ViewHolder();
            holder.trackName = (TextView) convertView.findViewById(R.id.trackName);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        Track track = getItem(position);
        if(track != null){
            holder.trackName.setText(track.getName());
        }

        return convertView;
    }

    static class ViewHolder{
        public TextView trackName;
    }


}
