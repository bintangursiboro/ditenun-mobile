package com.ditenun.appditenun.function.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ditenun.appditenun.R;

public class ImageItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ImageView thumnailImageView;
    private TextView nameText;
    private OnImageItemClickedListener listener;

    private int id;

    public ImageItemViewHolder(View itemView, OnImageItemClickedListener listener) {
        super(itemView);

        thumnailImageView = itemView.findViewById(R.id.thumbnail_image_view);
        nameText = itemView.findViewById(R.id.name_text);

        this.listener = listener;
        itemView.setOnClickListener(this);
    }

    public ImageView getThumnailImageView() {
        return thumnailImageView;
    }

    public TextView getNameText() {
        return nameText;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) listener.onImageItemClicked(id, v);
    }
}