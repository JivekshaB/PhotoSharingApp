/*
 *  Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://mindorks.com/license/apache-v2
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.instaapp.di.builder;

import com.instaapp.home.HomeActivity;
import com.instaapp.home.HomeActivityModule;
import com.instaapp.home.HomeFragmentProvider;
import com.instaapp.login.LoginActivity;
import com.instaapp.login.LoginActivityModule;
import com.instaapp.login.RegisterActivity;
import com.instaapp.login.RegistrationActivityModule;
import com.instaapp.profile.AccountSettingsActivity;
import com.instaapp.profile.AccountSettingsModule;
import com.instaapp.profile.EditProfileFragmentProvider;
import com.instaapp.profile.ProfileActivity;
import com.instaapp.profile.ProfileActivityModule;
import com.instaapp.profile.ProfileFragmentProvider;
import com.instaapp.profile.SignOutFragmentProvider;
import com.instaapp.profile.ViewCommentsFragmentProvider;
import com.instaapp.profile.ViewPostFragmentProvider;
import com.instaapp.profile.ViewProfileFragmentProvider;
import com.instaapp.search.SearchActivity;
import com.instaapp.search.SearchActivityModule;
import com.instaapp.share.GalleryFragmentProvider;
import com.instaapp.share.NextActivity;
import com.instaapp.share.NextActivityModule;
import com.instaapp.share.PhotoFragmentProvider;
import com.instaapp.share.ShareActivity;
import com.instaapp.share.ShareActivityModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;


@Module
public abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = {HomeActivityModule.class, HomeFragmentProvider.class, ViewCommentsFragmentProvider.class})
    abstract HomeActivity bindHomeActivity();

    @ContributesAndroidInjector(modules = LoginActivityModule.class)
    abstract LoginActivity bindLoginActivity();

    @ContributesAndroidInjector(modules = RegistrationActivityModule.class)
    abstract RegisterActivity bindRegisterActivity();

    @ContributesAndroidInjector(modules = {AccountSettingsModule.class,
            EditProfileFragmentProvider.class,
            SignOutFragmentProvider.class})
    abstract AccountSettingsActivity bindAccountSettingsActivity();

    @ContributesAndroidInjector(modules = {ProfileActivityModule.class,
            ProfileFragmentProvider.class,
            ViewProfileFragmentProvider.class,
            ViewPostFragmentProvider.class})
    abstract ProfileActivity bindProfileActivity();

    @ContributesAndroidInjector(modules = SearchActivityModule.class)
    abstract SearchActivity bindSearchActivity();

    @ContributesAndroidInjector(modules = {ShareActivityModule.class, GalleryFragmentProvider.class, PhotoFragmentProvider.class})
    abstract ShareActivity bindShareActivity();

    @ContributesAndroidInjector(modules = NextActivityModule.class)
    abstract NextActivity bindNextActivity();


}
