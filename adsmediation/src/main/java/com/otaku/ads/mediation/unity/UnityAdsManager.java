package com.otaku.ads.mediation.unity;

import android.app.Activity;
import android.content.Context;

import com.otaku.ads.mediation.callback.PopupAdsListener;
import com.otaku.ads.mediation.callback.RewardAdListener;
import com.otaku.ads.mediation.util.AdsLog;
import com.unity3d.mediation.IInitializationListener;
import com.unity3d.mediation.IInterstitialAdLoadListener;
import com.unity3d.mediation.IInterstitialAdShowListener;
import com.unity3d.mediation.IReward;
import com.unity3d.mediation.IRewardedAdLoadListener;
import com.unity3d.mediation.IRewardedAdShowListener;
import com.unity3d.mediation.InitializationConfiguration;
import com.unity3d.mediation.InterstitialAd;
import com.unity3d.mediation.RewardedAd;
import com.unity3d.mediation.UnityMediation;
import com.unity3d.mediation.errors.LoadError;
import com.unity3d.mediation.errors.SdkInitializationError;
import com.unity3d.mediation.errors.ShowError;


public class UnityAdsManager {
    private final String TAG = getClass().getSimpleName();
    private Activity mActivity;
    private String app_id = "";
    private String popup_id = "";
    private String reward_id = "";
    private InterstitialAd interstitialAd;
    private RewardedAd rewardedAd;

    public UnityAdsManager(Context context, String appId, String popup, String reward) {
        mActivity = (Activity) context;
        app_id = appId;
        popup_id = popup;
        reward_id = reward;
    }

    public void init() {
        InitializationConfiguration configuration = InitializationConfiguration.builder()
                .setGameId(app_id)
                .setInitializationListener(new IInitializationListener() {
                    @Override
                    public void onInitializationComplete() {
                        // Unity Mediation is initialized. Try loading an ad.
                        AdsLog.d(TAG, "Unity Mediation is successfully initialized.");
                        //loadInterstitialAd();
                    }

                    @Override
                    public void onInitializationFailed(SdkInitializationError errorCode, String msg) {
                        // Unity Mediation failed to initialize. Printing failure reason...
                        AdsLog.d(TAG, "Unity Mediation Failed to Initialize : " + msg);
                    }
                }).build();

        UnityMediation.initialize(configuration);
    }

    public void loadInterstitialAd() {
        AdsLog.d(TAG, "unity-loadInterstitialAd");
        interstitialAd = new InterstitialAd(mActivity, popup_id);
        // Implement a load listener interface:
        final IInterstitialAdLoadListener loadListener = new IInterstitialAdLoadListener() {
            @Override
            public void onInterstitialLoaded(InterstitialAd ad) {
                // Execute logic when the ad successfully loads.
                AdsLog.d(TAG, "onInterstitialLoaded");
            }

            @Override
            public void onInterstitialFailedLoad(InterstitialAd ad, LoadError error, String msg) {
                // Execute logic when the ad fails to load.
                AdsLog.d(TAG, "onInterstitialFailedLoad");
            }
        };

        // Load an ad:
        interstitialAd.load(loadListener);
    }

    public void showPopup(PopupAdsListener listener) {
        interstitialAd = new InterstitialAd(mActivity, popup_id);
        AdsLog.d(TAG, "unity-showPopup: " + interstitialAd.getAdState());
        interstitialAd.load(new IInterstitialAdLoadListener() {
            @Override
            public void onInterstitialLoaded(InterstitialAd interstitialAd) {
                AdsLog.d(TAG, "onInterstitialLoaded");
                // interstitial ad is ready to show
                // you can also check the ad state prior to showing by using interstitialAd.getState()
                interstitialAd.show(new IInterstitialAdShowListener() {
                    @Override
                    public void onInterstitialShowed(InterstitialAd interstitialAd) {
                        // The ad has started to show.
                        AdsLog.d(TAG, "onInterstitialShowed");
                    }

                    @Override
                    public void onInterstitialClicked(InterstitialAd interstitialAd) {
                        // The user has selected the ad.
                        AdsLog.d(TAG, "onInterstitialClicked");
                    }

                    @Override
                    public void onInterstitialClosed(InterstitialAd interstitialAd) {
                        // The ad has finished showing.
                        AdsLog.d(TAG, "onInterstitialClosed");
                    }

                    @Override
                    public void onInterstitialFailedShow(InterstitialAd interstitialAd, ShowError error, String msg) {
                        // An error occurred during the ad playback.
                        AdsLog.d(TAG, "onInterstitialFailedShow");
                    }
                });
            }

            @Override
            public void onInterstitialFailedLoad(InterstitialAd interstitialAd, LoadError error, String msg) {
                // interstitial ad has failed to load
                AdsLog.d(TAG, "onInterstitialFailedLoad");
            }
        });
    }

    public void showReward(RewardAdListener listener) {
        rewardedAd = new RewardedAd(mActivity, reward_id);
        rewardedAd.load(new IRewardedAdLoadListener() {
            @Override
            public void onRewardedLoaded(RewardedAd rewardedAd) {
                // rewarded ad is ready to show
                // you can also check the ad state prior to showing using interstitialAd.getState()
                AdsLog.d(TAG, "onRewardedLoaded");
                rewardedAd.show(new IRewardedAdShowListener() {
                    @Override
                    public void onRewardedShowed(RewardedAd rewardedAd) {
                        // Ad has played
                        AdsLog.d(TAG, "onRewardedShowed");
                    }

                    @Override
                    public void onRewardedClicked(RewardedAd rewardedAd) {
                        // Ad has been selected
                        AdsLog.d(TAG, "onRewardedClicked");
                    }

                    @Override
                    public void onRewardedClosed(RewardedAd rewardedAd) {
                        // Ad has been closed
                        AdsLog.d(TAG, "onRewardedClosed");
                    }

                    @Override
                    public void onRewardedFailedShow(RewardedAd rewardedAd, ShowError error, String msg) {
                        // Ad has failed to play
                        // Use the message and ShowError enum to determine the ad network and cause
                        AdsLog.d(TAG, "onRewardedFailedShow");
                    }

                    @Override
                    public void onUserRewarded(RewardedAd rewardedAd, IReward reward) {
                        // A reward can be issued based on the reward callback.
                        // Timing of this event can vary depending on the ad network to serve the impression.
                        AdsLog.d(TAG, "onUserRewarded");
                    }
                });
            }


            @Override
            public void onRewardedFailedLoad(RewardedAd rewardedAd, LoadError error, String msg) {
                // ad has failed to show
                // use the message and ShowError enum to determine ad network and cause
                AdsLog.d(TAG, "onRewardedFailedLoad");
            }
        });
    }
}
