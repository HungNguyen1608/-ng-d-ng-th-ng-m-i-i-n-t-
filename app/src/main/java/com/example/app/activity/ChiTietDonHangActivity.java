package com.example.app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.app.R;
import com.example.app.model.GuiTB;
import com.example.app.model.Item;
import com.example.app.model.SanPhamMoi;
import com.example.app.retrofit.ApiBanHang;
import com.example.app.retrofit.ApiPushNotifition;
import com.example.app.retrofit.RetrofitClient;
import com.example.app.retrofit.RetrofitClientTB;
import com.example.app.utils.Utils;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ChiTietDonHangActivity extends AppCompatActivity {
    Item item;
    TextView txttensp, txtsoluong, txtgia, txttinhtrang, txtnguoidat, txtemail;
    ImageView img;
    Button btncapnhat;
    RadioButton rb1, rb2,rb3, rb4, rb5;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_don_hang);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        initView();
        initControl();
        initData();
    }


    private void initData(){
        item =(Item) getIntent().getSerializableExtra("item");
        txttensp.setText(item.getTensanpham());
        //txtsoluong.setText(item.getSoluong());
        txtsoluong.setText("Số lượng: "+ String.valueOf(item.getSoluong()));
        String str_trangthai = null;
        int trangthai = item.getTrangthai();
        if(trangthai == 0){
            str_trangthai = "Đang chờ xử lý";
        } else if(trangthai == 1){
            str_trangthai = "Đã xác nhận";
        } else if(trangthai == 2){
            str_trangthai = "Đang giao hàng";
        } else if(trangthai == 3){
            str_trangthai = "Đã giao";
        } else if(trangthai == 4){
            str_trangthai = "Đã hủy";
        } else if(trangthai == 5){
            str_trangthai = "Hoàn hàng";
        }else if(trangthai == 6) {
            str_trangthai = "Hoàn thành";
        }
        txttinhtrang.setText("Trạng thái: "+ str_trangthai);
        //Glide.with(getApplicationContext()).load(item.getHinhanh()).into(img);
        if(item.getHinhanh().contains("http")){
            Glide.with(getApplicationContext()).load(item.getHinhanh()).into(img);
        } else {
            String hinh = Utils.BASE_URL + "images/" +item.getHinhanh();
            Glide.with(getApplicationContext()).load(hinh).into(img);
        }
        txtgia.setText("Giá: "+ item.getGia());
        Log.d("kiemtraid", String.valueOf(item.getIdsp()));
        getThemTT();
    }
    public void getThemTT(){
        compositeDisposable.add(apiBanHang.themTT(item.getIddonhang())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                            thongTinThemModel -> {
                                if(thongTinThemModel.isSuccess()){
                                    txtnguoidat.setText("Tên người mua: " +thongTinThemModel.getResult().get(0).getUsername());
                                    txtemail.setText("Email: " +thongTinThemModel.getResult().get(0).getEmail());
                                    token = thongTinThemModel.getResult().get(0).getThongbao();
                                }else {
                                    txtnguoidat.setText("Tên người mua: Trống");
                                    txtemail.setText("Email: Trống");

                                }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_LONG).show();
                        }
                ));
    }


    private void initControl() {
        btncapnhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capnhatdonhangshop();
            }
        });
    }
    private void guiTB(String tk){
        String token = tk;
        Map<String, String > data = new HashMap<>();
        data.put("title","Thông báo");
        data.put("body","Bạn có đơn hàng đã xác nhận và sẽ sớm giao đến bạn");
        GuiTB guiTB = new GuiTB(token, data);
        ApiPushNotifition apiPushNotifition = RetrofitClientTB.getInstance().create(ApiPushNotifition.class);
        compositeDisposable.add(apiPushNotifition.sendNotification(guiTB)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        nhanTB ->{

                        },
                        throwable -> {
                            Log.d("logtbb",throwable.getMessage());
                        }
                ));

    }

    private void capnhatdonhangshop() {
        Integer tt = 0;
        if(rb1.isChecked()){
            tt = 0;
        } else if (rb2.isChecked()) {
            tt = 1;
        }else if(rb3.isChecked()){
            tt = 2;
        }else if(rb4.isChecked()){
            tt = 3;
        }else if(rb5.isChecked()){
            tt = 4;
        }
        if(rb1.isChecked() == false && rb2.isChecked() == false && rb3.isChecked() == false && rb4.isChecked() == false && rb5.isChecked() == false){
            Toast.makeText(getApplicationContext(),"Vui lòng chọn trạng thái đơn hàng",Toast.LENGTH_SHORT).show();
        }else {
            if(tt == 1){
                getThemTT();
                guiTB(token);
            }
            compositeDisposable.add(apiBanHang.capnhatdonhangShop(Integer.parseInt(String.valueOf(item.getIddonhang())),Integer.parseInt(String.valueOf(item.getIdsp())),tt)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            thongTinThemModel -> {
                                if(thongTinThemModel.isSuccess()) {
                                    Toast.makeText(getApplicationContext(), thongTinThemModel.getMessage(), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(),QLDHActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            },
                            throwable -> {
                                Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_LONG).show();
                            }
                    ));
        }
    }

    private void initView() {
        txttensp = findViewById(R.id.txttensp);
        txtsoluong = findViewById(R.id.txtsoluong);
        txtgia = findViewById(R.id.txtgia);
        txttinhtrang = findViewById(R.id.txttinhtrang);
        txtnguoidat = findViewById(R.id.txtnguoidat);
        txtemail = findViewById(R.id.txtemail);
        img = findViewById(R.id.img);
        btncapnhat = findViewById(R.id.btncapnhat);
        rb1 = findViewById(R.id.rb1);
        rb2 = findViewById(R.id.rb2);
        rb3 = findViewById(R.id.rb3);
        rb4 = findViewById(R.id.rb4);
        rb5 = findViewById(R.id.rb5);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}