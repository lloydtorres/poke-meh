package com.lloydtorres.pokemeh;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.plattysoft.leonids.ParticleSystem;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by Lloyd on 2016-07-23.
 */
public class ResultsActivity extends AppCompatActivity {
    public static final String SLOWPOKE_KEY = "slowpokes";

    private ParticleSystem texmexer;
    private MediaPlayer win;

    private TextView slowpokeCount;
    private ImageView slowpokeImg;
    private TextView snark;
    private FancyButton btnK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        getSupportActionBar().hide();

        long slowpokes = getIntent().getLongExtra(SLOWPOKE_KEY, 0);

        slowpokeCount = (TextView) findViewById(R.id.catch_count);
        slowpokeImg = (ImageView) findViewById(R.id.slowpoke_fin);
        snark = (TextView) findViewById(R.id.snark);
        btnK = (FancyButton) findViewById(R.id.btn_k);

        slowpokeCount.setText(String.format("Caught Slowpoke × %d", slowpokes));
        String snarkContent;
        if (slowpokes <= 0) {
            snarkContent = "Good job bb, proud of u ;)";
        }
        else if (slowpokes == 1) {
            snarkContent = "At least u stayed still for a bit";
        }
        else if (slowpokes < 5) {
            snarkContent = "Calm down sheesh";
        }
        else if (slowpokes < 10) {
            snarkContent = "Brilliant, u stayed still long enough";
        }
        else if (slowpokes < 25) {
            snarkContent = "Maybe u should shake it off a bit";
        }
        else {
            snarkContent = "U ok fam?";
        }
        snark.setText(snarkContent);

        btnK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResultsActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        texmexer = new ParticleSystem(this, 50, R.drawable.texmex, 2000L);
        win = MediaPlayer.create(this, R.raw.win);

        texmexer.setSpeedRange(0.2f, 0.5f).oneShot(slowpokeImg, 150);
        win.start();
    }
}
