package com.example.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.app.R;
import com.example.app.adapter.UserAdapter;
import com.example.app.model.User;
import com.example.app.retrofit.ApiBanHang;
import com.example.app.retrofit.RetrofitClient;
import com.example.app.utils.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ThongtinUserActivity extends AppCompatActivity {
    TextView username,email,sdt,diachi,id;
    ImageView anhuser;
    Toolbar toolbar;
    String email1,username1;
    UserAdapter userAdapter;
    Button btneditTT,btnDKshop,btnttdonhang, btnxoatk;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thongtin_user);
        initView();
        initControl();
        ActionToolBar();
      //  qldonhang();
    }

    private void qldonhang() {
        btnttdonhang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), QLDHActivity.class);
                // Đặt cờ để xác định rằng Activity sẽ làm mới, trong trường hợp là MainActivity đã được khởi động trước đó
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // Khởi động MainActivity
                startActivity(intent);
                // Kết thúc Activity hiện tại
                finish();
            }
        });
    }

    private void ActionToolBar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                // Đặt cờ để xác định rằng Activity sẽ làm mới, trong trường hợp là MainActivity đã được khởi động trước đó
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // Khởi động MainActivity
                startActivity(intent);
                // Kết thúc Activity hiện tại
                finish();

            }
        });
    }
    private void initControl() {
        email1=Utils.user_current.getEmail();
        compositeDisposable.add(apiBanHang.thongtinuser(email1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModle -> {
                            if (userModle.isSuccess()) {
                                User user = userModle.getResult().get(0);
                                diachi.setText(user.getDiachi());
                                username.setText(user.getUsername());
                                sdt.setText(user.getMobile());
                                // Hiển thị avata bằng Glide
                                if (user.getAvartar() != null) {
                                    if (user.getAvartar().contains("http")) {
                                        Glide.with(this).load(user.getAvartar()).into(anhuser);
                                    } else {
                                        String hinh = Utils.BASE_URL + "images/" + user.getAvartar();
                                        Glide.with(this).load(hinh).into(anhuser);
                                    }
                                }
                                // Glide.with(this).load(user.getAvatar()).into(anhuser);
                                id.setText(String.valueOf(user.getId()));

                            }

                        }
                        ,
                        throwable -> {

                        }
                )
        );
        //lấy thông tin người dùng
        email.setText(Utils.user_current.getEmail());
//        sdt.setText(Utils.user_current.getMobile());
//        username.setText(Utils.user_current.getUsername());
        btneditTT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),CapnhapttActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnDKshop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), DkshopActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnxoatk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), XoaTKActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        // Gán các View với id tương ứng
        toolbar=findViewById(R.id.toolbar);
        username = findViewById(R.id.profile_name);
        email = findViewById(R.id.profile_email);
        sdt = findViewById(R.id.profile_phone);
        id = findViewById(R.id.profile_userID);
        diachi = findViewById(R.id.profile_diachi);
        anhuser =findViewById(R.id.profile_avatar); // Gán ImageView anhuser với id profile_avatar
        btnDKshop = findViewById(R.id.dkshop);
        btneditTT = findViewById(R.id.edit_profile_btn);
       // btnttdonhang=findViewById(R.id.donhang_TT);
        btnxoatk = findViewById(R.id.delete_account_btn);

    }


}