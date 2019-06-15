package com.ditenun.appditenun.function.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.ditenun.appditenun.R;
import com.ditenun.appditenun.dependency.App;
import com.ditenun.appditenun.dependency.models.Kristik;
import com.ditenun.appditenun.function.util.BitmapUtils;
import com.ditenun.appditenun.function.util.FileUtils;
import com.ditenun.appditenun.function.util.IntentParams;
import com.ditenun.appditenun.function.view.KristikDrawable;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class DetailKristikActivity extends AppCompatActivity {

    final static int CHECK_PERMISSION_WHEN_EXPORT = 1;

    @BindView(R.id.main_kristik_view)
    public ImageView mainKristikView;

    @BindView(R.id.edit_button)
    public Button editButton;

    @BindView(R.id.edit_warna_button)
    public Button editColorButton;

    @BindView(R.id.duplicate_kristik)
    public Button duplicateKristikButton;

    @Inject
    Realm realm;

    private int kristikId;
    private Kristik kristikModel;
    private byte[] kristikBytes;
    private Bitmap kristikBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_kristik);

        App.get(getApplicationContext()).getInjector().inject(this);

        ButterKnife.bind(this);

        registerListener();

        if (readIntentParams() && loadKristik()) {
            showKristik();
        } else {
            Toast.makeText(getApplicationContext(), "Tidak dapat membaca gambar kristik!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshKristik();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_kristik_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.export_image_menu:
                exportKristikAsImage();
                return true;
            case R.id.delete_menu:
                deleteKristik();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean readIntentParams() {
        Intent intent = getIntent();
        kristikId = intent.getIntExtra(IntentParams.KRISTIK_ID, 0);

        return kristikId != 0;
    }

    private void refreshKristik() {
        if (loadKristik()) {
            showKristik();
        } else {
            Toast.makeText(getApplicationContext(), "Tidak dapat membaca gambar kristik!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean loadKristik() {
        kristikModel = realm.where(Kristik.class).equalTo("id", kristikId).findFirst();
        if (kristikModel == null) return false;

        this.kristikBytes = kristikModel.getBytes();
        if (kristikBytes == null) return false;

        kristikBitmap = BitmapFactory.decodeByteArray(kristikBytes, 0, kristikBytes.length);
        //kristikBitmap = BitmapUtils.drawableToBitmap(new KristikDrawable(dataKristikBitmap));

        if (kristikBitmap == null) return false;

        return true;
    }

    private void showKristik() {
        mainKristikView.setImageBitmap(kristikBitmap);
    }

    private void registerListener() {
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditShapeKristikActivity();
            }
        });

        editColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditKristikActivity();
            }
        });

        duplicateKristikButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDuplicationPreviewActivity();
            }
        });
    }

    private void startDuplicationPreviewActivity() {
        Intent intent = new Intent(this, DuplicationPreviewActivity.class);

        String path = FileUtils.CreateTempFile("Motif", null, getCacheDir(), kristikModel.getBytes());

        intent.putExtra(IntentParams.IMAGE_PATH, path);
        intent.putExtra(IntentParams.DELETE_IMAGE_AFTER_READ, true);

        startActivity(intent);
    }

    private void startEditKristikActivity() {
        Intent intent = new Intent(this, EditKristikActivity.class);

        intent.putExtra(IntentParams.KRISTIK_ID, kristikId);

        startActivity(intent);
    }

    private void startEditShapeKristikActivity() {
        Intent intent = new Intent(this, EditShapeSizeKristikActivity.class);

        intent.putExtra(IntentParams.KRISTIK_ID, kristikId);

        startActivity(intent);
    }

    private void exportKristikAsImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                exportKristik();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CHECK_PERMISSION_WHEN_EXPORT);
            }
        } else {
            exportKristik();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == CHECK_PERMISSION_WHEN_EXPORT) {
            exportKristik();
        }
    }

    private void deleteKristik() {
        realm.beginTransaction();

        kristikModel.deleteFromRealm();

        realm.commitTransaction();

        finish();
    }

    private void exportKristik() {
        MediaStore.Images.Media.insertImage(getContentResolver(), kristikBitmap, "Kristik", "Gambar kristik yang buat dengan aplikasi DiTenun");

        Toast.makeText(this, "Gambar telah disimpan ke galeri.", Toast.LENGTH_LONG).show();
    }
}
