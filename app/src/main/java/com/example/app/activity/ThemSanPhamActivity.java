package com.example.app.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.app.R;
import com.example.app.adapter.SanPhamMoiAdapter;
import com.example.app.databinding.ActivityThemSanPhamBinding;
import com.example.app.model.LoaiHang;
import com.example.app.model.MessageModel;
import com.example.app.model.SanPhamMoi;
import com.example.app.retrofit.ApiBanHang;
import com.example.app.retrofit.RetrofitClient;
import com.example.app.utils.Utils;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Url;

public class ThemSanPhamActivity extends AppCompatActivity {
    Spinner spinner;
    ActivityThemSanPhamBinding binding;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    Toolbar toolbar;
    String mediaPath;
    DrawerLayout drawerLayout;
    Button btnthem;
    EditText tensp,giasp,giamgiasp,mota,hinh,soluong;
    int loai = 0;
    SanPhamMoi suasp;
    boolean kt= false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityThemSanPhamBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_them_san_pham);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        Intent intent = getIntent();
        suasp = (SanPhamMoi) intent.getSerializableExtra("sua");
        if(suasp != null){
            //sua sp
            kt = true;
            binding.btnThem.setText("Sửa sản phẩm");
            binding.edtensp.setText(suasp.getTensanpham());
            binding.edgiasp.setText(suasp.getGiasp());
            binding.edhinhanh.setText(suasp.getHinhanh());
            binding.edsoluongton.setText(String.valueOf(suasp.getSoluongton()));
            binding.edmota.setText(suasp.getMota());
            binding.spinnerloai.setSelection(suasp.getLoai());
            binding.edgiamgia.setText(String.valueOf(suasp.getGiamgia()));
        }else{
            //themsp
            kt = false;
        }
        initView();
        initData();
        ActionBar();
    }
    private void ActionBar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),QLDHActivity.class);
                // Đặt cờ để xác định rằng Activity sẽ làm mới, trong trường hợp là MainActivity đã được khởi động trước đó
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // Khởi động MainActivity
                startActivity(intent);
                // Kết thúc Activity hiện tại
                finish();
            }
        });
    }
    private void initData() {
        List<String> stringList = new ArrayList<>();
        //lay ten loại hàng
        compositeDisposable.add(apiBanHang.getloaihang()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        loaiHangModel -> {
                            List<LoaiHang> loaiHangList = loaiHangModel.getResult();
                            List<String> tenLoaiList = new ArrayList<>();
                            for (LoaiHang loaiHang : loaiHangList) {
                                tenLoaiList.add(loaiHang.getTenloai());
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tenLoaiList);
                            spinner.setAdapter(adapter);
                        },
                        throwable -> {
                            // Xử lý khi có lỗi xảy ra trong quá trình gọi API
                        }
                ));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loai = i+1;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                themsanpham();
            }
        });
        binding.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(ThemSanPhamActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
        binding.btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(kt == false) {
                    themsanpham();
                }else{
                    suasampham();
                }
            }
        });
    }

    private void suasampham() {
        String str_ten = binding.edtensp.getText().toString().trim();
        String str_gia = binding.edgiasp.getText().toString();
        String str_hinhanh = binding.edhinhanh.getText().toString();
        String str_mota = binding.edmota.getText().toString();
        String str_soluongton = binding.edsoluongton.getText().toString();
        String str_giamgia = binding.edgiamgia.getText().toString();
        Integer giamgia = Integer.parseInt(str_giamgia);
        Integer soluongton = Integer.parseInt(str_soluongton);

        if(TextUtils.isEmpty(str_ten) || TextUtils.isEmpty(str_gia) || TextUtils.isEmpty(str_hinhanh) || TextUtils.isEmpty(str_mota) || TextUtils.isEmpty(str_soluongton) || TextUtils.isEmpty(str_giamgia)){
            Toast.makeText(getApplicationContext(),"Vui lòng điền đầy đủ thông tin",Toast.LENGTH_SHORT).show();
        }
                        compositeDisposable.add(apiBanHang.suaSP(suasp.getId(), str_ten,loai+1,str_gia,str_hinhanh,soluongton,Utils.user_current.getId(),str_mota,giamgia)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        messageModel -> {
                                            if(messageModel.isSuccess())
                                            {
                                                Toast.makeText(getApplicationContext(),messageModel.getMessage(),Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(getApplicationContext(),messageModel.getMessage(),Toast.LENGTH_SHORT).show();
                                            }
                                        },throwable -> {
                                            Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                ));

    }

    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK && data != null) {
//            mediaPath = data.getDataString();
//            uploadMultipleFiles();
//            Log.d("log", "onActivityResult" + mediaPath);
//        } else {
//            Log.d("log", "onActivityResult: resultCode != RESULT_OK or data is null");
//        }
//    }
@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK && data != null) {
        Uri selectedImageUri = data.getData();
        if (selectedImageUri != null) {
            mediaPath = getPath(selectedImageUri);
            uploadMultipleFiles();
            Log.d("logg", "onActivityResult" + mediaPath);
        } else {
            Log.d("logg", "Selected image URI is null");
        }
    } else {
        Log.d("logg", "onActivityResult: resultCode != RESULT_OK or data is null");
    }
}

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
        Call<MessageModel> call = apiBanHang.uploadFile(fileToUpload1);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(Call < MessageModel > call, Response< MessageModel > response) {
                MessageModel messageModel = response.body();
                if (messageModel != null) {
                    if (messageModel.isSuccess()) {
                        binding.edhinhanh.setText(messageModel.getName());
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

    private void themsanpham() {
        String str_ten = binding.edtensp.getText().toString().trim();
        String str_gia = binding.edgiasp.getText().toString();
        String str_hinhanh = binding.edhinhanh.getText().toString();
        String str_mota = binding.edmota.getText().toString();
        String str_soluongton = binding.edsoluongton.getText().toString();
        String str_giamgia = binding.edgiamgia.getText().toString();

        // Kiểm tra nếu các trường thông tin đều đã được điền
        if (TextUtils.isEmpty(str_ten) || TextUtils.isEmpty(str_gia) || TextUtils.isEmpty(str_hinhanh) || TextUtils.isEmpty(str_mota) || TextUtils.isEmpty(str_soluongton) || TextUtils.isEmpty(str_giamgia)) {
            Toast.makeText(getApplicationContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return; // Thoát khỏi phương thức nếu có trường thông tin nào chưa điền
        }

        // Kiểm tra nếu giá và giảm giá là số
        if (!TextUtils.isDigitsOnly(str_gia) || !TextUtils.isDigitsOnly(str_giamgia)) {
            Toast.makeText(getApplicationContext(), "Nhập lại giá và giảm giá sản phẩm", Toast.LENGTH_SHORT).show();
            return; // Thoát khỏi phương thức nếu giá hoặc giảm giá không phải là số
        }

        // Chuyển đổi chuỗi thành số nguyên
        int giaSanPham = Integer.parseInt(str_gia);
        int giaGiamGia = Integer.parseInt(str_giamgia);

        // Kiểm tra nếu giảm giá nhỏ hơn giá sản phẩm
        if (giaGiamGia >= giaSanPham) {
            Toast.makeText(getApplicationContext(), "Nhập lại giảm giá sản phẩm", Toast.LENGTH_SHORT).show();
            return; // Thoát khỏi phương thức nếu giảm giá không hợp lệ
        }

        // Tiếp tục xử lý khi thông tin hợp lệ
        int soluongton = Integer.parseInt(str_soluongton);
        compositeDisposable.add(apiBanHang.themSP(str_ten, loai, str_gia, str_hinhanh, soluongton, Utils.user_current.getId(), str_mota, giaGiamGia)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            if (messageModel.isSuccess()) {
                                Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), QLSPActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }, throwable -> {
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }


    private void initView() {
        spinner = findViewById(R.id.spinnerloai);

    }

    @Override
    public void onDetachedFromWindow() {
        compositeDisposable.clear();
        super.onDetachedFromWindow();
    }
}