package com.ditenun.appditenun.function.activity;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.Sort;

import android.os.Bundle;

import com.ditenun.appditenun.R;
import com.ditenun.appditenun.dependency.App;
import com.ditenun.appditenun.dependency.models.Faq;
import com.ditenun.appditenun.function.adapter.ExpandableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class FaqActivity extends AppCompatActivity {
    public static final String KEY_ID_FAQ = "id_key";

    @Inject
    Realm realm;

    private Faq faq;

    RecyclerView expanderRecyclerView;
    List<Faq> listFaq = Collections.EMPTY_LIST;

    public static Intent createIntent(Context context, String id) {
        Intent intent = new Intent(context, FaqActivity.class);
        intent.putExtra(KEY_ID_FAQ, id);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        expanderRecyclerView = findViewById(R.id.recyclerViewFaq);

        Intent returnIntent = getIntent();
        String idFaq = returnIntent.getStringExtra(KEY_ID_FAQ);

        ArrayList<String> parentList = new ArrayList<>();
        ArrayList<ArrayList> childListHolder = new ArrayList<>();
        listFaq = new ArrayList<Faq>();

        realm = Realm.getDefaultInstance();
        listFaq = realm.where(Faq.class).findAll().sort("id", Sort.ASCENDING);

        Integer size = (Integer) listFaq.size() ;
        Integer i=0;

        for (i=0;i<size;i++){
            parentList.add(listFaq.get(i).getJudul());
            ArrayList<String> childNameList = new ArrayList<>();
            childNameList.add(listFaq.get(i).getDeskripsi());
            childListHolder.add(childNameList);
        }

//        parentList.add(faq.getJudul());
//        ArrayList<String> childNameList = new ArrayList<>();
//        childNameList.add(faq.getDeskripsi());
//        childListHolder.add(childNameList);

        ExpandableRecyclerViewAdapter expandableCategoryRecyclerViewAdapter =
                new ExpandableRecyclerViewAdapter(getApplicationContext(), parentList,
                        childListHolder);

        expanderRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        expanderRecyclerView.setAdapter(expandableCategoryRecyclerViewAdapter);

    }

    @Override
    public void onBackPressed() {
        startHomeActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void startHomeActivity(){
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(intent);

        finish();
    }
}
