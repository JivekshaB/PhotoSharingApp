package com.instaapp.share;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.instaapp.base.ViewModelProviderFactory;
import com.instaapp.profile.ViewProfileFragmentViewModel;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jiveksha on 8/20/18.
 */
@Module
public class NextActivityModule {


    @Provides
    @Named("NextActivity")
    ViewModelProvider.Factory nextActivityViewModelProvider(NextActivityViewModel nextActivityViewModel) {
        return new ViewModelProviderFactory<>(nextActivityViewModel);
    }


    @Provides
    NextActivityViewModel viewNextActivityViewModel(Context context, FirebaseAuth firebaseAuth, FirebaseDatabase firebaseDatabase, FirebaseStorage firebaseStorage) {
        return new NextActivityViewModel(context, firebaseAuth, firebaseDatabase, firebaseStorage);
    }

}
