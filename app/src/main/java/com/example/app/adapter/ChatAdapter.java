package com.example.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.model.Chat;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<Chat> chatList;
    private String sendId;
    private static final int TYPE_SEND = 1;
    private static final int TYPE_RECEIVER = 2;

    public ChatAdapter(Context context, List<Chat> chatList, String sendId) {
        this.context = context;
        this.chatList = chatList;
        this.sendId = sendId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == TYPE_SEND){
            view = LayoutInflater.from(context).inflate(R.layout.item_gui,parent,false);
            return new SendMessViewHolde(view);
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.item_nhan,parent,false);
            return new ReceiverViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == TYPE_SEND){
            ((SendMessViewHolde) holder).txtmess.setText(chatList.get(position).mess);
            ((SendMessViewHolde) holder).txtthoigiangui.setText(chatList.get(position).datetime);

        }else{
            ((ReceiverViewHolder) holder).txtmess.setText(chatList.get(position).mess);
            ((ReceiverViewHolder) holder).txtthoigiannhan.setText(chatList.get(position).datetime);

        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chatList.get(position).sendid.equals(sendId)){
            return TYPE_SEND;
        }else{
            return TYPE_RECEIVER;
        }
    }

    class SendMessViewHolde extends RecyclerView.ViewHolder{
        TextView txtmess, txtthoigiangui;

        public SendMessViewHolde(@NonNull View itemView) {
            super(itemView);
            txtmess = itemView.findViewById(R.id.txtmess);
            txtthoigiangui = itemView.findViewById(R.id.txtthoigiangui);
        }
    }
    class ReceiverViewHolder extends RecyclerView.ViewHolder{
        TextView txtmess, txtthoigiannhan;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            txtmess = itemView.findViewById(R.id.txtmess);
            txtthoigiannhan = itemView.findViewById(R.id.txtthoigiannhan);
        }
    }

}
