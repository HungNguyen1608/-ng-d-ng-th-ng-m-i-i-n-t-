package com.example.app.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.adapter.ChiTietAdapter;
import com.example.app.adapter.DonHangAdapter;
import com.example.app.model.DonHangModel;
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

public class QLDHActivity extends AppCompatActivity {

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    RecyclerView recyclerView;
    Toolbar toolbar;
    Spinner spinner;
    TextView txtkodon;
    Map<Integer, String> valueMap; // Biến toàn cục để lưu trữ giá trị của Spinner


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qldonhang);
        initView();
        initData();
        initToolbar();
        initSpinnerListener();
        getDataRecycleView(); // Load dữ liệu ban đầu
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, displayValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0); // Chọn giá trị mặc định là 0
        this.valueMap = valueMap;

    }

    private void initSpinnerListener() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                getDataRecycleView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Không cần xử lý trong trường hợp này
            }
        });
    }

    private void getDataRecycleView() {
        String selectedValue = spinner.getSelectedItem().toString();
        int trangthai = 0; // Giá trị mặc định nếu không thể chuyển đổi được
        try {
            // Tìm kiếm giá trị trong Map
            for (Map.Entry<Integer, String> entry : valueMap.entrySet()) {
                if (entry.getValue().equals(selectedValue)) {
                    trangthai = entry.getKey();
                    break;
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        compositeDisposable.add(apiBanHang.xemDonHangshop(Utils.user_current.getId(), trangthai)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        itemModel -> {
                            // Kiểm tra xem danh sách kết quả có rỗng không
                            if (itemModel != null && itemModel.getResult() != null && !itemModel.getResult().isEmpty()) {
                                // Nếu có dữ liệu, cập nhật RecyclerView với Adapter mới
                                ChiTietAdapter adapter = new ChiTietAdapter(itemModel.getResult(), getApplicationContext());
                                recyclerView.setAdapter(adapter);
                                txtkodon.setVisibility(View.GONE);
                            } else {
                                ChiTietAdapter adapter = new ChiTietAdapter(itemModel.getResult(), getApplicationContext());
                                recyclerView.setAdapter(adapter);
                                txtkodon.setVisibility(View.VISIBLE);
                            }
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
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        recyclerView = findViewById(R.id.recycleview);
        toolbar = findViewById(R.id.toolbar);
        spinner = findViewById(R.id.spinner);
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
