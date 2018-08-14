package com.instaapp.di.module;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.instaapp.di.annotation.FragmentContext;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jiveksha on 8/14/18.
 */
@Module
public class FragmentModule {

    private Fragment mFragment;

    public FragmentModule(Fragment fragment) {
        mFragment = fragment;
    }

    @Provides
    @FragmentContext
    Context provideContext() {
        return mFragment.getContext();
    }

}