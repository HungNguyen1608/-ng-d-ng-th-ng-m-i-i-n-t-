package com.example.app.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.app.R;
import com.example.app.retrofit.ApiBanHang;
import com.example.app.retrofit.RetrofitClient;
import com.example.app.utils.Utils;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class XoaTKActivity extends AppCompatActivity {
    Button btndelete;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xoa_tkactivity);
        initView();
        initControl();
    }

    private void initControl() {
        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });
    }

//    private void showDeleteConfirmationDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Xác nhận xóa tài khoản");
//        builder.setMessage("Bạn có chắc chắn muốn xóa tài khoản?");
//
//        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                // Xử lý khi người dùng đồng ý xóa tài khoản
//                deleteAccount();
//            }
//        });
//
//        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//            }
//        });
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa tài khoản");
        builder.setMessage("Bạn có chắc chắn muốn xóa tài khoản? Nhập mật khẩu để tiếp tục");

        // Thêm một EditText vào AlertDialog để người dùng nhập mật khẩu
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Xử lý khi người dùng đồng ý xóa tài khoản
                String password = input.getText().toString();
                deleteAccountOnFirebase(password);
                Log.d("kiemtraid",String.valueOf(Utils.user_current.getId()));
            }
        });

        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteAccount() {
        //Xoa voi csdl
        compositeDisposable.add(apiBanHang.xoatk(Utils.user_current.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                      userModel -> {
                          if(userModel.isSuccess()){
                              Toast.makeText(getApplicationContext(),userModel.getMessage(),Toast.LENGTH_SHORT).show();
                              Intent intent = new Intent(getApplicationContext(),DangNhapActivity.class);
                              startActivity(intent);
                              finish();
                          }
                      },throwable -> {
                            Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
                            Log.d("loixoa",throwable.getMessage());
                    }
                ));
    }
    private void deleteAccountOnFirebase(String password) {
        // Xác thực người dùng hiện tại
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Xác thực lại tài khoản người dùng (cần để thực hiện các hoạt động như xóa tài khoản)
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
            // Re-authenticate
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Xóa tài khoản người dùng từ Firebase
                            user.delete()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Xóa tài khoản thành công trên Firebase", Toast.LENGTH_SHORT).show();
                                            // Đăng xuất người dùng ra khỏi ứng dụng sau khi xóa tài khoản
                                            FirebaseAuth.getInstance().signOut();
                                            deleteAccount();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Xóa tài khoản thất bại trên Firebase", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(getApplicationContext(), "Xác thực không thành công", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        btndelete = findViewById(R.id.btndelete);

    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}