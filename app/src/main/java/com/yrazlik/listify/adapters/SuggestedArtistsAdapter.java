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

import java.util.ArrayList;

/**
 * Created by yrazlik on 9/1/15.
 */
public class SuggestedArtistsAdapter extends ArrayAdapter<Artist>{

    private Context mContext;
    private ArrayList<Artist> artists;
    private int layoutId;

    public SuggestedArtistsAdapter(Context context, int resource, ArrayList<Artist> objects) {
        super(context, resource, objects);
        mContext = context;
        artists = objects;
        this.layoutId = resource;
    }

    public ArrayList<Artist> getArtists() {
        return artists;
    }

    public void setArtists(ArrayList<Artist> artists) {
        this.artists = artists;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutId, parent, false);
            holder = new ViewHolder();
            holder.suggestedArtistName = (TextView) convertView.findViewById(R.id.suggestedArtistName);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        Artist artist = getItem(position);
        if(artist != null){
            holder.suggestedArtistName.setText(artist.getName());
        }

        return convertView;
    }

    @Override
    public int getCount() {
        if(artists == null){
            return 0;
        }
        return artists.size();
    }

    static class ViewHolder{
        public TextView suggestedArtistName;
    }

}
