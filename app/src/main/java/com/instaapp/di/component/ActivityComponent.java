package com.instaapp.di.component;

import com.instaapp.di.annotation.PerActivity;
import com.instaapp.di.module.ActivityModule;
import com.instaapp.home.HomeActivity;
import com.instaapp.login.LoginActivity;
import com.instaapp.login.RegisterActivity;
import com.instaapp.profile.AccountSettingsActivity;
import com.instaapp.profile.ProfileActivity;
import com.instaapp.search.SearchActivity;
import com.instaapp.share.ShareActivity;

import dagger.Component;

/**
 * Created by jiveksha on 8/14/18.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(HomeActivity homeActivity);

    void inject(SearchActivity searchActivity);

    void inject(ProfileActivity profileActivity);

    void inject(AccountSettingsActivity accountSettingsActivity);

    void inject(LoginActivity loginActivity);

    void inject(RegisterActivity registerActivity);

    void inject(ShareActivity shareActivity);
}
