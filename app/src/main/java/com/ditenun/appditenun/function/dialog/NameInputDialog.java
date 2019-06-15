package com.ditenun.appditenun.function.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.ditenun.appditenun.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NameInputDialog extends Dialog {

    @BindView(R.id.name_edit_text)
    EditText nameEditText;

    @BindView(R.id.positive_button)
    Button postiveButton;

    @BindView(R.id.negative_button)
    Button negativeButton;

    private String message;
    private String positiveLabel;
    private String negativeLabel;
    private OnNameInputtedListener listener;

    public NameInputDialog(@NonNull Context context, String message, String positiveLabel, String negativeLabel) {
        this(context, message, positiveLabel, negativeLabel, null);
    }

    public NameInputDialog(@NonNull Context context, String message, String positiveLabel, String negativeLabel, OnNameInputtedListener listener) {
        super(context);
        this.message = message;
        this.positiveLabel = positiveLabel;
        this.negativeLabel = negativeLabel;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_name_input);

        ButterKnife.bind(this);

        initView();

        registerListener();
    }

    private void registerListener() {
        postiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onPositive(nameEditText.getText().toString());
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onNegative();
            }
        });
    }

    private void initView() {
        nameEditText.setHint(message);
        postiveButton.setText(positiveLabel);
        negativeButton.setText(negativeLabel);
    }

    public void setListener(OnNameInputtedListener listener) {
        this.listener = listener;
    }
}
