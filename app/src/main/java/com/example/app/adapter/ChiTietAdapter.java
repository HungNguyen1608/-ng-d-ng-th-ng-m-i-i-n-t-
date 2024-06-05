package com.example.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app.R;
import com.example.app.activity.ChiTietActivity;
import com.example.app.activity.ChiTietDonHangActivity;
import com.example.app.interf.ItemClickListener;
import com.example.app.model.Item;
import com.example.app.utils.Utils;

import java.util.List;

public class ChiTietAdapter extends RecyclerView.Adapter<ChiTietAdapter.MyViewHolder> {

    List<Item> itemList;
    Context context;
    public ChiTietAdapter(List<Item> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chitiet_giohang,parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.txtten.setText(item.getTensanpham() + " ");
        holder.txtsoluong.setText("Số lượng: "+item.getSoluong() + " ");
        //Glide.with(context).load(item.getHinhanh()).into(holder.imagechitiet);
        if(item.getHinhanh().contains("http")){
            Glide.with(context).load(item.getHinhanh()).into(holder.imagechitiet);
        } else {
            String hinh = Utils.BASE_URL + "images/" +item.getHinhanh();
            Glide.with(context).load(hinh).into(holder.imagechitiet);
        }
        String str_trangthai = null;
        int trangthai = item.getTrangthai();
        if(trangthai == 0){
            str_trangthai = "Đang chờ xử lý";
        } else if(trangthai == 1){
            str_trangthai = "Đã xác nhận";
        } else if(trangthai == 2){
            str_trangthai = "Đang giao hàng";
        } else if(trangthai == 3){
            str_trangthai = "Đã giao";
        } else if(trangthai == 4){
            str_trangthai = "Đã hủy";
        } else if(trangthai == 5){
            str_trangthai = "Hoàn hàng";
        } else if(trangthai == 6) {
            str_trangthai = "Hoàn thành";
        }
        holder.txttrangthai.setText("Trạng thái: "+ str_trangthai);
        holder.setItemClickListener(new ItemClickListener(){
            @Override
            public void onClick(View view, int pos, boolean isLongClick) {
                if(Utils.user_current.getLoai() == 1){
                    if(!isLongClick){
                        //click
                        if(trangthai != 4 && trangthai != 6) {
                            Intent intent = new Intent(context, ChiTietDonHangActivity.class);
                            intent.putExtra("item", item);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    }
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imagechitiet;
        TextView txtten, txtsoluong, txttrangthai;
        private ItemClickListener itemClickListener;


        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            imagechitiet = itemView.findViewById(R.id.item_img);
            txtten = itemView.findViewById(R.id.item_tensp);
            txtsoluong = itemView.findViewById(R.id.item_soluong);
            txttrangthai = itemView.findViewById(R.id.item_trangthai);
            itemView.setOnClickListener(this);

        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }
        @Override
        public void onClick(View v) {
            // Check if itemClickListener is not null and handle the click event
            if (itemClickListener != null) {
                itemClickListener.onClick(v, getAdapterPosition(), false);
            }
        }
    }
}
