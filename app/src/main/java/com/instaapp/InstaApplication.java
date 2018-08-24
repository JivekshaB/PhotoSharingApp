package com.instaapp;

import com.crashlytics.android.Crashlytics;
import com.instaapp.di.component.ApplicationComponent;
import com.instaapp.di.component.DaggerApplicationComponent;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import io.fabric.sdk.android.Fabric;

/**
 * Created by jiveksha on 8/14/18.
 */

public class InstaApplication extends DaggerApplication {

    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        ApplicationComponent appComponent = DaggerApplicationComponent.builder().application(this).context(getApplicationContext()).build();
        appComponent.inject(this);
        return appComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
    }


}
