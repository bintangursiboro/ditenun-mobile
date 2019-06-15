package com.ditenun.appditenun.function.util;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.ditenun.appditenun.dependency.models.Pagination;

/**
 * Using this file must on behalf of Institut Teknologi Del & Piksel
 */

public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {
    RecyclerView.LayoutManager mLayoutManager;
    private int visibleThreshold = 5;
    private boolean loading;
    private Pagination pagination = new Pagination();

    public EndlessScrollListener(RecyclerView.LayoutManager layoutManager, Pagination pagination) {
        if (layoutManager instanceof LinearLayoutManager) {
            this.mLayoutManager = layoutManager;
        } else if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager newlayoutManager = (GridLayoutManager) layoutManager;
            this.mLayoutManager = layoutManager;
            visibleThreshold = visibleThreshold + newlayoutManager.getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager newLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            this.mLayoutManager = layoutManager;
            visibleThreshold = visibleThreshold + newLayoutManager.getSpanCount();
        }

        this.pagination = pagination;

        loading = !this.pagination.isExistNext();
    }

    public int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        int lastVisibleItemPosition = 0;
        int totalItemCount = mLayoutManager.getItemCount();

        if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) mLayoutManager).findLastVisibleItemPositions(null);
            lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);
        } else if (mLayoutManager instanceof LinearLayoutManager) {
            lastVisibleItemPosition = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        } else if (mLayoutManager instanceof GridLayoutManager) {
            lastVisibleItemPosition = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        }

        if (!loading && totalItemCount <= (lastVisibleItemPosition + visibleThreshold)) {
            loading = true;

            onLoadMore(pagination.getNextCursor(), pagination.getSize());
        }
    }

    public void setNextState(Pagination pagination) {
        if (pagination.isExistNext()) {
            this.pagination = pagination;
            loading = false;
        }
    }

    // Defines the process for actually loading more data based on page
    public abstract void onLoadMore(int cursor, int size);

}

