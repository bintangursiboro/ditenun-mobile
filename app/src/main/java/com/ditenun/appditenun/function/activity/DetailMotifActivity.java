package com.ditenun.appditenun.function.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;

import com.ditenun.appditenun.function.dialog.ConfirmationDialog;
import com.ditenun.appditenun.function.dialog.OnConfirmedListener;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.ditenun.appditenun.R;
import com.ditenun.appditenun.dependency.App;
import com.ditenun.appditenun.dependency.models.Motif;
import com.ditenun.appditenun.function.util.FileUtils;
import com.ditenun.appditenun.function.util.IntentParams;

import java.io.File;
import java.io.FileOutputStream;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class DetailMotifActivity extends AppCompatActivity {

    private static final int CHECK_PERMISSION_WHEN_EXPORT = 10;
    private static final int EDIT_BUTTON_POSITION_ID = 0;
    private static final int VIEW_DUPLICATION_BUTTON_POSITION_ID = 1;
    private static final int GENERATE_KRISTIK_BUTTON_POSITION_ID = 2;

    @BindView(R.id.toolbar)
    Toolbar mainToolbar;

    @BindView(R.id.export_button)
    Button exportButton;

    @BindView(R.id.home_button)
    Button homeButton;

    @BindView(R.id.main_image_view)
    ImageView mainImageView;

    @BindView(R.id.action_tab_layout)
    TabLayout actionTabLayout;

    @Inject
    Realm realm;

    private int motifId;
    private Motif motifModel;
    private Bitmap motifBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_motif);

        App.get(this).getInjector().inject(this);

        ButterKnife.bind(this);

        readIntentParams();

        setupToolbar();

        setupDisplay();

        registerListener();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadMotif();
        setupDisplay();
    }

    private void readIntentParams() {
        Intent intent = getIntent();
        motifId = intent.getIntExtra(IntentParams.MOTIF_ID, 0);

        loadMotif();
    }

    private void loadMotif() {
        motifModel = realm.where(Motif.class).equalTo("id", motifId).findFirst();
        motifBitmap = BitmapFactory.decodeByteArray(motifModel.getBytes(), 0, motifModel.getBytes().length);
    }

    private void setupToolbar() {
        setSupportActionBar(mainToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setupDisplay() {
        mainToolbar.setTitle(motifModel.getName());
        mainImageView.setImageBitmap(motifBitmap);
    }

    private void registerListener() {
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openExportConfirmationDialog();

            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(HomeActivity.createIntent(getApplicationContext()));
            }
        });

        actionTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                prformAction(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                prformAction(tab.getPosition());
            }
        });

    }

    private void prformAction(int buttonId) {
        switch (buttonId) {
            case EDIT_BUTTON_POSITION_ID:
                startEditMotifActivity();
                break;
            case VIEW_DUPLICATION_BUTTON_POSITION_ID:
                startDuplicationPreviewActivity();
                break;
            case GENERATE_KRISTIK_BUTTON_POSITION_ID:
                startGenerateKristikActivity();
                break;
        }
    }

    private void startEditMotifActivity() {
        Intent intent = new Intent(getApplicationContext(), EditMotifActivity.class);

        intent.putExtra(IntentParams.MOTIF_ID, motifId);

        startActivity(intent);
    }

    private void startGenerateKristikActivity() {
        Intent intent = new Intent(this, GenerateKristikActivity.class);

        String path = FileUtils.CreateTempFile("Motif", null, getCacheDir(), motifModel.getBytes());

        intent.putExtra(IntentParams.IMAGE_PATH, path);
        intent.putExtra(IntentParams.DELETE_IMAGE_AFTER_READ, true);

        startActivity(intent);
    }

    private void startDuplicationPreviewActivity() {
        Intent intent = new Intent(this, DuplicationPreviewActivity.class);

        String path = FileUtils.CreateTempFile("Motif", null, getCacheDir(), motifModel.getBytes());

        intent.putExtra(IntentParams.IMAGE_PATH, path);
        intent.putExtra(IntentParams.DELETE_IMAGE_AFTER_READ, true);

        startActivity(intent);
    }

    private void exportMotifAsImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                exportMotif();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CHECK_PERMISSION_WHEN_EXPORT);
            }
        } else {
            exportMotif();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == CHECK_PERMISSION_WHEN_EXPORT) {
            exportMotif();
        }
    }

    private void exportMotif() {
        MediaStore.Images.Media.insertImage(getContentResolver(), motifBitmap, "Motif", "Gambar motif yang buat dengan aplikasi DiTenun");

        String motifName = Integer.toString(motifId);
        createDirectoryAndSaveFile(motifBitmap, motifName);

        Toast.makeText(this, "Gambar telah disimpan ke galeri.", Toast.LENGTH_LONG).show();
    }

    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

        File direct = new File(Environment.getExternalStorageDirectory() + "/Ditenun");

        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/Ditenun/");
            wallpaperDirectory.mkdirs();
        }

        File file = new File(new File("/sdcard/Ditenun/"), fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openExportConfirmationDialog() {
        ConfirmationDialog exportConfirmationDialog = new ConfirmationDialog(this, getString(R.string.save_to_galery), "Ya", "Tidak");

        exportConfirmationDialog.setListener(new OnConfirmedListener() {
            @Override
            public void onPostive() {
                exportMotifAsImage();
                finish();
            }

            @Override
            public void onNegative() {
                exportConfirmationDialog.dismiss();
                finish();
            }
        });

        exportConfirmationDialog.show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
