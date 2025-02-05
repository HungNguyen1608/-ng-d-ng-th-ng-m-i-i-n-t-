package com.example.app.retrofit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientTB {
        private static Retrofit instance;
        public static Retrofit getInstance(){
            if(instance == null){
                instance = new Retrofit.Builder().baseUrl("https://fcm.googleapis.com/").addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                        .build();
            }
            return instance;
        }


}
