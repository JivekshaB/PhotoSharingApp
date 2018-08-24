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
public class ViewPostFragmentModule {


    @Provides
    @Named("ViewPostFragment")
    ViewModelProvider.Factory viewPostFragmentViewModelProvider(ViewPostFragmentViewModel viewPostFragmentViewModel) {
        return new ViewModelProviderFactory<>(viewPostFragmentViewModel);
    }

    @Provides
    ViewPostFragmentViewModel viewPostFragmentViewModel(Context context, FirebaseAuth firebaseAuth, FirebaseDatabase firebaseDatabase, FirebaseStorage firebaseStorage) {
        return new ViewPostFragmentViewModel(context, firebaseAuth, firebaseDatabase, firebaseStorage);
    }

}
