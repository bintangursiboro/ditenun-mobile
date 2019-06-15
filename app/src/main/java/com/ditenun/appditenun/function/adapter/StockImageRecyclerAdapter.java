package com.ditenun.appditenun.function.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.ditenun.appditenun.R;
import com.ditenun.appditenun.dependency.models.StockImage;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class StockImageRecyclerAdapter extends RealmRecyclerViewAdapter<StockImage, ImageItemViewHolder>
        implements OnImageItemClickedListener {

    private Fragment fragment;
    private OnImageItemClickedListener listener;

    public StockImageRecyclerAdapter(Fragment fragment, @Nullable OrderedRealmCollection<StockImage> data, boolean autoUpdate) {
        super(data, autoUpdate);

        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ImageItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_image_grid_item, viewGroup, false);

        return new ImageItemViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageItemViewHolder stockImageViewHolder, int i) {
        StockImage stockImage = getData().get(i);

        stockImageViewHolder.setId(stockImage.getId());
        stockImageViewHolder.getNameText().setVisibility(View.GONE);
        Glide.with(fragment).load(stockImage.getBytes()).asBitmap().into(stockImageViewHolder.getThumnailImageView());
    }

    @Override
    public void onImageItemClicked(int id, View view) {
        if (listener != null) listener.onImageItemClicked(id, view);
    }

    public void setOnItemClickedListener(OnImageItemClickedListener listener) {
        this.listener = listener;
    }
}
