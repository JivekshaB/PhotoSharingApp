package com.instaapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.instaapp.di.component.ActivityComponent;
import com.instaapp.di.component.ApplicationComponent;
import com.instaapp.di.component.DaggerFragmentComponent;
import com.instaapp.di.component.FragmentComponent;
import com.instaapp.di.module.FragmentModule;

/**
 * Created by jiveksha on 8/14/18.
 */

public class BaseFragment extends Fragment {

    private FragmentComponent mFragmentComponent;

    public FragmentComponent getFragmentComponent() {
        if (mFragmentComponent == null) {
            mFragmentComponent = DaggerFragmentComponent.builder()
                    .fragmentModule(new FragmentModule(this))
                    .activityComponent(((BaseActivity) getActivity()).getActivityComponent())
                    .build();


        }
        return mFragmentComponent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentComponent().inject(this);
    }


    /**
     * Convenience method to return a Dagger component.
     */
    protected
    @NonNull
    ActivityComponent getActivityComponent() {
        return ((BaseActivity) getActivity()).getActivityComponent();
    }

    /**
     * Returns the {@link InstaApplication} instance.
     */
    protected
    @NonNull
    InstaApplication application() {
        return (InstaApplication) getActivity().getApplication();
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
     * Returns the {@link FragmentComponent} instance.
     */
    protected
    @NonNull
    Context getFragmentContext() {
        return mFragmentComponent.getContext();
    }


}
