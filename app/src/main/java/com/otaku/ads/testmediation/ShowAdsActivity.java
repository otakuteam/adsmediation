package com.otaku.ads.testmediation;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.otaku.ads.mediation.AdsManager;
import com.otaku.ads.mediation.callback.PopupAdsListener;
import com.otaku.ads.mediation.callback.RewardAdListener;

public class ShowAdsActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ads);
        LogDebug.d(TAG, "OnCreate()");
        AdsManager.getInstance().showPopup(new PopupAdsListener() {
            @Override
            public void OnClose() {

            }

            @Override
            public void OnShowFail() {

            }
        });

        ((Button) findViewById(R.id.btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdsManager.getInstance().showReward(new RewardAdListener() {
                    @Override
                    public void OnClose() {
                        Toast.makeText(getApplicationContext(), "Close reward ads", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void OnShowFail() {
                        Toast.makeText(getApplicationContext(), "Fail reward ads", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void OnRewarded() {
                        Toast.makeText(getApplicationContext(), "Rewarded!!!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        LogDebug.i(TAG, "onResume()");
    }
}