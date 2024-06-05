package com.example.app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.app.R;
import com.example.app.adapter.ChatAdapter;
import com.example.app.model.Chat;
import com.example.app.utils.Utils;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    int iduser;
    String tensp;
    String str_id;
    RecyclerView recyclerView;
    ImageView imgSend;
    EditText edMess;
    Toolbar toolbar;
    FirebaseFirestore db;
    ChatAdapter adapter;
    List<Chat> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        iduser = getIntent().getIntExtra("id",0);
        tensp = getIntent().getStringExtra("tensp");
        str_id = String.valueOf(iduser);
        Log.d("ktid",str_id);
        initView();
        if(tensp != null){
            edMess.setText(tensp);
        }
        initControl();
        listen();
        insertU();
        ActionToolBar();
    }
    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void insertU() {
        HashMap<String, Object> user = new HashMap<>();
        user.put("id",Utils.user_current.getId());
        user.put("username",Utils.user_current.getUsername());
        db.collection(String.valueOf(str_id)).document(String.valueOf(Utils.user_current.getId())).set(user);
    }

    private void initControl() {
        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToFire();
            }
        });
    }

    private void sendToFire() {
        String str_mess = edMess.getText().toString().trim();
        if (!TextUtils.isEmpty(str_mess)) {
            HashMap<String, Object> message = new HashMap<>();
            message.put(Utils.SEND_ID, String.valueOf(Utils.user_current.getId()));
            message.put(Utils.RECEIVER_ID, str_id);
            message.put(Utils.MESS, str_mess);
            message.put(Utils.DATETIME, new Date());
            db.collection(Utils.PATH_CHAT).add(message);
            edMess.setText(""); // Clear the edit text after sending
        }
    }

    private void listen() {
        db.collection(Utils.PATH_CHAT).whereEqualTo(Utils.SEND_ID, String.valueOf(Utils.user_current.getId()))
                .whereEqualTo(Utils.RECEIVER_ID, str_id)
                .addSnapshotListener(eventListener);
        db.collection(Utils.PATH_CHAT).whereEqualTo(Utils.SEND_ID, str_id)
                .whereEqualTo(Utils.RECEIVER_ID, String.valueOf(Utils.user_current.getId()))
                .addSnapshotListener(eventListener);
    }

    private final com.google.firebase.firestore.EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    Chat chat = new Chat();
                    chat.sendid = documentChange.getDocument().getString(Utils.SEND_ID);
                    chat.receiverid = documentChange.getDocument().getString(Utils.RECEIVER_ID);
                    chat.mess = documentChange.getDocument().getString(Utils.MESS);
                    chat.datetime = format_d(documentChange.getDocument().getDate(Utils.DATETIME));
                    chat.dateObj = documentChange.getDocument().getDate(Utils.DATETIME); // Initialize dateObj
                    list.add(chat);
                }
            }
            Collections.sort(list, (obj1, obj2) -> {
                if (obj1.dateObj != null && obj2.dateObj != null) {
                    return obj1.dateObj.compareTo(obj2.dateObj);
                } else {
                    return 0;
                }
            });
            adapter.notifyDataSetChanged();
            //recyclerView.smoothScrollToPosition(list.size() - 1);
        }
    };


    private String format_d(Date date) {
        return new SimpleDateFormat("MMMM dd,yyyy- hh:mm a", Locale.getDefault()).format(date);
    }

    private void initView() {
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recycleview_chat);
        imgSend = findViewById(R.id.imagechat);
        toolbar = findViewById(R.id.toolbar3);
        edMess = findViewById(R.id.editputtex);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new ChatAdapter(getApplicationContext(), list, String.valueOf(Utils.user_current.getId()));
        recyclerView.setAdapter(adapter);
    }
}
