package com.instaapp.profile;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.instaapp.base.ViewModelProviderFactory;
import com.instaapp.home.HomeFragmentViewModel;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jiveksha on 8/20/18.
 */
@Module
public class SignOutFragmentModule {


    @Provides
    @Named("SignOutFragment")
    ViewModelProvider.Factory signOutFragmentViewModelProvider(SignOutFragmentViewModel signOutFragmentViewModel) {
        return new ViewModelProviderFactory<>(signOutFragmentViewModel);
    }


    @Provides
    SignOutFragmentViewModel signUpFragmentViewModel(Context context, FirebaseAuth firebaseAuth, FirebaseDatabase firebaseDatabase, FirebaseStorage firebaseStorage) {
        return new SignOutFragmentViewModel(context, firebaseAuth, firebaseDatabase, firebaseStorage);
    }

}
