package com.instaapp.search;

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
public class SearchActivityModule {


    @Provides
    @Named("SearchActivity")
    ViewModelProvider.Factory searchActivityViewModelProvider(SearchActivityViewModel searchActivityViewModel) {
        return new ViewModelProviderFactory<>(searchActivityViewModel);
    }

    @Provides
    SearchActivityViewModel viewSearchActivityViewModel(Context context, FirebaseAuth firebaseAuth, FirebaseDatabase firebaseDatabase, FirebaseStorage firebaseStorage) {
        return new SearchActivityViewModel(context, firebaseAuth, firebaseDatabase, firebaseStorage);
    }

}
