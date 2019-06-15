package com.ditenun.appditenun.function.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;

public class KristikView extends GridLayout {

    public enum SelectionMode {NONE, MULTI, MAGIC}

    private Bitmap kristikBitmap;
    private int rowCount;
    private int columnCount;

    private KristikCell cells[][];

    private SelectionMode selectionMode = SelectionMode.NONE;
    private List<KristikCell> selectedCells = new ArrayList<KristikCell>();
    private int magicSelectionId = 0;
    private OnKristikCellSelectedListener listener;

    public KristikView(@NonNull Context context) {
        this(context, null);
    }

    public KristikView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0, null);
    }

    public KristikView(@NonNull final Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, Bitmap kristikBitmap) {
        super(context, attrs, defStyleAttr);
    }

    public SelectionMode getSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(SelectionMode selectionMode) {
        this.selectionMode = selectionMode;
    }

    public void setSelectionListener(OnKristikCellSelectedListener listener) {
        this.listener = listener;
    }

    public void setImageBitmap(Bitmap bitmap) {

        this.kristikBitmap = bitmap;

        ClearCells();

        if (kristikBitmap == null) {
            this.columnCount = 0;
            this.rowCount = 0;
            this.cells = new KristikCell[0][0];
        } else {
            this.columnCount = kristikBitmap.getWidth();
            this.rowCount = kristikBitmap.getHeight();

            this.cells = new KristikCell[rowCount][columnCount];

            GenerateCells();
        }
    }

    public void deselect() {
        performDeselection();
        dispatchEvent();
    }

    private void ClearCells() {
        this.removeAllViews();
    }

    private void GenerateCells() {
        for (int r = 0; r < rowCount; r++) {
            Spec rowSpec = spec(r);
            for (int c = 0; c < columnCount; c++) {
                Spec colSpec = spec(c);
                LayoutParams params = new LayoutParams(rowSpec, colSpec);
                params.setMargins(1, 1, 1, 1);
                params.width = 20;
                params.height = 20;

                KristikCell cell = CreateCell(r, c);
                cells[r][c] = cell;
                addView(cell, params);
            }
        }
    }

    private KristikCell CreateCell(int row, int column) {
        KristikCell cell = new KristikCell(this, row, column);

        // Note :
        // Column == X And Row == Y
        int color = kristikBitmap.getPixel(column, row);

        cell.setBackground(new ColorDrawable(color));

        return cell;
    }

    public void OnCellClicked(KristikCell cell) {
        if (selectionMode == SelectionMode.MULTI) {
            performMultiSelection(cell);
            dispatchEvent();
        } else if (selectionMode == SelectionMode.MAGIC) {
            performMagicSelection(cell);
            dispatchEvent();
        }
    }

    private void dispatchEvent() {
        if (listener != null) {
            KristikCell[] cells = new KristikCell[selectedCells.size()];
            selectedCells.toArray(cells);

            listener.OnSelectionChanged(cells);
        }
    }

    private void performMultiSelection(KristikCell cell) {
        if(cell.isSelected()) {
            cell.setSelected(false);
            selectedCells.remove(cell);
        }else {
            cell.setSelected(true);
            selectedCells.add(cell);
        }
    }

    private void performMagicSelection(KristikCell cell) {

        ColorDrawable background = (ColorDrawable) cell.getBackground();

        magicSelectionId++;
        magicSelection(magicSelectionId, cell.getRow(), cell.getColumn(), background.getColor());
    }

    private void magicSelection(int selectionId, int row, int column, int color) {
        if (row < 0 || column < 0 || row >= rowCount || column >= columnCount) return;

        KristikCell cell = cells[row][column];

        if (cell.getLastSelectionId() == selectionId) return;

        cell.setLastSelectionId(selectionId);

        ColorDrawable background = (ColorDrawable) cell.getBackground();

        if (background.getColor() == color) {
            performMultiSelection(cell);

            magicSelection(selectionId, row, column + 1, color);
            magicSelection(selectionId, row, column - 1, color);

            magicSelection(selectionId, row + 1, column, color);
            magicSelection(selectionId, row - 1, column, color);

            magicSelection(selectionId, row + 1, column + 1, color);
            magicSelection(selectionId, row + 1, column - 1, color);

            magicSelection(selectionId, row - 1, column + 1, color);
            magicSelection(selectionId, row - 1, column - 1, color);
        }
    }

    private void performDeselection() {
        for (int i = 0; i < selectedCells.size(); i++) {
            selectedCells.get(i).setSelected(false);
        }

        selectedCells.clear();
    }
}
