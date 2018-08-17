package com.instaapp.di.module;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.instaapp.R;
import com.instaapp.di.annotation.ApplicationContext;
import com.instaapp.utils.FirebaseMethods;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jiveksha on 8/14/18.
 */

@Module
public class ApplicationModule {

    private final Application mApplication;

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
    FirebaseAuth providesFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    /**
     * Provide firebase auth state listener for all auth related flows
     *
     * @return {@link FirebaseAuth.AuthStateListener}
     */
    @Provides
    FirebaseAuth.AuthStateListener providesAuthStateListener() {
        return new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };
    }

    /**
     * Provides universal image loader for all image loading and caching
     *
     * @return {@link ImageLoader}
     */
    @Provides
    ImageLoader providesUniversalImageLoader() {
        ImageLoader imageLoader = ImageLoader.getInstance();
        final int defaultImage = R.drawable.ic_android;
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultImage)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .showImageOnLoading(defaultImage)
                .cacheOnDisk(true).cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300))
                .considerExifParams(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(provideContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(100 * 1024 * 1024).build();

        imageLoader.init(config);

        return imageLoader;
    }

    /**
     * Provides FirebaseMethods class for firebase related calls
     *
     * @return {@link FirebaseMethods}
     */
    @Provides
    FirebaseMethods providesFirebaseMethods() {
        return new FirebaseMethods(provideContext());
    }


    /**
     * Provides firebase database
     *
     * @return {@link com.google.firebase.database.FirebaseDatabase}
     */
    @Provides
    FirebaseDatabase providesFirebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }

}
