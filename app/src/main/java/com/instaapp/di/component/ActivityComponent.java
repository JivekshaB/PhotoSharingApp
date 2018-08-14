package com.instaapp.di.component;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.instaapp.BaseActivity;
import com.instaapp.di.annotation.ActivityContext;
import com.instaapp.di.annotation.PerActivity;
import com.instaapp.di.module.ActivityModule;

import dagger.Component;

/**
 * Created by jiveksha on 8/14/18.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(BaseActivity baseActivity);

    @ActivityContext
    Context getContext();

    AppCompatActivity getActivity();


}
