package com.ditenun.appditenun.function.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ditenun.appditenun.R;
import com.ditenun.appditenun.dependency.models.Faq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by TROJAN-016 on 2/12/2019.
 */

public class ExpandableRecyclerViewAdapter extends RecyclerView.Adapter<ExpandableRecyclerViewAdapter.ViewHolder> {

    ArrayList<String> nameList = new ArrayList<String>();
    ArrayList<ArrayList> itemNameList = new ArrayList<ArrayList>();
    List<Integer> counter = new ArrayList<Integer>();
    Context context;

    public ExpandableRecyclerViewAdapter(Context context,
                                         ArrayList<String> nameList,
                                         ArrayList<ArrayList> itemNameList) {
        this.nameList = nameList;
        this.itemNameList = itemNameList;
        this.context = context;

        Log.d("namelist", nameList.toString());

        for (int i = 0; i < nameList.size(); i++) {
            counter.add(0);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageButton dropBtn;
        RecyclerView cardRecyclerView;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.categoryTitle);
            dropBtn = itemView.findViewById(R.id.categoryExpandBtn);
            cardRecyclerView = itemView.findViewById(R.id.innerRecyclerView);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_collapseview, parent, false);

        ExpandableRecyclerViewAdapter.ViewHolder vh = new ExpandableRecyclerViewAdapter.ViewHolder(v);

        return vh;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.name.setText((CharSequence) nameList.get(position));

        InnerRecyclerViewAdapter itemInnerRecyclerView = new InnerRecyclerViewAdapter(itemNameList.get(position));

        holder.cardRecyclerView.setLayoutManager(new GridLayoutManager(context, 1));


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (counter.get(position) % 2 == 0) {
                    holder.cardRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    holder.cardRecyclerView.setVisibility(View.GONE);
                }

                counter.set(position, counter.get(position) + 1);

            }
        });
        holder.cardRecyclerView.setAdapter(itemInnerRecyclerView);

    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

}
