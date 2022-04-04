//package com.otaku.ads.testmediation;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//
//import com.otaku.ads.testmediation.ads.LogDebug;
//import com.unity3d.mediation.IInitializationListener;
//import com.unity3d.mediation.IInterstitialAdLoadListener;
//import com.unity3d.mediation.IInterstitialAdShowListener;
//import com.unity3d.mediation.InitializationConfiguration;
//import com.unity3d.mediation.InterstitialAd;
//import com.unity3d.mediation.UnityMediation;
//import com.unity3d.mediation.errors.LoadError;
//import com.unity3d.mediation.errors.SdkInitializationError;
//import com.unity3d.mediation.errors.ShowError;
//
//public class ShowAdsActivity2 extends AppCompatActivity {
//    private final String TAG = getClass().getSimpleName();
//    private InterstitialAd interstitialAd;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_show_ads2);
//
//        InitializationConfiguration initializationConfiguration = InitializationConfiguration.builder()
//                .setGameId(getString(R.string.unity_game_id))
//                .setInitializationListener(new IInitializationListener() {
//                    @Override
//                    public void onInitializationComplete() {
//                        // You are now ready to load and show ads.
//                        // Keep in mind that you can call load on an ad unit before initialization is complete.
//                        // All pre-init loads will be queued and will run after init has finished.
//                    }
//
//                    @Override
//                    public void onInitializationFailed(SdkInitializationError errorCode, String msg) {
//
//                    }
//                }).build();
//
//        UnityMediation.initialize(initializationConfiguration);
//        interstitialAd = new InterstitialAd(this, getString(R.string.unity_popup_id));
//        interstitialAd.load(new IInterstitialAdLoadListener() {
//            @Override
//            public void onInterstitialLoaded(InterstitialAd interstitialAd) {
//                LogDebug.d(TAG, "onInterstitialLoaded");
//                // interstitial ad is ready to show
//                // you can also check the ad state prior to showing by using interstitialAd.getState()
//                interstitialAd.show(new IInterstitialAdShowListener() {
//                    @Override
//                    public void onInterstitialShowed(InterstitialAd interstitialAd) {
//                        // The ad has started to show.
//                        LogDebug.d(TAG, "onInterstitialShowed");
//                    }
//
//                    @Override
//                    public void onInterstitialClicked(InterstitialAd interstitialAd) {
//                        // The user has selected the ad.
//                        LogDebug.d(TAG, "onInterstitialClicked");
//                    }
//
//                    @Override
//                    public void onInterstitialClosed(InterstitialAd interstitialAd) {
//                        // The ad has finished showing.
//                        LogDebug.d(TAG, "onInterstitialClosed");
//                    }
//
//                    @Override
//                    public void onInterstitialFailedShow(InterstitialAd interstitialAd, ShowError error, String msg) {
//                        // An error occurred during the ad playback.
//                        LogDebug.d(TAG, "onInterstitialFailedShow");
//                    }
//                });
//            }
//
//            @Override
//            public void onInterstitialFailedLoad(InterstitialAd interstitialAd, LoadError error, String msg) {
//                // interstitial ad has failed to load
//                LogDebug.d(TAG, "onInterstitialFailedLoad");
//            }
//        });
//    }
//}