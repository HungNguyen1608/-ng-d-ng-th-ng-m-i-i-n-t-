package com.example.app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.R;
import com.example.app.adapter.SanPhamMoiAdapter;
import com.example.app.model.SanPhamMoi;
import com.example.app.retrofit.ApiBanHang;
import com.example.app.retrofit.RetrofitClient;
import com.example.app.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SPShopActivity extends AppCompatActivity {
    TextView txtshop;
    RecyclerView recyclerView;
    Toolbar toolbar;
    ApiBanHang apiBanHang;
    List<SanPhamMoi> list;
    SanPhamMoiAdapter sanPhamMoiAdapter ;
    Integer idshop;
    String tenshop;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spshop);
        idshop = getIntent().getIntExtra("id",0);
        Log.d("idshop",String.valueOf(idshop));
        tenshop = getIntent().getStringExtra("tenshop");
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        initView();
        ActionToolBar();
        txtshop.setText(tenshop);
        getSpMoi();
    }

    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView() {
        txtshop = findViewById(R.id.txttenshop);
        recyclerView = findViewById(R.id.recycleview);
        toolbar = findViewById(R.id.toolbar);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
    }
    private void getSpMoi(){
        compositeDisposable.add(apiBanHang.getSpAdmin(idshop)
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
}