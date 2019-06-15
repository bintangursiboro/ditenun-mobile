package com.ditenun.appditenun.function.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

public class KristikCell extends View implements View.OnClickListener {
    private KristikView kristikView;
    private int row;
    private int column;
    private boolean isSelected;
    private int lastSelectionId;
    private Paint selectionPaint;

    public KristikCell(KristikView kristikView, int row, int column) {
        super(kristikView.getContext());

        this.kristikView = kristikView;
        this.row = row;
        this.column = column;

        selectionPaint = new Paint();
        selectionPaint.setStyle(Paint.Style.STROKE);
        selectionPaint.setColor(Color.GREEN);
        selectionPaint.setStrokeWidth(3);

        setOnClickListener(this);
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        if (isSelected != selected) {
            isSelected = selected;
            invalidate();
        }
    }

    public int getLastSelectionId() {
        return lastSelectionId;
    }

    public void setLastSelectionId(int lastSelectionId) {
        this.lastSelectionId = lastSelectionId;
    }

    public int getColor() {
        return ((ColorDrawable) getBackground()).getColor();
    }

    @Override
    public void onClick(View v) {
        kristikView.OnCellClicked((KristikCell) v);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isSelected) {
            canvas.drawRect(0, 0, getWidth(), getHeight(), selectionPaint);
        }
    }
}
