package com.ditenun.appditenun.function.adapter;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ditenun.appditenun.R;
import com.ditenun.appditenun.R2;
import com.ditenun.appditenun.dependency.models.MotifTenun;
import com.ditenun.appditenun.dependency.modules.APIModule;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Using this file must on behalf of Institut Teknologi Del & Piksel
 */

public class BrowseMotifRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int MOTIF = 55;
    public static final int PROGRESS_BAR = 66;
    public static final int HEADER_MOTIF = 33;
    private List<MotifTenun> listMotif;
    private List<Object> listObject = new ArrayList<>();
    private OnClickItemMotifListener onClickItemMotifListener;
    private OnIdShownListener onIdShownListener;

    public BrowseMotifRecyclerViewAdapter(List<Object> listObject, OnClickItemMotifListener onClickItemMotifListener, OnIdShownListener onIdShownListener) {
        this.listObject = listObject;
        this.onClickItemMotifListener = onClickItemMotifListener;
        this.onIdShownListener = onIdShownListener;
    }

    public void setData(List<Object> listObject) {
        this.listObject = listObject;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (listObject.get(position) instanceof String) {
            return HEADER_MOTIF;
        } else if (listObject.get(position) instanceof MotifTenun) {
            return MOTIF;
        } else
            return PROGRESS_BAR;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case MOTIF:
                View v1 = layoutInflater.inflate(R.layout.layout_image_grid_item, parent, false);
                viewHolder = new BrowseMotifRecyclerViewAdapter.ViewHolderMotif(v1);
                break;
            case PROGRESS_BAR:
                View progressBarView = layoutInflater.inflate(R.layout.progress_bar, parent, false);
                viewHolder = new BrowseMotifRecyclerViewAdapter.ViewHolderProgressBar(progressBarView);
                break;
            case HEADER_MOTIF:
                View viewHeader = layoutInflater.inflate(R.layout.layout_header_list_motif, parent, false);
                viewHolder = new BrowseMotifRecyclerViewAdapter.ViewHolderHeader(viewHeader);
                break;
            default:
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MotifTenun motifTemp;

        switch (holder.getItemViewType()) {
            case MOTIF:
                motifTemp = (MotifTenun) listObject.get(position);

                BrowseMotifRecyclerViewAdapter.ViewHolderMotif viewHolderMotif = (BrowseMotifRecyclerViewAdapter.ViewHolderMotif) holder;
                String imgUrl = APIModule.ENDPOINT + motifTemp.getImageMotif();
                Glide glide = null;
                glide.with(viewHolderMotif.imageView.getContext())
                        .load(imgUrl)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .override(256, 250)
                        .centerCrop()
                        .fitCenter()
                        .crossFade()
                        .into(viewHolderMotif.imageView);

                viewHolderMotif.imageView.setOnClickListener(v -> onClickItemMotifListener.OnClickItemMotif(motifTemp.getId(), viewHolderMotif.imageView));
                break;
            case PROGRESS_BAR:
                BrowseMotifRecyclerViewAdapter.ViewHolderProgressBar viewHolderProgressBar = (BrowseMotifRecyclerViewAdapter.ViewHolderProgressBar) holder;
                viewHolderProgressBar.progressBar.setIndeterminate(true);

                if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
                    StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
                    layoutParams.setFullSpan(true);
                }
                break;
            case HEADER_MOTIF:
                String headerID = (String) listObject.get(position);
                BrowseMotifRecyclerViewAdapter.ViewHolderHeader viewHolderHeader = (BrowseMotifRecyclerViewAdapter.ViewHolderHeader) holder;
                viewHolderHeader.tv_header.setText(onIdShownListener.onIdLoaded(headerID));
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        if(listObject.size()<1){
            return 0;
        }else{
            return listObject.size();
        }

    }

    public void addFooter() {
        listMotif.add(null);
        notifyItemInserted(listMotif.size() - 1);
    }

    public void removeFooter() {
        int indexFooter = listMotif.indexOf(null);
        listMotif.remove(indexFooter);
        notifyItemRemoved(indexFooter);
    }

    public void addItems(List<MotifTenun> newList) {
        int startSize = listMotif.size();
        listMotif.addAll(newList);
        int endSize = listMotif.size();

        notifyItemRangeInserted(startSize, endSize);
    }

    public void clear() {
        listMotif.clear();
        notifyDataSetChanged();
    }

    public interface OnClickItemMotifListener {
        void OnClickItemMotif(String idMotif, View imageThumb);
    }

    public interface OnIdShownListener {
        String onIdLoaded(String id);
    }

    class ViewHolderMotif extends RecyclerView.ViewHolder {
        @BindView(R.id.thumbnail_image_view)
        ImageView imageView;

        public ViewHolderMotif(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class ViewHolderProgressBar extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public ViewHolderProgressBar(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }

    class ViewHolderHeader extends RecyclerView.ViewHolder {
        @BindView(R2.id.titleHeader)
        TextView tv_header;

        public ViewHolderHeader(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
