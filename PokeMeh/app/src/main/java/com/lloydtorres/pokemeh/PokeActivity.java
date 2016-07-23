package com.lloydtorres.pokemeh;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.plattysoft.leonids.ParticleSystem;
import com.thalmic.myo.Hub;

/**
 * Created by Lloyd on 2016-07-23.
 */
public class PokeActivity extends AppCompatActivity {

    PokeThread pThread;

    DonutProgress progress;
    TextView countdown;
    ImageView slowpoke;
    TextView message;

    ParticleSystem texmexer;
    MediaPlayer cry;
    MediaPlayer bgm;

    boolean animateSlowpoke = false;
    long slowpokes = 0;
    long milliseconds = 0;

    class PokeThread extends Thread {
        boolean isRunning = false;
        long lastUpdate = System.currentTimeMillis();

        public void setRunning(boolean b) {
            isRunning = b;
        }

        @Override
        public void run() {
            while (isRunning) {
                long curTime = System.currentTimeMillis();
                if (curTime - lastUpdate > 1) {
                    lastUpdate = curTime;
                    milliseconds++;

                    if (milliseconds > 15000L) {
                        milliseconds = 0L;
                        slowpokes++;
                        animateSlowpoke = true;
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            countdown.setText(Long.toString(milliseconds/1000L));
                            progress.setProgress((int) (100*((double) milliseconds/15000L)));
                            message.setText(String.format("Slowpoke × %d", slowpokes));

                            if (animateSlowpoke) {
                                if (slowpokes == 1) {
                                    slowpoke.setVisibility(View.VISIBLE);
                                }

                                if (slowpokes < 5) {
                                    slowpoke.getLayoutParams().height += slowpoke.getLayoutParams().height * 1.05;
                                    slowpoke.getLayoutParams().width += slowpoke.getLayoutParams().width * 1.05;

                                    RotateAnimation anim = new RotateAnimation(0f, -360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                    anim.setInterpolator(new LinearInterpolator());
                                    anim.setRepeatCount(Animation.INFINITE);
                                    anim.setDuration(1000);
                                    slowpoke.startAnimation(anim);
                                }

                                texmexer.setSpeedRange(0.2f, 0.5f).oneShot(progress, 50);
                                cry.start();
                                animateSlowpoke = false;
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poke);
        getSupportActionBar().hide();

        texmexer = new ParticleSystem(this, 50, R.drawable.texmex, 1000L);

        progress = (DonutProgress) findViewById(R.id.progressbar);
        countdown = (TextView) findViewById(R.id.countdown);
        slowpoke = (ImageView) findViewById(R.id.slowpoke);
        message = (TextView) findViewById(R.id.message);

        cry = MediaPlayer.create(this, R.raw.cry);
        bgm = MediaPlayer.create(this, R.raw.bgm);
        bgm.setLooping(true);
        bgm.start();

        Hub hub = Hub.getInstance();
        hub.init(this, getPackageName());

        pThread = new PokeThread();
        pThread.setRunning(true);
        pThread.start();
    }
}
