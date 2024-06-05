package com.example.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app.R;
import com.example.app.interf.ImageClickListener;
import com.example.app.model.GioHang;
import com.example.app.model.Item;
import com.example.app.utils.Utils;

import java.text.DecimalFormat;
import java.util.List;

public class ThanhToanAdapter extends RecyclerView.Adapter<ThanhToanAdapter.MyViewHolder>{
    List<GioHang> gioHangList;

    Context context;

    public ThanhToanAdapter(List<GioHang> gioHangList, Context context) {
        this.gioHangList = gioHangList;
        this.context = context;
    }

    @NonNull
    @Override
    public ThanhToanAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thanhtoan,parent,false);
        return new ThanhToanAdapter.MyViewHolder(view);
    }
//lấy dữ liệu
    @Override
    public void onBindViewHolder(@NonNull ThanhToanAdapter.MyViewHolder holder, int position) {
        GioHang gioHang = gioHangList.get(position);
        holder.item_giohang_tensp.setText(gioHang.getTensanpham());
        holder.item_giohang_soluong.setText(gioHang.getSoluong()+ " ");
        if(gioHang.getHinhanh().contains("http")){
            Glide.with(context).load(gioHang.getHinhanh()).into(holder.item_giohang_image);
        } else {
            String hinh = Utils.BASE_URL + "images/" +gioHang.getHinhanh();
            Glide.with(context).load(hinh).into(holder.item_giohang_image);
        }
        long gia = Long.parseLong(String.valueOf(gioHang.getGiasp()));
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.item_giohang_gia.setText("Giá: " + decimalFormat.format(gia) + "Đ");
        holder.item_giohang_giasp2.setText(decimalFormat.format(gia*gioHang.getSoluong()) + "Đ");

    }

    @Override
    public int getItemCount() {
        return gioHangList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView item_giohang_image,imgCong,imgTru;
        TextView item_giohang_tensp, item_giohang_gia, item_giohang_soluong, item_giohang_giasp2;
        ImageClickListener listener;
        ConstraintLayout btnxoa;
        CheckBox item_checkbox;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item_giohang_image = itemView.findViewById(R.id.item_giohang_image);
            item_giohang_tensp = itemView.findViewById(R.id.item_giohang_tensp);
            item_giohang_gia = itemView.findViewById(R.id.item_giohang_gia);
            item_giohang_soluong = itemView.findViewById(R.id.item_giohang_soluong);
            item_giohang_giasp2 = itemView.findViewById(R.id.item_giohang_giasp2);
//            imgCong = itemView.findViewById(R.id.item_giohang_cong);
//            imgTru = itemView.findViewById(R.id.item_giohang_tru);
// item_checkbox = itemView.findViewById(R.id.item_checkbox);
//  btnxoa=itemView.findViewById(R.id.xoa);
        }
    }
}
