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
public class ProfileFragmentModule {


    @Provides
    @Named("ProfileFragment")
    ViewModelProvider.Factory profileFragmentViewModelProvider(ProfileFragmentViewModel profileFragmentViewModel) {
        return new ViewModelProviderFactory<>(profileFragmentViewModel);
    }

    @Provides
    ProfileFragmentViewModel profileFragmentViewModel(Context context, FirebaseAuth firebaseAuth, FirebaseDatabase firebaseDatabase, FirebaseStorage firebaseStorage) {
        return new ProfileFragmentViewModel(context, firebaseAuth, firebaseDatabase, firebaseStorage);
    }

}
