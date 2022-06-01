package com.otaku.ads.testmediation;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.otaku.ads.mediation.AdsManager;
import com.otaku.ads.mediation.callback.OpenAdsListener;
import com.otaku.ads.mediation.util.AdsLog;

public class App extends Application implements Application.ActivityLifecycleCallbacks, LifecycleObserver {

    private Activity currentActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(this);

        AdsManager.getInstance().setAdmobApp(getString(R.string.admob_app_id))
                .setAdmobOpen(getString(R.string.admob_open_id))
                .setAdmobBanner(getString(R.string.admob_banner_id))
                .setAdmobPopup(getString(R.string.admob_interstitial_id))
                .setAdmobReward(getString(R.string.admob_reward_id))
                .setUnityApp(getString(R.string.unity_game_id))
                .setUnityPopup(getString(R.string.unity_popup_id))
                .setUnityReward(getString(R.string.unity_reward_id))
                .init(this);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    /**
     * LifecycleObserver method that shows the app open ad when the app moves to foreground.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected void onMoveToForeground() {
        // Show the ad (if available) when the app moves to foreground.
        AdsLog.i("SplashActivity_openads", "onMoveToForeground");
        AdsManager.getInstance().showOpenAdIfAvailable(currentActivity);
    }

    /**
     * ActivityLifecycleCallback methods.
     */
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        // An ad activity is started when an ad is showing, which could be AdActivity class from Google
        // SDK or another activity class implemented by a third party mediation partner. Updating the
        // currentActivity only when an ad is not showing will ensure it is not an ad activity, but the
        // one that shows the ad.
        if (!AdsManager.getInstance().isShowingOpenAd()) {
            currentActivity = activity;
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
    }

    /**
     * Shows an app open ad.
     *
     * @param activity                 the activity that shows the app open ad
     * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
     */
    public void showAdIfAvailable(
            @NonNull Activity activity,
            @NonNull OpenAdsListener onShowAdCompleteListener) {
        // We wrap the showAdIfAvailable to enforce that other classes only interact with MyApplication
        // class.
        AdsLog.i("SplashActivity_openads", "showAdIfAvailable");
        AdsManager.getInstance().showOpenAdIfAvailable(activity, onShowAdCompleteListener);
    }

}
