package com.ditenun.appditenun.function.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.ditenun.appditenun.R;

public class DuplicateView extends GridLayout {

    final static int COLUMN_COUNT = 5;

    private Bitmap bitmap;
    private float bitmapAspect;

    private int cellWidth;
    private int cellHeight;

    private int columnCount = COLUMN_COUNT;
    private int rowCount;

    private ImageView cells[][];
    private int visibleRow = 0;
    private int visibleColumn = 0;

    public DuplicateView(Context context) {
        super(context);
    }

    public DuplicateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DuplicateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImageBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.bitmapAspect = bitmap.getWidth() / (float) bitmap.getHeight();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InitCells();
            }
        }, 500);
    }

    private void InitCells() {
        int width = getWidth();
        int height = getHeight();

        cellWidth = width / COLUMN_COUNT;
        cellHeight = Math.round(cellWidth / bitmapAspect);

        rowCount = height / cellHeight;

        ClearCells();
        GenerateCells();
    }

    private void ClearCells() {
        cells = null;
        removeAllViews();
    }

    @SuppressLint("ResourceAsColor")
    private void GenerateCells() {
        cells = new ImageView[rowCount][columnCount];

        for (int r = 0; r < rowCount; r++) {
            GridLayout.Spec rowSpec = spec(r);
            for (int c = 0; c < columnCount; c++) {
                GridLayout.Spec colSpec = spec(c);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, colSpec);
                params.width = cellWidth;
                params.height = cellHeight;
                params.setMargins(0, 0, 0, 0);
                ImageView cell = CreateCell(r, c);
                cell.setBackgroundColor(R.color.gray);
                cell.setVisibility(View.INVISIBLE);
                cells[InverseRowIndex(r)][c] = cell;
                addView(cell, params);
            }
        }

        cells[0][0].setVisibility(View.VISIBLE);
    }

    private int InverseRowIndex(int r) {
        return rowCount - 1 - r;
    }

    private ImageView CreateCell(int row, int column) {
        ImageView imageView = new ImageView(getContext());
        imageView.setImageBitmap(bitmap);

        return imageView;
    }

    public void IncreaseRow() {
        if (visibleRow < rowCount - 1) {
            visibleRow++;
            RepaintCells();
        }
    }

    public void DecreaseRow() {
        if (visibleRow > 0) {
            visibleRow--;
            RepaintCells();
        }
    }

    public void IncreaseColumn() {
        if (visibleColumn < columnCount - 1) {
            visibleColumn++;
            RepaintCells();
        }
    }

    public void DecreaseColumn() {
        if (visibleColumn > 0) {
            visibleColumn--;
            RepaintCells();
        }
    }

    private void RepaintCells() {
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {

                if (r <= visibleRow && c <= visibleColumn) {
                    cells[r][c].setVisibility(View.VISIBLE);
                } else {
                    cells[r][c].setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}
