package com.instaapp.login;

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
public class RegistrationActivityModule {

    @Provides
    @Named("RegisterActivity")
    ViewModelProvider.Factory registerViewModelProvider(RegistrationViewModel registrationViewModel) {
        return new ViewModelProviderFactory<>(registrationViewModel);
    }

    @Provides
    RegistrationViewModel provideRegisterViewModel(Context context, FirebaseAuth firebaseAuth, FirebaseDatabase firebaseDatabase, FirebaseStorage firebaseStorage) {
        return new RegistrationViewModel(context, firebaseAuth, firebaseDatabase, firebaseStorage);
    }

}
