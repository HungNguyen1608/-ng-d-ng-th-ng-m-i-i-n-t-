package com.example.app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.app.R;
import com.example.app.adapter.DonHangAdapter;
import com.example.app.adapter.GioHangAdapter;
import com.example.app.model.EventBus.TinhTongEvent;
import com.example.app.model.GioHang;
import com.example.app.retrofit.ApiBanHang;
import com.example.app.retrofit.RetrofitClient;
import com.example.app.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GioHangActivity extends AppCompatActivity {
    TextView gioHangRong, tongTien;
    Toolbar toolbar;
    RecyclerView recyclerView;
    ConstraintLayout btnMuahang;
    GioHangAdapter adapter;
    List<GioHang> gioHangList;
    long tongtien;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gio_hang);
        initView();
        //layGioHang();
        initControl();
        tinhTongTien();

    }

    private void tinhTongTien() {
        tongtien = 0;
        for(int i= 0;i<Utils.mangmuahang.size();i++){
            tongtien = tongtien + (Utils.mangmuahang.get(i).getGiasp() * Utils.mangmuahang.get(i).getSoluong());
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        tongTien.setText(decimalFormat.format(tongtien));
    }

    private void initControl() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if(Utils.manggiohang.size() == 0){
            gioHangRong.setVisibility(View.VISIBLE);
        }else {
            adapter = new GioHangAdapter(getApplicationContext(),Utils.manggiohang);
            recyclerView.setAdapter(adapter);
        }
        btnMuahang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ThanhToanActivity.class);
                intent.putExtra("tongtien",tongtien);
                //Utils.manggiohang.clear();
                startActivity(intent);
            }
        });
    }

    public void capnhat(int iduser, int idsp, int soluong) {
        compositeDisposable.add(apiBanHang.capNhatGioHang(iduser, idsp, soluong)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        gioHangModel -> {
                            if(gioHangModel.isSuccess() == true) {

                                Log.d("GHActivity", "Utils.manggiohang: " + gioHangModel.isSuccess()); // Thêm dòng này
                            }
                        },throwable -> {
                            Log.e("AXemGioHangActivity", throwable.getMessage());
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void initView(){
        gioHangRong = findViewById(R.id.txtgiohangtrong);
        toolbar = findViewById(R.id.toobar);
        recyclerView = findViewById(R.id.recycleviewgiohang);
        btnMuahang = findViewById(R.id.btnmuahang);
        tongTien = findViewById(R.id.txttongtien);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    private void capnhatgiohang(){
        compositeDisposable.add(apiBanHang.xoaGioHang(Utils.user_current.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        gioHangModel -> {
                            if(gioHangModel.isSuccess()){
                                for(int i =0; i < Utils.manggiohang.size(); i++){
                                    capnhat(Utils.user_current.getId(),Utils.manggiohang.get(i).getIdsp(),Utils.manggiohang.get(i).getSoluong());
                                }
                                //Toast.makeText(getApplicationContext(),gioHangModel.getMessage().toString(),Toast.LENGTH_SHORT).show();
                            }
                        },throwable -> {
                            Log.e("XemGioHangActivity", throwable.getMessage());
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }
    @Override
    protected void onDestroy() {
        capnhatgiohang();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void eventTinhTien(TinhTongEvent event){
        if(event != null){
            tinhTongTien();
        }
    }
}