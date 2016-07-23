package com.lloydtorres.pokemeh;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.thalmic.myo.Hub;
import com.thalmic.myo.scanner.ScanActivity;

import mehdi.sakout.fancybuttons.FancyButton;

public class MenuActivity extends AppCompatActivity {

    FancyButton btnPlay;
    FancyButton btnMyoScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        getSupportActionBar().hide();

        Hub hub = Hub.getInstance();
        hub.init(this, getPackageName());
        hub.setSendUsageData(false);

        btnPlay = (FancyButton) findViewById(R.id.btn_play);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, PokeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnMyoScanner = (FancyButton) findViewById(R.id.btn_myosync);
        btnMyoScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, ScanActivity.class);
                startActivity(intent);
            }
        });
    }
}
