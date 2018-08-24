package com.instaapp.share;

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
public class PhotoFragmentModule {


    @Provides
    @Named("PhotoFragment")
    ViewModelProvider.Factory photoFragmentViewModelProvider(PhotoFragmentViewModel photoFragmentViewModel) {
        return new ViewModelProviderFactory<>(photoFragmentViewModel);
    }


    @Provides
    PhotoFragmentViewModel viewPhotoFragmentViewModel(Context context, FirebaseAuth firebaseAuth, FirebaseDatabase firebaseDatabase, FirebaseStorage firebaseStorage) {
        return new PhotoFragmentViewModel(context, firebaseAuth, firebaseDatabase, firebaseStorage);
    }

}
