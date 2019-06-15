package com.ditenun.appditenun.function.activity;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ditenun.appditenun.R;
import com.ditenun.appditenun.dependency.App;
import com.ditenun.appditenun.dependency.models.StockImage;
import com.ditenun.appditenun.function.adapter.ViewPagerAdapter;
import com.ditenun.appditenun.function.fragment.MyMotifFragment;
import com.ditenun.appditenun.function.fragment.NationalMotifFragment;
import com.ditenun.appditenun.function.util.BitmapUtils;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class HomeActivity extends AppCompatActivity  {

    private static final int REQUEST_ID_IMAGE_CAPTURE = 100;
    private static final int REQUEST_ID_PICK_GALERY = 101;
    private static final int TIME_INTERVAL = 2000;

    @Inject
    Realm realm;

    @BindView(R.id.main_tab_layout)
    TabLayout mainTabLayout;

    @BindView(R.id.main_view_pager)
    ViewPager mainViewPager;

    @BindView(R.id.linearLayoutCamera)
    LinearLayout linearLayoutCamera;

    @BindView(R.id.linearLayoutTambahFoto)
    LinearLayout linearLayoutTambahFoto;

    private long backButtonPressTime;

    private DrawerLayout dl;
    private NavigationView nv;

    Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dl = (DrawerLayout)findViewById(R.id.container);
        nv = (NavigationView)findViewById(R.id.navigation_view);
        logoutBtn = (Button) findViewById(R.id.logoutBtn);

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
//                    case R.id.account:
//                        Toast.makeText(HomeActivity.this, "My Account",Toast.LENGTH_SHORT).show();
//                        break;
//                    case R.id.settings:
//                        Toast.makeText(HomeActivity.this, "Settings",Toast.LENGTH_SHORT).show();
//                        break;
                    case R.id.feedback:
                        startFeedbackActivity();
                        break;
                    case R.id.faq:
                        startFaqActivity();
                        break;
                    case R.id.privacy:
                        Uri uri = Uri.parse( "https://sites.google.com/view/ditenun-privacy/home" );
                        startActivity( new Intent( Intent.ACTION_VIEW, uri ) );
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences("login", MODE_PRIVATE).edit().putBoolean("logged", false).apply();
                startLoginActivity();
            }
        });

        App.get(this).getInjector().inject(this);

        ButterKnife.bind(this);

        setupTab();

        registerListener();
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }

    private void setupTab() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.add(new NationalMotifFragment(), getResources().getString(R.string.national_motif));
        adapter.add(new MyMotifFragment(), getResources().getString(R.string.my_motif));

        mainViewPager.setAdapter(adapter);

        mainTabLayout.setupWithViewPager(mainViewPager);
    }

    private void registerListener() {
        linearLayoutCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCameraActivity();
            }
        });

        linearLayoutTambahFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startImageChooserActivity();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (backButtonPressTime + TIME_INTERVAL > System.currentTimeMillis()) {
            moveTaskToBack(true);
            finish();
        } else {
            Toast.makeText(getBaseContext(), getResources().getString(R.string.press_once_more_to_exit), Toast.LENGTH_SHORT).show();
        }
        backButtonPressTime = System.currentTimeMillis();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ID_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                saveStockImage(imageBitmap);
            }
        } else if (requestCode == REQUEST_ID_PICK_GALERY && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                Uri imageUri = data.getData();

                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    saveStockImage(imageBitmap);
                } catch (IOException e) {
                    showErrorMessage(e.getMessage());
                }
            } else if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                for (int i = 0; i < mClipData.getItemCount(); i++) {
                    ClipData.Item item = mClipData.getItemAt(i);

                    Uri imageUri = item.getUri();

                    try {
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        saveStockImage(imageBitmap);
                    } catch (IOException e) {
                        showErrorMessage(e.getMessage());
                    }
                }
            }
        }
    }

    private void saveStockImage(Bitmap bitmap) {
        Number lastId = realm.where(StockImage.class).max("id");
        int nextId = (lastId == null) ? 1 : lastId.intValue() + 1;

        realm.beginTransaction();
        StockImage stockImage = realm.createObject(StockImage.class, nextId);
        stockImage.setBytes(BitmapUtils.convertToBytes(bitmap));
        stockImage.setName("StockImage_" + stockImage.getId());
        realm.insertOrUpdate(stockImage);
        realm.commitTransaction();
    }

    private void startCameraActivity() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(intent, REQUEST_ID_IMAGE_CAPTURE);
    }

    private void startImageChooserActivity() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_image)), REQUEST_ID_PICK_GALERY);
    }

    private void showErrorMessage(String message) {
        Toast.makeText(this, getResources().getString(R.string.error) + ": " + message, Toast.LENGTH_LONG).show();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(intent);

        finish();
    }

    private void startFeedbackActivity() {
        Intent intent = new Intent(getApplicationContext(), FeedbackActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(intent);

        finish();
    }

    private void startFaqActivity() {
        Intent intent = new Intent(getApplicationContext(), FaqActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(intent);

        finish();
    }
}
