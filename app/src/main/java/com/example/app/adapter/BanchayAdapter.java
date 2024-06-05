package com.example.app.adapter;

import android.content.Context;
import android.content.Intent;
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

public class BanchayAdapter extends RecyclerView.Adapter<BanchayAdapter.MyViewHolder> {
    Context context;
    List<SanPhamMoi> array;
    private int viewType;

    public static final int VIEW_TYPE_1 = 0;
    public static final int VIEW_TYPE_2 = 1;


    public BanchayAdapter(Context context, List<SanPhamMoi>array, int viewType) {
        this.context = context;
        this.array = array;
        this.viewType = viewType;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item;
        switch (viewType) {
            case VIEW_TYPE_1:
                item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banchay, parent, false);
                break;
            case VIEW_TYPE_2:
                item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banchay, parent, false);
                break;
            default:
                item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banchay, parent, false);
                break;
        }
        return new MyViewHolder(item);
    }

    public int getItemViewType(int position) {
        // Thực hiện logic để xác định loại view cho item tại vị trí position
        // Ví dụ: nếu item tại position đặc biệt, trả về VIEW_TYPE_SPECIAL, và ngược lại
        return position % 2 == 0 ? VIEW_TYPE_1 : VIEW_TYPE_2;
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position){
        SanPhamMoi sanPhamMoi = array.get(position);
        if (sanPhamMoi != null) {
            holder.itemsp_tensp.setText(sanPhamMoi.getTensanpham());
            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            float giacu = Float.parseFloat(sanPhamMoi.getGiasp());
            float giamgia = sanPhamMoi.getGiamgia();
            Integer giamoi = (int) (giacu * (1 - giamgia/100));

            // Kiểm tra null trước khi gán giá trị cho TextView
            if (holder.txtgia2 != null) {
                holder.txtgia2.setText("Giá: "+decimalFormat.format(giamoi)+" Đ");
            }

            Log.d("loggg", sanPhamMoi.getHinhanh());
            if (sanPhamMoi.getHinhanh() != null) {
                if (sanPhamMoi.getHinhanh().contains("http")) {
                    Glide.with(context).load(sanPhamMoi.getHinhanh()).into(holder.imghinhanh);
                } else {
                    String hinh = Utils.BASE_URL + "images/" +sanPhamMoi.getHinhanh();
                    Glide.with(context).load(hinh).into(holder.imghinhanh);
                }
            }

            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int pos, boolean isLongClick) {
                    if (!isLongClick) {
                        //click
                        Intent intent = new Intent(context, ChiTietActivity.class);
                        intent.putExtra("chitiet", sanPhamMoi);
                        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else {
                        EventBus.getDefault().postSticky(new SuaXoaEvent(sanPhamMoi));
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount(){
        return array.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView txtgia2,txtten, itemsp_tensp;
        ImageView imghinhanh;
        private ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtgia2 = itemView.findViewById(R.id.itemsp_giaspbc);
            itemsp_tensp = itemView.findViewById(R.id.tenspbc);
            imghinhanh = itemView.findViewById(R.id.imghinhanhbc);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onClick(view, getAdapterPosition(), false);
                }
            });
            if(Utils.user_current.getLoai() == 1) {
                //itemView.setOnCreateContextMenuListener(this);
                itemView.setOnLongClickListener(this);
            }

        }

        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(),true);
            return false;
        }
    }
}
