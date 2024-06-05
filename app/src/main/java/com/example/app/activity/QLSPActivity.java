package com.example.app.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.DirectedAcyclicGraph;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.app.R;
import com.example.app.adapter.SanPhamMoiAdapter;
import com.example.app.model.EventBus.SuaXoaEvent;
import com.example.app.model.SanPhamMoi;
import com.example.app.retrofit.ApiBanHang;
import com.example.app.retrofit.RetrofitClient;
import com.example.app.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class QLSPActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    Button btnThem;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    List<SanPhamMoi> list;
    SanPhamMoiAdapter sanPhamMoiAdapter ;
    SanPhamMoi sanPhamMoiSX;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qlspactivity);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        initView();
        initControl();
        ActionToolBar();

    }

    private void initControl() {
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ThemSanPhamActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        btnThem = findViewById(R.id.btnThem);
        recyclerView = findViewById(R.id.recycleview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
//        recyclerViewManHinhChinh.setLayoutManager(layoutManager);
//        recyclerViewManHinhChinh.setHasFixedSize(true);

    }
    private void getSpMoi()
    {
        compositeDisposable.add(apiBanHang.getSpAdmin(Utils.user_current.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if(sanPhamMoiModel.isSuccess()){
                                Log.d("loggg", String.valueOf(sanPhamMoiModel.getResult().get(0).getSoluongton()));
                                list = sanPhamMoiModel.getResult();
                                sanPhamMoiAdapter = new SanPhamMoiAdapter(getApplicationContext(),list);
                                recyclerView.setAdapter(sanPhamMoiAdapter);
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(),"khong ket noi duoc voi server"+ throwable.getMessage(),Toast.LENGTH_LONG).show();
                        }
                )
        );
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals("Sửa")){
            suaSanPham();
        }else if(item.getTitle().equals("Xóa")) {
            xoaSanPham();
        }
            return super.onContextItemSelected(item);
    }
    private void xoaSanPham(){
        compositeDisposable.add(apiBanHang.xoaSP(Utils.user_current.getId(),sanPhamMoiSX.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            if(messageModel.isSuccess()){
                                Toast.makeText(getApplicationContext(), messageModel.getMessage(),Toast.LENGTH_SHORT).show();
                                getSpMoi();
                            }
                        },throwable -> {
                            Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
                        }

                ));
    }
    private void suaSanPham(){
        Intent intent = new Intent(getApplicationContext(),ThemSanPhamActivity.class);
        intent.putExtra("sua",sanPhamMoiSX);
        startActivity(intent);
    }

    private void ActionToolBar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Subscribe(sticky = true, threadMode =  ThreadMode.MAIN)
    public void suaxoa(SuaXoaEvent event){
        if(event != null){
            sanPhamMoiSX = event.getSanPhamMoi();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        getSpMoi();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}