package com.otaku.ads.testmediation;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.otaku.ads.mediation.callback.OpenAdsListener;
import com.otaku.ads.mediation.util.AdsLog;

public class SplashActivity extends AppCompatActivity {
    private final String TAG = "SplashActivity_openads";
    boolean isAdShowed = false;
    boolean isDataLoaded = false;
    private long secondsRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //fake data loading
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isDataLoaded = true;
                if (isAdShowed) startMainActivity();
            }
        }, 5000);

        //wait waitTime second for load open ad
        int waitTime = 3;
        createTimer(waitTime);

        //timeout case
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startMainActivity();
            }
        }, 10000);
    }

    private void createTimer(long seconds) {
        final TextView counterTextView = findViewById(R.id.timer);

        CountDownTimer countDownTimer =
                new CountDownTimer(seconds * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        secondsRemaining = ((millisUntilFinished / 1000) + 1);
                        counterTextView.setText("App is done loading in: " + secondsRemaining);
                    }

                    @Override
                    public void onFinish() {
                        secondsRemaining = 0;
                        counterTextView.setText("Done.");

                        Application application = getApplication();

                        // If the application is not an instance of MyApplication, log an error message and
                        // start the MainActivity without showing the app open ad.
                        if (!(application instanceof App)) {
                            AdsLog.i(TAG, "Failed to cast application to MyApplication.");
                            isAdShowed = true;
                            if (isDataLoaded)
                                startMainActivity();
                            return;
                        }

                        // Show the app open ad.
                        ((App) application)
                                .showAdIfAvailable(
                                        SplashActivity.this,
                                        new OpenAdsListener() {
                                            @Override
                                            public void OnShowAdComplete() {
                                                AdsLog.i(TAG, "OnShowAdComplete.");
                                                isAdShowed = true;
                                                if (isDataLoaded)
                                                    startMainActivity();
                                            }
                                        });
                    }
                };
        countDownTimer.start();
    }

    public void startMainActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }
}