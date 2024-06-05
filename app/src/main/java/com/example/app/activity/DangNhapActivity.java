package com.example.app.activity;
import static android.content.ContentValues.TAG;

import com.example.app.adapter.UserAdapter;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.R;
import com.example.app.model.User;
import com.example.app.model.UserModel;
import com.example.app.retrofit.ApiBanHang;
import com.example.app.retrofit.RetrofitClient;
import com.example.app.utils.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class DangNhapActivity extends AppCompatActivity {
    TextView txtdangki, txtquenmatkhau;
    EditText edEmail, edPass;
    Button btnDangnhap;
    LoginButton btnFacebook;
    SignInButton btnGoogle;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private GoogleSignInClient client;
    boolean isLogin = false;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);
        initView();
        initControl();
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(this, options);
        //khoi tao
        firebaseAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create();

        btnFacebook.setReadPermissions("email", "public_profile");
        btnFacebook.registerCallback(
                callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException error) {
                    }
                });

    }




        private void initView() {
        Paper.init(this);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        txtdangki = findViewById(R.id.txtdangki);
        edEmail = findViewById(R.id.edEmail);
        edPass = findViewById(R.id.edPass);
        btnDangnhap = findViewById(R.id.btndangnhap);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        txtquenmatkhau = findViewById(R.id.txtquenmatkhau);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);
        //doc du lieu
        if(Paper.book().read("email") != null && Paper.book().read("pass") != null){
            //edEmail.setText(Paper.book().read("email"));
            //edPass.setText(Paper.book().read("pass"));
            if(Paper.book().read("islogin") != null){
                boolean flag = Paper.book().read("islogin");
                if(flag){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //dangNhap(Paper.book().read("email"), Paper.book().read("pass"));
                        }
                    },2000);
                }
            }
        }
    }
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:$token");
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            // Lấy thông tin người dùng từ tài khoản Facebook của họ
                            if (user != null) {
                                String uid = user.getUid();
                                String email = user.getEmail();
                                String displayName = user.getDisplayName();
                                Uri photoUrl = user.getPhotoUrl();
                                String phoneNumber = user.getPhoneNumber();
                                if(uid == null){
                                    uid = "";
                                }
                                if(email == null){
                                    email = "";
                                }
                                if(displayName == null){
                                    displayName = "";
                                }
                                if(phoneNumber == null){
                                    phoneNumber = "";
                                }
                                String str_photoUrl = String.valueOf(photoUrl);
                                if(str_photoUrl == null){
                                    str_photoUrl ="";
                                }
                                Utils.user_current.setEmail(email);
                                // Sử dụng thông tin người dùng như cần thiết
                                compositeDisposable.add(apiBanHang.dangnhapGoogle(email, "firebase", displayName, phoneNumber, uid, str_photoUrl)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(
                                                userModel -> {
                                                    if (userModel.isSuccess()) {
                                                        // Chuyển đến màn hình chính sau khi đăng nhập thành công
                                                        Paper.book().write("user", userModel.getResult());
                                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        // Xử lý khi đăng nhập không thành công
                                                        Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_LONG).show();
                                                        //Paper.book().write("user", userModel.getResult());
                                                    }
                                                },
                                                throwable -> {
                                                    // Xử lý lỗi khi gọi API
                                                    Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                                                    Log.e("DangKiActivity", "Error occurred", throwable);
                                                }
                                        ));
                                getUser(email);
                            }
                            //updateUI(user);
                        } else {
                            Log.e("signInWithCredential:failure", task.getException().toString());
                            Toast.makeText(DangNhapActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void getUser(String email) {
        email= Utils.user_current.getEmail();
        Toast.makeText(getApplicationContext(), email, Toast.LENGTH_LONG).show();
        compositeDisposable.add(apiBanHang.thongtinuser(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if (userModel.isSuccess()) {
                                Log.d("logg", String.valueOf(userModel.getResult().get(0).getLoai()));
                                Utils.user_current.setEmail(userModel.getResult().get(0).getEmail());
                                Utils.user_current.setLoai(userModel.getResult().get(0).getLoai());
                                Utils.user_current.setId(userModel.getResult().get(0).getId());
                            }
                        }
                        ,
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "khong ket noi duoc voi server1" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                )
        );

    }
    private void updateUI(FirebaseUser user) {
        if(user != null){
            Intent intent = new Intent(DangNhapActivity.this,MainActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Vui lòng đăng nhập để tiếp tục",Toast.LENGTH_SHORT).show();
        }
    }
    private void dangNhap(String email, String pass){

        compositeDisposable.add(apiBanHang.dangNhap(email, pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if (userModel.isSuccess()) {
                                isLogin = true;
                                Paper.book().write("islogin", isLogin);
                                Utils.user_current = userModel.getResult().get(0);
                                Utils.user_current.setPass(pass);
                                //luu tt nguoi dung
                                Paper.book().write("user", userModel.getResult().get(0));
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }, throwable -> {
                            Log.e("DangNhapActivity", "Error occurred", throwable);
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }


    private void initControl() {
        txtdangki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),DangKiActivity.class);
                startActivity(intent);
            }
        });
        txtquenmatkhau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),QuenMatKhauActivity.class);
                startActivity(intent);
            }
        });
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = client.getSignInIntent();
                startActivityForResult(intent,1234);
            }
        });

        btnDangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_email = edEmail.getText().toString().trim();
                String str_pass = edPass.getText().toString().trim();
                if (TextUtils.isEmpty((str_email))) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập Email", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty((str_pass))) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập Pass", Toast.LENGTH_SHORT).show();
                } else {
                    Paper.book().write("email", str_email);
                    Paper.book().write("pass", str_pass);
//                    if(firebaseUser != null){
                    //user da dang nhap firebase
                    //dangNhap(str_email,str_pass);

//                    }else{
                    firebaseAuth.signInWithEmailAndPassword(str_email, str_pass)
                            .addOnCompleteListener(DangNhapActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        dangNhap(str_email, str_pass);
                                    }else {
                                        Toast.makeText(getApplicationContext(),"Tài khoản hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                //dangNhap(str_email,str_pass);
//                }
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        //sau khi dang nhap thanh cong parameter sang dang nhap
//        if(Utils.user_current.getEmail() != null && Utils.user_current.getPass() != null)
//        {
//            edEmail.setText(Utils.user_current.getEmail());
//            edPass.setText(Utils.user_current.getPass());
//        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1234){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    // Sau khi đăng nhập bằng tài khoản Google
                                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                    if (firebaseUser != null) {
                                        String uid = firebaseUser.getUid();
                                        String email = firebaseUser.getEmail();
                                        String displayName = firebaseUser.getDisplayName();
                                        String phoneNumber = firebaseUser.getPhoneNumber();
                                        Uri photoUrl = firebaseUser.getPhotoUrl();
                                        Utils.user_current.setEmail(email);
                                        Utils.user_current.setUid(uid);
                                        Log.d("checkuser", uid+email+displayName+phoneNumber+(photoUrl));
                                        if(uid == null){
                                            uid = "";
                                        }
                                        if(email == null){
                                            email = "";
                                        }
                                        if(displayName == null){
                                            displayName = "";
                                        }
                                        if(phoneNumber == null){
                                            phoneNumber = "";
                                        }
                                        String str_photoUrl = String.valueOf(photoUrl);
                                        if(str_photoUrl == null){
                                            str_photoUrl ="";
                                        }
                                        Paper.book().write("email",email);
                                        // Đăng nhập thành công bằng tài khoản Google, gửi dữ liệu lên server
                                        compositeDisposable.add(apiBanHang.dangnhapGoogle(email, "firebase", displayName, phoneNumber, uid, str_photoUrl)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(
                                                        userModel -> {
                                                            if (userModel.isSuccess()) {
                                                                // Chuyển đến màn hình chính sau khi đăng nhập thành công
                                                                Paper.book().write("user", userModel.getResult());
                                                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            } else {
                                                                // Xử lý khi đăng nhập không thành công
                                                                Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_LONG).show();
                                                                //Paper.book().write("user", userModel.getResult());
                                                            }
                                                        },
                                                        throwable -> {
                                                            // Xử lý lỗi khi gọi API
                                                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                                                            Log.e("DangKiActivity1", "Error occurred", throwable);
                                                        }
                                                ));
                                        getUser(email);
                                    }
//                                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//                                    startActivity(intent);
                                }else {
                                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }catch (ApiException e){
                e.printStackTrace();
            }
        }
    }
}