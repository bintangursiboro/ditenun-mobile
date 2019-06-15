package com.ditenun.appditenun.function.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ditenun.appditenun.R;
import com.ditenun.appditenun.dependency.models.MotifTenun;
import com.ditenun.appditenun.dependency.modules.APIModule;
import com.ditenun.appditenun.function.activity.GenerateMotifActivity;
import com.ditenun.appditenun.function.util.IntentParams;

import java.util.List;

public class RecyclerViewAdapterMotif extends RecyclerView.Adapter<RecyclerViewAdapterMotif.MyViewHolder> {

    private Context mContext ;
    private List<MotifTenun> mData ;

    public RecyclerViewAdapterMotif(Context mContext, List<MotifTenun> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.recyclerview_motif,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        int models = position+1;
        holder.model_generate.setText("Model "+models);
        String imgUrl = APIModule.ENDPOINT + mData.get(position).getImageMotif();
        Glide glide = null;

        glide.with(holder.hasil_generate.getContext())
                .load(imgUrl)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .override(256, 250)
                .centerCrop()
                .fitCenter()
                .crossFade()
                .into(holder.hasil_generate);

        holder.hasil_generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GenerateMotifActivity.class);
                intent.putExtra(IntentParams.TENUN_IMAGE_ID, mData.get(position).getId());

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView model_generate;
        ImageView hasil_generate;
        CardView cardView ;

        public MyViewHolder(View itemView) {
            super(itemView);

            model_generate = (TextView) itemView.findViewById(R.id.model_generate);
            hasil_generate = (ImageView) itemView.findViewById(R.id.hasil_generate);
            cardView = (CardView) itemView.findViewById(R.id.cardviewulos_id);
        }
    }
}
