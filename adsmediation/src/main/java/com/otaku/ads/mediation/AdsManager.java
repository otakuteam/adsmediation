package com.otaku.ads.mediation;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.otaku.ads.mediation.admob.AdmobAdsManager;
import com.otaku.ads.mediation.callback.BannerAdsListener;
import com.otaku.ads.mediation.callback.OpenAdsListener;
import com.otaku.ads.mediation.callback.PopupAdsListener;
import com.otaku.ads.mediation.callback.RewardAdListener;
import com.otaku.ads.mediation.unity.UnityAdsManager;
import com.otaku.ads.mediation.util.AdsLog;
import com.otaku.ads.mediation.util.AdsPreferenceUtil;

public class AdsManager {
    private final String TAG = getClass().getSimpleName();
    private UnityAdsManager unityAdsManager;
    private AdmobAdsManager admobAdsManager;
    private boolean mEnableAd = true;
    private boolean mShowBanner = true;
    private long mPreviousTime = 0;
    private boolean hasAdmob = true;

    //ads id
    private String admobAppId, admobOpenId, admobBanner, admobPopup, admobReward, unityAppId, unityPopup, unityReward;

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

    public AdsManager setAdmobOpen(String openId) {
        this.admobOpenId = openId;
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
        AdsPreferenceUtil.getInstance().init(context);
        admobAdsManager = new AdmobAdsManager(context, admobAppId, admobBanner, admobPopup, admobReward, admobOpenId);
        admobAdsManager.init();
        unityAdsManager = new UnityAdsManager(context, unityAppId, unityPopup, unityReward);
        unityAdsManager.init();
        mEnableAd = AdsPreferenceUtil.getInstance().getBoolean(AdsConstants.PREF_ENABLE_AD, true);
        if (admobAppId == null || admobAppId.length() == 0) {
            hasAdmob = false;
        }
    }

    public void showBanner(AdView banner) {
        try {
            if (mEnableAd && mShowBanner) {
                AdsLog.d(TAG, "showBanner: has_admob");
                admobAdsManager.showBanner(banner, new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                    }
                });
            }
        } catch (Exception e) {
            //AdsLog.e(TAG, e.getMessage());
        }
    }

    public void showBannerViewGroup(ViewGroup banner) {
        try {
            if (mEnableAd && mShowBanner) {
                AdsLog.d(TAG, "showBanner: has_admob");
                admobAdsManager.showBanner(banner, new BannerAdsListener() {
                    @Override
                    public void OnLoadFail() {

                    }
                });
            }
        } catch (Exception e) {
            //AdsLog.e(TAG, e.getMessage());
        }
    }

    public void showPopup(Activity activity, PopupAdsListener listener) {
        try {
            if (canShowPopup()) {
                if (hasAdmob) {
                    AdsLog.d(TAG, "showPopup: has_admob");
                    admobAdsManager.showPopup(activity, new PopupAdsListener() {
                        @Override
                        public void OnClose() {
                            listener.OnClose();
                        }

                        @Override
                        public void OnShowFail() {
                            unityAdsManager.showPopup(activity, new PopupAdsListener() {
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
                    unityAdsManager.showPopup(activity, new PopupAdsListener() {
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

    public void showUnityPopup(Activity activity, PopupAdsListener listener) {
        try {
            if (canShowPopup()) {
                unityAdsManager.showPopup(activity, new PopupAdsListener() {
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

    public void showReward(Activity activity, RewardAdListener listener) {
        try {
            if (hasAdmob) {
                AdsLog.d(TAG, "showReward: has_admob");
                admobAdsManager.showReward(activity, new RewardAdListener() {
                    @Override
                    public void OnClose() {
                        listener.OnClose();
                        AdsLog.d(TAG, "admob OnClose");
                    }

                    @Override
                    public void OnShowFail() {
                        AdsLog.d(TAG, "admob OnShowFail");
                        unityAdsManager.showReward(activity, new RewardAdListener() {
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
                unityAdsManager.showReward(activity, new RewardAdListener() {
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

    public void showUnityReward(Activity activity, RewardAdListener listener) {
        try {
            unityAdsManager.showReward(activity, new RewardAdListener() {
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
        } catch (Exception e) {
            listener.OnShowFail();
            //AdsLog.e(TAG, e.getMessage());
        }
    }

    public void showOpenAdIfAvailable(Activity activity) {
        try {
            if (hasAdmob)
                admobAdsManager.showAdIfAvailable(activity);
        } catch (Exception e) {

        }
    }

    public boolean isShowingOpenAd() {
        return admobAdsManager.isShowingAd;
    }

    public void showOpenAdIfAvailable(Activity activity, OpenAdsListener onShowAdCompleteListener) {
        try {
            if (hasAdmob)
                admobAdsManager.showAdIfAvailable(activity, onShowAdCompleteListener);
        } catch (Exception e) {

        }
    }

    public void setShowBanner(boolean showBanner) {
        mShowBanner = showBanner;
        AdsPreferenceUtil.getInstance().putBoolean(AdsConstants.PREF_ENABLE_BANNER, mShowBanner);
    }

    public long getLimitTime() {
        long interval = AdsPreferenceUtil.getInstance().getLong(AdsConstants.PREF_AD_TIME, 15); //in second
        AdsLog.d(TAG, "limit_time: " + interval);
        return interval * 1000;
    }

    public void setLimitTime(long time) {
        AdsPreferenceUtil.getInstance().putLong(AdsConstants.PREF_AD_TIME, time);
    }

    public void setLimitAdmobTime(long time) {
        AdsPreferenceUtil.getInstance().putLong(AdsConstants.PREF_ADMOB_TIME, time);
    }

    public long getLimitAdmobTime() {
        long interval = AdsPreferenceUtil.getInstance().getLong(AdsConstants.PREF_ADMOB_TIME, 30); //in second
        AdsLog.d(TAG, "limit_admob_time: " + interval);
        return interval * 1000;
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