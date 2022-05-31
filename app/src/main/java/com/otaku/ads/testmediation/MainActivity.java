package com.otaku.ads.testmediation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdView;
import com.otaku.ads.mediation.AdsManager;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdsManager.getInstance().showBanner((AdView) findViewById(R.id.publisherAdView));
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