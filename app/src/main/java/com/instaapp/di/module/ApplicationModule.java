package com.instaapp.di.module;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.instaapp.di.annotation.ApplicationContext;
import com.instaapp.utils.UniversalImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jiveksha on 8/14/18.
 */

@Module
public class ApplicationModule {

    private final Application mApplication;

    private FirebaseAuth mFirebaseAuth;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private UniversalImageLoader mImageLoader;


    public ApplicationModule(Application app) {
        mApplication = app;
    }

    /**
     * Provides application level context
     *
     * @return {@link ApplicationContext}
     */
    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication.getApplicationContext();
    }

    /**
     * provides application instance
     *
     * @return {@link com.instaapp.InstaApplication}
     */
    @Provides
    Application provideApplication() {
        return mApplication;
    }

    /**
     * Provide firebase auth instance for all auth related flows
     *
     * @return {@link FirebaseAuth}
     */
    @Provides
    FirebaseAuth providesFriebaseAuth() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        return mFirebaseAuth;
    }

    /**
     * Provide firebase auth state listener for all auth related flows
     *
     * @return {@link FirebaseAuth.AuthStateListener}
     */
    @Provides
    FirebaseAuth.AuthStateListener providesAuthStateListener() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };
        return mAuthStateListener;
    }

    /**
     * Provides universal image loader for all image loading and caching
     *
     * @return {@link ImageLoader}
     */
    @Provides
    UniversalImageLoader providesUniverImageLoader() {
        mImageLoader = new UniversalImageLoader(provideContext());
        ImageLoader.getInstance().init(mImageLoader.getConfig());
        return mImageLoader;
    }

}
