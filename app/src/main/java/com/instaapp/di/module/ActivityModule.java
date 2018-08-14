package com.instaapp.di.module;

import android.app.Activity;
import android.content.Context;

import com.instaapp.di.annotation.ActivityContext;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jiveksha on 8/14/18.
 */
@Module
public class ActivityModule {

    private Activity mActivity;

    public ActivityModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    @ActivityContext
    Context provideContext() {
        return mActivity;
    }

    @Provides
    Activity provideActivity() {
        return mActivity;
    }
}