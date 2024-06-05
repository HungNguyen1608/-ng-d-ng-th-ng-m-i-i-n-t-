package com.example.app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.icu.number.CompactNotation;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.app.R;
import com.example.app.adapter.ChiTietAdapter;
import com.example.app.adapter.DonHangAdapter;
import com.example.app.adapter.QldonhanguserAdapter;
import com.example.app.model.DonHang;
import com.example.app.retrofit.ApiBanHang;
import com.example.app.retrofit.RetrofitClient;
import com.example.app.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class XemDonHangActivity extends AppCompatActivity {
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    RecyclerView recyclerView;
    Toolbar toolbar;

    Spinner spinner;
    Integer kt = 0;

    TextView txtdanhgia,txtkodon;
//            , txtchoxacnha, txtdaxaccnhan, txtdanggia, txtdagia0, txtthanhcong, txtdahuy;

    Map<Integer, String> valueMap; // Biến toàn cục để lưu trữ giá trị


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xem_don_hang);
        initView();
        initToolbar();
        initData();
        Log.d("kiemtrare1",String.valueOf(Utils.user_current.getId()));
    }

    private void initData() {
        Map<Integer, String> valueMap = new HashMap<>();
        valueMap.put(0, "Đơn hàng chờ xử lý");
        valueMap.put(1, "Đơn hàng đã xác nhận");
        valueMap.put(2, "Đơn hàng đang giao");
        valueMap.put(3, "Đơn hàng đã giao");
        valueMap.put(4, "Đơn hàng đã hủy");
        valueMap.put(5, "Hoàn hàng");
        valueMap.put(6, "Hoàn thành");

        List<String> displayValues = new ArrayList<>(valueMap.values());
       LinearLayout buttonLayout = findViewById(R.id.linearLayout2);

        // Khởi tạo các nút và đặt văn bản cho chúng
        for (int i = 0; i < valueMap.size(); i++) {
            Button button = new Button(this);
            button.setText(valueMap.get(i)); // Đặt văn bản cho nút từ valueMap
            final int index = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleButtonClick(index);
                }
            });
           buttonLayout.addView(button); // Thêm nút vào layout của bạn
        }
    }

    // Phương thức để xử lý sự kiện khi nút được nhấn
    private void handleButtonClick(int index) {
        switch (index) {
            case 0:
                // Xử lý Đơn hàng chờ xử lý
                getDataRecycleView(0);
                break;
            case 1:
                // Xử lý Đơn hàng đã xác nhận
                getDataRecycleView(1);
                break;
            case 2:
                // Xử lý Đơn hàng đang giao
                getDataRecycleView(2);
                break;
            case 3:
                // Xử lý Đơn hàng đã giao
                getDataRecycleView(3);
                break;
            case 4:
                // Xử lý Đơn hàng đã hủy
                getDataRecycleView(4);
                break;
            case 5:
                // Xử lý Hoàn hàng
                getDataRecycleView(5);
                break;
            case 6:
                // Xử lý Hoàn thành
                getDataRecycleView(6);
                break;
            default:
                break;
        }
    }
    private void getDataRecycleView(int trangthai) {
        compositeDisposable.add(apiBanHang.xemDonHanguser(Utils.user_current.getId(), trangthai)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        itemModel -> {
                            // Kiểm tra xem danh sách kết quả có rỗng không
                            if (itemModel != null && itemModel.getResult() != null && !itemModel.getResult().isEmpty()) {
                                // Nếu có dữ liệu, cập nhật RecyclerView với Adapter mới
                                kt = 1;
                                QldonhanguserAdapter adapter = new QldonhanguserAdapter(itemModel.getResult(), getApplicationContext());
                                recyclerView.setAdapter(adapter);
                                txtkodon.setVisibility(View.GONE);
                            } else {
                                QldonhanguserAdapter adapter = new QldonhanguserAdapter(itemModel.getResult(), getApplicationContext());
                                kt = 2;
                                recyclerView.setAdapter(adapter);
                                txtkodon.setVisibility(View.VISIBLE);
                            }
                            Log.d("kiemtrare",String.valueOf(kt));
                        }, throwable -> {
                            // Xử lý lỗi trong trường hợp không thể kết nối hoặc lỗi từ server
                            Log.e("QlDH", throwable.getMessage());
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView() {
//        txtchoxacnha = findViewById(R.id.txtchoxacnhan);
//        txtdaxaccnhan = findViewById(R.id.txtdaxacnhan);
//        txtdanggia = findViewById(R.id.txtdanggiao);
//        txtdagia0 = findViewById(R.id.txtdagiaohang);
//        txtthanhcong = findViewById(R.id.txtgiaohangtc);
//        txtdahuy = findViewById(R.id.txtdonhuy);

        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        recyclerView = findViewById(R.id.recycleview_donhang);
        toolbar = findViewById(R.id.toolbar);
        // LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        txtkodon = findViewById(R.id.item_trangthai);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
