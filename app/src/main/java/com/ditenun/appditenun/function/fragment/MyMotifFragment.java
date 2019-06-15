package com.ditenun.appditenun.function.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ditenun.appditenun.R;
import com.ditenun.appditenun.dependency.App;
import com.ditenun.appditenun.dependency.models.Kristik;
import com.ditenun.appditenun.dependency.models.Motif;
import com.ditenun.appditenun.dependency.models.StockImage;
import com.ditenun.appditenun.dependency.network.TenunNetworkInterface;
import com.ditenun.appditenun.function.activity.DetailMotifActivity;
import com.ditenun.appditenun.function.activity.DetailKristikActivity;
import com.ditenun.appditenun.function.activity.EditKristikActivity;
import com.ditenun.appditenun.function.activity.GenerateMotifActivity;
import com.ditenun.appditenun.function.adapter.OnImageItemClickedListener;
import com.ditenun.appditenun.function.adapter.StockImageRecyclerAdapter;
import com.ditenun.appditenun.function.adapter.UserKristikRecyclerAdapter;
import com.ditenun.appditenun.function.adapter.UserMotifRecyclerAdapter;
import com.ditenun.appditenun.function.util.IntentParams;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.Sort;

public class MyMotifFragment extends Fragment {

    private final int GRID_SIZE = 3;

    @BindView(R.id.container)
    CoordinatorLayout rootContainer;

    @BindView(R.id.image_recycler_view)
    RecyclerView imageRecyclerView;

    @BindView(R.id.stock_image_button)
    TextView stockImageButton;

    @BindView(R.id.motif_button)
    TextView motifButton;

    @BindView(R.id.kristik_button)
    TextView kristikButton;

    @BindView(R.id.kristik_blank_button)
    TextView kristikBlankButton;

    @Inject
    TenunNetworkInterface tenunNetworkInterface;

    @Inject
    Realm realm;

    RecyclerView.Adapter stockImageAdapter;
    RecyclerView.Adapter motifAdapter;
    RecyclerView.Adapter kristikAdapter;

    public MyMotifFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_my_motif, container, false);

        ButterKnife.bind(this, itemView);

        App.get(getActivity()).getInjector().inject(this);

        setHasOptionsMenu(true);

        return itemView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupAdapter();

        setupLayoutManager();

        registerListener();

        setupItemDecorator();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        imageRecyclerView.setAdapter(null);
    }

    private void setupItemDecorator() {
        imageRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.left = 5;
                outRect.right = 5;
                outRect.bottom = 5;
                outRect.top = 5;
                outRect.top = 5;
            }
        });
    }

    private void setupAdapter() {
        stockImageAdapter = new StockImageRecyclerAdapter(this, realm.where(StockImage.class).findAll().sort("id", Sort.DESCENDING), true);
        motifAdapter = new UserMotifRecyclerAdapter(this, realm.where(Motif.class).findAll().sort("id", Sort.DESCENDING), true);
        kristikAdapter = new UserKristikRecyclerAdapter(this, realm.where(Kristik.class).findAll().sort("id", Sort.DESCENDING), true);

        imageRecyclerView.setAdapter(stockImageAdapter);
    }

    private void setupLayoutManager() {
        @SuppressLint("WrongConstant") GridLayoutManager manager = new GridLayoutManager(getActivity(), GRID_SIZE, GridLayoutManager.VERTICAL, false);
        imageRecyclerView.setLayoutManager(manager);
    }

    private void registerListener() {

        ((StockImageRecyclerAdapter) stockImageAdapter).setOnItemClickedListener(new OnImageItemClickedListener() {
            @Override
            public void onImageItemClicked(int id, View view) {
                startCreateMotifActivity(id);
            }
        });

        ((UserMotifRecyclerAdapter) motifAdapter).setOnItemClickedListener(new OnImageItemClickedListener() {
            @Override
            public void onImageItemClicked(int id, View view) {
                startDetailMotifActivity(id);
            }
        });

        ((UserKristikRecyclerAdapter) kristikAdapter).setOnItemClickedListener(new OnImageItemClickedListener() {
            @Override
            public void onImageItemClicked(int id, View view) {
                startDetailKristikActivity(id);
            }
        });

        stockImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stockImageButton.setTypeface(null, Typeface.BOLD);
                motifButton.setTypeface(null, Typeface.NORMAL);
                kristikButton.setTypeface(null, Typeface.NORMAL);

                imageRecyclerView.setAdapter(stockImageAdapter);
            }
        });

        motifButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stockImageButton.setTypeface(null, Typeface.NORMAL);
                motifButton.setTypeface(null, Typeface.BOLD);
                kristikButton.setTypeface(null, Typeface.NORMAL);

                imageRecyclerView.setAdapter(motifAdapter);
            }
        });

        kristikButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stockImageButton.setTypeface(null, Typeface.NORMAL);
                motifButton.setTypeface(null, Typeface.NORMAL);
                kristikButton.setTypeface(null, Typeface.BOLD);

                imageRecyclerView.setAdapter(kristikAdapter);
            }
        });

        kristikBlankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBlankEditKristikActivity();
            }
        });
    }

    private void startBlankEditKristikActivity() {
        Intent intent = new Intent(getActivity(), EditKristikActivity.class);

        startActivity(intent);
    }

    private void startCreateMotifActivity(int id) {
        Intent intent = new Intent(getContext(), GenerateMotifActivity.class);
        intent.putExtra(IntentParams.STOCK_IMAGE_ID, id);

        startActivity(intent);
    }

    private void startDetailMotifActivity(int id) {
        Intent intent = new Intent(getContext(), DetailMotifActivity.class);
        intent.putExtra(IntentParams.MOTIF_ID, id);

        startActivity(intent);
    }

    private void startDetailKristikActivity(int id) {
        Intent intent = new Intent(getContext(), DetailKristikActivity.class);
        intent.putExtra(IntentParams.KRISTIK_ID, id);

        startActivity(intent);
    }
}
