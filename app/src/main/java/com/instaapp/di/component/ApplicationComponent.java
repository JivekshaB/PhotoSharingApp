package com.instaapp.di.component;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.instaapp.InstaApplication;
import com.instaapp.di.annotation.ApplicationContext;
import com.instaapp.di.module.ApplicationModule;
import com.instaapp.utils.FirebaseMethods;
import com.instaapp.utils.UniversalImageLoader;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by jiveksha on 8/13/18.
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(InstaApplication instaApplication);

    @ApplicationContext
    Context getContext();

    UniversalImageLoader getUniversalImageLoader();

    FirebaseAuth getFirebaseAuth();

    FirebaseAuth.AuthStateListener getAuthStateListener();

    FirebaseDatabase getFirebaseDatabase();

    FirebaseMethods getFirebaseMethods();

}
