package com.lloydtorres.pokemeh;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.plattysoft.leonids.ParticleSystem;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

/**
 * Created by Lloyd on 2016-07-23.
 */
public class PokeActivity extends AppCompatActivity {

    private PokeThread pThread;

    private boolean isGettingBaseline = true;
    private int gyroSamples = 0;
    private double gyroRefMag = 0;
    private int accelSamples = 0;
    private double accelRefMag = 0;
    private int orientSamples = 0;
    private double rollRefMag = 0;
    private double yawRefMag = 0;
    private double pitchRefMag = 0;

    private DonutProgress progress;
    private TextView countdown;
    private ImageView slowpoke;
    private TextView message;

    private ParticleSystem texmexer;
    private MediaPlayer cry;
    private MediaPlayer bgm;

    private boolean animateSlowpoke = false;
    private long slowpokes = 0;
    private long milliseconds = 0;

    private Boolean isLostRecorded = false;

    class PokeThread extends Thread {
        boolean isRunning = false;
        long lastUpdate = System.currentTimeMillis();

        public void setRunning(boolean b) {
            isRunning = b;
        }

        @Override
        public void run() {
            while (isRunning) {
                if (isGettingBaseline) {
                    if (accelSamples > 512) {
                        isGettingBaseline = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                message.setText("Slowpoke × 0");
                                progress.setVisibility(View.VISIBLE);
                                countdown.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
                else {
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
                                        slowpoke.getLayoutParams().height += slowpoke.getLayoutParams().height * 1.025;
                                        slowpoke.getLayoutParams().width += slowpoke.getLayoutParams().width * 1.025;

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
    }

    public DeviceListener mListener = new AbstractDeviceListener() {
        @Override
        public void onOrientationData(Myo myo, long timestamp, Quaternion q) {
            double yaw = Math.atan2(2.0*(q.y()*q.z() + q.w()*q.x()), q.w()*q.w() - q.x()*q.x() - q.y()*q.y() + q.z()*q.z());
            double pitch = Math.asin(-2.0*(q.x()*q.z() - q.w()*q.y()));
            double roll = Math.atan2(2.0*(q.x()*q.y() + q.w()*q.z()), q.w()*q.w() + q.x()*q.x() - q.y()*q.y() - q.z()*q.z());
            if (isGettingBaseline) {
                yawRefMag = ((yawRefMag * orientSamples) + yaw) / (orientSamples + 1);
                pitchRefMag = ((pitchRefMag * orientSamples) + pitch) / (orientSamples + 1);
                rollRefMag = ((rollRefMag * orientSamples) + roll) / (orientSamples + 1);
                orientSamples++;
            }
            else {
                if (Math.abs(yaw - yawRefMag) > 0.1 ||
                        Math.abs(roll - rollRefMag) > 0.1 ||
                        Math.abs(pitch - pitchRefMag) > 0.1) {
                    lose();
                }
            }
        }

        @Override
        public void onAccelerometerData (Myo myo, long timestamp, Vector3 accel) {
            double calcMag = Math.sqrt(Math.pow(accel.x(), 2) + Math.pow(accel.y(), 2) + Math.pow(accel.z(), 2));
            if (isGettingBaseline) {
                accelRefMag = ((accelRefMag * accelSamples) + calcMag) / ++accelSamples;
            }
            else {
                if (Math.abs(accelRefMag - calcMag) > 0.7) {
                    lose();
                }
            }
        }

        @Override
        public void onGyroscopeData (Myo myo, long timestamp, Vector3 gyro) {
            double calcMag = Math.sqrt(Math.pow(gyro.x(), 2) + Math.pow(gyro.y(), 2) + Math.pow(gyro.z(), 2));
            if (isGettingBaseline) {
                gyroRefMag = ((gyroRefMag * gyroSamples) + calcMag) / ++gyroSamples;
            }
            else {
                if (Math.abs(gyroRefMag - calcMag) > 5) {
                    lose();
                }
            }
        }
    };

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
        hub.setSendUsageData(false);
        hub.addListener(mListener);

        pThread = new PokeThread();
        pThread.setRunning(true);
        pThread.start();
    }

    private void lose() {
        synchronized(isLostRecorded) {
            if (!isLostRecorded) {
                try {
                    pThread.setRunning(false);
                    pThread.join();
                    isLostRecorded = true;
                }
                catch (Exception e) {

                }

            }
        }
    }
}
