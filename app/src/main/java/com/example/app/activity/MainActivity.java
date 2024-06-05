package com.example.app.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.app.R;
import com.example.app.adapter.GioHangAdapter;
import com.example.app.adapter.LoaiSpAdapter;
import com.example.app.adapter.SanPhamMoiAdapter;
import com.example.app.adapter.UserAdapter;
import com.example.app.model.BanchayAdapter;
import com.example.app.model.LoaiSp;
import com.example.app.model.LoaiSpModel;
import com.example.app.model.SanPhamMoi;
import com.example.app.model.SanPhamMoiModel;
import com.example.app.model.User;
import com.example.app.model.UserModel;
import com.example.app.retrofit.ApiBanHang;
import com.example.app.retrofit.RetrofitClient;
import com.example.app.utils.Utils;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nex3z.notificationbadge.NotificationBadge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;



public class MainActivity extends AppCompatActivity {
    ListView listViewManHinhChinh2;
    UserAdapter userAdapter;
    List<User> mangtaikhoan;
    String email, username;
    Toolbar toolbar;
    ImageSlider image_slider;
    //ViewFlipper viewFlipper;
    RecyclerView recyclerViewManHinhChinh,recyclerViewManHinhChinh2;
    TextView txtbchay;
    NavigationView navigationView;
    ListView listViewManHinhChinh;
    DrawerLayout drawerLayout;
    LoaiSpAdapter loaiSpadapter;
    List<LoaiSp> mangloaisp;
    //LoaiSpModel loaiSpModel;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);

