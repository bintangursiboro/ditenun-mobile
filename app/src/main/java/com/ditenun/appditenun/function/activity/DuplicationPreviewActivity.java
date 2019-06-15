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
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ditenun.appditenun.R;
import com.ditenun.appditenun.dependency.App;
import com.ditenun.appditenun.function.dialog.ConfirmationDialog;
import com.ditenun.appditenun.function.dialog.OnConfirmedListener;
import com.ditenun.appditenun.function.util.IntentParams;
import com.ditenun.appditenun.function.view.DuplicateView;
import com.ditenun.appditenun.function.util.FileUtils;
import com.ditenun.appditenun.function.util.motion.OnSwipeTouchListener;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DuplicationPreviewActivity extends AppCompatActivity {

    final static int CHECK_PERMISSION_WHEN_EXPORT = 1;

    @BindView(R.id.container)
    public View rootContainer;

    @BindView(R.id.toolbar)
    Toolbar mainToolbar;

    @BindView(R.id.export_button)
    Button exportButton;

    @BindView(R.id.home_button)
    Button homeButton;

    @BindView(R.id.main_grid_layout)
    public DuplicateView mainDuplicateView;

    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duplication_preview);

        App.get(getApplicationContext()).getInjector().inject(this);

        ButterKnife.bind(this);

        if (readIntentParams()) {
            showImage();
        } else {
            Toast.makeText(getApplicationContext(), "Tidak dapat membaca gambar!", Toast.LENGTH_LONG).show();
        }

        setupToolbar();

        registerListener();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setupToolbar() {
        mainToolbar.setTitle(R.string.view_duplication);
        setSupportActionBar(mainToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private boolean readIntentParams() {
        Intent intent = getIntent();
        String path = intent.getStringExtra(IntentParams.IMAGE_PATH);
        boolean needDeleted = intent.getBooleanExtra(IntentParams.DELETE_IMAGE_AFTER_READ, false);

        if (path != null && loadImageFromPath(path, needDeleted)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean loadImageFromPath(String path, boolean needDeleted) {
        byte[] imageBytes = FileUtils.LoadImageFile(path, needDeleted);

        if (imageBytes != null) {
            imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            return true;
        } else {
            return false;
        }
    }

    private void registerListener() {
        mainDuplicateView.setClickable(false);

        rootContainer.setOnTouchListener(new OnSwipeTouchListener() {
            @Override
            public boolean onSwipeRight() {
                mainDuplicateView.IncreaseColumn();
                return true;
            }

            @Override
            public boolean onSwipeLeft() {
                mainDuplicateView.DecreaseColumn();
                return true;
            }

            @Override
            public boolean onSwipeTop() {
                mainDuplicateView.IncreaseRow();
                return true;
            }

            @Override
            public boolean onSwipeBottom() {
                mainDuplicateView.DecreaseRow();
                return true;
            }
        });

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
    }

    private void showImage() {
        mainDuplicateView.setImageBitmap(imageBitmap);
    }

    private void exportKristikAsImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                exportDuplication();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CHECK_PERMISSION_WHEN_EXPORT);
            }
        } else {
            exportDuplication();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == CHECK_PERMISSION_WHEN_EXPORT) {
            exportDuplication();
        }
    }

    private void exportDuplication() {
        MediaStore.Images.Media.insertImage(getContentResolver(), mainDuplicateView.viewToBitmap(mainDuplicateView), "Duplikasi", "Gambar duplikasi yang buat dengan aplikasi DiTenun");

        Toast.makeText(this, "Gambar telah disimpan ke galeri.", Toast.LENGTH_LONG).show();
    }

    private void openExportConfirmationDialog() {
        ConfirmationDialog exportConfirmationDialog = new ConfirmationDialog(this, getString(R.string.save_to_galery), "Ya", "Tidak");

        exportConfirmationDialog.setListener(new OnConfirmedListener() {
            @Override
            public void onPostive() {
                exportKristikAsImage();
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

    @Override
    public void onBackPressed() {
        finish();
    }
}
