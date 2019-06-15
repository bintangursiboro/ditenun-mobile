package com.ditenun.appditenun.function.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.ditenun.appditenun.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfirmationDialog extends Dialog {

    @BindView(R.id.message_text)
    public TextView messageText;

    @BindView(R.id.positive_button)
    public Button postiveButton;

    @BindView(R.id.negative_button)
    public Button negativeButton;

    private String message;
    private String positiveLabel;
    private String negativeLabel;
    private OnConfirmedListener listener;

    public ConfirmationDialog(@NonNull Context context, String message, String positiveLabel, String negativeLabel) {
        this(context, message, positiveLabel, negativeLabel, null);
    }

    public ConfirmationDialog(@NonNull Context context, String message, String positiveLabel, String negativeLabel, OnConfirmedListener listener) {
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
        setContentView(R.layout.dialog_confirmation);

        ButterKnife.bind(this);

        initView();

        registerListener();
    }

    private void registerListener() {
        postiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onPostive();
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
        messageText.setText(message);
        postiveButton.setText(positiveLabel);
        negativeButton.setText(negativeLabel);
    }

    public void setListener(OnConfirmedListener listener) {
        this.listener = listener;
    }
}
