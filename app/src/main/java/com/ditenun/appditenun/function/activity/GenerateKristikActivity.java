package com.ditenun.appditenun.function.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ditenun.appditenun.R;
import com.ditenun.appditenun.R2;
import com.ditenun.appditenun.dependency.App;
import com.ditenun.appditenun.dependency.models.Kristik;
import com.ditenun.appditenun.dependency.network.TenunNetworkInterface;
import com.ditenun.appditenun.function.dialog.ConfirmationDialog;
import com.ditenun.appditenun.function.dialog.NameInputDialog;
import com.ditenun.appditenun.function.dialog.OnConfirmedListener;
import com.ditenun.appditenun.function.dialog.OnNameInputtedListener;
import com.ditenun.appditenun.function.util.BitmapUtils;
import com.ditenun.appditenun.function.util.IntentParams;
import com.ditenun.appditenun.function.view.KristikDrawable;
import com.ditenun.appditenun.dependency.modules.APIModule;
import com.ditenun.appditenun.function.util.FileUtils;
import com.otaliastudios.zoom.ZoomLayout;

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

import static com.ditenun.appditenun.function.activity.GenerateMotifActivity.EDIT_IMAGE_INTENT_CODE;

public class GenerateKristikActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mainToolbar;

    @BindView(R.id.save_button)
    Button saveButton;

    @BindView(R.id.home_button)
    Button homeButton;

    @BindView(R.id.kristik_zoom_layout)
    public ZoomLayout kristikZoomLayout;

    @BindView(R.id.motif_image_view)
    public ImageView motifImageView;

    @BindView(R.id.main_kristik_view)
    public ImageView mainKristikView;

    @BindView(R.id.progress_bar)
    public ProgressBar progressBar;

    @BindView(R.id.generate_button)
    public Button generateButton;

    @BindView(R.id.radioGroupUkuranKristik)
    RadioGroup kristikSizeRadioGroup;

    @BindView(R.id.radioGroupJumlahWarna)
    RadioGroup colorSizeRadioGroup;

    @BindView(R.id.edit_button)
    Button editButton;

    @Inject
    TenunNetworkInterface tenunNetworkInterface;

    @Inject
    Realm realm;

    private byte[] motifBytes;
    private Bitmap motifBitmap;
    private Bitmap kristikBitmap;
    private Bitmap kristikToEdit;

    @BindView(R2.id.container)
    LinearLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_generate_kristik);

        App.get(getApplicationContext()).getInjector().inject(this);

        ButterKnife.bind(this);

        setupToolbar();

        if (readIntentParam()) {
            setupCurrentView();
        } else {
            Toast.makeText(getApplicationContext(), "Tidak dapat membaca gambar!", Toast.LENGTH_LONG).show();
        }

        setupDisplay();

        registerListener();
    }

    @Override
    public boolean onSupportNavigateUp() {
        if(kristikBitmap!= null){
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
        if(kristikBitmap!= null){
            dialogConfirmation();
        }
        else {
            super.onBackPressed();
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == EDIT_IMAGE_INTENT_CODE && resultCode == RESULT_OK)
        {
            String path = data.getStringExtra(IntentParams.IMAGE_PATH);
            boolean needDeleted = data.getBooleanExtra(IntentParams.DELETE_IMAGE_AFTER_READ, false);

            byte[] imageBytes = FileUtils.LoadImageFile(path, needDeleted);
            kristikBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            Bitmap showableKristik = kristikBitmap;
                    //BitmapUtils.drawableToBitmap(new KristikDrawable(kristikBitmap));
            showKristikPreview(showableKristik);
        }
    }

    private boolean readIntentParam() {
        Intent intent = getIntent();
        String path = intent.getStringExtra(IntentParams.IMAGE_PATH);
        boolean needDeleted = intent.getBooleanExtra(IntentParams.DELETE_IMAGE_AFTER_READ, false);

        if (path != null && loadImageFromPath(path, needDeleted)) {
            return true;
        } else {
            return false;
        }
    }

    private void setupToolbar() {
        setSupportActionBar(mainToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setupDisplay() {
        mainToolbar.setTitle(R.string.generate_kristik);
        saveButton.setVisibility(View.INVISIBLE);
        editButton.setVisibility(View.INVISIBLE);
    }

    private boolean loadImageFromPath(String path, boolean needDeleted) {
        motifBytes = FileUtils.LoadImageFile(path, needDeleted);

        if (motifBytes != null) {
            motifBitmap = BitmapFactory.decodeByteArray(motifBytes, 0, motifBytes.length);
            return true;
        } else {
            return false;
        }
    }

    private void registerListener() {
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearKristikPreview();
                generateKristik();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveKristik();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(kristikBitmap!= null){
                    dialogHomeConfirmation();
                }
                else {
                    startActivity(HomeActivity.createIntent(getApplicationContext()));
                }

            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditKristikActivity();
            }
        });
    }

    private void saveKristik() {
        Resources resources = getResources();
        NameInputDialog nameInputDialog = new NameInputDialog(
                this,
                resources.getString(R.string.kristik_name),
                resources.getString(R.string.save),
                resources.getString(R.string.cencel)
        );

        nameInputDialog.setListener(new OnNameInputtedListener() {
            @Override
            public void onPositive(String name) {
                nameInputDialog.dismiss();

                int id = saveKristik(name);
                startDetailKristikActivity(id);
                finish();
            }

            @Override
            public void onNegative() {
                nameInputDialog.dismiss();
            }
        });

        nameInputDialog.show();
    }

    private int saveKristik(String name) {
        Number maxId = realm.where(Kristik.class).max("id");
        int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;

        realm.beginTransaction();
        Kristik kristik = realm.createObject(Kristik.class, nextId);
        kristik.setBytes(BitmapUtils.convertToBytes(kristikBitmap));
        kristik.setName(name);
        realm.insertOrUpdate(kristik);
        realm.commitTransaction();

        return kristik.getId();
    }

    private void startDetailKristikActivity(int kristikId) {
        Intent intent = new Intent(this, DetailKristikActivity.class);
        intent.putExtra(IntentParams.KRISTIK_ID, kristikId);

        startActivity(intent);
    }

    private void startEditKristikActivity() {
        Intent intent = new Intent(this, EditKristikActivity.class);
        Bitmap kristikForEdit = BitmapUtils.drawableToBitmap(new KristikDrawable(kristikToEdit));
        String path = FileUtils.CreateTempFile("Kristik", null, getCacheDir(), BitmapUtils.convertToBytes(kristikForEdit));

        intent.putExtra(IntentParams.IMAGE_PATH, path);
        intent.putExtra(IntentParams.DELETE_IMAGE_AFTER_READ, true);
        intent.putExtra(IntentParams.REQUEST_RESULT, true);

        startActivityForResult(intent, EDIT_IMAGE_INTENT_CODE);
    }

    private void setupCurrentView() {
        showMotifPreview(motifBitmap);

        hideLoading();
    }

    public int getKristikSize() {
        int selectedIdRepeatEvent = kristikSizeRadioGroup.getCheckedRadioButtonId();
        if (selectedIdRepeatEvent == R.id.radioButtonBesar) {
            return 7;
        } else if (selectedIdRepeatEvent == R.id.radioButtonSedang) {
            return 4;
        } else if (selectedIdRepeatEvent == R.id.radioButtonKecil) {
            return 2;
        } else {
            return 4;
        }
    }

    private int getColorSize() {
        int selectedIdRepeatEvent = colorSizeRadioGroup.getCheckedRadioButtonId();
        if (selectedIdRepeatEvent == R.id.radioButton2Warna) {
            return 5;
        } else if (selectedIdRepeatEvent == R.id.radioButton5Warna) {
            return 15;
        } else if (selectedIdRepeatEvent == R.id.radioButton50Warna) {
            return 30;
        } else {
            return 5;
        }
    }

    private void generateKristik() {
        int kristikSize = getKristikSize();
        int colorSize = getColorSize();
        requestKristikFromServer(kristikSize, colorSize, motifBytes);
        requestKristikToEdit(kristikSize,colorSize, motifBytes);
        showLoading();
    }

    private void requestKristikToEdit(int squareSize, int colorAmount, byte[] motifBytes){
        RequestBody photoBody = RequestBody.create(MediaType.parse("image/*"), motifBytes);
        MultipartBody.Part photoPart = MultipartBody.Part.createFormData("img_file", "motif.jpg", photoBody);

        tenunNetworkInterface.kristikEditor(APIModule.ACCESS_TOKEN_TEMP, squareSize, colorAmount, photoPart).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    kristikToEdit =BitmapFactory.decodeStream(response.body().byteStream());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void requestKristikFromServer(int squareSize, int colorAmount, byte[] motifBytes) {
        RequestBody photoBody = RequestBody.create(MediaType.parse("image/*"), motifBytes);
        MultipartBody.Part photoPart = MultipartBody.Part.createFormData("img_file", "motif.jpg", photoBody);

        tenunNetworkInterface.hasilKristik(APIModule.ACCESS_TOKEN_TEMP, squareSize, colorAmount, photoPart).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    kristikBitmap = BitmapFactory.decodeStream(response.body().byteStream());
                    //Kelanjutan TA .1
//                    Bitmap showableKristik = BitmapUtils.drawableToBitmap(new KristikDrawable(kristikBitmap));
                    showKristikPreview(kristikBitmap);
                }

                hideLoading();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                hideLoading();
            }
        });
    }

    private void showMotifPreview(Bitmap bitmap) {
        motifImageView.setImageBitmap(bitmap);

        motifImageView.setVisibility(View.VISIBLE);
        kristikZoomLayout.setVisibility(View.INVISIBLE);
    }

    private void clearKristikPreview() {
        mainKristikView.setImageBitmap(null);
    }

    private void showKristikPreview(Bitmap bitmap) {
        mainKristikView.setImageBitmap(bitmap);
        kristikZoomLayout.zoomTo(1, false);

        motifImageView.setVisibility(View.INVISIBLE);
        kristikZoomLayout.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        generateButton.setEnabled(false);
        saveButton.setVisibility(View.INVISIBLE);
        editButton.setVisibility(View.INVISIBLE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        generateButton.setEnabled(true);
        saveButton.setVisibility(View.VISIBLE);
        editButton.setVisibility(View.VISIBLE);
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
                GenerateKristikActivity.this.finish();
            }

            @Override
            public void onNegative() {
                confirmationDialog.dismiss();
            }
        });

        confirmationDialog.show();

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
