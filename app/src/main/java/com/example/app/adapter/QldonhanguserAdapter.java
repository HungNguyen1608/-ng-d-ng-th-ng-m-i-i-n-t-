package com.example.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app.R;
import com.example.app.activity.ChiTietDonHangActivity;
import com.example.app.activity.DanhGiaActivity;
import com.example.app.activity.HoanHangActivity;
import com.example.app.activity.HuyDonActivity;
import com.example.app.interf.ItemClickListener;
import com.example.app.model.Item;
import com.example.app.utils.Utils;

import java.text.DecimalFormat;
import java.util.List;

public class QldonhanguserAdapter extends RecyclerView.Adapter<QldonhanguserAdapter.MyViewHolder> {
    List<Item> itemList;

    Context context;
    Button btnhuydon,btndanhgia,btnthanhcong,btnhoanhang;

    public QldonhanguserAdapter(List<Item> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public QldonhanguserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemdonhanguser,parent,false);
        return new QldonhanguserAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QldonhanguserAdapter.MyViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.txtdiachi.setText("Địa chỉ: "+item.getDiachi());
        holder.txtten.setText(item.getTensanpham() + " ");
        holder.txtsoluong.setText("Số lượng: "+item.getSoluong() + " ");
        int gia= Integer.parseInt(item.getGia());
        int sl=Integer.parseInt(String.valueOf(item.getSoluong()));
        int tongtien=gia*sl;
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.txttongtien.setText("Tổng tiền: "+decimalFormat.format(tongtien) + " đ");
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
            btnhuydon.setVisibility(View.VISIBLE);
        } else if(trangthai == 1){
            str_trangthai = "Đã xác nhận";
        } else if(trangthai == 2){
            str_trangthai = "Đang giao hàng";
        } else if(trangthai == 3){
            str_trangthai = "Đã giao";
            btnhoanhang.setVisibility(View.VISIBLE);
           // btnthanhcong.setVisibility(View.VISIBLE);
            btndanhgia.setVisibility(View.VISIBLE);

        } else if(trangthai == 4){
            str_trangthai = "Đã hủy";
        } else if(trangthai == 5){
            str_trangthai = "Hoàn hàng";
        }else if(trangthai == 6){
            str_trangthai = "Hoàn thành";
        }
        holder.txttrangthai.setText("Trạng thái: "+ str_trangthai);
        btnhuydon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HuyDonActivity.class);
                intent.putExtra("item", item);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        btndanhgia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, DanhGiaActivity.class);
                intent.putExtra("item", item);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        btnhoanhang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, HoanHangActivity.class);
                intent.putExtra("item", item);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
//        holder.setItemClickListener(new ItemClickListener(){
//            @Override
//            public void onClick(View view, int pos, boolean isLongClick) {
//                if(Utils.user_current.getLoai() == 1){
//                    if(!isLongClick){
//                        //click
//                        Intent intent = new Intent(context, ChiTietDonHangActivity.class);
//                        intent.putExtra("item", item);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);
//                    }
//                }
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imagechitiet;
        TextView txtten;
        TextView txtsoluong;
        TextView txttrangthai;
        TextView txtdiachi;
        TextView txttongtien;
        TextView txtsodt;
        private ItemClickListener itemClickListener;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imagechitiet = itemView.findViewById(R.id.item_img);
            txtten = itemView.findViewById(R.id.item_tensp);
            txtdiachi = itemView.findViewById(R.id.item_diachi);
            txttongtien = itemView.findViewById(R.id.item_tongtien);
            txtsodt = itemView.findViewById(R.id.item_sodt);
            btnhuydon=itemView.findViewById(R.id.btnhuydon);
            btndanhgia=itemView.findViewById(R.id.btndanhgia);
            txtsoluong = itemView.findViewById(R.id.item_soluong);
            txttrangthai = itemView.findViewById(R.id.item_trangthai);
            btnthanhcong=itemView.findViewById(R.id.btnthanhcong);
            btnhoanhang = itemView.findViewById(R.id.btnhoanhang);
          //  itemView.setOnClickListener(this);
        }
    }
}
