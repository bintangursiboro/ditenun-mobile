package com.ditenun.appditenun.function.activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ditenun.appditenun.R;
import com.ditenun.appditenun.dependency.App;
import com.ditenun.appditenun.dependency.models.Faq;
import com.ditenun.appditenun.dependency.models.ResponseGetFeedback;
import com.ditenun.appditenun.dependency.models.User;
import com.ditenun.appditenun.dependency.modules.APIModule;
import com.ditenun.appditenun.dependency.network.TenunNetworkInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class FeedbackActivity extends AppCompatActivity {

    EditText subjekFb;
    EditText deskripsiFb;

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
        setContentView(R.layout.activity_feedback);

        App.get(this).getInjector().inject(this);

        final RatingBar ratingBar = (RatingBar) findViewById(R.id.rating_rating_bar);
        Button submitBtn = (Button) findViewById(R.id.sendFb);
        subjekFb = (EditText) findViewById(R.id.subjekFeedback);
        deskripsiFb = (EditText) findViewById(R.id.deskripsiFeedback);
        User user = realm.where(User.class).findFirst();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subjek = subjekFb.getText().toString();
                String deskripsi = deskripsiFb.getText().toString();
                Integer rating = (Integer) Math.round(ratingBar.getRating());
                Integer user_id = (Integer) user.getId();
                if(validateField(subjek, deskripsi, rating, user_id)){
                    //do login
                    if (checkPost(subjek, deskripsi, rating, user_id)){
                        startHomeActivity();
                        successResponse();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startHomeActivity();
    }

    @Override
    protected void onResume() { super.onResume(); }

    private boolean validateField(String subjek, String deskripsi, Integer rating, Integer user_id){
        if(subjek == null || subjek.trim().length() == 0){
            Toast.makeText(this, "Masukkan subjek", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(deskripsi == null || deskripsi.trim().length() == 0){
            Toast.makeText(this, "Masukkan deskripsi", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(rating == null){
            Toast.makeText(this, "Berikan rating", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkPost(String subjek, String deskripsi, Integer rating, Integer user_id){
        post(subjek, deskripsi, rating, user_id);
        return true;
    }

    private void post(String subjek, String deskripsi, Integer rating, Integer user_id){
        tenunNetworkInterface.feedback(APIModule.ACCESS_TOKEN_TEMP, subjek, deskripsi, rating, user_id).enqueue(new Callback<ResponseGetFeedback>() {
            @Override
            public void onResponse(Call<ResponseGetFeedback> call, Response<ResponseGetFeedback> response) {
                if (response.isSuccessful()) {
                    successResponse();
                }
            }

            @Override
            public void onFailure(Call<ResponseGetFeedback> call, Throwable t) {
                Log.e("On Failure", t.getMessage());
                failResponse();
            }
        });
    }

    private void startHomeActivity(){
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(intent);

        finish();
    }

    private boolean successResponse(){
        Toast.makeText(this, "Terimakasih atas penilaian Anda", Toast.LENGTH_SHORT).show();
        return true;
    }

    private boolean failResponse(){
        Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
        return false;
    }
}
