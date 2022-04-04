package com.otaku.ads.testmediation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.otaku.ads.mediation.AdsManager;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdsManager.getInstance().setAdmobApp(getString(R.string.admob_app_id))
                .setAdmobBanner(getString(R.string.admob_banner_id))
                .setAdmobPopup(getString(R.string.admob_interstitial_id))
                .setAdmobReward(getString(R.string.admob_reward_id))
                .setUnityApp(getString(R.string.unity_game_id))
                .setUnityPopup(getString(R.string.unity_popup_id))
                .setUnityReward(getString(R.string.unity_reward_id))
                .init(this);
        AdsManager.getInstance().showBanner((ViewGroup) findViewById(R.id.banner));
        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ShowAdsActivity.class);
                startActivity(intent);
            }
        });

//        ((Button) findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), ShowAdsActivity2.class);
//                startActivity(intent);
//            }
//        });
    }
}