package com.example.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app.R;
import com.example.app.activity.ChiTietActivity;
import com.example.app.interf.ItemClickListener;
import com.example.app.model.SanPhamMoi;
import com.example.app.utils.Utils;

import java.text.DecimalFormat;
import java.util.List;

public class DienThoaiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<SanPhamMoi> array;
    private static final int VIEW_TYPE_DATA = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    public DienThoaiAdapter(Context context, List<SanPhamMoi> array) {
        this.context = context;
        this.array = array;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_DATA)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dienthoai, parent,false);
            return new MyViewHolder(view);
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading,parent,false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyViewHolder){
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            SanPhamMoi sanPham = array.get(position);
//            myViewHolder.tensp.setText(sanPham.getTensanpham());
//            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
//            myViewHolder.giasp.setText("Gia: "+ decimalFormat.format(Double.parseDouble(sanPham.getGiasp()))+"D");
//            myViewHolder.mota.setText(sanPham.getMota());
            myViewHolder.tensp.setText(sanPham.getTensanpham().trim());
            float giacu = Float.parseFloat(sanPham.getGiasp());
            float giamgia = sanPham.getGiamgia();
            Integer giamoi = (int) (giacu * (1 - giamgia/100));
            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            myViewHolder.giasp.setText("Giá: "+ decimalFormat.format(giamoi)+ "đ");
            Log.d("logg", sanPham.getHinhanh());
            //myViewHolder.idsp.setText(sanPham.getId()+"");
            myViewHolder.mota.setText("Mô tả: "+sanPham.getMota());

            //Glide.with(context).load(sanPham.getHinhanh()).into(myViewHolder.hinhanh);
            if(sanPham.getHinhanh().contains("http")){
                Glide.with(context).load(sanPham.getHinhanh()).into(myViewHolder.hinhanh);
            } else {
                String hinh = Utils.BASE_URL + "images/" +sanPham.getHinhanh();
                Glide.with(context).load(hinh).into(myViewHolder.hinhanh);
            }
            myViewHolder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int pos, boolean isLongClick) {
                    if(!isLongClick){
                        //click
                        Intent intent = new Intent(context, ChiTietActivity.class);
                        intent.putExtra("chitiet",sanPham);
                        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            });
        }else {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        if(holder instanceof MyViewHolder && array.get(position) != null){
//            MyViewHolder myViewHolder = (MyViewHolder) holder;
//            SanPhamMoi sanPham = array.get(position);
//            myViewHolder.tensp.setText(sanPham.getTensanpham());
//            myViewHolder.giasp.setText("Gia: " + sanPham.getGiasp() + "D");
//            Log.d("logg", sanPham.getHinhanh());
//            myViewHolder.idsp.setText(String.valueOf(sanPham.getId())); // Chuyển đổi sang kiểu String và gán giá trị
//            Glide.with(context).load(sanPham.getHinhanh()).into(myViewHolder.hinhanh);
//        } else if (holder instanceof LoadingViewHolder) {
//            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
//            loadingViewHolder.progressBar.setIndeterminate(true);
//        }
//    }


    @Override
    public int getItemViewType(int position) {
        return array.get(position) == null ? VIEW_TYPE_LOADING:VIEW_TYPE_DATA;
    }

    @Override
    public int getItemCount() {
        return array.size();
    }
    public class LoadingViewHolder extends RecyclerView.ViewHolder{
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView){
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tensp,giasp,mota,idsp;
        ImageView hinhanh;
        private ItemClickListener itemClickListener;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tensp = itemView.findViewById(R.id.itemdt_ten);
            giasp = itemView.findViewById(R.id.itemdt_gia);
            mota = itemView.findViewById(R.id.itemdt_mota);
            //idsp = itemView.findViewById(R.id.itemdt_idsp);
            hinhanh = itemView.findViewById(R.id.itemdt_image);
            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(),false);
        }
    }
}
