package com.ditenun.appditenun.function.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ditenun.appditenun.R;
import com.ditenun.appditenun.dependency.App;
import com.ditenun.appditenun.dependency.models.Kristik;
import com.ditenun.appditenun.function.dialog.ConfirmationDialog;
import com.ditenun.appditenun.function.dialog.OnConfirmedListener;
import com.ditenun.appditenun.function.util.BitmapUtils;
import com.ditenun.appditenun.function.util.FileUtils;
import com.ditenun.appditenun.function.util.IntentParams;
import com.ditenun.appditenun.function.view.KristikCell;
import com.ditenun.appditenun.function.view.KristikView;
import com.ditenun.appditenun.function.view.OnKristikCellSelectedListener;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import yuku.ambilwarna.AmbilWarnaDialog;

public class EditKristikActivity extends AppCompatActivity implements ColorPickerDialogListener {

    @BindView(R.id.toolbar)
    Toolbar mainToolbar;

    @BindView(R.id.save_button)
    Button saveButton;

    @BindView(R.id.buttonHome)
    Button buttonHome;

    @BindView(R.id.linear_layout_toggle_button)
    LinearLayout linearLayoutToggleButton;

    @BindView(R.id.main_kristik_view)
    public KristikView mainKristikView;

    @BindView(R.id.color_button)
    public Button colorButton;

    @BindView(R.id.textViewChooseColor)
    public TextView textViewChooseColor;

    @BindView(R.id.linearLayoutChooseColor)
    public LinearLayout linearLayoutChooseColor;

    @BindView(R.id.multi_selector_button)
    public ToggleButton multiSelectorButton;

    @BindView(R.id.magic_selector_button)
    public ToggleButton magicSelectorButton;

    @Inject
    Realm realm;

    private Bitmap kristikBitmap;
    private Kristik kristikModel;
    private KristikCell[] selectedCells = new KristikCell[0];

