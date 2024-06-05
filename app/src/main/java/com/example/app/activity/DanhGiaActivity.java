package com.example.app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.app.R;
import com.example.app.model.Item;
import com.example.app.retrofit.ApiBanHang;
import com.example.app.retrofit.RetrofitClient;
import com.example.app.utils.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import android.text.TextUtils;
import androidx.appcompat.widget.Toolbar;


public class DanhGiaActivity extends AppCompatActivity {
    ImageView image;
    Item item;
    EditText eddanhgia;
    TextView txtten;
    TextView txtsoluong;
    TextView txttrangthai;
    TextView txtdiachi;
    TextView txttongtien;
    TextView txtsodt;
    RecyclerView recyclerView;
    ConstraintLayout btngiuphanhoi;
    ApiBanHang apiBanHang;
    Toolbar toolbar;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_gia);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        initView();
        initControl();
        initData();
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

    private void initControl() {
        if (eddanhgia != null) {
            btngiuphanhoi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String danhgiaText = eddanhgia.getText().toString();
                    if (TextUtils.isEmpty(danhgiaText)) {
                        Toast.makeText(getApplicationContext(), "Bạn chưa nhập đánh giá sản phẩm", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Tiếp tục với quá trình xử lý khi đã có đánh giá
                    compositeDisposable.add(apiBanHang.hoanthanhdon(Integer.parseInt(String.valueOf(item.getIddonhang())), Integer.parseInt(String.valueOf(item.getIdsp())))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    thongTinThemModel -> {
                                        if (thongTinThemModel.isSuccess()) {
                                            Toast.makeText(getApplicationContext(), thongTinThemModel.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    },
                                    throwable -> {
                                        Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                            ));
                    // Đánh giá sản phẩm
                    compositeDisposable.add(apiBanHang.danhgia(String.valueOf(item.getIdsp()),String.valueOf(Utils.user_current.getId()), danhgiaText)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    thongTinThemModel -> {
                                        if (thongTinThemModel.isSuccess()) {
                                            Toast.makeText(getApplicationContext(), thongTinThemModel.getMessage(), Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), XemDonHangActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    },
                                    throwable -> {
                                        Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                            ));
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập đánh giá sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }


    private void initView() {
        image = findViewById(R.id.item_img);
        txtten = findViewById(R.id.item_tensp);
        txtdiachi =findViewById(R.id.item_diachi);
        txttongtien =findViewById(R.id.item_tongtien);
        txtsodt = findViewById(R.id.item_sodt);
        txtsoluong = findViewById(R.id.item_soluong);
        btngiuphanhoi=findViewById(R.id.giuphanhoi);
        eddanhgia=findViewById(R.id.edit_danhgia);
        toolbar = findViewById(R.id.toolbar);
    }

    private void initData() {
        item =(Item) getIntent().getSerializableExtra("item");
        if (txtdiachi != null) {
            txtdiachi.setText("Địa chỉ: " + item.getDiachi());
        } else {
            // Xử lý trường hợp txtdiachi là null
        }
        txtten.setText(item.getTensanpham() + " ");
        txtsoluong.setText("Số lượng: "+item.getSoluong() + " ");
        int gia= Integer.parseInt(item.getGia());
        int sl=Integer.parseInt(String.valueOf(item.getSoluong()));
        int tongtien=gia*sl;
        txttongtien.setText("Tổng tiền: "+tongtien );
        //Glide.with(context).load(item.getHinhanh()).into(holder.imagechitiet);
        if(item.getHinhanh().contains("http")){
            Glide.with(getApplicationContext()).load(item.getHinhanh()).into(image);
        } else {
            String hinh = Utils.BASE_URL + "images/" +item.getHinhanh();
            Glide.with(getApplicationContext()).load(hinh).into(image);
        }

    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}