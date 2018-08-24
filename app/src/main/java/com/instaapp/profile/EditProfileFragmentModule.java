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
public class EditProfileFragmentModule {

    @Provides
    @Named("EditProfileFragment")
    ViewModelProvider.Factory editProfileViewModelProvider(EditProfileFragmentViewModel editProfileFragmentViewModel) {
        return new ViewModelProviderFactory<>(editProfileFragmentViewModel);
    }


    @Provides
    EditProfileFragmentViewModel editProfileFragmentViewModel(Context context, FirebaseAuth firebaseAuth, FirebaseDatabase firebaseDatabase, FirebaseStorage firebaseStorage) {
        return new EditProfileFragmentViewModel(context, firebaseAuth, firebaseDatabase, firebaseStorage);
    }

}
