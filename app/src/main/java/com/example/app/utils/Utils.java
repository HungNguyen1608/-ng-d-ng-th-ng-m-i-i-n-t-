package com.example.app.utils;

import com.example.app.model.GioHang;
import com.example.app.model.User;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static  String BASE_URL = "http://192.168.135.139/banhang2/";
    public static List<GioHang> manggiohang = new ArrayList<>();
    public static List<GioHang> mangmuahang = new ArrayList<>();
    public static List<GioHang> mangmuahang2 = new ArrayList<>();
    public static User user_current = new User();
    public static User mangshop = new User();
    public static String ID_RECEIVER ;
    public static final String SEND_ID = "idsend" ;
    public static final String RECEIVER_ID = "idreceiver";
    public static final String MESS = "message";
    public static final String DATETIME = "datetime";
    public static final String PATH_CHAT = "chat";


}
