package com.infinity.dev.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomAdapter extends BaseAdapter{

    List<MovieContract> list;
    Context context;
    LayoutInflater inflater;

    public CustomAdapter(Context context, List<MovieContract> list){
        this.context = context;
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = inflater.inflate(R.layout.movies_grid_item, parent,false);

        TextView name = (TextView) convertView.findViewById(R.id.title);
        name.setText(list.get(position).getOriginal_title());
        ImageView poster = (ImageView) convertView.findViewById(R.id.moviePoster);
        String BASE_URL = "http://image.tmdb.org/t/p/";
        String SIZE = "w185";
        Picasso.with(context).load(BASE_URL + SIZE + list.get(position).getPoster_path()).into(poster);
        return convertView;
    }
}