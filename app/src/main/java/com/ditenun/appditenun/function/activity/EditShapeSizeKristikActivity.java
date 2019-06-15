package com.ditenun.appditenun.function.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ditenun.appditenun.R;
import com.ditenun.appditenun.dependency.App;
import com.ditenun.appditenun.dependency.models.Kristik;
import com.ditenun.appditenun.dependency.models.Motif;
import com.ditenun.appditenun.dependency.network.TenunNetworkInterface;
import com.ditenun.appditenun.function.dialog.ConfirmationDialog;
import com.ditenun.appditenun.function.dialog.OnConfirmedListener;
import com.ditenun.appditenun.function.util.BitmapUtils;
import com.ditenun.appditenun.function.util.FileUtils;
import com.ditenun.appditenun.function.util.IntentParams;
import com.google.android.material.tabs.TabLayout;
import com.theartofdev.edmodo.cropper.CropImageView;

import javax.inject.Inject;

public class EditShapeSizeKristikActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mainToolbar;

    @BindView(R.id.save_button)
    Button saveButton;

    @BindView(R.id.home_button)
    Button homeButton;

    @BindView(R.id.action_tab_container)
    ViewGroup actionTabContainer;

    @BindView(R.id.action_tab_layout)
    TabLayout actionTabLayout;

    @BindView(R.id.crop_image_view)
    CropImageView cropImageView;

    @BindView(R.id.crop_button)
    Button cropButton;

    @BindView(R.id.left_rotate_button)
    Button leftRotateButton;

    @BindView(R.id.right_rotate_button)
    Button rightRotateButton;

    @BindView(R.id.vertical_flip_button)
    Button verticalFlipButton;

    @BindView(R.id.horizontal_flip_button)
    Button horizontalFlipButton;

    @Inject
    Realm realm;
    @Inject
    TenunNetworkInterface tenunNetworkInterface;

    private Bitmap kristikBitmap;
    private Kristik kristikModel;
    private boolean isRequestResult;
    private boolean isChanged;

    private int currentTabId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shape_size_kristik);

        App.get(this).getInjector().inject(this);

        ButterKnife.bind(this);

        readIntentParams();

        setupToolbar();

        setupInitialDisplay();

        registerListener();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void readIntentParams() {
        Intent intent = getIntent();
        boolean needDeleted = intent.getBooleanExtra(IntentParams.DELETE_IMAGE_AFTER_READ, false);
        String imagePath = intent.getStringExtra(IntentParams.IMAGE_PATH);
        int kristikId = intent.getIntExtra(IntentParams.KRISTIK_ID, 0);

        isRequestResult = intent.getBooleanExtra(IntentParams.REQUEST_RESULT, false);

        if (imagePath != null) {
            byte[] kristikBytes = FileUtils.LoadImageFile(intent.getStringExtra(IntentParams.IMAGE_PATH), needDeleted);
            kristikBitmap = BitmapFactory.decodeByteArray(kristikBytes, 0, kristikBytes.length);
        } else if (kristikId != 0) {
            kristikModel = realm.where(Kristik.class).equalTo("id", kristikId).findFirst();
            kristikBitmap = BitmapFactory.decodeByteArray(kristikModel.getBytes(), 0, kristikModel.getBytes().length);
        }

    }

    private void setupToolbar() {
        setSupportActionBar(mainToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setupInitialDisplay() {
        if (isRequestResult) {
            saveButton.setText(R.string.ok);
            mainToolbar.setTitle(R.string.edit_kristik);
        } else {
            saveButton.setText(R.string.save);
//            mainToolbar.setTitle(getResources().getString(R.string.edit) + " " + kristikModel.getName());
            mainToolbar.setTitle(getResources().getString(R.string.edit));
        }

        showPreviewImage();

        refreshTab();
    }

    private void showPreviewImage() {
        cropImageView.setImageBitmap(kristikBitmap);
    }

    private void registerListener() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRequestResult) {
                    sendIntentOkResult();
                } else {
                    saveKristik();
                }
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRequestResult && isChanged) {
                    homeConfirmationDialog();
                }
                else {
                    startActivity(HomeActivity.createIntent(getApplicationContext()));
                }
            }
        });

        actionTabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTabId = tab.getPosition();
                refreshTab();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performCrop();
            }
        });

        verticalFlipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFlipVertical();
            }
        });

        horizontalFlipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFlipHorizontal();
            }
        });

        rightRotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRotateRight();
            }
        });

        leftRotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRotateLeft();
            }
        });
    }


    private void performCrop() {
        isChanged = true;
        kristikBitmap = cropImageView.getCroppedImage();
        cropImageView.resetCropRect();

        showPreviewImage();
    }

    private void performFlipHorizontal() {
        isChanged = true;
        cropImageView.resetCropRect();
        cropImageView.setFlippedHorizontally(!cropImageView.isFlippedHorizontally());
        kristikBitmap = cropImageView.getCroppedImage();
        showPreviewImage();
    }

    private void performFlipVertical() {
        isChanged = true;
        cropImageView.resetCropRect();
        cropImageView.setFlippedVertically(!cropImageView.isFlippedVertically());
        kristikBitmap = cropImageView.getCroppedImage();
        showPreviewImage();
    }

    private void performRotateRight() {
        isChanged = true;
        cropImageView.resetCropRect();
        cropImageView.rotateImage(90);
        kristikBitmap = cropImageView.getCroppedImage();
        showPreviewImage();
    }

    private void performRotateLeft() {
        isChanged = true;
        cropImageView.resetCropRect();
        cropImageView.rotateImage(-90);
        kristikBitmap = cropImageView.getCroppedImage();
        showPreviewImage();
    }

    private void sendIntentOkResult() {
        Intent intent = new Intent();

        String path = FileUtils.CreateTempFile("Kristik", null, getCacheDir(), BitmapUtils.convertToBytes(kristikBitmap));
        intent.putExtra(IntentParams.IMAGE_PATH, path);
        intent.putExtra(IntentParams.DELETE_IMAGE_AFTER_READ, true);

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void sendIntentCencelResult() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    private void saveKristik() {
        realm.beginTransaction();
        kristikModel.setBytes(BitmapUtils.convertToBytes(kristikBitmap));
        realm.insertOrUpdate(kristikModel);
        realm.commitTransaction();

        isChanged = false;

        Toast.makeText(getApplicationContext(), "Perubahan disimpan.", Toast.LENGTH_LONG).show();
    }

    private void refreshTab() {
        for (int i = 0; i < actionTabContainer.getChildCount(); i++) {
            actionTabContainer.getChildAt(i).setVisibility(i == currentTabId ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (!isRequestResult && isChanged) {
            openExitConfirmationDialog();
        }
        else {
            sendIntentCencelResult();
        }
    }

    private void openExitConfirmationDialog() {
        ConfirmationDialog exitConfirmationDialog = new ConfirmationDialog(this, getString(R.string.change_not_applied_apply), "Ya", "Tidak");

        exitConfirmationDialog.setListener(new OnConfirmedListener() {
            @Override
            public void onPostive() {
                saveKristik();
                finish();
            }

            @Override
            public void onNegative() {
                exitConfirmationDialog.dismiss();
                finish();
            }
        });

        exitConfirmationDialog.show();
    }

    private void homeConfirmationDialog() {
        ConfirmationDialog exitConfirmationDialog = new ConfirmationDialog(this, getString(R.string.change_not_applied_apply), "Ya", "Tidak");

        exitConfirmationDialog.setListener(new OnConfirmedListener() {
            @Override
            public void onPostive() {
                saveKristik();
                startActivity(HomeActivity.createIntent(getApplicationContext()));
                finish();
            }

            @Override
            public void onNegative() {
                exitConfirmationDialog.dismiss();
                startActivity(HomeActivity.createIntent(getApplicationContext()));
                finish();
            }
        });

        exitConfirmationDialog.show();
    }
}
