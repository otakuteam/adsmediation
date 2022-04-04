package com.otaku.ads.mediation.admob;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.otaku.ads.mediation.callback.BannerAdsListener;
import com.otaku.ads.mediation.callback.PopupAdsListener;
import com.otaku.ads.mediation.callback.RewardAdListener;
import com.otaku.ads.mediation.util.AdsLog;

public class AdmobAdsManager {
    public final String TAG = getClass().getSimpleName();
    private Context mContext;
    public static InterstitialModel intersAds;
    public static RewardModel rewardAd;
    private PopupAdsListener adPopupListener;
    private RewardAdListener adRewardListener;
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
        //ad on item_twoway image on gird clicked
        PublisherInterstitialAd ad = new PublisherInterstitialAd(context);
        ad.setAdUnitId(popup_id);
        ad.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                loadInterstitialAd(intersAds);
                adPopupListener.OnClose();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                if (intersAds.isReloaded() == false) {
                    intersAds.setReloaded(true);
                    loadInterstitialAd(intersAds);
                }
            }
        });
        intersAds = new InterstitialModel().setAd(ad)
                .setShow(true); // set show this ad

        RewardedVideoAd reward = MobileAds.getRewardedVideoAdInstance(mContext);
        reward.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                AdsLog.d(TAG, "onRewardedVideoAdLoaded");
            }

            @Override
            public void onRewardedVideoAdOpened() {
                AdsLog.d(TAG, "onRewardedVideoAdOpened");
            }

            @Override
            public void onRewardedVideoStarted() {
                AdsLog.d(TAG, "onRewardedVideoStarted");
            }

            @Override
            public void onRewardedVideoAdClosed() {
                AdsLog.d(TAG, "onRewardedVideoAdClosed");
                loadReward(rewardAd);
                if (adRewardListener != null)
                    adRewardListener.OnClose();
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                AdsLog.d(TAG, "onRewarded");
                loadReward(rewardAd);
                if (adRewardListener != null)
                    adRewardListener.OnRewarded();
                AdsLog.d(TAG, "onRewarded: " + rewardItem);
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
                AdsLog.d(TAG, "onRewardedVideoAdLeftApplication");
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                AdsLog.d(TAG, "onRewardedVideoAdFailedToLoad");
                if (adRewardListener != null)
                    adRewardListener.OnShowFail();
                if (rewardAd.isReloaded() == false) {
                    rewardAd.setReloaded(true);
                    loadReward(rewardAd);
                }
            }

            @Override
            public void onRewardedVideoCompleted() {
                AdsLog.d(TAG, "onRewardedVideoCompleted");
            }
        });
        rewardAd = new RewardModel().setAd(reward)
                .setShow(true); // set show this ad

    }

    public void init() {
        MobileAds.initialize(mContext,
                app_id);
        loadInterstitialAd(intersAds);
        loadReward(rewardAd);
    }

    public void showBanner(ViewGroup banner, BannerAdsListener listener) {
        if (banner != null) banner.removeAllViews();
        PublisherAdView adView = new PublisherAdView(mContext);
        adView.setAdSizes(AdSize.SMART_BANNER);
        adView.setAdUnitId(banner_id);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                listener.OnLoadFail();
            }
        });

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        banner.addView(adView, params);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder()
                .build();
        adView.loadAd(adRequest);
    }

    private void loadInterstitialAd(InterstitialModel adModel) {
        adModel.getAd().loadAd(new PublisherAdRequest.Builder()
//                .addTestDevice("C15580731DB985062B527414D86E5447")
                .build());
    }

    private long getCurrentTime() {
        return System.currentTimeMillis();
    }

    private long getLimitTime() {
        return 0 * 1000; //40s
    }

    public void showPopup(InterstitialModel adModel, PopupAdsListener listener) {
        adPopupListener = listener;
        if (adModel.isShow()) {
            long currentTime = getCurrentTime();
            if (currentTime - adModel.getLastTimeShow() >= getLimitTime()) {
                adModel.setLastTimeShow(currentTime);
                //show interstial ad
                if (canShowInterstitialAd(adModel)) {
                    adModel.setReloaded(false);
                    AdsLog.i(TAG, "show");
                    adModel.getAd().show();
                } else {
                    AdsLog.i(TAG, "show error");
                    loadInterstitialAd(adModel);
                    if (adModel.getAd() == null || !adModel.getAd().isLoaded())
                        listener.OnShowFail();
                    else listener.OnClose();
                }
            } else {
                AdsLog.i(TAG, "not show not enough interval time");
                listener.OnClose();
            }
        } else {
            AdsLog.i(TAG, "not show");
            listener.OnClose();
        }

    }

    private boolean canShowInterstitialAd(InterstitialModel adModel) {
        boolean canShow = adModel.getAd() != null && adModel.getAd().isLoaded();
        if (canShow) {
            if (!mCount) mCount = true;
            else mCount = false;
        }
        return (mCount && canShow);
    }

    //############### Reward ads ###################
    private void loadReward(RewardModel adModel) {
        adModel.getAd().loadAd(reward_id, new AdRequest.Builder()
//                .addTestDevice(DEVICE_TEST_ID)
                .build());
    }

    public void showReward(RewardAdListener listener) {
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
                    rewardAd.getAd().show();
                } else {
                    loadReward(rewardAd);
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
        return (rewardAd.getAd() != null && rewardAd.getAd().isLoaded());
    }

    private long getLimitRewardTime() {
        return 0 * 1000;
    }

}
