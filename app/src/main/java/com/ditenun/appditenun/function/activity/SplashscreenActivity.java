package com.ditenun.appditenun.function.activity;

import android.content.Intent;

import com.ditenun.appditenun.dependency.models.ResponseGenerateFile;
import com.ditenun.appditenun.dependency.models.ResponseGetFaq;
import com.ditenun.appditenun.dependency.models.User;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.ditenun.appditenun.R;
import com.ditenun.appditenun.dependency.App;
import com.ditenun.appditenun.dependency.models.ResponseGetData;
import com.ditenun.appditenun.dependency.models.ResponseGetDataMotif;
import com.ditenun.appditenun.dependency.modules.APIModule;
import com.ditenun.appditenun.dependency.network.TenunNetworkInterface;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SplashscreenActivity extends AppCompatActivity {

    final static String DEFAULT_SIZE = "all";
    final static int DEFAULT_CURSOR = 0;

    public SharedPreferences sp;

    @BindView(R.id.container)
    RelativeLayout rootContainer;

    @Inject
    Realm realm;

    @Inject
    TenunNetworkInterface tenunNetworkInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        App.get(getApplicationContext()).getInjector().inject(this);

        sp = getSharedPreferences("login", MODE_PRIVATE);

        ButterKnife.bind(this);

        getSupportActionBar().hide();

        fetchData();
    }

    private void fetchData() {
        requestMotifFromServer();
    }

    private void requestMotifFromServer() {
        tenunNetworkInterface.getListMotif(APIModule.ACCESS_TOKEN_TEMP, DEFAULT_CURSOR, DEFAULT_SIZE).enqueue(new Callback<ResponseGetDataMotif>() {
            @Override
            public void onResponse(Call<ResponseGetDataMotif> call, Response<ResponseGetDataMotif> response) {
                if (response.isSuccessful()) {
                    realm.beginTransaction();
                    realm.executeTransactionAsync(realm1 -> realm1.insertOrUpdate(response.body().getData()));
                    realm.commitTransaction();
                } else {
                    Timber.i(String.valueOf(response.code()));
                }

                requestGenerateMotifFromServer();
            }

            @Override
            public void onFailure(Call<ResponseGetDataMotif> call, Throwable t) {
                showErrorMessage(t.getMessage());
            }
        });
    }

    private void requestGenerateMotifFromServer() {
        tenunNetworkInterface.getListGenerate(APIModule.ACCESS_TOKEN_TEMP, DEFAULT_CURSOR, DEFAULT_SIZE).enqueue(new Callback<ResponseGenerateFile>() {
            @Override
            public void onResponse(Call<ResponseGenerateFile> call, Response<ResponseGenerateFile> response) {
                if (response.isSuccessful()) {
                    realm.beginTransaction();
                    realm.executeTransactionAsync(realm2 -> realm2.insertOrUpdate(response.body().getData()));
                    realm.commitTransaction();
                } else {
                    Timber.i(String.valueOf(response.code()));
                }

                requestTenunFromServer();
            }

            @Override
            public void onFailure(Call<ResponseGenerateFile> call, Throwable t) {
                showErrorMessage(t.getMessage());
            }
        });
    }

    private void requestTenunFromServer() {
        tenunNetworkInterface.getListTenun(APIModule.ACCESS_TOKEN_TEMP, DEFAULT_CURSOR, DEFAULT_SIZE).enqueue(new Callback<ResponseGetData>() {
            @Override
            public void onResponse(Call<ResponseGetData> call, Response<ResponseGetData> response) {
                if (response.isSuccessful()) {
                    realm.beginTransaction();
                    realm.executeTransactionAsync(realm3 -> realm3.insertOrUpdate(response.body().getData()));
                    realm.commitTransaction();

                    requestFaqFromServer();
                }
            }

            @Override
            public void onFailure(Call<ResponseGetData> call, Throwable t) {
                showErrorMessage(t.getMessage());
            }
        });
    }

    private void requestFaqFromServer() {
        tenunNetworkInterface.getListFaq(APIModule.ACCESS_TOKEN_TEMP).enqueue(new Callback<ResponseGetFaq>() {
            @Override
            public void onResponse(Call<ResponseGetFaq> call, Response<ResponseGetFaq> response) {
                if (response.isSuccessful()) {
                    realm.beginTransaction();
                    realm.executeTransactionAsync(realm4 -> realm4.insertOrUpdate(response.body().getData()));
                    realm.commitTransaction();

                    if(sp.getBoolean("logged", true)){
                        startHomeActivity();
                    } else {
                        startLoginActivity();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseGetFaq> call, Throwable t) {
                showErrorMessage(t.getMessage());
            }
        });
    }

    private void retryFecthData() {
        fetchData();
    }

    private void showErrorMessage(String message) {
        createSnackbar(getResources().getString(R.string.error) + ": " + message, true).show();
    }

    private Snackbar createSnackbar(String message, boolean isAllowedToContinue) {
        Snackbar snackbar = Snackbar.make(rootContainer, message, Snackbar.LENGTH_INDEFINITE);
        if (isAllowedToContinue) {
            snackbar.setAction(R.string.skip, v -> {
                startLoginActivity();
            });
        } else {
            snackbar.setAction(R.string.retry, v -> {
                retryFecthData();
            });
        }

        return snackbar;
    }


    private void startLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(intent);

        finish();
    }

    private void startHomeActivity() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(intent);

        finish();
    }
}
