package com.psika.kiblat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class AdzanActivity extends AppCompatActivity {

    private ImageButton back;
    private TextView subuhTv;
    private TextView dzuhurTv;
    private TextView asharTv;
    private TextView maghribTv;
    private TextView isyaTv;
    private TextView timeTv;
    private TextView dateTv;
    private LocationUtil locationUtil;
    private static final String API_BASE_URL = "https://api.aladhan.com/v1/timings/"; // API waktu adzan

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adzan);

        back = findViewById(R.id.back);
        subuhTv = findViewById(R.id.subuhTv);
        dzuhurTv = findViewById(R.id.dzuhurTv);
        asharTv = findViewById(R.id.asharTv);
        maghribTv = findViewById(R.id.maghribTv);
        isyaTv = findViewById(R.id.isyaTv);
        timeTv = findViewById(R.id.timeTv);
        dateTv = findViewById(R.id.dateTv);


        // Memanggil fungsi lokasi
        locationUtil = new LocationUtil(this, new LocationUtil.LocationCallback() {
            @Override
            public void onLocationReceived(double latitude, double longitude) {
                // Memanggil fungsi data API
                adzanApiRequest(latitude, longitude);
            }
        });

        // Panggil fungsi waktu dan tanggal
        setTimeAndDate();

        // Panggil fungsi cek izin lokasi
        locationUtil.checkAndRequestLocationPermission();


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdzanActivity.this, MainActivity.class));
            }
        });
    }

    // Menangani hasil permintaan izin lokasi
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationUtil.onRequestPermissionsResult(this, requestCode, grantResults);
    }



    // Mengambil data API
    private void adzanApiRequest(double latitude, double longitude) {
        // Inisialisasi RequestQueue dengan Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Inisialisasi GSON
        final Gson gson = new Gson();

        // Memanggil fungsi tanggal hari ini
        String currentDate = getCurrentDate();

        // Link dinamis dengan lokasi
        String apiURL = API_BASE_URL +
                currentDate +
                "?latitude=" + latitude +
                "&longitude=" + longitude +
                "&method=20"; // method KEMENAG RI


        // Membuat StringRequest dengan method GET
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                apiURL,
                response -> {
                    // Log respon
                    Log.d("API Response", response);

                    // Panggil fungsi ApiResponse dan Parse JSON dengan GSON
                    ApiResponse apiResponse = gson.fromJson(response, ApiResponse.class);

                    // Ambil data jadwal sholat
                    if (apiResponse != null && apiResponse.getData() != null) {
                        // Panggil fungsi TimingsModel
                        ApiResponse.TimingsModel prayerData = apiResponse.getData();
                        if (prayerData != null) {
                            Map<String, String> timings = prayerData.getTimings();
                            if (timings != null) {
                                // Set TV
                                subuhTv.setText(timings.get("Fajr"));
                                dzuhurTv.setText(timings.get("Dhuhr"));
                                asharTv.setText(timings.get("Asr"));
                                maghribTv.setText(timings.get("Maghrib"));
                                isyaTv.setText(timings.get("Isha"));

                                // Tampilkan TV
                                subuhTv.setVisibility(View.VISIBLE);
                                dzuhurTv.setVisibility(View.VISIBLE);
                                asharTv.setVisibility(View.VISIBLE);
                                maghribTv.setVisibility(View.VISIBLE);
                                isyaTv.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                },
                error -> {
                    // Mengatasi Error
                    Log.e("API Error", "Error occurred: " + error.getMessage());
                });

        // Menambah ke Volley
        requestQueue.add(stringRequest);
    }

    // Mapping API
    private static class ApiResponse {
        // Mapping data
        @SerializedName("data")
        private TimingsModel data;

        public TimingsModel getData() {
            return data;
        }

        // Mapping timings
        private static class TimingsModel {
            @SerializedName("timings")
            private Map<String, String> timings;

            public Map<String, String> getTimings() {
                return timings;
            }
        }
    }

    // Fungis waktu dan tanggal
    private void setTimeAndDate() {
        Calendar calendar = Calendar.getInstance();

        // Format jam
        SimpleDateFormat jamFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String jam = jamFormat.format(calendar.getTime());

        // Format tanggal
        SimpleDateFormat tanggalFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        String tanggal = tanggalFormat.format(calendar.getTime());

        // Set TV
        timeTv.setText(jam);
        dateTv.setText(tanggal);

        // Tampilkann TV
        timeTv.setVisibility(View.VISIBLE);
        dateTv.setVisibility(View.VISIBLE);
    }

    // Fungsi tanggal hari ini
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(Calendar.getInstance().getTime());
    }
}
