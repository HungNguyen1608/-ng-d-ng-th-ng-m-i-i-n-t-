package com.example.app.activity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;

public class LienHeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lien_he);

        // Khai báo WebView
        WebView mapWebView = findViewById(R.id.mapWebView);

        // Kích hoạt JavaScript và các cài đặt khác cho WebView
        WebSettings webSettings = mapWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(true);

        // Load đường dẫn của bản đồ Google Maps vào WebView
        mapWebView.loadUrl("https://www.google.com/maps/place/Tr%C6%B0%E1%BB%9Dng+%C4%90%E1%BA%A1i+H%E1%BB%8Dc+Kinh+T%E1%BA%BF+K%E1%BB%B9+Thu%E1%BA%ADt+C%C3%B4ng+Nghi%E1%BB%87p/@20.9803549,105.8709348,17z/data=!3m1!4b1!4m12!1m5!3m4!2zMjDCsDU4JzQ5LjkiTiAxMDXCsDUyJzMyLjkiRQ!8m2!3d20.9805152!4d105.8757949!3m5!1s0x3135afd765487289:0x21bd5839ba683d5f!8m2!3d20.980355!4d105.8758057!16s%2Fg%2F11b808s8_p?entry=ttu");
    }
}
