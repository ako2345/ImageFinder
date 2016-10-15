package com.example.imagefinder.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.imagefinder.R;
import com.example.imagefinder.mvp.model.ImageInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {
    private List<ImageInfo> imageInfoList = new ArrayList<>();
    private final Picasso picasso;

    ImageListAdapter(Context context) {
        this.picasso = Picasso.with(context);
    }

    void setImageInfoList(List<ImageInfo> imageInfoList) {
        this.imageInfoList = imageInfoList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageInfo imageInfo = imageInfoList.get(position);
        String imageUri = imageInfo.imageUri;
        String pageUri = imageInfo.pageUri;
        holder.title.setText(pageUri);
        picasso.load(imageUri).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return imageInfoList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final ImageView image;

        ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            image = (ImageView) view.findViewById(R.id.image);
        }
    }
}
