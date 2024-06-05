package com.example.app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.app.R;
import com.example.app.model.Item;
import com.example.app.retrofit.ApiBanHang;
import com.example.app.retrofit.RetrofitClient;
import com.example.app.utils.Utils;

import org.w3c.dom.Text;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HoanHangActivity extends AppCompatActivity {
    ImageView image;
    Item item;
    TextView txtten;
    TextView txtsoluong;
    TextView txttrangthai;
    TextView txtdiachi;
    TextView txttongtien;
    TextView txtsodt;
    Button btnhoanhang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    EditText edhoanhang;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hoan_hang);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        initView();
        ActionToolBar();
        initData();
    }
    private void initData() {
        item = (Item) getIntent().getSerializableExtra("item");
        txtdiachi.setText("Địa chỉ: " + item.getDiachi());
        txtten.setText(item.getTensanpham() + " ");
        txtsoluong.setText("Số lượng: " + item.getSoluong() + " ");
        int gia = Integer.parseInt(item.getGia());
        int sl = Integer.parseInt(String.valueOf(item.getSoluong()));
        int tongtien = gia * sl;
        txttongtien.setText("Tổng tiền: " + tongtien);
        //Glide.with(context).load(item.getHinhanh()).into(holder.imagechitiet);
        if (item.getHinhanh().contains("http")) {
            Glide.with(getApplicationContext()).load(item.getHinhanh()).into(image);
        } else {
            String hinh = Utils.BASE_URL + "images/" + item.getHinhanh();
            Glide.with(getApplicationContext()).load(hinh).into(image);
        }
        String str_trangthai = null;
        int trangthai = item.getTrangthai();
        if (trangthai == 0) {
            str_trangthai = "Đang chờ xử lý";
            btnhoanhang.setVisibility(View.VISIBLE);
        } else if (trangthai == 1) {
            str_trangthai = "Đã xác nhận";
        } else if (trangthai == 2) {
            str_trangthai = "Đang giao hàng";
        } else if (trangthai == 3) {
            str_trangthai = "Đã giao";
        } else if (trangthai == 4) {
            str_trangthai = "Đã hủy";
        } else if (trangthai == 5) {
            str_trangthai = "Hoàn hàng";
        } else if (trangthai == 5) {
            str_trangthai = "Hoàn thành";
        }

        txttrangthai.setText("Trạng thái: " + str_trangthai);
        if (edhoanhang != null) {
            btnhoanhang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String hoanhangText = edhoanhang.getText().toString();
                    if (TextUtils.isEmpty(hoanhangText)) {
                        Toast.makeText(getApplicationContext(), "Bạn chưa nhập lý do hoàn hàng", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.d("iddonhangt", String.valueOf(item.getIddonhang()));
                    Log.d("idspt", String.valueOf(item.getIdsp()));
                    compositeDisposable.add(apiBanHang.hoanhang(item.getIddonhang(), item.getIdsp())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    thongTinThemModel -> {
                                        if (thongTinThemModel.isSuccess()) {
                                            Toast.makeText(getApplicationContext(), thongTinThemModel.getMessage(), Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), XemDonHangActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    },
                                    throwable -> {
                                        Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                            ));

                }
            });

        } else {
            Toast.makeText(getApplicationContext(), "Vui lòng nhập lý do hoàn hàng", Toast.LENGTH_SHORT).show();
        }
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
    private void initView() {
        image = findViewById(R.id.item_img);
        txtten = findViewById(R.id.item_tensp);
        txtdiachi =findViewById(R.id.item_diachi);
        txttongtien =findViewById(R.id.item_tongtien);
        txtsodt = findViewById(R.id.item_sodt);
        btnhoanhang=findViewById(R.id.btnhoanhang);
        txtsoluong = findViewById(R.id.item_soluong);
        txttrangthai =findViewById(R.id.item_trangthai);
        edhoanhang = findViewById(R.id.edhoanhang);
        toolbar = findViewById(R.id.toolbar);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}