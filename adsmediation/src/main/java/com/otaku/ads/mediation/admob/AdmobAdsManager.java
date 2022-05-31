package com.otaku.ads.mediation.admob;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.otaku.ads.mediation.AdsConstants;
import com.otaku.ads.mediation.callback.BannerAdsListener;
import com.otaku.ads.mediation.callback.PopupAdsListener;
import com.otaku.ads.mediation.callback.RewardAdListener;
import com.otaku.ads.mediation.util.AdsLog;
import com.otaku.ads.mediation.util.AdsPreferenceUtil;

import org.jetbrains.annotations.NotNull;

public class AdmobAdsManager {
    public final String TAG = getClass().getSimpleName();
    private Context mContext;
    public static InterstitialModel intersAds;
    public static RewardModel rewardAd;
    private PopupAdsListener adPopupListener;
    private RewardAdListener adRewardListener;
    private AppOpenAd appOpenAd;
    private String app_id = "";
    private String banner_id = "";
    private String popup_id = "";
    private String reward_id = "";


    private boolean mCount = false;

    public AdmobAdsManager(Context context, String appId, String bannerid, String popupid, String rewardid) {
        mContext = context;
        app_id = appId;
        banner_id = bannerid;
        popup_id = popupid;
        reward_id = rewardid;

        intersAds = new InterstitialModel().setShow(true);
        rewardAd = new RewardModel().setShow(true);
    }

    public void init() {
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(mContext, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        loadInterstitialAd();
        loadReward();
    }

    public void showBanner(AdView adView, AdListener listener) {
        AdRequest adRequest = new AdRequest.Builder()
//                    .addTestDevice("C15580731DB985062B527414D86E5447")
                .build();
        adView.loadAd(adRequest);
    }

    public void showBanner(ViewGroup banner, BannerAdsListener listener) {
        if (banner != null) banner.removeAllViews();
        AdView adView = new AdView(mContext);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(banner_id);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                listener.OnLoadFail();
            }
        });

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        banner.addView(adView, params);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        adView.loadAd(adRequest);
    }

    private void loadInterstitialAd() {
        AdsLog.i(TAG, "Admob - loadInterstitialAd()");
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                mContext,
                popup_id,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        intersAds.setAd(interstitialAd);
                        AdsLog.i(TAG, "onAdLoaded");
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        intersAds.setAd(null);
                                        AdsLog.d(TAG, "The ad was dismissed.");
                                        loadInterstitialAd();
                                        if (adPopupListener != null) adPopupListener.OnClose();
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        intersAds.setAd(null);
                                        loadInterstitialAd();
                                        AdsLog.d(TAG, "The ad failed to show.");
                                        if (adPopupListener != null) adPopupListener.OnShowFail();
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        AdsLog.d(TAG, "The ad was shown.");
                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        AdsLog.i(TAG, loadAdError.getMessage());
                        intersAds.setAd(null);
                        if (intersAds.isReloaded() == false) {
                            intersAds.setReloaded(true);
                            loadInterstitialAd();
                        }
                        if (adPopupListener != null) adPopupListener.OnShowFail();
                    }
                });
    }

    private long getCurrentTime() {
        return System.currentTimeMillis();
    }

    private long getLimitTime() {
        long interval = AdsPreferenceUtil.getInstance().getLong(AdsConstants.PREF_ADMOB_TIME, 30); //in second
        AdsLog.d(TAG, "limit_admob_time: " + interval);
        return interval * 1000;
    }

    public void showPopup(Activity activity, PopupAdsListener listener) {
        adPopupListener = listener;
        if (intersAds.isShow()) {
            long currentTime = getCurrentTime();
            if (currentTime - intersAds.getLastTimeShow() >= getLimitTime()) {
                intersAds.setLastTimeShow(currentTime);
                //show interstial ad
                if (canShowInterstitialAd(intersAds)) {
                    AdsLog.i(TAG, "show");
                    intersAds.getAd().show(activity);
                } else {
                    listener.OnShowFail();
                }
            } else {
                AdsLog.i(TAG, "not show not enough interval time");
                listener.OnShowFail();
            }
        } else {
            AdsLog.i(TAG, "not show");
            listener.OnShowFail();
        }
    }

    private boolean canShowInterstitialAd(InterstitialModel adModel) {
        boolean canShow = adModel.getAd() != null;
        if (canShow) {
            if (!mCount) mCount = true;
            else mCount = false;
        }
        return (mCount && canShow);
    }

    //############### Reward ads ###################
    private void loadReward() {
        AdsLog.i(TAG, "Admob - loadReward()");
        if (rewardAd.getAd() == null) {
            rewardAd.setLoading(true);
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(
                    mContext,
                    reward_id,
                    adRequest,
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                            AdsLog.d(TAG, loadAdError.getMessage());
                            rewardAd.setAd(null);
                            rewardAd.setLoading(false);
                            if (rewardAd.isReloaded() == false) {
                                rewardAd.setReloaded(true);
                                loadReward();
                            }
                            if (adRewardListener != null)
                                adRewardListener.OnShowFail();

                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            rewardAd.setAd(rewardedAd);
                            rewardAd.setLoading(false);
                        }
                    });
        }
    }

    public void showReward(Activity activity, RewardAdListener listener) {
        adRewardListener = listener;
        if (rewardAd.isShow()) {
            AdsLog.d(TAG, "isShow");
            long currentTime = getCurrentTime();
            if (currentTime - rewardAd.getLastTimeShow() >= getLimitRewardTime()) {
                rewardAd.setLastTimeShow(currentTime);
                //show reward ad
                AdsLog.d(TAG, "check can show reward");
                if (canShowReward(rewardAd)) {
                    AdsLog.d(TAG, "canshow");
                    rewardAd.setReloaded(false);
                    rewardAd.getAd().setFullScreenContentCallback(
                            new FullScreenContentCallback() {
                                @Override
                                public void onAdShowedFullScreenContent() {
                                    // Called when ad is shown.
                                    AdsLog.d(TAG, "onAdShowedFullScreenContent");
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(AdError adError) {
                                    // Called when ad fails to show.
                                    AdsLog.d(TAG, "onAdFailedToShowFullScreenContent");
                                    // Don't forget to set the ad reference to null so you
                                    // don't show the ad a second time.
                                    rewardAd.setAd(null);
                                    if (!rewardAd.isLoading()) loadReward();
                                }

                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    // Called when ad is dismissed.
                                    // Don't forget to set the ad reference to null so you
                                    // don't show the ad a second time.
                                    AdsLog.d(TAG, "onAdDismissedFullScreenContent");
                                    rewardAd.setAd(null);
                                    if (!rewardAd.isLoading()) loadReward();
                                }
                            });
                    rewardAd.getAd().show(
                            activity,
                            new OnUserEarnedRewardListener() {
                                @Override
                                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                    AdsLog.d(TAG, "onRewarded");
                                    if (!rewardAd.isLoading()) loadReward();
                                    if (adRewardListener != null)
                                        adRewardListener.OnRewarded();
                                    AdsLog.d(TAG, "onRewarded: " + rewardItem);
                                }
                            });
                } else {
                    if (listener != null)
                        listener.OnShowFail();
                }
            } else {
                if (listener != null)
                    listener.OnShowFail();
            }
        } else {
            if (listener != null)
                listener.OnShowFail();
        }
    }

    private boolean canShowReward(RewardModel rewardAd) {
        return (rewardAd.getAd() != null);
    }

    private long getLimitRewardTime() {
        return 0 * 1000;
    }

}
