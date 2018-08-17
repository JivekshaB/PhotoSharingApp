package com.instaapp.di.component;

import android.content.Context;

import com.instaapp.BaseFragment;
import com.instaapp.di.annotation.FragmentContext;
import com.instaapp.di.annotation.PerFragment;
import com.instaapp.di.module.FragmentModule;

import dagger.Component;

/**
 * Created by jiveksha on 8/14/18.
 */

@PerFragment
@Component(dependencies = {ActivityComponent.class}, modules = FragmentModule.class)
public interface FragmentComponent {

    void inject(BaseFragment baseFragment);

    @FragmentContext
    Context getContext();
}
