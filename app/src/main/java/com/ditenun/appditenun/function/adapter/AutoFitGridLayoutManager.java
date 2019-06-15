package com.ditenun.appditenun.function.adapter;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;


/**
 * Using this file must on behalf of Institut Teknologi Del & Piksel
 */

public class AutoFitGridLayoutManager extends GridLayoutManager {
    private final int DEFAULT_WIDTH_DP = 256;

    private int mColumnWidth;
    private boolean isColumnChange;

    public AutoFitGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public AutoFitGridLayoutManager(Context context, int columnWidth) {
        /* Initially set spanCount to 1, will be changed automatically later. */

        super(context, 1);
        setColumnWidth(measureWidth(context, columnWidth));
    }

    public AutoFitGridLayoutManager(Context context, int columnWidth, int orientation, boolean reverseLayout) {
        /* Initially set spanCount to 1, will be changed automatically later. */

        super(context, 1, orientation, reverseLayout);
        setColumnWidth(measureWidth(context, columnWidth));
    }

    private int measureWidth(Context context, int columnWidth) {
        if (columnWidth <= 0) {
            /* Set default columnWidth value It is better to move this constant
            to static constant on top, but we need context to convert it to dp, so can't really
            do so. */
            columnWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_WIDTH_DP,
                    context.getResources().getDisplayMetrics());
        }
        return columnWidth;
    }

    public void setColumnWidth(int newColumnWidth) {
        if (newColumnWidth > 0 && newColumnWidth != mColumnWidth) {
            mColumnWidth = newColumnWidth;
            isColumnChange = true;
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        int width = getWidth();
        int height = getHeight();

        if (isColumnChange && mColumnWidth > 0 && width > 0 && height > 0) {
            int totalSpace;
            if (getOrientation() == VERTICAL) {
                totalSpace = width - getPaddingRight() - getPaddingLeft();
            } else {
                totalSpace = height - getPaddingTop() - getPaddingBottom();
            }
            int spanCount = Math.max(1, totalSpace / mColumnWidth);
            setSpanCount(spanCount);
            isColumnChange = false;
        }

        super.onLayoutChildren(recycler, state);
    }
}
