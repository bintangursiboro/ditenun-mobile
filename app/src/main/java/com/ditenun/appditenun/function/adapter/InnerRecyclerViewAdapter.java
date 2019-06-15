package com.ditenun.appditenun.function.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ditenun.appditenun.R;
import com.ditenun.appditenun.dependency.models.Faq;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by TROJAN-016 on 2/12/2019.
 */

public class InnerRecyclerViewAdapter extends RecyclerView.Adapter<InnerRecyclerViewAdapter.ViewHolder> {
    public ArrayList<String> nameList = new ArrayList<String>();

    public InnerRecyclerViewAdapter(ArrayList nameList) {

        this.nameList = nameList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.itemTextView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_expand_item_view, parent, false);


        InnerRecyclerViewAdapter.ViewHolder vh = new InnerRecyclerViewAdapter.ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.name.setText(nameList.get(position));

    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }


}