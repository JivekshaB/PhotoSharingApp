package com.instaapp.profile;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by jiveksha on 8/20/18.
 */
@Module
public abstract class SignOutFragmentProvider {

    @ContributesAndroidInjector(modules = SignOutFragmentModule.class)
    abstract SignOutFragment provideSignOutFragmentFactory();
}