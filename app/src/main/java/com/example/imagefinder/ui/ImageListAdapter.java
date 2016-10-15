package com.example.imagefinder.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.imagefinder.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {
    private List<String> imageUriList = new ArrayList<>();
    private final Picasso picasso;

    public ImageListAdapter(Context context) {
        this.picasso = Picasso.with(context);
    }

    public void setImageUriList(List<String> imageUriList) {
        this.imageUriList = imageUriList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String imageUri = imageUriList.get(position);
        holder.title.setText(imageUri);
        picasso.load(imageUri).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return imageUriList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final ImageView image;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            image = (ImageView) view.findViewById(R.id.image);
        }
    }
}
