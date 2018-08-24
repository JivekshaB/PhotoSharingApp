package com.instaapp.profile;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by jiveksha on 8/20/18.
 */
@Module
public abstract class ViewCommentsFragmentProvider {

    @ContributesAndroidInjector(modules = ViewCommentsFragmentModule.class)
    abstract ViewCommentsFragment provideCommentFragmentFactory();
}