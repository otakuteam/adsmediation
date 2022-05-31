package com.otaku.ads.mediation.admob;

import com.google.android.gms.ads.rewarded.RewardedAd;

public class RewardModel {
    private boolean isShow = true;
    private boolean isReloaded = false;
    private boolean isLoading = false;
    private long lastTimeShow = 0;
    private RewardedAd ad;

    public RewardModel() {

    }

    public boolean isLoading() {
        return isLoading;
    }

    public RewardModel setLoading(boolean loading) {
        isLoading = loading;
        return this;
    }

    public boolean isShow() {
        return isShow;
    }

    public RewardModel setShow(boolean show) {
        isShow = show;
        return this;
    }

    public boolean isReloaded() {
        return isReloaded;
    }

    public RewardModel setReloaded(boolean reloaded) {
        isReloaded = reloaded;
        return this;
    }

    public long getLastTimeShow() {
        return lastTimeShow;
    }

    public RewardModel setLastTimeShow(long lastTimeShow) {
        this.lastTimeShow = lastTimeShow;
        return this;
    }

    public RewardedAd getAd() {
        return ad;
    }

    public RewardModel setAd(RewardedAd ad) {
        this.ad = ad;
        return this;
    }
}
