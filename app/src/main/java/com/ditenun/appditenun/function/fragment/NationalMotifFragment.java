package com.ditenun.appditenun.function.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ditenun.appditenun.R;
import com.ditenun.appditenun.dependency.App;
import com.ditenun.appditenun.dependency.models.Tenun;
import com.ditenun.appditenun.dependency.network.TenunNetworkInterface;
import com.ditenun.appditenun.function.activity.DetailTenunActivity;
import com.ditenun.appditenun.function.adapter.BrowseTenunRecyclerViewAdapter;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.Sort;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class NationalMotifFragment extends Fragment implements BrowseTenunRecyclerViewAdapter.OnClickItemTenunListener {
    static final String LAYOUT_EMPTY = "emptyList";
    static final String LAYOUT_ERROR = "error";
    static final String LAYOUT_LOADING = "loading";
    static final String LAYOUT_SUCCESS = "success";

    @BindView(R.id.browseTenunCoordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.recyclerViewListTenun)
    RecyclerView recyclerView;

    @BindView(R.id.spinnerIcon)
    IconTextView spinner;

    @Inject
    TenunNetworkInterface tenunNetworkInterface;

    @Inject
    Realm realm;

    List<Tenun> listTenun = Collections.EMPTY_LIST;
    List<Object> listObject = new ArrayList<>();
    BrowseTenunRecyclerViewAdapter adapter;
    RecyclerView.LayoutManager linearLayoutManager;

    GridLayoutManager manager;

    private ScaleInAnimationAdapter scaleInAnimationAdapter;

    public NationalMotifFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_tenun, container, false);

        ButterKnife.bind(this, itemView);

        App.get(getActivity()).getInjector().inject(this);

        setHasOptionsMenu(true);

        return itemView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new BrowseTenunRecyclerViewAdapter(listObject, this);
        scaleInAnimationAdapter = new ScaleInAnimationAdapter(adapter);
        recyclerView.setAdapter(scaleInAnimationAdapter);

        linearLayoutManager = new LinearLayoutManager(getActivity());

        manager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case BrowseTenunRecyclerViewAdapter.TENUN:
                        return 1;
                    case BrowseTenunRecyclerViewAdapter.HEADER_TENUN:
                        return manager.getSpanCount();
                    default:
                        return 0;
                }
            }
        });
        recyclerView.setLayoutManager(manager);

        populateInitialData();
    }

    private void populateInitialData() {
        realm.executeTransactionAsync(realm1 -> {
            listTenun = realm1.copyFromRealm(realm1.where(Tenun.class).findAll().sort("asalTenun", Sort.DESCENDING));
        }, () -> {
            if (!listTenun.isEmpty()) {
                adapter.setData(populateNewListObject(listTenun));
                updateLayout(LAYOUT_SUCCESS);
            } else {
                updateLayout(LAYOUT_EMPTY);
            }
        });
    }

    private void updateLayout(String status) {
        switch (status) {
            case LAYOUT_SUCCESS:
                spinner.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                break;
            case LAYOUT_EMPTY:
                spinner.setText("{fa-info 200%}  No data found");
                break;
            case LAYOUT_ERROR:
                spinner.setText("{fa-info 200%} Error");
                break;
            case LAYOUT_LOADING:
                recyclerView.setVisibility(View.GONE);
                spinner.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private List<Object> populateNewListObject(List<Tenun> listTenun) {
        List<Object> tempListObject = new ArrayList<>();

        String asalTenun = listTenun.get(0).getAsalTenun();
        tempListObject.add(asalTenun);

        for (Tenun tenun : listTenun) {
            if (!asalTenun.equals(tenun.getAsalTenun())) {
                asalTenun = tenun.getAsalTenun();
                tempListObject.add(asalTenun);
                tempListObject.add(tenun);

            } else {
                tempListObject.add(tenun);
            }
        }

        return tempListObject;
    }

    @Override
    public void OnClickItemTenun(String idTenun, View imageThumb) {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), imageThumb, "thumb");
        startActivity(DetailTenunActivity.createIntent(getActivity(), idTenun), optionsCompat.toBundle());
    }
}
