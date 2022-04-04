package com.otaku.ads.mediation.admob;

import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;

public class InterstitialModel {

    private boolean isShow = true;
    private boolean isReloaded = false;
    private long lastTimeShow = 0;
    private PublisherInterstitialAd ad;

    public InterstitialModel() {

    }

    public boolean isShow() {
        return isShow;
    }

    public InterstitialModel setShow(boolean show) {
        isShow = show;
        return this;
    }

    public boolean isReloaded() {
        return isReloaded;
    }

    public InterstitialModel setReloaded(boolean reloaded) {
        isReloaded = reloaded;
        return this;
    }

    public long getLastTimeShow() {
        return lastTimeShow;
    }

    public InterstitialModel setLastTimeShow(long lastTimeShow) {
        this.lastTimeShow = lastTimeShow;
        return this;
    }

    public PublisherInterstitialAd getAd() {
        return ad;
    }

    public InterstitialModel setAd(PublisherInterstitialAd ad) {
        this.ad = ad;
        return this;
    }
}
