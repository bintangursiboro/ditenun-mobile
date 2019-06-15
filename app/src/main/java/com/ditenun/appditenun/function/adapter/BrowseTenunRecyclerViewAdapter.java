package com.ditenun.appditenun.function.adapter;

import androidx.cardview.widget.CardView;
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
import com.ditenun.appditenun.dependency.models.Tenun;
import com.ditenun.appditenun.dependency.modules.APIModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Using this file must on behalf of Institut Teknologi Del & Piksel
 */

public class BrowseTenunRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TENUN = 55;
    public static final int PROGRESS_BAR = 66;
    public static final int HEADER_TENUN = 33;
    private List<Tenun> listTenun;
    private List<Object> listObject = new ArrayList<>();
    private OnClickItemTenunListener onClickItemTenunListener;

    public BrowseTenunRecyclerViewAdapter(List<Object> listObject, OnClickItemTenunListener onClickItemTenunListener) {
        this.listObject = listObject;
        this.onClickItemTenunListener = onClickItemTenunListener;
    }

    public void setData(List<Object> listObject) {
        this.listObject = listObject;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (listObject.get(position) instanceof String) {
            return HEADER_TENUN;
        } else if (listObject.get(position) == null) {
            return PROGRESS_BAR;
        } else
            return TENUN;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case TENUN:
                View v1 = layoutInflater.inflate(R.layout.layout_single_item_tenun, parent, false);
                viewHolder = new ViewHolderTenun(v1);
                break;
            case PROGRESS_BAR:
                View progressBarView = layoutInflater.inflate(R.layout.progress_bar, parent, false);
                viewHolder = new ViewHolderProgressBar(progressBarView);
                break;
            case HEADER_TENUN:
                View viewHeader = layoutInflater.inflate(R.layout.layout_header, parent, false);
                viewHolder = new ViewHolderHeader(viewHeader);
                break;
            default:
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Tenun tenunTemp;

        switch (holder.getItemViewType()) {
            case TENUN:
                tenunTemp = (Tenun) listObject.get(position);

                ViewHolderTenun viewHolderTenun = (ViewHolderTenun) holder;
                viewHolderTenun.textViewId.setText(tenunTemp.getNamaTenun());
                String imgUrl = APIModule.ENDPOINT + tenunTemp.getImageSrc();
                Glide glide = null;
                glide.with(viewHolderTenun.imageView.getContext())
                        .load(imgUrl)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .override(400,200)
                        .centerCrop()
                        .crossFade()
                        .into(viewHolderTenun.imageView);
                viewHolderTenun.cardView.setOnClickListener(v -> onClickItemTenunListener.OnClickItemTenun(tenunTemp.getId(), viewHolderTenun.imageView));
                break;
            case PROGRESS_BAR:
                ViewHolderProgressBar viewHolderProgressBar = (ViewHolderProgressBar) holder;
                viewHolderProgressBar.progressBar.setIndeterminate(true);

                if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
                    StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
                    layoutParams.setFullSpan(true);
                }
                break;
            case HEADER_TENUN:
                String header = (String) listObject.get(position);
                ViewHolderHeader viewHolderHeader = (ViewHolderHeader) holder;
                viewHolderHeader.tv_header.setText(header);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return listObject.size();
    }

    public void addFooter() {
        listTenun.add(null);
        notifyItemInserted(listTenun.size() - 1);
    }

    public void removeFooter() {
        int indexFooter = listTenun.indexOf(null);
        listTenun.remove(indexFooter);
        notifyItemRemoved(indexFooter);
    }

    public void addItems(List<Tenun> newList) {
        int startSize = listTenun.size();
        listTenun.addAll(newList);
        int endSize = listTenun.size();

        notifyItemRangeInserted(startSize, endSize);
    }

    public void clear() {
        listTenun.clear();
        notifyDataSetChanged();
    }

    public interface OnClickItemTenunListener {
        void OnClickItemTenun(String idTenun, View imageThumb);
    }

    class ViewHolderTenun extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewId;
        CardView cardView;

        public ViewHolderTenun(View itemView) {
            super(itemView);
            textViewId = (TextView) itemView.findViewById(R.id.textId);
            imageView = (ImageView) itemView.findViewById(R.id.thumbTenun);
            cardView = (CardView) itemView.findViewById(R.id.container_for_item);
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
        TextView tv_header;

        public ViewHolderHeader(View itemView) {
            super(itemView);

            tv_header = (TextView) itemView.findViewById(R.id.titleHeader);
        }
    }
}
