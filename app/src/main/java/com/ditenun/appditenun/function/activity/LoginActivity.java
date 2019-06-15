package com.ditenun.appditenun.function.activity;

import android.content.Intent;

import com.ditenun.appditenun.dependency.models.ResponseGetUser;
import com.ditenun.appditenun.dependency.modules.APIModule;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ditenun.appditenun.R;
import com.ditenun.appditenun.dependency.App;
import com.ditenun.appditenun.dependency.network.TenunNetworkInterface;

import javax.inject.Inject;

import butterknife.BindView;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail;
    EditText edtPassword;
    Button btnLogin;
    Button btnGoToRegister;

    @BindView(R.id.container)
    RelativeLayout rootContainer;

    @Inject
    Realm realm;

    @Inject
    TenunNetworkInterface tenunNetworkInterface;

    public SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        App.get(this).getInjector().inject(this);

        edtEmail = (EditText) findViewById(R.id.email);
        edtPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.login);
        btnGoToRegister = (Button) findViewById(R.id.gotoRegister);

        sp = getSharedPreferences("login", MODE_PRIVATE);

        if(sp.getBoolean("logged",true)){
            startHomeActivity();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                //validate form
                if(validateLogin(email, password)){
                    //do login
                    doLogin(email, password);
                }
            }
        });

        btnGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegisterActivity();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private boolean validateLogin(String email, String password){
        if(email == null || email.trim().length() == 0){
            Toast.makeText(this, "Masukkan email atau nomor hp anda", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password == null || password.trim().length() == 0){
            Toast.makeText(this, "Masukkan password anda", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void doLogin(String email, String password){
        tenunNetworkInterface.login(APIModule.ACCESS_TOKEN_TEMP, email, password).enqueue(new Callback<ResponseGetUser>() {
            @Override
            public void onResponse(Call<ResponseGetUser> call, Response<ResponseGetUser> response) {
                if (response.isSuccessful()) {
                    realm.beginTransaction();
                    realm.executeTransactionAsync(realm10 -> realm10.insertOrUpdate(response.body().getData()));
                    realm.commitTransaction();

                    startHomeActivity();
                    sp.edit().putBoolean("logged",true).apply();
                } else {
                    wrongAccount();
                }
            }

            @Override
            public void onFailure(Call<ResponseGetUser> call, Throwable t) {
                Log.e("On Failure", t.getMessage());
                failResponse();
            }
        });
    }

    private boolean failResponse(){
        Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
        return false;
    }

    private boolean wrongAccount(){
        Toast.makeText(this, "Akun tidak terdaftar", Toast.LENGTH_SHORT).show();
        return false;
    }

    private void startHomeActivity() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(intent);

        finish();
    }

    private void startRegisterActivity() {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(intent);

        finish();
    }
}
