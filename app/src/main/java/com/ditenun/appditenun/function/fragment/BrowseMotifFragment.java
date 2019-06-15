package com.ditenun.appditenun.function.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ditenun.appditenun.R;
import com.ditenun.appditenun.R2;
import com.ditenun.appditenun.dependency.App;
import com.ditenun.appditenun.dependency.models.MotifTenun;
import com.ditenun.appditenun.dependency.models.Tenun;
import com.ditenun.appditenun.dependency.network.TenunNetworkInterface;
import com.ditenun.appditenun.function.activity.HomeActivity;
import com.ditenun.appditenun.function.adapter.AutoFitGridLayoutManager;
import com.ditenun.appditenun.function.adapter.BrowseMotifRecyclerViewAdapter;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BrowseMotifFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BrowseMotifFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BrowseMotifFragment extends Fragment implements BrowseMotifRecyclerViewAdapter.OnClickItemMotifListener, BrowseMotifRecyclerViewAdapter.OnIdShownListener {
    public final static int REQUEST_CODE = 4;
    private static Parcelable SAVED_INSTANCE;
    private final String EMPTY_LIST = "emptyList";
    private final String ERROR = "error";
    private final String LOADING = "loading";
    private final String SUCCESS = "success";
    private final int SPAN_COUNT = 3;   //CHANGE THIS FOR SPAN COUNT, WE WILL REVISIT THIS THING AFTER MAIN FUNCTION DONE

    @BindView(R2.id.browseMotifCoordinatorLayout)
    CoordinatorLayout coordinatorLayout;
//    @BindView(R2.id.toolbarBrowseMotif)
//    Toolbar toolbar;
    @BindView(R2.id.recyclerViewListMotif)
    RecyclerView recyclerView;
    @BindView(R2.id.spinnerIcon)
    IconTextView spinner;
    @Inject
    TenunNetworkInterface tenunNetworkInterface;
    @Inject
    Realm realm;
    List<MotifTenun> listMotif = Collections.EMPTY_LIST;
    List<Object> listObject = new ArrayList<>();
    BrowseMotifRecyclerViewAdapter adapter;
    AutoFitGridLayoutManager gridLayoutManager;
    private ScaleInAnimationAdapter scaleInAnimationAdapter;

    private OnFragmentInteractionListener mListener;

    public BrowseMotifFragment() {
        // Required empty public constructor
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }

    public static BrowseMotifFragment newInstance() {
        BrowseMotifFragment fragment = new BrowseMotifFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.get(getActivity()).getInjector().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View V = inflater.inflate(R.layout.fragment_browse_motif, container, false);
        ButterKnife.bind(this, V);

        return V;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new BrowseMotifRecyclerViewAdapter(listObject, this, this);
        scaleInAnimationAdapter = new ScaleInAnimationAdapter(adapter);
        recyclerView.setAdapter(scaleInAnimationAdapter);

        gridLayoutManager = new AutoFitGridLayoutManager(getActivity(), 230);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case BrowseMotifRecyclerViewAdapter.MOTIF:
                        return 1;
                    case BrowseMotifRecyclerViewAdapter.HEADER_MOTIF:
                        return gridLayoutManager.getSpanCount();
                    default:
                        return 0;
                }
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);

        if (SAVED_INSTANCE == null) {
            populateInitialData();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void populateInitialData() {
        realm.executeTransactionAsync(realm1 -> {
            listMotif = realm1.copyFromRealm(realm1.where(MotifTenun.class).findAll().sort("idTenun"));
        }, () -> {
            adapter.setData(populateNewListObject(listMotif));
            updateLayout(SUCCESS);
        });
    }

    private List<Object> populateNewListObject(List<MotifTenun> listMotif) {
        List<Object> tempListObject = new ArrayList<>();
        if(listMotif.size()>0){
            String idTenun = listMotif.get(0).getIdTenun();
            tempListObject.add(idTenun);

            for (MotifTenun motif : listMotif) {
                if (!idTenun.equals(motif.getIdTenun())) {
                    idTenun = motif.getIdTenun();
                    tempListObject.add(idTenun);
                    tempListObject.add(motif);

                } else {
                    tempListObject.add(motif);
                }
            }

            return tempListObject;
        }
        else
            return null;
    }

    private void updateLayout(String status) {
        switch (status) {
            case SUCCESS:
                spinner.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                break;
            case EMPTY_LIST:
                spinner.setText("{fa-info 200%}  No data found");
                break;
            case ERROR:
                spinner.setText("{fa-info 200%} Error");
                break;
            case LOADING:
                recyclerView.setVisibility(View.GONE);
                spinner.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            SAVED_INSTANCE = gridLayoutManager.onSaveInstanceState();
        } else {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            SAVED_INSTANCE = linearLayoutManager.onSaveInstanceState();
        }
    }

    @Override
    public void OnClickItemMotif(String idMotif, View imageThumb) {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), imageThumb, "thumb");

//        startActivity(DetailMotifActivity.createIntent(getActivity(), idMotif), optionsCompat.toBundle());
    }

    @Override
    public String onIdLoaded(String id) {
        try{
            String namaTenun = "";
            namaTenun = realm.where(Tenun.class).equalTo("id", id).findFirst().getNamaTenun();
            return namaTenun;
        }catch (Exception e){
            return e.getMessage();
        }

    }

    @Override
    public void onDestroy() {
        SAVED_INSTANCE = null;
        super.onDestroy();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
