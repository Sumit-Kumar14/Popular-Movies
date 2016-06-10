package com.infinity.dev.popularmovies;

/**
 * Created by sumitkumar on 3/6/16.
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CastingAdapter extends RecyclerView.Adapter<CastingAdapter.ViewHolder>{

    Context context;
    List<CastingContract.Cast> list;
    LayoutInflater inflater;

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private ImageView poster;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            poster = (ImageView) itemView.findViewById(R.id.moviePoster);
        }
    }

    public CastingAdapter(Context context, List<CastingContract.Cast> list){
        this.context = context;
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.display_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(list.get(position).getName());
        Picasso.with(context).load("http://image.tmdb.org/t/p/w320" + list.get(position).getProfile_path())
                .error(R.drawable.notfound)
                .placeholder(R.drawable.movie)
                .into(holder.poster);
    }
}