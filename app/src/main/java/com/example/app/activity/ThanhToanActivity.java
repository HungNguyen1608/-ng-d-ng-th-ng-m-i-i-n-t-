package com.example.app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.adapter.ThanhToanAdapter;
import com.example.app.model.CreateOrder;
import com.example.app.model.GioHang;
import com.example.app.model.GuiTB;
import com.example.app.retrofit.ApiBanHang;
import com.example.app.retrofit.ApiPushNotifition;
import com.example.app.retrofit.RetrofitClient;
import com.example.app.retrofit.RetrofitClientTB;
import com.example.app.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
//momo zalo
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;
//import vn.momo.momo_partner.AppMoMoLib;

public class ThanhToanActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView txtTongtien;
    EditText edSodienthoai, edEmail, edDiachi;
    Button btnDatHang,btnttzalopay;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    long tongTien;
    int totalItem;
    //momo
    int iddonhang;;
    RecyclerView recyclerView;
    ThanhToanAdapter adapter;
    List<GioHang> gioHangList;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);
        initView();
        initControl();
        countItem();
         //zalo

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);
        //hiển thi sản phẩm

        // Khởi tạo RecyclerView
        recyclerView = findViewById(R.id.recycleviewthanhtoan);

        // Khởi tạo danh sách gioHangList
        gioHangList = new ArrayList<>();

        // Thêm dữ liệu vào gioHangList từ Utils.mangmuahang hoặc từ nơi khác
        gioHangList.addAll(Utils.mangmuahang);

        // Khởi tạo và thiết lập adapter
        adapter = new ThanhToanAdapter(gioHangList, this);

        // Thiết lập layout manager cho RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Thiết lập adapter cho RecyclerView
        recyclerView.setAdapter(adapter);
    }

    private void countItem() {
        totalItem = 0;
        for(int i=0; i<Utils.mangmuahang.size();i++){
            totalItem = totalItem + Utils.mangmuahang.get(i).getSoluong();
        }
    }

    private void initControl() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.mangmuahang.clear();
                finish();
            }
        });
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        //tổng tiền
       // tongTien = 0;
        tongTien = getIntent().getLongExtra("tongtien", 0);
        if (tongTien != 0) {
            txtTongtien.setText("Tổng tiền: "+decimalFormat.format(tongTien) +" đ");
        } else {
//            long tongtien = 0; // Khai báo và khởi tạo biến tongtien
//            // Lặp qua danh sách sản phẩm trong Utils.mangmuahang để tính tổng tiền
//            for(int i = 0; i < Utils.mangmuahang.size(); i++) {
//                tongtien += (Utils.mangmuahang.get(i).getGiasp() * Utils.mangmuahang.get(i).getSoluong());
//            }
//            DecimalFormat decimalFormat1 = new DecimalFormat("###,###,###");
//            String formattedTongTien = decimalFormat1.format(tongtien); // Format tổng tiền
//            txtTongtien.setText(decimalFormat.format(tongtien));
            // Khai báo biến tổng tiền
            tongTien = 0;
            // Lặp qua từng sản phẩm trong Utils.mangmuahang
            for (GioHang gioHang : Utils.mangmuahang) {
                // Tính tổng tiền của từng sản phẩm
                long giaSanPham = Long.parseLong(String.valueOf(gioHang.getGiasp())); // Giá của sản phẩm
                int soLuong = gioHang.getSoluong(); // Số lượng của sản phẩm
                long tongTienSanPham = giaSanPham * soLuong; // Tổng tiền của sản phẩm
                tongTien += tongTienSanPham; // Cộng vào tổng tiền
            }
            // Hiển thị tổng tiền trên TextView
            txtTongtien.setText("Tổng tiền: "+decimalFormat.format(tongTien)+" đ");
        }
        edEmail.setText(Utils.user_current.getEmail());
        edSodienthoai.setText(Utils.user_current.getMobile());
        btnDatHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_diachi = edDiachi.getText().toString().trim();
                if(TextUtils.isEmpty(str_diachi)){
                    Toast.makeText(getApplicationContext(),"Vui lòng nhập địa chỉ giao hàng",Toast.LENGTH_SHORT).show();
                }else {
                    String str_email = Utils.user_current.getEmail();
                    String str_sdt = Utils.user_current.getMobile();
                    int id = Utils.user_current.getId();
                    Log.d("mangmuahang2" , new Gson().toJson(Utils.mangmuahang));
                    compositeDisposable.add(apiBanHang.createOder(str_email,str_sdt,String.valueOf(tongTien),id,str_diachi,totalItem,new Gson().toJson(Utils.mangmuahang))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    userModel -> {
                                        Toast.makeText(getApplicationContext(),"Thành công",Toast.LENGTH_SHORT).show();
                                        layToken();
                                        xoagiohang();
                                        Log.d("manggiohang" , new Gson().toJson(Utils.manggiohang));
                                        capnhatgiohang();
                                        Utils.mangmuahang.clear();
                                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    },throwable -> {
                                        Log.d("test" , throwable.getMessage());
                                        Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                            ));
                }
            }
        });
        btnttzalopay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_diachi = edDiachi.getText().toString().trim();
                if(TextUtils.isEmpty(str_diachi)){
                    Toast.makeText(getApplicationContext(),"Vui lòng nhập địa chỉ giao hàng",Toast.LENGTH_SHORT).show();
                }else {
                    String str_email = Utils.user_current.getEmail();
                    String str_sdt = Utils.user_current.getMobile();
                    int id = Utils.user_current.getId();
                    Log.d("text" , new Gson().toJson(Utils.mangmuahang));
                    compositeDisposable.add(apiBanHang.createOder(str_email,str_sdt,String.valueOf(tongTien),id,str_diachi,totalItem,new Gson().toJson(Utils.mangmuahang))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    messageModel -> {
                                        Toast.makeText(getApplicationContext(),"Thanh cong them",Toast.LENGTH_SHORT).show();
                                        xoagiohang();
                                        capnhatgiohang();
                                        Utils.mangmuahang.clear();
                                        iddonhang = Integer.parseInt(messageModel.getIddonhang());
                                        Log.d("iddonhang",String.valueOf(iddonhang));
                                        //Toast.makeText(getApplicationContext(),iddonhang,Toast.LENGTH_SHORT).show();
                                        requestzalo();
                                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                        startActivity(intent);

                                    },throwable -> {
                                        Log.e("test1" , throwable.getMessage());
                                        Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                            ));

                }

            }
        });
    }
    private void layToken(){
        for(int i = 0; i< Utils.mangmuahang.size();i++) {
            compositeDisposable.add(apiBanHang.layTokenChuShop(Utils.mangmuahang.get(i).getIdsp())
                    .observeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            thongBaoModel -> {
                                guiTB(thongBaoModel.getResult().get(0).getThongbao());
                                Log.d("mathongbao",thongBaoModel.getResult().get(0).getThongbao());
                            },throwable -> {
                                Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                    ));
        }
    }
    private void guiTB(String tk){
        String token = tk;
        Map<String, String > data = new HashMap<>();
        data.put("title","Thông báo");
        data.put("body","Bạn có đơn hàng mới");
        GuiTB guiTB = new GuiTB(token, data);
        ApiPushNotifition apiPushNotifition = RetrofitClientTB.getInstance().create(ApiPushNotifition.class);
        compositeDisposable.add(apiPushNotifition.sendNotification(guiTB)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        nhanTB ->{

                        },
                        throwable -> {
                            Log.d("logtb",throwable.getMessage());
                        }
                ));
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
    private void capnhatgiohang(){
        compositeDisposable.add(apiBanHang.xoaGioHang(Utils.user_current.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        gioHangModel -> {
                            if(gioHangModel.isSuccess()){
                                for(int i =0; i < Utils.manggiohang.size(); i++){
                                        capnhat(Utils.user_current.getId(),Utils.manggiohang.get(i).getIdsp() , Utils.manggiohang.get(i).getSoluong());
                                }
                                //Toast.makeText(getApplicationContext(),gioHangModel.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        },throwable -> {
                            Log.e("XemGioHangActivity", throwable.getMessage());
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void xoagiohang() {
        List<GioHang> phanTuCanXoa = new ArrayList<>();
        // Lặp qua từng phần tử trong manggiohang
        for (GioHang gioHang : Utils.manggiohang) {
            // Nếu phần tử này cũng có trong mangmuahang, thì thêm vào danh sách cần xóa
            if (Utils.mangmuahang.contains(gioHang)) {
                phanTuCanXoa.add(gioHang);
            }
        }
        // Xóa các phần tử cần xóa khỏi manggiohang
        Utils.manggiohang.removeAll(phanTuCanXoa);
    }
    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        toolbar = findViewById(R.id.toolbar);
        edSodienthoai = findViewById(R.id.edSodienthoai);
        edEmail = findViewById(R.id.edEmail);
        edDiachi = findViewById(R.id.edDiachi);
        btnDatHang = findViewById(R.id.btnDathang);
        btnttzalopay = findViewById(R.id.btnDathangzalo);
        txtTongtien = findViewById(R.id.txtTongtien);
        recyclerView=findViewById(R.id.recycleviewthanhtoan);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
    private void requestzalo() {
        CreateOrder orderApi = new CreateOrder();
        try {
            JSONObject data = orderApi.createOrder(String.valueOf(tongTien));
            String code = data.getString("return_code");
            Log.d("test", code);
            if (code.equals("1")) {
                String token = data.getString("zp_trans_token");
                Log.d("test", token);
                Intent intent = getPackageManager().getLaunchIntentForPackage("vn.zalopay.sdk");
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Ứng dụng ZaloPay Sandbox không được cài đặt trên thiết bị của bạn", Toast.LENGTH_SHORT).show();
                }

                ZaloPaySDK.getInstance().payOrder(ThanhToanActivity.this, token, "demozpdk://app", new PayOrderListener() {
                    @Override
                    public void onPaymentSucceeded(String s, String s1, String s2) {
                        compositeDisposable.add(apiBanHang.themtokenpay(iddonhang,token)
                                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                                        messageModel -> {
                                            if (messageModel.isSuccess()){
                                                Toast.makeText(getApplicationContext(),token,Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        },
                                        throwable -> {

                                        }
                                ));
                    }

                    @Override
                    public void onPaymentCanceled(String s, String s1) {

                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {

                    }
                });

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}