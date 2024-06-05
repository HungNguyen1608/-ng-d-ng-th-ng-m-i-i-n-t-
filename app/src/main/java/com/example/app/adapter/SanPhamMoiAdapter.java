package com.example.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.ContextMenu;
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
import com.example.app.interf.ItemClickListener;
import com.example.app.model.EventBus.SuaXoaEvent;
import com.example.app.model.SanPhamMoi;
import com.example.app.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.List;

public class SanPhamMoiAdapter extends RecyclerView.Adapter<SanPhamMoiAdapter.MyViewHolder> {
    Context context;
    List<SanPhamMoi> array;

    public SanPhamMoiAdapter(Context context, List<SanPhamMoi> array) {
        this.context = context;
        this.array = array;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sp_moi,parent,false);
        return new MyViewHolder(item);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position){
        SanPhamMoi sanPhamMoi = array.get(position);
        holder.itemsp_tensp.setText(sanPhamMoi.getTensanpham());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.txtgia1.setPaintFlags(holder.txtgia1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        float giacu = Float.parseFloat(sanPhamMoi.getGiasp());
        float giamgia = sanPhamMoi.getGiamgia();
        Integer giamoi = (int) (giacu * (1 - giamgia/100));
        holder.txtgia1.setText("Giá cũ: "+ decimalFormat.format(giacu)+ "Đ");
        holder.txtgia2.setText("Giá: "+decimalFormat.format(giamoi)+" Đ");
        holder.itemsp_daban.setText("Đã bán: "+ sanPhamMoi.getDaban());
        Log.d("loggg", sanPhamMoi.getHinhanh());
        if(sanPhamMoi.getHinhanh().contains("http")){
            Glide.with(context).load(sanPhamMoi.getHinhanh()).into(holder.imghinhanh);

        }else {
            String hinh = Utils.BASE_URL + "images/" +sanPhamMoi.getHinhanh();
            Glide.with(context).load(hinh).into(holder.imghinhanh);
        }
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int pos, boolean isLongClick) {
                if(!isLongClick){
                    //click
                    Intent intent = new Intent(context, ChiTietActivity.class);
                    intent.putExtra("chitiet",sanPhamMoi);
                    intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else{
                    EventBus.getDefault().postSticky(new SuaXoaEvent(sanPhamMoi));
                }
            }
        });
        if (holder.txtten != null && holder.txtgia1 != null && holder.imghinhanh != null) {
            holder.txtten.setText(sanPhamMoi.getTensanpham());
           // DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
           // holder.txtgia.setText("Gia: "+ decimalFormat.format(Double.parseDouble(sanPhamMoi.getGiasp()))+ "D");
            //holder.txtgia.setText(sanPhamMoi.getGiasp());


        } else {
            Log.e("SanPhamMoiAdapter", "TextView is null");
        }

    }
    @Override
    public int getItemCount(){
        return array.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, View.OnLongClickListener {
        TextView txtgia1,txtgia2,txtten, itemsp_tensp, itemsp_daban;
        ImageView imghinhanh;
        private ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtgia1 = itemView.findViewById(R.id.itemsp_giasp1);
            txtgia2 = itemView.findViewById(R.id.itemsp_giasp2);
            itemsp_tensp = itemView.findViewById(R.id.itemsp_tensp);
            imghinhanh = itemView.findViewById(R.id.imghinhanh);
            itemsp_daban = itemView.findViewById(R.id.itemsp_daban);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onClick(view, getAdapterPosition(), false);
                }
            });
            if(Utils.user_current.getLoai() == 1) {
                itemView.setOnCreateContextMenuListener(this);
                itemView.setOnLongClickListener(this);
            }


        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add(0,0,getAdapterPosition(),"Sửa");
            contextMenu.add(0,0,getAdapterPosition(),"Xóa");
        }

        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(),true);
            return false;
        }
    }
}
