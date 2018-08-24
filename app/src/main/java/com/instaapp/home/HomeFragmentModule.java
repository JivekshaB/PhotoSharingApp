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
 * Created by jiveksha on 8/20/18.
 */
@Module
public class HomeFragmentModule {

    @Provides
    @Named("HomeFragment")
    ViewModelProvider.Factory homeFragmentViewModelProvider(HomeFragmentViewModel homeFragmentViewModel) {
        return new ViewModelProviderFactory<>(homeFragmentViewModel);
    }


    @Provides
    HomeFragmentViewModel homeFragmentViewModel(Context context, FirebaseAuth firebaseAuth, FirebaseDatabase firebaseDatabase, FirebaseStorage firebaseStorage) {
        return new HomeFragmentViewModel(context, firebaseAuth, firebaseDatabase, firebaseStorage);
    }

}
