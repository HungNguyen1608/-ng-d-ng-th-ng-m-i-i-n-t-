package com.example.app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.app.R;
import com.example.app.adapter.DanhGiaAdapter;
import com.example.app.adapter.DienThoaiAdapter;
import com.example.app.model.DanhGia;
import com.example.app.model.DanhGiaModle;
import com.example.app.model.GioHang;
import com.example.app.model.SanPhamMoi;
import com.example.app.model.User;
import com.example.app.retrofit.ApiBanHang;
import com.example.app.retrofit.RetrofitClient;
import com.example.app.utils.Utils;
import com.google.gson.Gson;
import com.nex3z.notificationbadge.NotificationBadge;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
/// thây đổi drawable
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ChiTietActivity extends AppCompatActivity {
    ScrollView danhgia;
    TextView tensp, giasp, mota, shop,daban;
    TextView btnthem;
    ImageView imghinhanh,chat,anhshop,img_rohang;
    Spinner spinner;
    ConstraintLayout contLaymota,contLaydanhgia,btnmuangay;
    Toolbar toolbarctsp;
    SanPhamMoi sanPhamMoi;
    DanhGia danhGia;
    List<DanhGia> array;
    NotificationBadge badge;
    CompositeDisposable compositeDisposable=new CompositeDisposable();
    ApiBanHang apiBanHang;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    FrameLayout frameLayoutGioHang;
    GioHang gioHang = new GioHang();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet);
        //
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        initView();
        ActionToolBar();
        initData();
        initControl();
    }
    private void initControl(){
        btnthem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                themGioHang();
            }
        });
        contLaymota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //Mô tả
                // Lấy tham chiếu đến ConstraintLayout trong layout của bạn
                ConstraintLayout constraintLayout = findViewById(R.id.mota);
                // Thiết lập hoặc xóa background của ConstraintLayout
                // Để thiết lập background từ drawable:
                 constraintLayout.setBackgroundResource(R.drawable.gachchan);
            //đánh giá
                ConstraintLayout constraintLayout1 = findViewById(R.id.danhgia);
                // Thiết lập hoặc xóa background của ConstraintLayout
                // Để thiết lập background từ drawable:
                constraintLayout1.setBackground(null);

                mota.setVisibility(View.VISIBLE);
                danhgia.setVisibility(View.GONE);

            }
        });
        contLaydanhgia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstraintLayout constraintLayout = findViewById(R.id.mota);
                constraintLayout.setBackground(null);
            //Đánh giá
                ConstraintLayout constraintLayout1 = findViewById(R.id.danhgia);
                constraintLayout1.setBackgroundResource(R.drawable.gachchan);
            //ẩn mô tả
                mota.setVisibility(View.GONE);
                danhgia.setVisibility(View.VISIBLE);

            }
        });
        btnmuangay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Lấy số được chọn từ Spinner
                int selectedNumber = (int) spinner.getSelectedItem();
                // Đặt số được chọn vào trường soluong trong đối tượng gioHang
                gioHang.setSoluong(selectedNumber);
                // Tạo một danh sách mới để chứa các đối tượng GioHang
                Utils.mangmuahang = new ArrayList<>();
                // Thêm đối tượng GioHang vào danh sách
                Utils.mangmuahang.add(gioHang);
                Log.d("mangmuahang1",new Gson().toJson(Utils.mangmuahang));
               Intent thanhtoan = new Intent(getApplicationContext(), ThanhToanActivity.class);
               startActivity(thanhtoan);
               finish();


            }
        });

    }

    private void themGioHang(){
        if(Utils.manggiohang.size() > 0){
            boolean flag = false;
            int soluong = Integer.parseInt(spinner.getSelectedItem().toString());
            for(int i=0; i < Utils.manggiohang.size();i++){
                if(Utils.manggiohang.get(i).getIdsp() == sanPhamMoi.getId()){
                    Utils.manggiohang.get(i).setSoluong(soluong + Utils.manggiohang.get(i).getSoluong()) ;
//                    long giasp = Long.parseLong(sanPhamMoi.getGiasp());
//                    long gia = giasp * Utils.manggiohang.get(i).getSoluong();
                    float giacu = Float.parseFloat(sanPhamMoi.getGiasp());
                    float giamgia = sanPhamMoi.getGiamgia();
                    Integer giamoi = (int) (giacu * (1 - giamgia/100));
                    long gia = Long.parseLong(String.valueOf(giamoi)) * Utils.manggiohang.get(i).getSoluong();
                    Utils.manggiohang.get(i).setGiasp(giamoi);
                    flag = true;
                }
            }
            if(flag == false){
//                long giasp = Long.parseLong(sanPhamMoi.getGiasp());
//                long gia = giasp * soluong;
                float giacu = Float.parseFloat(sanPhamMoi.getGiasp());
                float giamgia = sanPhamMoi.getGiamgia();
                Integer giamoi = (int) (giacu * (1 - giamgia/100));
                long gia = giamoi * soluong;
                GioHang gioHang = new GioHang();
                gioHang.setGiasp(giamoi);
                gioHang.setSoluong(soluong);
                gioHang.setIdsp(sanPhamMoi.getId());
                gioHang.setTensanpham(sanPhamMoi.getTensanpham());
                gioHang.setHinhanh(sanPhamMoi.getHinhanh());
                Utils.manggiohang.add(gioHang);
            }
        }else {
            int soluong = Integer.parseInt(spinner.getSelectedItem().toString());
            //long gia = Long.parseLong(sanPhamMoi.getGiasp()) * soluong;
            float giacu = Float.parseFloat(sanPhamMoi.getGiasp());
            float giamgia = sanPhamMoi.getGiamgia();
            Integer giamoi = (int) (giacu * (1 - giamgia/100));
            long gia = giamoi * soluong;
            GioHang gioHang = new GioHang();
            gioHang.setGiasp(giamoi);
            gioHang.setSoluong(soluong);
            gioHang.setIdsp(sanPhamMoi.getId());
            gioHang.setTensanpham(sanPhamMoi.getTensanpham());
            gioHang.setHinhanh(sanPhamMoi.getHinhanh());
            Utils.manggiohang.add(gioHang);
        }
        int totalItem = 0;
        for(int i=0; i <Utils.manggiohang.size();i++){
            totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
        }
        badge.setText(String.valueOf(totalItem));
    }
    private void initData(){
        sanPhamMoi =(SanPhamMoi) getIntent().getSerializableExtra("chitiet");
        tensp.setText(sanPhamMoi.getTensanpham());
        mota.setText(sanPhamMoi.getMota());
        //Glide.with(getApplicationContext()).load(sanPhamMoi.getHinhanh()).into(imghinhanh);
        if(sanPhamMoi.getHinhanh().contains("http")){
            Glide.with(getApplicationContext()).load(sanPhamMoi.getHinhanh()).into(imghinhanh);
        } else {
            String hinh = Utils.BASE_URL + "images/" +sanPhamMoi.getHinhanh();
            Glide.with(getApplicationContext()).load(hinh).into(imghinhanh);
        }
        //giasp.setText("Gia: " + sanPhamMoi.getGiasp() + "D");
        float giacu = Float.parseFloat(sanPhamMoi.getGiasp());
        float giamgia = sanPhamMoi.getGiamgia();
        Integer giamoi = (int) (giacu * (1 - giamgia/100));
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        giasp.setText(decimalFormat.format(giamoi)+ "đ");
        shop.setText(sanPhamMoi.getUsername());
        daban.setText(String.valueOf("Đã bán: "+ sanPhamMoi.getDaban()));
//        int i = sanPhamMoi.getSoluongton();
//        Integer[] so = new Integer[]{1,2,3,4,5,6,7,8,9,10};
//        ArrayAdapter<Integer> adapterspin = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,so);
        int i = sanPhamMoi.getSoluongton();
        List<Integer> soList = new ArrayList<>();
        if (i >= 10) {
            // Nếu i lớn hơn hoặc bằng 10, sử dụng mảng từ 1 đến 10
            for (int j = 1; j <= 10; j++) {
                soList.add(j);
            }
        } else {
            // Ngược lại, sử dụng mảng từ 1 đến i
            for (int j = 1; j <= i; j++) {
                soList.add(j);
            }
        }
        if(soList.size() == 0){
            Toast.makeText(getApplicationContext(),"Sản phẩm tạm thời hết hàng",Toast.LENGTH_SHORT).show();
            btnthem.setEnabled(false);
            btnmuangay.setEnabled(false);
        }

        // Chuyển List<Integer> thành Integer[]
        Integer[] so = soList.toArray(new Integer[0]);

        // Khởi tạo ArrayAdapter cho Spinner
        ArrayAdapter<Integer> adapterspin = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, so);
        spinner.setAdapter(adapterspin);
        //thông tin shop
        String idshop=String.valueOf(sanPhamMoi.getIdshop());
        compositeDisposable.add(apiBanHang.thongtinshop(idshop)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModle -> {
                            if (userModle.isSuccess()) {
                                User user = userModle.getResult().get(0);
                                shop.setText(user.getUsername());
                                // Hiển thị avata bằng Glide
                                if (user.getAvartar() != null) {
                                    if (user.getAvartar().contains("http")) {
                                        Glide.with(this).load(user.getAvartar()).into(anhshop);
                                    } else {
                                        String hinh = Utils.BASE_URL + "images/" + user.getAvartar();
                                        Glide.with(this).load(hinh).into(anhshop);
                                    }
                                }
                            }
                        }
                        ,
                        throwable -> {

                        }
                )
        );
        //lâý dữ liệu phần đánh giá
        compositeDisposable.add(apiBanHang.laydanhgia(String.valueOf(sanPhamMoi.getId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        danhGiaModle -> {
                            array=danhGiaModle.getResult();
                            DanhGiaAdapter danhGiaAdapter = new DanhGiaAdapter(getApplicationContext(), array);
                            recyclerView.setAdapter(danhGiaAdapter);
                        },
                        throwable -> {

                        }
                ));
        // mua ngay
//        GioHang gioHang = new GioHang();
        gioHang.setTensanpham(sanPhamMoi.getTensanpham());
        gioHang.setHinhanh(sanPhamMoi.getHinhanh());
        gioHang.setIdsp(sanPhamMoi.getId());
        gioHang.setGiasp(giamoi);
        //Thanh toán

//        // Lấy số được chọn từ Spinner
//        int selectedNumber = (int) spinner.getSelectedItem();
//        // Đặt số được chọn vào trường soluong trong đối tượng gioHang
//        gioHang.setSoluong(selectedNumber);
//        // Tạo một danh sách mới để chứa các đối tượng GioHang
//        Utils.mangmuahang2 = new ArrayList<>();
//        // Thêm đối tượng GioHang vào danh sách
//        Utils.mangmuahang2.add(gioHang);

    }
    private void initView(){
        tensp = findViewById(R.id.txttensp);
        daban = findViewById(R.id.txtdaban);
        giasp = findViewById(R.id.txtgiasp);
        mota = findViewById(R.id.txtmotachitiet);
        btnthem = findViewById(R.id.btnthemvaogiohang);
        spinner = findViewById(R.id.spinner);
        imghinhanh = findViewById(R.id.imgchitiet);
        anhshop=findViewById(R.id.imgshop);
        toolbarctsp = findViewById(R.id.toolbarctsp);
        badge = findViewById(R.id.menu_sl);
        shop = findViewById(R.id.txtshop);
        chat=findViewById(R.id.imgmessctsp);
        contLaymota=findViewById(R.id.mota);
        contLaydanhgia=findViewById(R.id.danhgia);
        danhgia=findViewById(R.id.danhgia_list);
        img_rohang = findViewById(R.id.img_rohang);
        btnmuangay=findViewById(R.id.btngiohangmuangay);
        frameLayoutGioHang =  findViewById(R.id.frameGioHang);
        frameLayoutGioHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent giohang = new Intent(getApplicationContext(), GioHangActivity.class);
                startActivity(giohang);
            }
        });
        if(Utils.manggiohang != null){
            int totalItem = 0;
            for(int i=0; i <Utils.manggiohang.size();i++){
                totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
            }
            badge.setText(String.valueOf(totalItem));
        }
        anhshop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SPShopActivity.class);
                intent.putExtra("id",sanPhamMoi.getIdshop());
                intent.putExtra("tenshop",sanPhamMoi.getUsername());
                startActivity(intent);
            }
        });
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
                intent.putExtra("id",sanPhamMoi.getIdshop());
                intent.putExtra("tensp",sanPhamMoi.getTensanpham());
                //Utils.ID_RECEIVER = String.valueOf(sanPhamMoi.getIdshop());
                //Log.d("ktid",String.valueOf(sanPhamMoi.getIdshop()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        img_rohang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent giohang = new Intent(getApplicationContext(), GioHangActivity.class);
                startActivity(giohang);
            }
        });
        //danh giá
        recyclerView=findViewById(R.id.RC_Danhgia);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
    }

    private void ActionToolBar(){
        setSupportActionBar(toolbarctsp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarctsp.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        if(Utils.manggiohang != null) {
            int totalItem = 0;
            for (int i = 0; i < Utils.manggiohang.size(); i++) {
                totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
            }
            badge.setText(String.valueOf(totalItem));
            super.onResume();
        }
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
    @Override
    protected void onDestroy() {
        capnhatgiohang();
        super.onDestroy();
    }
}