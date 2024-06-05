package com.example.app.retrofit;


import io.reactivex.rxjava3.core.Observable;

import com.example.app.model.GuiTB;
import com.example.app.model.NhanTB;

import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiPushNotifition {
   @Headers(
           {
                  "Content-Type: application/json",
                    "Authorization: key=AAAAkefHynE:APA91bHfAGKC9w5nTlMneO4hjA2bcj_6kBWMeB2AjxdLXywtrsIEWo3v2IW_vDQ6oNAagfMjQ2ibaJ091_HxJB4n9YLN2mOy1HolccD4s2lkITpORr5atvXyg6u91iCC1WN-JERMhPnc"
           }
   )
    @POST("fcm/send")
    Observable<NhanTB> sendNotification(@Body GuiTB data);
}
