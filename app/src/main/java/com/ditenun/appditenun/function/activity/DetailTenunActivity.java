package com.ditenun.appditenun.function.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ditenun.appditenun.R;
import com.ditenun.appditenun.R2;
import com.ditenun.appditenun.dependency.App;
import com.ditenun.appditenun.dependency.models.MotifTenun;
import com.ditenun.appditenun.dependency.models.Tenun;
import com.ditenun.appditenun.dependency.modules.APIModule;
import com.ditenun.appditenun.dependency.network.TenunNetworkInterface;
import com.ditenun.appditenun.function.adapter.RecyclerViewAdapterMotif;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.Sort;
import timber.log.Timber;

public class DetailTenunActivity extends AppCompatActivity {
    public static final String KEY_ID_TENUN = "id_key";

    @Inject
    Realm realm;
    @BindView(R2.id.thumbTenun)
    ImageView thumbTenun;
    @BindView(R2.id.descTenun)
    TextView textDescTenun;
    @BindView(R2.id.historyTenun)
    TextView textHistoryTenun;
    @BindView(R2.id.functionTenun)
    TextView textFunctionTenun;
    @BindView(R2.id.originTenun)
    TextView textOriginTenun;
    @BindView(R2.id.titleTenun)
    TextView textTitleTenun;
    @BindView(R2.id.coordinatorLayoutDetailTenun)
    CoordinatorLayout coordinatorLayout;

    ImageView imageViewBarBack;
    @BindView(R2.id.buttonHome)
    Button buttonHome;

    MenuItem generateMenu;
    List<MotifTenun> listMotif = Collections.EMPTY_LIST;
    //    MaterialDialog dialog2;
    private Tenun tenun;
    @Inject
    TenunNetworkInterface tenunNetworkInterface;

    public static Intent createIntent(Context context, String id) {
        Intent intent = new Intent(context, DetailTenunActivity.class);
        intent.putExtra(KEY_ID_TENUN, id);

        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tenun);

        App.get(getApplicationContext()).getInjector().inject(this);
        ButterKnife.bind(this);
        listMotif = new ArrayList<MotifTenun>();
        imageViewBarBack = (ImageView) findViewById(R.id.imageViewBarBack);

        imageViewBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(HomeActivity.createIntent(getApplicationContext()));
            }
        });

        Intent returnIntent = getIntent();
        String idTenun = returnIntent.getStringExtra(KEY_ID_TENUN);

        listMotif = realm.where(MotifTenun.class).equalTo("idTenun",idTenun).findAll().sort("id", Sort.ASCENDING);
        Timber.i("Jumlah Motif: ", listMotif.size());
        RecyclerView myrv = (RecyclerView) findViewById(R.id.recyclerviewulos_id);
        RecyclerViewAdapterMotif myAdapter = new RecyclerViewAdapterMotif(this, listMotif);
        myrv.setLayoutManager(new GridLayoutManager(this, 3));
        myrv.setAdapter(myAdapter);

        tenun = realm.where(Tenun.class)
                .equalTo("id", idTenun)
                .findFirst();
        String imgUrl = APIModule.ENDPOINT + tenun.getImageSrc();

        /* for beauty toolbar */
        SimpleTarget target = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                thumbTenun.setImageBitmap(resource);
                Palette palette = Palette.from(resource).generate();
                int defaultColor;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    defaultColor = getResources().getColor(R.color.colorPrimaryDark, getTheme());
                } else {
                    defaultColor = getResources().getColor(R.color.colorPrimaryDark);
                }

                int newPrimaryColor = palette.getLightVibrantColor(defaultColor);
                int newAccentColor = palette.getDarkVibrantColor(defaultColor);

//                toolbar.setBackgroundColor(newPrimaryColor);
//                toolbar.setNavigationIcon(IconUtils.getIconDrawableWithColor(getApplicationContext(), IoniconsIcons.ion_android_arrow_back, newAccentColor));
//                textToolbar.setTextColor(newAccentColor);
            }
        };

        Glide.with(getApplicationContext())
                .load(imgUrl)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .asIs()
                .into(target);

        Timber.i(tenun.getImageSrc());
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContent();

//        thumbTenun.setOnClickListener(v -> startActivity(PreviewActivity.createIntent(getApplicationContext(), imgUrl)));
    }

    private void setContent() {
        textHistoryTenun.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/garamond_regular_bold.ttf"));
        textHistoryTenun.setTextColor(Color.BLACK);
        textHistoryTenun.setText(tenun.getSejarahTenun());

        textDescTenun.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/myriad_condensed.ttf"));
        textDescTenun.setTextColor(Color.BLACK);
        textDescTenun.setText(tenun.getDeskripsiTenun());

        textTitleTenun.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/garamond_regular_bold.ttf"));
        textTitleTenun.setTextColor(Color.BLACK);
        textTitleTenun.setText(tenun.getNamaTenun());
    }

    @Override
    public void onBackPressed(){
        finish();
    }



//    private void generateNewMotif(String algoritma, String sourceFile, String model, String warna, String idMotif) {
//        progressBar.setVisibility(View.VISIBLE);
//        RequestBodyGenerateImg2 requestBodyGenerateImg2 = new RequestBodyGenerateImg2(algoritma, sourceFile, model, warna, idMotif);
//
//        tenunNetworkInterface.generateNewMotif(APIModule.ACCESS_TOKEN_TEMP, requestBodyGenerateImg2).enqueue(new Callback<ResponseGetGeneratedImage>() {
//            @Override
//            public void onResponse(Call<ResponseGetGeneratedImage> call, Response<ResponseGetGeneratedImage> response) {
//
//                if (response.isSuccessful()) {
//                    String newImgUrl = response.body().getExec_result();
//                    List<Generate> generate = response.body().getData();
//                    progressBar.setVisibility(View.GONE);
//                } else {
//                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Error to generate", Snackbar.LENGTH_INDEFINITE);
//                    snackbar.setAction("DISMISS", v -> {
//                        snackbar.dismiss();
//                    });
//                    snackbar.show();
//                    progressBar.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseGetGeneratedImage> call, Throwable t) {
//                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Error to generate", Snackbar.LENGTH_INDEFINITE);
//                snackbar.setAction("DISMISS", v -> {
//                    snackbar.dismiss();
//                });
//                snackbar.show();
//                progressBar.setVisibility(View.GONE);
//            }
//        });
//    }


}