    List<SanPhamMoi> mangSpMoi;
    SanPhamMoiAdapter spAdapter;
    NotificationBadge badge;
    FrameLayout frameLayout;
    ImageView imgsearch,imgmess;

    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Tạo Adapter với viewType là VIEW_TYPE_2
        com.example.app.adapter.BanchayAdapter adapter = new com.example.app.adapter.BanchayAdapter(this, new ArrayList<SanPhamMoi>(), com.example.app.adapter.BanchayAdapter.VIEW_TYPE_1);
        Anhxa();
        //ActionViewFlipper();
        ActionBar();
        Paper.init(this);
        if(Paper.book().read("user") != null){
            User user = Paper.book().read("user");
            Utils.user_current = user;
        }
        Log.d("idlay",String.valueOf(Utils.user_current.getId()));
        getToken();
        if (isConnected(this)) {
            //Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_LONG).show();
            LaySLGH();
            ActionViewFlipper();
            getLoaiSanPham();
            getSpMoi();
            getbanchay();
            getuser();
            getEventClick();
        } else {
            Toast.makeText(getApplicationContext(), "ko co internet", Toast.LENGTH_LONG).show();
        }
        if(Utils.user_current.getLoai() == 1){
            flag = true;
        }else{
            flag = false;
        }

    }

    private void LaySLGH() {
        compositeDisposable.add(apiBanHang.xemGioHang(Utils.user_current.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        gioHangModel -> {
                            Utils.manggiohang = gioHangModel.getResult();
                            Log.d("MainActivity", "Utils.manggiohang: " + gioHangModel.getResult().toString()+ Utils.user_current.getId()); // Thêm dòng này
                            updateBadge();
                        },throwable -> {
                            Log.e("XemGioHangActivity", throwable.getMessage());
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));

    }
    private void getToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if(!TextUtils.isEmpty(s)){
                            compositeDisposable.add(apiBanHang.capNhatTB(Utils.user_current.getId(),s)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            messageModel -> {

                                            },throwable -> {
                                                //Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_LONG).show();
                                            }
                                    ));
                        }
                    }
                });
    }
    private void updateBadge() {
        int totalItem = 0;
        for (int i = 0; i < Utils.manggiohang.size(); i++) {
            totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
        }
        badge.setText(String.valueOf(totalItem));
    }


    private void getEventClick()
    {
        listViewManHinhChinh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        Intent trangchu = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(trangchu);
                        break;
                    case 1:
                        Intent doientu = new Intent(getApplicationContext(),DienThoaiActivity.class);
                        doientu.putExtra("loai",1);
                        startActivity(doientu);
                        break;
                    case 2:
                        Intent laptop = new Intent(getApplicationContext(),DienThoaiActivity.class);
                        laptop.putExtra("loai",2);
                        startActivity(laptop);
                        break;
                    case 3:
                        Intent chat = new Intent(getApplicationContext(), LienHeActivity.class);
                        startActivity(chat);
                        break;
                    case 4:
                        Intent thongtin = new Intent(getApplicationContext(),ThongtinUserActivity.class);
                        startActivity(thongtin);
                        break;
                    case 5:
                        Intent xemdon = new Intent(getApplicationContext(),XemDonHangActivity.class);
                        startActivity(xemdon);
                        break;
                    case 6:
                        if(flag) {
                            Intent qlsp = new Intent(getApplicationContext(), QLSPActivity.class);
                            startActivity(qlsp);
                        }
                        else{
                            Paper.book().delete("user");
                            Utils.user_current = null;
                            boolean check = false;
                            if(Utils.user_current == null){
                                check = true;
                            }
                            Utils.manggiohang.clear();
                            Log.d("kiemtrauser",String.valueOf(check));
                            FirebaseAuth.getInstance().signOut();
                            LoginManager.getInstance().logOut();
                            Intent dangnhap = new Intent(getApplicationContext(),DangNhapActivity.class);
                            startActivity(dangnhap);
                            finish();
                        }
                        break;
                    case 7:
                        Intent qldh = new Intent(getApplicationContext(),QLDHActivity.class);
                        startActivity(qldh);
                        break;
                    case 8:
                        Intent thongke = new Intent(getApplicationContext(),ThongKeActivity.class);
                        startActivity(thongke);
                        break;
                    case 9:
                        Paper.book().delete("user");
                        Utils.user_current = null;
                        Utils.manggiohang.clear();
                        FirebaseAuth.getInstance().signOut();
                        LoginManager.getInstance().logOut();
                        Intent dangnhap = new Intent(getApplicationContext(),DangNhapActivity.class);
                        startActivity(dangnhap);
                        finish();
                        break;
                }
            }
        });
    }
    private void getSpMoi()
    {
        compositeDisposable.add(apiBanHang.getSpMoi(Utils.user_current.getId())
                .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                sanPhamMoiModel -> {
                                    if(sanPhamMoiModel.isSuccess()){
                                        Log.d("log", String.valueOf(sanPhamMoiModel.getResult().toString()));
                                        mangSpMoi = sanPhamMoiModel.getResult();
                                        spAdapter = new SanPhamMoiAdapter(getApplicationContext(),mangSpMoi);
                                        recyclerViewManHinhChinh.setAdapter(spAdapter);
                                    }
                                },
                                throwable -> {
                                    Toast.makeText(getApplicationContext(),"Không kết nối được với server0"+ throwable.getMessage(),Toast.LENGTH_LONG).show();
                                }
                        )
                );
    }
    private void getLoaiSanPham(){
        compositeDisposable.add(apiBanHang.getLoaiSp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        loaiSpModel -> {
                            if (loaiSpModel.isSuccess()) {
                                mangloaisp = loaiSpModel.getResult();
                                Log.d("logggg",String.valueOf(Utils.user_current.getLoai()));
                                if(flag){
                                    mangloaisp.add(new LoaiSp("Quản lý sản phẩm","https://cdn-icons-png.freepik.com/512/7656/7656430.png"));
                                    mangloaisp.add(new LoaiSp("Quản lý đơn hàng","https://boxme.asia/vi/wp-content/uploads/sites/7/2020/07/icon-12.1.png"));
                                    mangloaisp.add(new LoaiSp("Thống kê doanh thu","https://cdn.pixabay.com/photo/2021/02/19/13/13/graph-6030184_960_720.png"));
                                }
                                mangloaisp.add(new LoaiSp("Đăng xuất","https://cdn-icons-png.flaticon.com/512/5543/5543009.png"));
                                loaiSpadapter = new LoaiSpAdapter(getApplicationContext(), mangloaisp);
                                listViewManHinhChinh.setAdapter(loaiSpadapter);
                            }
                        },
                        throwable -> {
                        }
                )
        );
    }

    //slide
    private void ActionViewFlipper() {
        List<SlideModel> image = new ArrayList<>();
        image.add( new SlideModel("https://cdn2.cellphones.com.vn/insecure/rs:fill:690:300/q:90/plain/https://dashboard.cellphones.com.vn/storage/GALAXY-AI-WEEK-homepage.png", null));
        image.add( new SlideModel("https://cdn2.cellphones.com.vn/insecure/rs:fill:690:300/q:90/plain/https://dashboard.cellphones.com.vn/storage/Sliding%20air%2013mb.png", null));
        image.add(new SlideModel("https://cdn2.cellphones.com.vn/insecure/rs:fill:690:300/q:90/plain/https://dashboard.cellphones.com.vn/storage/thu-cu-dong-ho-sliding.jpg", null));
        image_slider.setImageList(image, ScaleTypes.CENTER_CROP);

    }

    private void ActionBar() {
        toolbar = findViewById(R.id.toolbarmanhinhchinh);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void Anhxa() {
        //ban chay
        txtbchay=findViewById(R.id.txtbanchay);
        //
        recyclerViewManHinhChinh2 = findViewById(R.id.recycleview1);
        RecyclerView.LayoutManager layoutManager1 = new GridLayoutManager(this, 5, GridLayoutManager.VERTICAL, false);
        recyclerViewManHinhChinh2.setLayoutManager(layoutManager1);
        recyclerViewManHinhChinh2.setHasFixedSize(true);
        toolbar = findViewById(R.id.toolbarmanhinhchinh);
        //viewFlipper = findViewById(R.id.viewflipper);
        image_slider = findViewById(R.id.image_slider);
        recyclerViewManHinhChinh = findViewById(R.id.recycleview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerViewManHinhChinh.setLayoutManager(layoutManager);
        recyclerViewManHinhChinh.setHasFixedSize(true);
        listViewManHinhChinh = findViewById(R.id.listviewmanhinhchinh);
        listViewManHinhChinh2 = findViewById(R.id.listviewmanhinhchinh2);
        navigationView = findViewById(R.id.navigationview);
        drawerLayout = findViewById(R.id.drawerLayout);
        badge = findViewById(R.id.menu_sl);
        frameLayout = findViewById(R.id.frameGioHang);
        imgsearch = findViewById(R.id.imgsearch);
        imgmess = findViewById(R.id.imgmess);
        mangloaisp = new ArrayList<>();
        mangSpMoi = new ArrayList<>();
        if(Utils.manggiohang == null){
            Utils.manggiohang = new ArrayList<>();
        }else {
            int totalItem = 0;
            for(int i=0; i <Utils.manggiohang.size();i++){
                totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
            }
            badge.setText(String.valueOf(totalItem));
        }
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent giohang = new Intent(getApplicationContext(), GioHangActivity.class);
                startActivity(giohang);
            }
        });
        imgsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,TimKiemActivity.class);
                startActivity(intent);
            }
        });
        imgmess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ChatMainActivity.class);
                startActivity(intent);
            }
        });
        //ban chay
        txtbchay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),BanchayActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        int totalItem = 0;
        for(int i=0; i <Utils.manggiohang.size();i++){
            totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
        }
        badge.setText(String.valueOf(totalItem));
    }

    private boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (wifi != null && wifi.isConnected()) || (mobile != null && mobile.isConnected());
    }
    @Override
    protected void onDestroy(){
        compositeDisposable.clear();
        super.onDestroy();
    }
    private void getuser() {
        email= Utils.user_current.getEmail();
        username= Utils.user_current.getUsername();
        Log.d("ktemail",email);
        //Toast.makeText(getApplicationContext(), email, Toast.LENGTH_LONG).show();
        compositeDisposable.add(apiBanHang.thongtinuser(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if (userModel.isSuccess()) {
                                Log.d("logg", String.valueOf(userModel.getResult().get(0).getLoai()));
                                Utils.user_current.setEmail(userModel.getResult().get(0).getEmail());
                                //Utils.user_current.setPass(userModel.getResult().get(0).getPass());
                                Utils.user_current.setLoai(userModel.getResult().get(0).getLoai());
                                Utils.user_current.setId(userModel.getResult().get(0).getId());
                                mangtaikhoan = userModel.getResult();
                                userAdapter = new UserAdapter(mangtaikhoan, getApplicationContext());
                                listViewManHinhChinh2.setAdapter(userAdapter);
                            }
                        }
                        ,
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "khong ket noi duoc voi server1" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("loitrongmain","Error",throwable);
                        }
                )
        );

    }
    private void getbanchay() {
        compositeDisposable.add(apiBanHang.getbanchay(Utils.user_current.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if (sanPhamMoiModel.isSuccess()) {
                                Log.d("log", String.valueOf(sanPhamMoiModel.getResult().get(0).getLoai()));

                                // Lấy danh sách sản phẩm mới từ kết quả trả về
                                List<SanPhamMoi> mangSpMoi = sanPhamMoiModel.getResult();

                                // Kiểm tra nếu có ít nhất 5 sản phẩm mới
                                if (mangSpMoi.size() >= 5) {
                                    // Tạo danh sách mới chỉ chứa 5 sản phẩm đầu tiên
                                    List<SanPhamMoi> firstFiveProducts = mangSpMoi.subList(0, 5);

                                    // Tạo adapter mới với danh sách này
                                    BanchayAdapter spAdapter = new BanchayAdapter(getApplicationContext(), firstFiveProducts, BanchayAdapter.VIEW_TYPE_1);

                                    // Đặt adapter vào RecyclerView
                                    recyclerViewManHinhChinh2.setAdapter(spAdapter);
                                } else {
                                    // Nếu có ít hơn 5 sản phẩm, hiển thị tất cả
                                    BanchayAdapter spAdapter = new BanchayAdapter(getApplicationContext(), mangSpMoi, BanchayAdapter.VIEW_TYPE_1);
                                    recyclerViewManHinhChinh2.setAdapter(spAdapter);
                                }
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(),"Không kết nối được với server2"+ throwable.getMessage(),Toast.LENGTH_LONG).show();
                        }
                )
        );
    }

}
