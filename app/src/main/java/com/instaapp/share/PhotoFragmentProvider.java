package com.instaapp.share;

import com.instaapp.profile.ViewProfileFragment;
import com.instaapp.profile.ViewProfileFragmentModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by jiveksha on 8/20/18.
 */
@Module
public abstract class PhotoFragmentProvider {

    @ContributesAndroidInjector(modules = PhotoFragmentModule.class)
    abstract PhotoFragment providePhotoFragmentFactory();
}