package com.ditenun.appditenun.function.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.Nullable;

import com.ditenun.appditenun.dependency.models.Generate;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ditenun.appditenun.R;
import com.ditenun.appditenun.dependency.App;
import com.ditenun.appditenun.dependency.models.Motif;
import com.ditenun.appditenun.dependency.models.StockImage;
import com.ditenun.appditenun.dependency.models.MotifTenun;
import com.ditenun.appditenun.dependency.modules.APIModule;
import com.ditenun.appditenun.dependency.network.TenunNetworkInterface;
import com.ditenun.appditenun.function.dialog.ConfirmationDialog;
import com.ditenun.appditenun.function.dialog.NameInputDialog;
import com.ditenun.appditenun.function.dialog.OnConfirmedListener;
import com.ditenun.appditenun.function.dialog.OnNameInputtedListener;
import com.ditenun.appditenun.function.util.BitmapUtils;
import com.ditenun.appditenun.function.util.FileUtils;
import com.ditenun.appditenun.function.util.IntentParams;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenerateMotifActivity extends AppCompatActivity {

    static final int EDIT_IMAGE_INTENT_CODE = 102;
    static final int GENERATE_MOTIF_MATRIX_PARAM = 2;
    static final int GENERATE_MOTIF_COLOR_PARAM = 1;

    @BindView(R.id.container)
    View rootContainer;

    @BindView(R.id.toolbar)
    Toolbar mainToolbar;

    @BindView(R.id.main_image_view)
    ImageView mainImageView;

    @BindView(R.id.edit_button)
    Button editButton;

    @BindView(R.id.generate_motif_button)
    Button generateMotifButton;

    @BindView(R.id.generate_kristik_button)
    Button generateKristikButton;

    @BindView(R.id.save_button)
    Button saveButton;

    @BindView(R.id.home_button)
    Button homeButton;

    @BindView(R.id.loading_progress_bar)
    ProgressBar loading_progress_bar;

    @Inject
    TenunNetworkInterface tenunNetworkInterface;

    int stockImageId;
    String tenunImageId;

    StockImage stockImageModel;
    MotifTenun tenunImageModel;

    byte[] seedImageBytes;
    Bitmap seedImageBimap;
    Bitmap outputImageBitmap;

    @Inject
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_motif);

        App.get(this).getInjector().inject(this);

        ButterKnife.bind(this);

        readIntentParams();

        loadImage();

        registerListener();

        setupToolbar();

        hideLoading();
    }

    private void setupToolbar() {
        mainToolbar.setTitle(R.string.generate_motif);
        setSupportActionBar(mainToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if(outputImageBitmap!= null){
            dialogConfirmation();
        }
        else {
            onBackPressed();
            finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(outputImageBitmap!= null){
            dialogConfirmation();
        }
        else {
            super.onBackPressed();
            finish();
        }

    }

    private void dialogConfirmation() {
        Resources resources = getResources();
        ConfirmationDialog confirmationDialog = new ConfirmationDialog(
                this,
                resources.getString(R.string.keluartanpasimpan),
                resources.getString(R.string.kembali),
                resources.getString(R.string.tidak)
        );

        confirmationDialog.setListener(new OnConfirmedListener() {
            @Override
            public void onPostive() {
                confirmationDialog.dismiss();
                GenerateMotifActivity.this.finish();
            }

            @Override
            public void onNegative() {
                confirmationDialog.dismiss();
            }
        });

        confirmationDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == EDIT_IMAGE_INTENT_CODE && resultCode == RESULT_OK)
        {
            String path = data.getStringExtra(IntentParams.IMAGE_PATH);
            boolean needDeleted = data.getBooleanExtra(IntentParams.DELETE_IMAGE_AFTER_READ, false);

            byte[] imageBytes = FileUtils.LoadImageFile(path, needDeleted);
            outputImageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            showMotifPreview(outputImageBitmap);
        }
    }

    private void readIntentParams() {
        Intent intent = getIntent();

        stockImageId = intent.getIntExtra(IntentParams.STOCK_IMAGE_ID, 0);
        tenunImageId = intent.getStringExtra(IntentParams.TENUN_IMAGE_ID);
    }

    private void loadImage() {
        if (stockImageId != 0) {
            stockImageModel = realm.where(StockImage.class).equalTo("id", stockImageId).findFirst();
            seedImageBytes = stockImageModel.getBytes();
            seedImageBimap = BitmapFactory.decodeByteArray(seedImageBytes, 0, seedImageBytes.length);

            outputImageBitmap = seedImageBimap;
            showMotifPreview(outputImageBitmap);
        } else if (tenunImageId != null) {
            tenunImageModel = realm.where(MotifTenun.class).equalTo("id", tenunImageId).findFirst();
            getTenunImage(tenunImageModel.getImageMotif());
        }
    }

    private void registerListener() {
        generateMotifButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performGenerateMotif();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditActivity();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMotif();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(outputImageBitmap!= null){
                    dialogHomeConfirmation();
                }
                else {
                    startActivity(HomeActivity.createIntent(getApplicationContext()));
                }
            }
        });

        generateKristikButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGenerateKristikActivity();
            }
        });
    }


    private void saveMotif() {
        Resources resources = getResources();
        NameInputDialog nameInputDialog = new NameInputDialog(
                this,
                resources.getString(R.string.motif_name),
                resources.getString(R.string.save),
                resources.getString(R.string.cencel)
        );

        nameInputDialog.setListener(new OnNameInputtedListener() {
            @Override
            public void onPositive(String name) {
                nameInputDialog.dismiss();

                int id = saveMotif(name);
                startDetailMotifActivity(id);
                finish();
            }

            @Override
            public void onNegative() {
                nameInputDialog.dismiss();
            }
        });

        nameInputDialog.show();
    }


    private int saveMotif(String name) {
        Number maxId = realm.where(Motif.class).max("id");
        int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;

        realm.beginTransaction();
        Motif motif = realm.createObject(Motif.class, nextId);
        motif.setBytes(BitmapUtils.convertToBytes(outputImageBitmap));
        motif.setName(name);
        realm.insertOrUpdate(motif);
        realm.commitTransaction();

        return motif.getId();
    }

    private void performGenerateMotif() {
        showLoading();
//        if(tenunImageId!=null){
//            realm.beginTransaction();
//            List<Generate> listGenerate = realm.where(Generate.class).equalTo("idMotif", tenunImageModel.getId()).findAll();
//            realm.commitTransaction();
//            Generate newGenerateMotif = getRandomMotifFromList(listGenerate);
//
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    getTenunImage(newGenerateMotif.getGenerateFile());
//                }
//            }, 3000);
//
//        }else{
            requestMotifFromServer(GENERATE_MOTIF_MATRIX_PARAM, GENERATE_MOTIF_COLOR_PARAM, seedImageBytes);
//        }
    }

    private Generate getRandomMotifFromList(List<Generate> generateList){
        Random random = new Random();
        int randomIndex = random.nextInt(generateList.size());
        return generateList.get(randomIndex);
    }

    private void requestMotifFromServer(int matrix, int color, byte[] motifBytes) {
        RequestBody photoBody = RequestBody.create(MediaType.parse("image/*"), motifBytes);
        MultipartBody.Part photoPart = MultipartBody.Part.createFormData("img_file", "motif.jpg", photoBody);

        tenunNetworkInterface.generateMotif(APIModule.ACCESS_TOKEN_TEMP, matrix, color, photoPart).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    outputImageBitmap = BitmapFactory.decodeStream(response.body().byteStream());
                    showMotifPreview(outputImageBitmap);
                } else {
                    showMessage(response.message());
                }

                hideLoading();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showMessage(t.getLocalizedMessage());
                hideLoading();
            }
        });
    }

    private void showMotifPreview(Bitmap bitmap) {
        mainImageView.setImageBitmap(bitmap);
    }

    private void startGenerateKristikActivity() {
        Intent intent = new Intent(this, GenerateKristikActivity.class);

        String path = FileUtils.CreateTempFile("Motif", null, getCacheDir(), BitmapUtils.convertToBytes(outputImageBitmap));

        intent.putExtra(IntentParams.IMAGE_PATH, path);
        intent.putExtra(IntentParams.DELETE_IMAGE_AFTER_READ, true);

        startActivity(intent);
    }

    private void showLoading() {
        loading_progress_bar.setVisibility(View.VISIBLE);

        generateMotifButton.setEnabled(false);
        generateKristikButton.setEnabled(false);
        saveButton.setEnabled(false);
    }

    private void hideLoading() {
        loading_progress_bar.setVisibility(View.GONE);

        generateMotifButton.setEnabled(true);
        generateKristikButton.setEnabled(true);
        saveButton.setEnabled(true);
    }

    private void showMessage(String message) {
        Snackbar snackbar = Snackbar.make(rootContainer, message, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });

        snackbar.show();
    }

    private void startEditActivity() {
        Intent intent = new Intent(this, EditMotifActivity.class);

        String path = FileUtils.CreateTempFile("Motif", null, getCacheDir(), BitmapUtils.convertToBytes(outputImageBitmap));

        intent.putExtra(IntentParams.IMAGE_PATH, path);
        intent.putExtra(IntentParams.DELETE_IMAGE_AFTER_READ, true);
        intent.putExtra(IntentParams.REQUEST_RESULT, true);

        startActivityForResult(intent, EDIT_IMAGE_INTENT_CODE);
    }

    private void startDetailMotifActivity(int motifId) {
        Intent intent = new Intent(this, DetailMotifActivity.class);
        intent.putExtra(IntentParams.MOTIF_ID, motifId);

        startActivity(intent);
    }

    private void getTenunImage(String imgUrl){
        tenunNetworkInterface.getTentunImage(APIModule.ENDPOINT + imgUrl).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        seedImageBytes = response.body().bytes();
                        seedImageBimap = BitmapFactory.decodeByteArray(seedImageBytes, 0, seedImageBytes.length);

                        outputImageBitmap = seedImageBimap;
                        showMotifPreview(outputImageBitmap);
                    } catch (IOException e) {
                        showMessage(e.getMessage());
                    }
                    hideLoading();
                } else {
                    showMessage(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showMessage(t.getLocalizedMessage());
            }
        });
    }

    private void dialogHomeConfirmation() {
        Resources resources = getResources();
        ConfirmationDialog confirmationDialog = new ConfirmationDialog(
                this,
                resources.getString(R.string.keluartanpasimpan),
                resources.getString(R.string.kembali),
                resources.getString(R.string.tidak)
        );

        confirmationDialog.setListener(new OnConfirmedListener() {
            @Override
            public void onPostive() {
                confirmationDialog.dismiss();
                startActivity(HomeActivity.createIntent(getApplicationContext()));
                finish();
            }

            @Override
            public void onNegative() {
                confirmationDialog.dismiss();
            }
        });

        confirmationDialog.show();

    }
}
