package com.instaapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.instaapp.di.component.ActivityComponent;
import com.instaapp.di.component.ApplicationComponent;
import com.instaapp.di.component.DaggerActivityComponent;
import com.instaapp.di.module.ActivityModule;
import com.instaapp.utils.FirebaseMethods;

/**
 * Created by jiveksha on 8/14/18.
 */

public class BaseActivity extends AppCompatActivity {

    private ActivityComponent mActivityComponent;

    @NonNull
    public ActivityComponent getActivityComponent() {
        if (mActivityComponent == null) {
            mActivityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(this))
                    .applicationComponent(InstaApplication.get(this).getComponent())
                    .build();
        }
        return mActivityComponent;
    }

    /**
     * Returns the {@link InstaApplication} instance.
     */
    protected
    @NonNull
    InstaApplication application() {
        return (InstaApplication) getApplication();
    }

    /**
     * Convenience method to return a Dagger component.
     */
    protected
    @NonNull
    ApplicationComponent getApplicationComponent() {
        return application().getComponent();
    }

    /**
     * Returns the {@link ActivityComponent} instance.
     */
    protected
    @NonNull
    Context getActivityContext() {
        return mActivityComponent.getContext();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
    }

    @NonNull
    protected FirebaseAuth getFireBaseAuth() {
        return getApplicationComponent().getFirebaseAuth();
    }

    @NonNull
    protected FirebaseAuth.AuthStateListener getFireBaseAuthListener() {
        return getApplicationComponent().getAuthStateListener();
    }

    @NonNull
    protected FirebaseDatabase getFirebaseDatabase() {
        return getApplicationComponent().getFirebaseDatabase();
    }

    @NonNull
    protected FirebaseMethods getFirebaseMethods() {
        return getApplicationComponent().getFirebaseMethods();
    }


}
