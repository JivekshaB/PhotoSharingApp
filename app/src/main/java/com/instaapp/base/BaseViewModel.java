package com.instaapp.base;/*
 *  Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://mindorks.com/license/apache-v2
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.instaapp.R;
import com.instaapp.utils.FirebaseMethods;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.lang.ref.WeakReference;

/**
 * Created by amitshekhar on 07/07/17.
 */

public abstract class BaseViewModel<N> extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final FirebaseAuth mFirebaseAuth;

    private final FirebaseDatabase mFirebaseDatabase;

    private final FirebaseStorage mFirebaseStorage;

    private FirebaseMethods mFirebaseMethods;

    private ImageLoader mImageLoader;

    private WeakReference<N> mNavigator;

    /**
     * Init View Model to provide FirebasAuth, FirebaseDatabase, FirebaseStorage, FirebaseMethods, ImageLoader
     *
     * @param context          :{@link Context}
     * @param firebaseAuth     :{@link FirebaseAuth}
     * @param firebaseDatabase :{@link FirebaseDatabase}
     * @param firebaseStorage  :{@link FirebaseStorage}
     */
    public BaseViewModel(Context context, FirebaseAuth firebaseAuth
            , FirebaseDatabase firebaseDatabase, FirebaseStorage firebaseStorage) {
        this.mFirebaseAuth = firebaseAuth;
        this.mFirebaseDatabase = firebaseDatabase;
        this.mFirebaseStorage = firebaseStorage;
        setFirebaseMethods(context);
        setImageLoader(context);
    }

    /**
     * Getter method for FirebaseAuth
     *
     * @return
     */
    protected FirebaseAuth getFirebaseAuth() {
        return mFirebaseAuth;
    }

    /**
     * Getter method for FirebaseDatabase
     *
     * @return
     */
    protected FirebaseDatabase getFirebaseDatabase() {
        return mFirebaseDatabase;
    }

    /**
     * Setter method for FirebaseMethod()
     */
    private void setFirebaseMethods(Context context) {
        mFirebaseMethods = new FirebaseMethods(context, mFirebaseAuth, mFirebaseDatabase, mFirebaseStorage.getReference());
    }

    /**
     * Getter method for FirebaseMethod()
     *
     * @return :{@link FirebaseMethods}
     */
    public FirebaseMethods getFirebaseMethods() {
        return mFirebaseMethods;
    }

    /**
     * Setter method for Imageloader with all configurations
     */
    public void setImageLoader(Context context) {
        mImageLoader = ImageLoader.getInstance();
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

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(100 * 1024 * 1024).build();

        mImageLoader.init(config);
    }

    /**
     * Getter method for Imageloader with all configurations
     *
     * @return :{@link ImageLoader}
     */
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    /**
     * This method will be called when this ViewModel is no longer used and will be destroyed.
     * <p>
     * It is useful when ViewModel observes some data and you need to clear this subscription to
     * prevent a leak of this ViewModel.
     */
    @Override
    protected void onCleared() {
        super.onCleared();
    }

    /**
     * Get the navigator weak reference
     *
     * @return navigator
     */
    public N getNavigator() {
        return mNavigator.get();
    }

    /**
     * Set the navigator weak reference
     */
    public void setNavigator(N navigator) {
        this.mNavigator = new WeakReference<>(navigator);
    }


}
