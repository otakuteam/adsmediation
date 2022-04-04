package com.otaku.ads.mediation;

import android.content.Context;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.otaku.ads.mediation.admob.AdmobAdsManager;
import com.otaku.ads.mediation.callback.BannerAdsListener;
import com.otaku.ads.mediation.callback.PopupAdsListener;
import com.otaku.ads.mediation.callback.RewardAdListener;
import com.otaku.ads.mediation.unity.UnityAdsManager;
import com.otaku.ads.mediation.util.AdsLog;
import com.otaku.ads.mediation.util.AdsPreferenceUtil;

import static com.otaku.ads.mediation.admob.AdmobAdsManager.intersAds;

public class AdsManager {
    private final String TAG = getClass().getSimpleName();
    private UnityAdsManager unityAdsManager;
    private AdmobAdsManager admobAdsManager;
    private boolean mEnableAd = true;
    private long mPreviousTime = 0;
    private boolean hasAdmob = true;

    //ads id
    private String admobAppId, admobBanner, admobPopup, admobReward, unityAppId, unityPopup, unityReward;

    private AdsManager() {
    }

    private static AdsManager instance;

    public static AdsManager getInstance() {
        if (instance == null) {
            instance = new AdsManager();
        }
        return instance;
    }

    public AdsManager setAdmobApp(String appid) {
        this.admobAppId = appid;
        return this;
    }

    public AdsManager setAdmobBanner(String banner) {
        this.admobBanner = banner;
        return this;
    }

    public AdsManager setAdmobPopup(String popup) {
        this.admobPopup = popup;
        return this;
    }

    public AdsManager setAdmobReward(String reward) {
        this.admobReward = reward;
        return this;
    }

    public AdsManager setUnityApp(String appid) {
        this.unityAppId = appid;
        return this;
    }

    public AdsManager setUnityPopup(String popup) {
        this.unityPopup = popup;
        return this;
    }

    public AdsManager setUnityReward(String reward) {
        this.unityReward = reward;
        return this;
    }

    public void init(Context context) {
        admobAdsManager = new AdmobAdsManager(context, admobAppId, admobBanner, admobPopup, admobReward);
        admobAdsManager.init();
        unityAdsManager = new UnityAdsManager(context, unityAppId, unityPopup, unityReward);
        unityAdsManager.init();
        mEnableAd = AdsPreferenceUtil.getInstance().getBoolean(AdsConstants.PREF_ENABLE_AD, true);
        if (admobAppId == null || admobAppId.length() == 0) {
            hasAdmob = false;
        }
    }

    public void showBanner(ViewGroup banner) {
        try {
            AdsLog.d(TAG, "showBanner: has_admob");
            admobAdsManager.showBanner(banner, new BannerAdsListener() {
                @Override
                public void OnLoadFail() {
                    AdsLog.d(TAG, "showBanner: OnLoadFail");
                }
            });
        } catch (Exception e) {
            //AdsLog.e(TAG, e.getMessage());
        }
    }

    public void showPopup(PopupAdsListener listener) {
        try {
            if (canShowPopup()) {
                if (hasAdmob) {
                    AdsLog.d(TAG, "showPopup: has_admob");
                    admobAdsManager.showPopup(intersAds, new PopupAdsListener() {
                        @Override
                        public void OnClose() {
                            listener.OnClose();
                        }

                        @Override
                        public void OnShowFail() {
                            unityAdsManager.showPopup(new PopupAdsListener() {
                                @Override
                                public void OnClose() {
                                    listener.OnClose();
                                }

                                @Override
                                public void OnShowFail() {
                                    listener.OnShowFail();
                                }
                            });
                        }
                    });
                } else {
                    unityAdsManager.showPopup(new PopupAdsListener() {
                        @Override
                        public void OnClose() {
                            listener.OnClose();
                        }

                        @Override
                        public void OnShowFail() {
                            listener.OnShowFail();
                        }
                    });
                }
                mPreviousTime = System.currentTimeMillis();
            }
        } catch (Exception e) {
            listener.OnShowFail();
            //AdsLog.e(TAG, e.getMessage());
        }
    }

    private boolean canShowPopup() {
        //check period to show
        long currentTime = System.currentTimeMillis();
        AdsLog.i(TAG, "canShowPopup: " + getLimitTime() + " " + (currentTime - mPreviousTime));
        return mEnableAd && (currentTime - mPreviousTime >= getLimitTime());
    }

    public void showReward(RewardAdListener listener) {
        try {
            if (hasAdmob) {
                AdsLog.d(TAG, "showReward: has_admob");
                admobAdsManager.showReward(new RewardAdListener() {
                    @Override
                    public void OnClose() {
                        listener.OnClose();
                        AdsLog.d(TAG, "admob OnClose");
                    }

                    @Override
                    public void OnShowFail() {
                        AdsLog.d(TAG, "admob OnShowFail");
                        unityAdsManager.showReward(new RewardAdListener() {
                            @Override
                            public void OnClose() {
                                listener.OnClose();
                            }

                            @Override
                            public void OnShowFail() {
                                listener.OnShowFail();
                            }

                            @Override
                            public void OnRewarded() {
                                listener.OnRewarded();
                            }
                        });
                    }

                    @Override
                    public void OnRewarded() {
                        AdsLog.d(TAG, "admob OnRewarded");
                        listener.OnRewarded();
                    }
                });
            } else {
                unityAdsManager.showReward(new RewardAdListener() {
                    @Override
                    public void OnClose() {
                        listener.OnClose();
                    }

                    @Override
                    public void OnShowFail() {
                        listener.OnShowFail();
                    }

                    @Override
                    public void OnRewarded() {
                        listener.OnRewarded();
                    }
                });
            }
        } catch (Exception e) {
            listener.OnShowFail();
            //AdsLog.e(TAG, e.getMessage());
        }
    }


    public long getLimitTime() {
        long interval = AdsPreferenceUtil.getInstance().getLong(AdsConstants.PREF_AD_TIME, 15); //in second
        return interval * 1000;
    }

    public void setLimitTime(long time) {
        AdsPreferenceUtil.getInstance().putLong(AdsConstants.PREF_AD_TIME, time);
    }

    public void enableAd() {
        mEnableAd = true;
        AdsPreferenceUtil.getInstance().putBoolean(AdsConstants.PREF_ENABLE_AD, mEnableAd);
    }

    public void muteAdsForever() {
        mEnableAd = false;
        AdsPreferenceUtil.getInstance().putBoolean(AdsConstants.PREF_ENABLE_AD, mEnableAd);
    }
}