package com.ditenun.appditenun.function.activity;

import android.content.Intent;

import com.ditenun.appditenun.dependency.models.ResponseGetUser;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.ditenun.appditenun.R;
import com.ditenun.appditenun.dependency.App;
import com.ditenun.appditenun.dependency.modules.APIModule;
import com.ditenun.appditenun.dependency.network.TenunNetworkInterface;

import javax.inject.Inject;

import butterknife.BindView;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText nameReg;
    EditText emailReg;
    EditText passwordReg;
    EditText phoneReg;
    EditText addressReg;
    Button btnRegister;

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
        setContentView(R.layout.activity_register);

        App.get(this).getInjector().inject(this);

        Spinner spinner = (Spinner) findViewById(R.id.tenun_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.jenis_tenun, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        nameReg = (EditText) findViewById(R.id.nameReg);
        emailReg = (EditText) findViewById(R.id.emailReg);
        passwordReg = (EditText) findViewById(R.id.passwordReg);
        phoneReg = (EditText) findViewById(R.id.phoneReg);
        addressReg = (EditText) findViewById(R.id.addressReg);
        btnRegister = (Button) findViewById(R.id.register);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameReg.getText().toString();
                String email = emailReg.getText().toString();
                String password = passwordReg.getText().toString();
                String phone = phoneReg.getText().toString();
                String address = addressReg.getText().toString();
                String jenis_tenun = spinner.getSelectedItem().toString();

                if(validateRegister(name, email, password, phone, address, jenis_tenun)){
                    //do login
//                    if (register(name, email, password, phone, address, jenis_tenun)){
                        doRegister(name, email, password, phone, address, jenis_tenun);
//                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private boolean register(String name, String email, String password, String phone, String address, String jenis_tenun){
        doRegister(name, email, password, phone, address, jenis_tenun);
        return true;
    }

    private boolean validateRegister(String name, String email, String password, String phone, String address, String jenis_tenun){
        if(name == null || name.trim().length() == 0){
            Toast.makeText(this, "Masukkan nama anda", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(email == null || email.trim().length() == 0){
            Toast.makeText(this, "Masukkan email anda", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password == null || password.trim().length() == 0){
            Toast.makeText(this, "Masukkan password anda", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(phone == null || phone.trim().length() == 0){
            Toast.makeText(this, "Masukkan phone anda", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(address == null || address.trim().length() == 0){
            Toast.makeText(this, "Masukkan address anda", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(jenis_tenun == null || jenis_tenun.trim().length() == 0){
            Toast.makeText(this, "Pilih jenis tenun anda", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void doRegister(String name, String email, String password, String phone, String address, String jenis_tenun){
        String loginEmail = email;
        String loginPass = password;
        tenunNetworkInterface.register(APIModule.ACCESS_TOKEN_TEMP, name, email, password, phone, address, jenis_tenun).enqueue(new Callback<ResponseGetUser>() {
            @Override
            public void onResponse(Call<ResponseGetUser> call, Response<ResponseGetUser> response) {
                if (response.isSuccessful()) {
                    realm.beginTransaction();
                    realm.executeTransactionAsync(realm10 -> realm10.insert(response.body().getData()));
                    realm.commitTransaction();
                    startHomeActivity();
                    successResponse();
                }
            }

            @Override
            public void onFailure(Call<ResponseGetUser> call, Throwable t) {
                failResponse();
                Log.e("On Failure", t.getMessage());
            }
        });
    }

    private void doLogin(String email, String password){
        tenunNetworkInterface.login(APIModule.ACCESS_TOKEN_TEMP, email, password).enqueue(new Callback<ResponseGetUser>() {
            @Override
            public void onResponse(Call<ResponseGetUser> call, Response<ResponseGetUser> response) {
                if (response.isSuccessful()) {
                    realm.beginTransaction();
                    realm.executeTransactionAsync(realm10 -> realm10.insert(response.body().getData()));
                    realm.commitTransaction();
                }
            }

            @Override
            public void onFailure(Call<ResponseGetUser> call, Throwable t) {
                failResponse();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(intent);

        finish();
    }

    private void startHomeActivity() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(intent);
        getSharedPreferences("login", MODE_PRIVATE).edit().putBoolean("logged", true).apply();

        finish();
    }

    private boolean successResponse(){
        Toast.makeText(this, "Berhasil mendaftar", Toast.LENGTH_SHORT).show();
        return true;
    }

    private boolean failResponse(){
        Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
        return false;
    }
}
