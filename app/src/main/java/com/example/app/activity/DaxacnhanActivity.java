package com.example.app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.app.R;
import com.example.app.adapter.DonHangAdapter;
import com.example.app.retrofit.ApiBanHang;
import com.example.app.retrofit.RetrofitClient;
import com.example.app.utils.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DaxacnhanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daxacnhan);
    }
}
//package com.example.app.activity;
//
//        import androidx.appcompat.app.AppCompatActivity;
//        import androidx.recyclerview.widget.LinearLayoutManager;
//        import androidx.recyclerview.widget.RecyclerView;
//
//        import android.icu.number.CompactNotation;
//        import android.os.Bundle;
//        import android.util.Log;
//        import android.view.View;
//        import android.widget.Toast;
//        import androidx.appcompat.widget.Toolbar;
//
//        import com.example.app.R;
//        import com.example.app.adapter.DonHangAdapter;
//        import com.example.app.model.DonHang;
//        import com.example.app.retrofit.ApiBanHang;
//        import com.example.app.retrofit.RetrofitClient;
//        import com.example.app.utils.Utils;
//
//        import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
//        import io.reactivex.rxjava3.disposables.CompositeDisposable;
//        import io.reactivex.rxjava3.schedulers.Schedulers;
//
//public class XemDonHangActivity extends AppCompatActivity {
//    CompositeDisposable compositeDisposable = new CompositeDisposable();
//    ApiBanHang apiBanHang;
//    RecyclerView recyclerView;
//    Toolbar toolbar;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_xem_don_hang);
//        initView();
//        initToolbar();
//        getOrder();
//
//    }
//
//    private void getOrder() {
//        compositeDisposable.add(apiBanHang.xemDonHang(Utils.user_current.getId())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        donHangModel -> {
//                            DonHangAdapter adapter = new DonHangAdapter(getApplicationContext(), donHangModel.getResult());
//                            recyclerView.setAdapter(adapter);
//
//                            //Toast.makeText(getApplicationContext(),donHangModel.getResult().get(0).getItem().get(0).getTensp(),Toast.LENGTH_SHORT).show();
//                        },throwable -> {
//                            Log.e("XemDonHangActivity", throwable.getMessage());
//                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                ));
//    }
//
//    private void initToolbar() {
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//    }
//
//    private void initView() {
//        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
//        recyclerView = findViewById(R.id.recycleview_donhang);
//        toolbar = findViewById(R.id.toolbar);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//    }
//
//    @Override
//    protected void onDestroy() {
//        compositeDisposable.clear();
//        super.onDestroy();
//    }
//}