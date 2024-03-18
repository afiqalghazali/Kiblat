package com.psika.kiblat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardView btnGoToKiblat = findViewById(R.id.btnGoToKiblat);
        CardView btnGoToMasjid = findViewById(R.id.btnGoToMasjid);
        CardView btnGoToAdzan = findViewById(R.id.btnGoToAdzan);

        // Tombol KiblatActivity
        btnGoToKiblat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, KiblatActivity.class));
            }
        });

        // Tombol MasjidActivity
        btnGoToMasjid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MasjidActivity.class));
            }
        });

        // Tombol AdzanActivity
        btnGoToAdzan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AdzanActivity.class));
            }
        });
    }
}