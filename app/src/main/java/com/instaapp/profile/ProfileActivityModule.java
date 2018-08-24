package com.instaapp.profile;

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
public class ProfileActivityModule {

    @Provides
    @Named("ProfileActivity")
    ViewModelProvider.Factory profileActivityViewModelProvider(ProfileActivityViewModel profileActivityViewModel) {
        return new ViewModelProviderFactory<>(profileActivityViewModel);
    }

    @Provides
    ProfileActivityViewModel provideProfileActivityViewModel(Context context, FirebaseAuth firebaseAuth, FirebaseDatabase firebaseDatabase, FirebaseStorage firebaseStorage) {
        return new ProfileActivityViewModel(context, firebaseAuth, firebaseDatabase, firebaseStorage);
    }

}
