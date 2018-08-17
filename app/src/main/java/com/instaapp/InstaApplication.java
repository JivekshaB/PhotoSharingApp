package com.instaapp;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.instaapp.di.component.ApplicationComponent;
import com.instaapp.di.component.DaggerApplicationComponent;
import com.instaapp.di.module.ApplicationModule;
import io.fabric.sdk.android.Fabric;

/**
 * Created by jiveksha on 8/14/18.
 */

public class InstaApplication extends Application {


    protected ApplicationComponent applicationComponent;

    public static InstaApplication get(Context context) {
        return (InstaApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        applicationComponent.inject(this);
    }

    public ApplicationComponent getComponent() {
        return applicationComponent;
    }

}
