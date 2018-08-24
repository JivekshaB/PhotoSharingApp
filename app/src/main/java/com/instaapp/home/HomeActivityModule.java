package com.instaapp.home;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.instaapp.base.ViewModelProviderFactory;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jiveksha on 8/18/18.
 */
@Module
public class HomeActivityModule {

    @Provides
    @Named("HomeActivity")
    ViewModelProvider.Factory homeViewModelProvider(HomeViewModel homeViewModel) {
        return new ViewModelProviderFactory<>(homeViewModel);
    }

    @Provides
    HomeViewModel provideHomeViewModel(Context context, FirebaseAuth firebaseAuth, FirebaseDatabase firebaseDatabase, FirebaseStorage firebaseStorage) {
        return new HomeViewModel(context, firebaseAuth, firebaseDatabase, firebaseStorage);
    }

}