    private boolean isRequestResult;
    private boolean isKristikDitry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_kristik);

        App.get(getApplicationContext()).getInjector().inject(this);

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

    @Override
    public void onBackPressed() {
        if (!isRequestResult && isKristikDitry) {
            openExitConfirmationDialog();
        } else {
            sendIntentCencelResult();
        }
    }

    private void readIntentParams() {
        Intent intent = getIntent();

        boolean needDeleted = intent.getBooleanExtra(IntentParams.DELETE_IMAGE_AFTER_READ, false);
        String imagePath = intent.getStringExtra(IntentParams.IMAGE_PATH);
        int motifId = intent.getIntExtra(IntentParams.KRISTIK_ID, 0);

        isRequestResult = intent.getBooleanExtra(IntentParams.REQUEST_RESULT, false);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;

        if (imagePath != null) {
            byte[] motifBytes = FileUtils.LoadImageFile(intent.getStringExtra(IntentParams.IMAGE_PATH), needDeleted);
            kristikBitmap = BitmapFactory.decodeByteArray(motifBytes, 0, motifBytes.length, options);
        } else if (motifId != 0) {
            kristikModel = realm.where(Kristik.class).equalTo("id", motifId).findFirst();
            kristikBitmap = BitmapFactory.decodeByteArray(kristikModel.getBytes(), 0, kristikModel.getBytes().length, options);
        }else{
            kristikBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.bitmap, options);
            kristikBitmap = Bitmap.createScaledBitmap(kristikBitmap, 50, 40, false);
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

        colorButton.setVisibility(View.INVISIBLE);
        textViewChooseColor.setVisibility(View.GONE);

        showKristik();
    }

    private void showKristik() {
        mainKristikView.setImageBitmap(kristikBitmap);
    }

    private void registerListener() {

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openSaveConfirmationDialog();
            }
        });

        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRequestResult && isKristikDitry) {
                    openHomeConfirmationDialog();
                } else {
                    startActivity(HomeActivity.createIntent(getApplicationContext()));
                    finish();
                }
            }
        });


        mainKristikView.setSelectionListener(new OnKristikCellSelectedListener() {
            @Override
            public void OnSelectionChanged(KristikCell[] cells) {
                selectedCells = cells;
                if (cells.length > 0) {
                    ((GradientDrawable)colorButton.getBackground()).setColor(cells[cells.length - 1].getColor());
                    colorButton.setVisibility(View.VISIBLE);
                    textViewChooseColor.setVisibility(View.VISIBLE);
                } else {
                    colorButton.setVisibility(View.INVISIBLE);
                    textViewChooseColor.setVisibility(View.INVISIBLE);
                }
            }
        });

        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCells.length > 0) {
                    openSelectColorDialog();
                }
            }
        });

        multiSelectorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (multiSelectorButton.isChecked()) {
                    magicSelectorButton.setChecked(false);
                    mainKristikView.setSelectionMode(KristikView.SelectionMode.MULTI);
                    mainKristikView.deselect();
                } else if(mainKristikView.getSelectionMode() == KristikView.SelectionMode.MULTI) {
                    mainKristikView.setSelectionMode(KristikView.SelectionMode.NONE);
                    mainKristikView.deselect();
                }
            }
        });

        magicSelectorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (magicSelectorButton.isChecked()) {
                    multiSelectorButton.setChecked(false);
                    mainKristikView.setSelectionMode(KristikView.SelectionMode.MAGIC);
                    mainKristikView.deselect();
                } else if(mainKristikView.getSelectionMode() == KristikView.SelectionMode.MAGIC) {
                    mainKristikView.setSelectionMode(KristikView.SelectionMode.NONE);
                    mainKristikView.deselect();
                }
            }
        });
    }

    private void sendIntentOkResult() {
        Intent intent = new Intent();

        String path = FileUtils.CreateTempFile("Motif", null, getCacheDir(), BitmapUtils.convertToBytes(kristikBitmap));
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

    private void openSelectColorDialog() {
        int lastSelectedColor = selectedCells[selectedCells.length-1].getColor();
        ColorPickerDialog.newBuilder().setColor(lastSelectedColor).show(this);
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        for (int i = 0; i < selectedCells.length; i++) {
            KristikCell cell = selectedCells[i];
            cell.setBackgroundColor(color);
            // Note : Column == X And Row == Y
            kristikBitmap.setPixel(cell.getColumn(), cell.getRow(), color);
            ((GradientDrawable)colorButton.getBackground()).setColor(color);
        }

        isKristikDitry = true;
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    private void saveKristik() {
        byte[] bytes = BitmapUtils.convertToBytes(kristikBitmap);
        if(kristikModel!=null){
            realm.beginTransaction();
            kristikModel.setBytes(bytes);
            realm.insertOrUpdate(kristikModel);
            realm.commitTransaction();
        }else createNewKristikModel(bytes);

        isKristikDitry = false;
    }

    private void createNewKristikModel(byte[] bytes){
        realm.beginTransaction();
        Number maxId = realm.where(Kristik.class).max("id");
        int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;
        kristikModel= realm.createObject(Kristik.class, nextId);
        kristikModel.setBytes(bytes);
        kristikModel.setName("Kristik-"+nextId);
        realm.commitTransaction();
    }

    private void openExitConfirmationDialog() {
        ConfirmationDialog exitConfirmationDialog = new ConfirmationDialog(this, "Simpan perubahan?", "Ya", "Tidak");

        exitConfirmationDialog.setListener(new OnConfirmedListener() {
            @Override
            public void onPostive() {
                saveKristik();
                exitConfirmationDialog.dismiss();
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

    private void openSaveConfirmationDialog() {
        ConfirmationDialog saveConfirmationDialog = new ConfirmationDialog(this, "Simpan perubahan?", "Ya", "Tidak");

        saveConfirmationDialog.setListener(new OnConfirmedListener() {
            @Override
            public void onPostive() {
                if (isRequestResult) {
                    sendIntentOkResult();
                } else {
                    saveKristik();
                }
                finish();
//                saveKristik();
//                saveConfirmationDialog.dismiss();

            }

            @Override
            public void onNegative() {
                saveConfirmationDialog.dismiss();
                finish();
            }
        });

        saveConfirmationDialog.show();
    }

    private void openHomeConfirmationDialog() {
        ConfirmationDialog exitConfirmationDialog = new ConfirmationDialog(this, "Simpan perubahan?", "Ya", "Tidak");

        exitConfirmationDialog.setListener(new OnConfirmedListener() {
            @Override
            public void onPostive() {
                saveKristik();
                exitConfirmationDialog.dismiss();
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
