package com.example.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app.R;
import com.example.app.model.DanhGia;
import com.example.app.model.SanPhamMoi;
import com.example.app.utils.Utils;

import java.text.DecimalFormat;
import java.util.List;

public class DanhGiaAdapter extends RecyclerView.Adapter<DanhGiaAdapter.MyViewHolder> {
    Context context;
    List<DanhGia> array;

    public DanhGiaAdapter(Context context, List<DanhGia> array) {
        this.context = context;
        this.array = array;
    }

    @NonNull
    @Override
    public DanhGiaAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_danhgiasp, parent,false);
        return new DanhGiaAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DanhGiaAdapter.MyViewHolder holder, int position) {
        if(holder instanceof DanhGiaAdapter.MyViewHolder) {
            DanhGiaAdapter.MyViewHolder myViewHolder = (DanhGiaAdapter.MyViewHolder) holder;
            DanhGia danhGia = array.get(position);
            myViewHolder.tennguoidung.setText(danhGia.getUsername().trim());
            myViewHolder.thoigiandg.setText(danhGia.getThoigian());
            myViewHolder.danhgia.setText(danhGia.getNoidung());

            //Glide.with(context).load(sanPham.getHinhanh()).into(myViewHolder.hinhanh);
            String avatar = danhGia.getAvartar();
            if (avatar != null && avatar.contains("http")) {
                Glide.with(context).load(avatar).into(myViewHolder.hinhanhnd);
            } else {
                String hinh = Utils.BASE_URL + "images/" + avatar;
                Glide.with(context).load(hinh).into(myViewHolder.hinhanhnd);
            }
        }
    }


    @Override
    public int getItemCount() {
        if (array != null) {
            return array.size();
        } else {
            return 0;
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tennguoidung,danhgia,thoigiandg;
        ImageView hinhanhnd;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tennguoidung = itemView.findViewById(R.id.tendanhgia);
            thoigiandg = itemView.findViewById(R.id.txtthoigiandg);
            danhgia = itemView.findViewById(R.id.txtnoidungdg);
            hinhanhnd = itemView.findViewById(R.id.imgdangia);
        }
    }
}
