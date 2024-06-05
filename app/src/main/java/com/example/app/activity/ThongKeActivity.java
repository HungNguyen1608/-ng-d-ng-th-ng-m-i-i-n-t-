package com.example.app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.app.R;
import com.example.app.retrofit.ApiBanHang;
import com.example.app.retrofit.RetrofitClient;
import com.example.app.utils.Utils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ThongKeActivity extends AppCompatActivity {
    Toolbar toolbar;
    Spinner spinner;
    PieChart pieChart;
    BarChart barChart;
    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_ke);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        initview();
        ActionToolBar();
        initData();
        initSpinnerListener(); // Thêm dòng này để thiết lập sự kiện lắng nghe cho Spinner
        getDataChart();
        //barchart
        settingbarchart();
        getthongkethang();
    }

//    private void getthongkethang() {
//        compositeDisposable.add(apiBanHang.thongKethang(Utils.user_current.getId(), currentYear)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        thongKeModel -> {
//                            Log.d("loithongke", thongKeModel.getResult().toString());
//                            if (thongKeModel.isSuccess()){
//                                    List<BarEntry> listdata = new ArrayList<>();
//                                    for (int i = 0; i < thongKeModel.getResult().size(); i++) {
//                                        String tongtien = thongKeModel.getResult().get(i).getTongtienthang();
//                                        String thang = thongKeModel.getResult().get(i).getThang();
//                                        listdata.add(new BarEntry(Float.parseFloat(tongtien),Integer.parseInt(thang)));
//                                    }
//                                    // Cập nhật dữ liệu mới vào biểu đồ
//                                    BarDataSet barDataSet = new BarDataSet(listdata, "Thống kê");
//                                    barDataSet.setValueTextSize(14f);
//                                    barDataSet.setValueTextColor(Color.RED);
//                                    barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
//                                    BarData data=new BarData(barDataSet);
//                                    barChart.animateXY(2000,2000);
//                                    barChart.setData(data);
//                                    barChart.invalidate();
//                                    Log.d("l", thongKeModel.getResult().toString());
//                                }
//
//
//                        }, throwable -> {
//                            Log.d("lo", throwable.getMessage());
//                        }
//                ));
//
//    }
private void getthongkethang() {
    compositeDisposable.add(apiBanHang.thongKethang(Utils.user_current.getId(), currentYear)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                    thongKeModel -> {
                        Log.d("loithongke", thongKeModel.getResult().get(0).getThang()+" "+ thongKeModel.getResult().get(0).getTongtienthang());
                        if (thongKeModel.isSuccess()) {
                            List<BarEntry> listdata = new ArrayList<>();
                            for (int i = 0; i < thongKeModel.getResult().size(); i++) {
                                String tongtien = thongKeModel.getResult().get(i).getTongtienthang();
                                int thang = thongKeModel.getResult().get(i).getThang();
                                listdata.add(new BarEntry(thang,Float.parseFloat(tongtien)));
                            }
                            if (listdata.isEmpty()) {
                                // Nếu danh sách dữ liệu rỗng, thông báo cho người dùng và không cập nhật biểu đồ
                                Toast.makeText(ThongKeActivity.this, "Không có dữ liệu thống kê.", Toast.LENGTH_SHORT).show();
                            } else {
                                // Cập nhật dữ liệu mới vào biểu đồ
                                BarDataSet barDataSet = new BarDataSet(listdata, "Thống kê");
                                barDataSet.setValueTextSize(6f);
                                barDataSet.setValueTextColor(Color.RED);
                                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                                BarData data = new BarData(barDataSet);
                                barChart.animateXY(2000, 2000);
                                barChart.setData(data);
                                barChart.invalidate();
                            }
                        }
                    }, throwable -> {
                        Log.e("loithongke1", throwable.getMessage());
                    }
            ));
}

    private void settingbarchart() {
        barChart.getDescription().setEnabled(false);
        barChart.setDrawValueAboveBar(false);
        XAxis xAxis=barChart.getXAxis();
        xAxis.setAxisMaximum(12);
        xAxis.setAxisMinimum(0);
        YAxis yAxisright=barChart.getAxisRight();
        // yAxisright.setAxisMaximum(12);
        yAxisright.setAxisMinimum(0);
        YAxis yAxisleft=barChart.getAxisLeft();
        yAxisleft.setAxisMinimum(0);
    }

    private void initSpinnerListener() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // Gọi lại phương thức getDataChart() khi người dùng thay đổi giá trị của Spinner
                getDataChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Không cần xử lý trong trường hợp này
            }
        });
    }

    private void getDataChart() {
        List<PieEntry> list = new ArrayList<>();
        int month;
        try {
            month = Integer.parseInt(spinner.getSelectedItem().toString());
        } catch (NumberFormatException e) {
            // Xử lý trường hợp không thể parse chuỗi thành số nguyên
            e.printStackTrace();
            return;
        }
        compositeDisposable.add(apiBanHang.thongKe(Utils.user_current.getId(), month, currentYear)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        thongKeModel -> {
                            if (thongKeModel.isSuccess()) {
                                // Xóa dữ liệu cũ
                                list.clear();
                                for (int i = 0; i < thongKeModel.getResult().size(); i++) {
                                    String tensanpham = thongKeModel.getResult().get(i).getTensanpham();
                                    int tong = thongKeModel.getResult().get(i).getTong();
                                    list.add(new PieEntry(tong, tensanpham));
                                }
                                // Cập nhật dữ liệu mới vào biểu đồ
                                PieDataSet pieDataSet = new PieDataSet(list, "Thống kê");
                                PieData data = new PieData();
                                data.setValueTextSize(12f);
                                data.setDataSet(pieDataSet);
                                data.setValueFormatter(new PercentFormatter(pieChart));
                                pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                                pieChart.setData(data);
                                pieChart.animateXY(2000, 2000);
                                pieChart.setUsePercentValues(true);
                                pieChart.getDescription().setEnabled(false);
                                pieChart.invalidate();
                                Log.d("l", thongKeModel.getResult().toString());
                            }
                        }, throwable -> {
                            Log.d("lo", throwable.getMessage());
                        }
                ));

    }
    private void initData() {
        Integer[] so = new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12};
        ArrayAdapter<Integer> adapterspin = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, so);
        spinner.setAdapter(adapterspin);
        // Đặt giá trị mặc định cho Spinner là tháng hiện tại
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        spinner.setSelection(currentMonth - 1); // -1 vì vị trí bắt đầu từ 0
    }
    private void initview() {
        toolbar = findViewById(R.id.toolbar);
        spinner = findViewById(R.id.spinner);
        pieChart = findViewById(R.id.pieChart);
        barChart=findViewById(R.id.barchart);
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
}