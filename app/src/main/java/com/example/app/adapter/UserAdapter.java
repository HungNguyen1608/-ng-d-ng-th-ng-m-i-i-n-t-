package com.example.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.app.R;
import com.example.app.activity.ThongtinUserActivity;
import com.example.app.model.User;
import com.example.app.utils.Utils;

import java.util.List;

public class UserAdapter extends BaseAdapter {
    User user;
    List<User>arr;
    Context context;

    public UserAdapter(List<User> arr, Context context) {
        this.arr = arr;
        this.context = context;
    }
    @Override
    public int getCount() {
        return arr.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
    public class ViewHolder{
        TextView emailuser,tenusde;
        ImageView anhuser;
    }
    //đưa dữ liệu lấy đực ra item_avata
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view == null){
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.item_user, null);
            viewHolder.emailuser = view.findViewById(R.id.emailuser);
            viewHolder.tenusde = view.findViewById(R.id.name_user);
            viewHolder.anhuser = view.findViewById(R.id.imguser);
            view.setTag(viewHolder);
            viewHolder.anhuser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentuser=new Intent(context, ThongtinUserActivity.class);
                    intentuser.addFlags(intentuser.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intentuser);
                }
            });
        }else {
            viewHolder = (ViewHolder) view.getTag();

        }
        viewHolder.emailuser.setText(arr.get(i).getEmail());
        viewHolder.tenusde.setText(arr.get(i).getUsername());
        // Kiểm tra và sử dụng Glide chỉ khi các biến không null
        // Log.d("anh",arr.get(i).getAvatar());
        if (arr.get(i).getAvartar() != null) {
            if (arr.get(i).getAvartar().contains("http")) {
                Glide.with(context).load(arr.get(i).getAvartar()).into(viewHolder.anhuser);
            } else {
                String hinh = Utils.BASE_URL + "images/" + arr.get(i).getAvartar();
                Glide.with(context).load(hinh).into(viewHolder.anhuser);
            }
        } else {
            Log.d("TAG", "User or avatar is null");
        }

        return view;
    }
}
