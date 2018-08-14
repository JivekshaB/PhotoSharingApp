package com.instaapp.di.component;

import com.instaapp.di.annotation.PerFragment;
import com.instaapp.di.module.FragmentModule;
import com.instaapp.home.HomeFragment;
import com.instaapp.profile.EditProfileFragment;
import com.instaapp.profile.ProfileFragment;
import com.instaapp.profile.SignOutFragment;
import com.instaapp.profile.ViewCommentsFragment;
import com.instaapp.profile.ViewPostFragment;
import com.instaapp.profile.ViewProfileFragment;
import com.instaapp.share.GalleryFragment;
import com.instaapp.share.PhotoFragment;

import dagger.Component;

/**
 * Created by jiveksha on 8/14/18.
 */

@PerFragment
@Component(dependencies = ActivityComponent.class, modules = FragmentModule.class)
public interface FragmentComponent {

    void inject(HomeFragment homeFragment);

    void inject(GalleryFragment galleryFragment);

    void inject(PhotoFragment photoFragment);

    void inject(ProfileFragment profileFragment);

    void inject(EditProfileFragment editProfileFragment);

    void inject(SignOutFragment signOutFragment);

    void inject(ViewProfileFragment viewProfileFragment);

    void inject(ViewCommentsFragment viewCommentsFragment);

    void inject(ViewPostFragment viewPostFragment);


}
