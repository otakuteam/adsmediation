package com.otaku.ads.mediation.admob;

import com.google.android.gms.ads.reward.RewardedVideoAd;

public class RewardModel {
    private boolean isShow = true;
    private boolean isReloaded = false;
    private long lastTimeShow = 0;
    private RewardedVideoAd ad;

    public RewardModel() {

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

    public RewardedVideoAd getAd() {
        return ad;
    }

    public RewardModel setAd(RewardedVideoAd ad) {
        this.ad = ad;
        return this;
    }
}
