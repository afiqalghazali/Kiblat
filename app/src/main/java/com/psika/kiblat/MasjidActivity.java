package com.psika.kiblat;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MasjidActivity extends AppCompatActivity {

    private String mapUrl;
    private WebView webView;
    private LocationUtil locationUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masjid);

        // Inisialisasi WebView
        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Memanggil fungsi lokasi
        locationUtil = new LocationUtil(this, (latitude, longitude) -> {
            // Mengganti link dinamis dengan lokasi saat ini
            mapUrl = "https://www.google.com/maps/search/masjid/@" + latitude + "," + longitude + ",15z?entry=ttu";

            // Load Google Maps
            webView.loadUrl(mapUrl);
        });

        // Gunakan nilai default jika lokasi tidak valid atau null
        mapUrl = "https://www.google.com/maps/search/masjid/@-6.175392,106.827153,15z?entry=ttu";

        // Load Google Maps default
        webView.loadUrl(mapUrl);

        // Panggil fungsi cek izin lokasi
        locationUtil.checkAndRequestLocationPermission();
    }

    // Menangani hasil permintaan izin lokasi
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationUtil.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
