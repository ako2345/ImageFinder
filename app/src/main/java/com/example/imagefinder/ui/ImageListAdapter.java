package com.example.imagefinder.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.imagefinder.R;
import com.example.imagefinder.mvp.model.ImageInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for the list of search results.
 */
class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {
    List<ImageInfo> imageInfoList = new ArrayList<>();
    final OnItemClickListener listener;

    ImageListAdapter(OnItemClickListener listener) {
        this.listener = listener;
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
        // get image info
        final ImageInfo imageInfo = imageInfoList.get(position);

        // host page URI
        final String pageUri = imageInfo.contextLink;
        holder.pageUri.setText(pageUri);

        // image dimensions
        final String dimensions = String.format(Locale.getDefault(), "%d x %d", imageInfo.width, imageInfo.height);
        holder.dimensions.setText(dimensions);

        // image view
        holder.image.getLayoutParams().width = imageInfo.thumbnailWidth;
        holder.image.getLayoutParams().height = imageInfo.thumbnailHeight;
        final String imageUri = imageInfo.thumbnailLink;
        Picasso.with(holder.image.getContext())
                .load(imageUri)
                .placeholder(R.drawable.ic_progress_animation)
                .into(holder.image);

        // associated images
        holder.associatedImages.removeAllViews();
        if (imageInfo.associatedImagesList != null && !imageInfo.associatedImagesList.isEmpty()) {
            for (String associatedImageUrl : imageInfo.associatedImagesList) {
                ImageView imageView = new ImageView(holder.associatedImages.getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 0, 16);
                imageView.setLayoutParams(lp);
                Picasso.with(holder.associatedImages.getContext())
                        .load(associatedImageUrl)
                        .placeholder(R.drawable.ic_progress_animation)
                        .into(imageView);
                holder.associatedImages.addView(imageView);
            }
        }
    }

    @Override
    public int getItemCount() {
        return imageInfoList.size();
    }

    /**
     * View holder.
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView pageUri;
        final TextView dimensions;
        final ImageView image;
        final ViewGroup associatedImages;

        ViewHolder(View view) {
            super(view);
            pageUri = (TextView) view.findViewById(R.id.pageLink);
            dimensions = (TextView) view.findViewById(R.id.dimensions);
            image = (ImageView) view.findViewById(R.id.image);
            associatedImages = (ViewGroup) view.findViewById(R.id.associatedImages);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClicked(getAdapterPosition());
        }
    }

    interface OnItemClickListener {
        void onItemClicked(int position);
    }
}
