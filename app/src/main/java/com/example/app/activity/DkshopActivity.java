package com.example.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.app.R;
import com.example.app.adapter.UserAdapter;
import com.example.app.model.User;
import com.example.app.model.UserModel;
import com.example.app.retrofit.ApiBanHang;
import com.example.app.retrofit.RetrofitClient;
import com.example.app.utils.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DkshopActivity extends AppCompatActivity {
    UserAdapter userAdapter;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
    CheckBox item_checkbox;
    UserModel username;
    ImageView avatar;
    String email1,ten1;
    EditText ten;
    String str_email;
    String str_pass;
    String str_diachi;
    String str_mobile;
    Toolbar toolbar;
    Button btndkshop,btnchonanh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dkshop);
        initView();
        initcontroll();
        capnhapttshop();
        ActionToolBar();
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
    private void capnhapttshop() {
        item_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    btndkshop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String str_username = ten.getText().toString().trim();
                            compositeDisposable.add(apiBanHang.dkshop(str_email,str_pass,str_username,str_mobile,str_diachi)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            userModel -> {
                                                if(userModel.isSuccess()){
                                                    Toast.makeText(getApplicationContext(),userModel.getMessage(),Toast.LENGTH_LONG).show();
                                                    //thiêt lâp thông tin
                                                    Utils.user_current.setEmail(str_email);
                                                    Utils.user_current.setPass(str_pass);
                                                    Utils.user_current.setDiachi(str_diachi);
                                                    Utils.user_current.setUsername(str_username);
                                                    Utils.user_current.setMobile(str_mobile);
                                                    Intent intent = new Intent(getApplicationContext(), ThongtinUserActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }else{
                                                    Toast.makeText(getApplicationContext(),userModel.getMessage(),Toast.LENGTH_LONG).show();
                                                }
                                            },
                                            throwable -> {

                                            }
                                    ));

                        }
                    });


                }else {

                }

            }
        });

    }


    private void initcontroll() {
        email1= Utils.user_current.getEmail();
        compositeDisposable.add(apiBanHang.thongtinuser(email1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModle -> {
                            if (userModle.isSuccess()) {
                                User user = userModle.getResult().get(0);
                                 str_email=user.getEmail();
                                 str_pass=user.getPass();
                                 str_diachi=user.getDiachi();
                                 str_mobile=user.getMobile();
                                ten.setText(user.getUsername());
                                // Hiển thị avata bằng Glide
                                Glide.with(this).load(user.getAvartar()).into(avatar);

                            }

                        }
                        ,
                        throwable -> {

                        }
                )
        );


    }

    private void initView() {

        item_checkbox=findViewById(R.id.cbshop);
        btndkshop=findViewById(R.id.btndkshop);
        ten=findViewById(R.id.tenshop);
        avatar=findViewById(R.id.imgavatar);
        toolbar=findViewById(R.id.toolbardkshop);
        
    }
}