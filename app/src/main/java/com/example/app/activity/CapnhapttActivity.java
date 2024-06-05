package com.example.app.activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.app.R;
import com.example.app.adapter.UserAdapter;
import com.example.app.databinding.ActivityCapnhatttBinding;
import com.example.app.model.MessageModel;
import com.example.app.model.User;
import com.example.app.retrofit.ApiBanHang;
import com.example.app.retrofit.RetrofitClient;
import com.example.app.utils.Utils;
import com.github.dhaval2404.imagepicker.ImagePicker;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//anh
import com.example.app.databinding.ActivityThemSanPhamBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;

public class CapnhapttActivity extends AppCompatActivity {
    ActivityCapnhatttBinding binding;
    EditText username,sdt,diachi,id,edanh;
    TextView email;
    Toolbar toolbar;
    ImageView anhuser;
    String email1,username1;

    UserAdapter userAdapter;
    Button btncapnhat,btnchonanh, btn_edit_pass;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    //cn anh
    String mediaPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capnhattt);
        //anhh
        binding=ActivityCapnhatttBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();
        initControl();
        capnhatttuser();
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
    private void capnhatttuser() {
        btncapnhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_username = username.getText().toString().trim();
                String str_email = email.getText().toString().trim();
                //String str_pass = pass.getText().toString().trim();
                String str_diachi = diachi.getText().toString().trim();
                String str_mobile = sdt.getText().toString().trim();
                String str_hinhanh= binding.edhinhanhus.getText().toString().trim();
                if(TextUtils.isEmpty((str_email))){
                    Toast.makeText(getApplicationContext(),"Bạn chưa nhập Email",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty((str_username))) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập Username", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty((str_diachi))){
                    Toast.makeText(getApplicationContext(),"Bạn chưa nhập địa chỉ",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty((str_mobile))){
                    Toast.makeText(getApplicationContext(),"Bạn chưa nhập số điện thoại",Toast.LENGTH_SHORT).show();
                }else
                {
                    //post data database
                    compositeDisposable.add(apiBanHang.capnhattt(str_email,"firebase",str_username,str_mobile,str_diachi,str_hinhanh)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    userModel -> {
                                        if(userModel.isSuccess()){
                                            Toast.makeText(getApplicationContext(),userModel.getMessage(),Toast.LENGTH_LONG).show();
                                            Utils.user_current.setEmail(str_email);
                                            Utils.user_current.setPass("firebase");
                                            Utils.user_current.setDiachi(str_diachi);
                                            Utils.user_current.setUsername(str_username);
                                            Utils.user_current.setMobile(str_mobile);
                                            Utils.user_current.setAvartar(str_hinhanh);
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
                    //post data firebase:

                }
            }
        });
        btn_edit_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),QuenMatKhauActivity.class);
                startActivity(intent);
            }
        });

    }
    private void postpass(String pass) {

    }


    private void initControl() {
        email1= Utils.user_current.getEmail();
        compositeDisposable.add(apiBanHang.thongtinuser(email1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModle -> {
                            if (userModle.isSuccess()) {
                                User user = userModle.getResult().get(0);
                                diachi.setText(user.getDiachi());
                                //pass.setText(user.getPass());
                                username.setText(user.getUsername());
                                sdt.setText(user.getMobile());
                                 edanh.setText(user.getAvartar());
                                // Hiển thị avata bằng Glide
                                if (user.getAvartar() != null) {
                                    if (user.getAvartar().contains("http")) {
                                        Glide.with(this).load(user.getAvartar()).into(anhuser);
                                    } else {
                                        String hinh = Utils.BASE_URL + "images/" + user.getAvartar();
                                        Glide.with(this).load(hinh).into(anhuser);
                                    }
                                }
//                                if (edanh==null)
//                                {
//                                    Glide.with(this).load(user.getAvartar()).into(anhuser);
//                                }else {
//                                    String hinh = Utils.BASE_URL + "images/" + user.getAvartar();
//                                    Glide.with(this).load(hinh).into(anhuser);
//                                }
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

        //
        //chon anh
        btnchonanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(CapnhapttActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();

            }
        });

    }
    //anh


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mediaPath=data.getDataString();
        uploadMultipleFiles();

    }

    private void initView() {
        // Gán các View với id tương ứng
        username = findViewById(R.id.edit_tenuser);
        btn_edit_pass = findViewById(R.id.btn_edit_pass);
        email = findViewById(R.id.edit_emailuser);
        toolbar=findViewById(R.id.toolbarcntt);
        sdt = findViewById(R.id.edit_sdt);
        diachi = findViewById(R.id.edit_diachi);
        edanh = findViewById(R.id.edhinhanhus);
        anhuser=findViewById(R.id.img_avatar);
        btncapnhat =findViewById(R.id.btncapnhat);
        btnchonanh =findViewById(R.id.btnchon_anh);

    }
    //anh
    private String getPath(Uri uri){
        String result;
        Cursor cursor = getContentResolver().query(uri,null,null,null, null);
        if(cursor == null){
            result = uri.getPath();
        }else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            cursor.close();
        }
        return result;
    }

    private void uploadMultipleFiles() {
        Uri uri = Uri.parse(mediaPath);
        File file = new File(getPath(uri));
        // Parsing any Media type file
        RequestBody requestBody1 = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload1 = MultipartBody.Part.createFormData("file", file.getName(), requestBody1);
        Call<MessageModel> call = apiBanHang.uploadimguser(fileToUpload1);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(Call < MessageModel > call, Response< MessageModel > response) {
                MessageModel messageModel = response.body();
                if (messageModel != null) {
                    if (messageModel.isSuccess()) {
                        binding.edhinhanhus.setText(messageModel.getName());
                    } else {
                        Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //Log.v("Response", messageModel.toString());
                    if (messageModel != null) {
                        Log.v("Response", messageModel.toString());
                    } else {
                        Log.v("Response", "messageModel is null");
                    }

                }
            }
            @Override
            public void onFailure(Call < MessageModel > call, Throwable t) {
                Log.d("log",t.getMessage());
            }
        });
    }


}