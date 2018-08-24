package com.instaapp.share;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.instaapp.BR;
import com.instaapp.R;
import com.instaapp.base.BaseActivity;
import com.instaapp.databinding.ActivityNextBinding;
import com.instaapp.utils.UniversalImageLoader;

import javax.inject.Inject;
import javax.inject.Named;


/**
 * Created by User on 7/24/2017.
 */

public class NextActivity extends BaseActivity<ActivityNextBinding, NextActivityViewModel> implements NextActivityNavigator {

    private static final String TAG = NextActivity.class.getSimpleName();

    private String imgUrl;
    private Intent intent;

    @Inject
    @Named("NextActivity")
    ViewModelProvider.Factory mViewModelFactory;

    private ActivityNextBinding mActivityNextBinding;
    private NextActivityViewModel mNextActivityViewModel;

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_next;
    }

    @Override
    public NextActivityViewModel getViewModel() {
        mNextActivityViewModel = ViewModelProviders.of(this, mViewModelFactory).get(NextActivityViewModel.class);
        return mNextActivityViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: started AccountSettings");
        mNextActivityViewModel.setNavigator(this);
        setUp();
    }

    private void setUp() {
        mActivityNextBinding = getViewDataBinding();
        mNextActivityViewModel.init();
        mActivityNextBinding.topNextToolBar.ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the activity");
                finish();
            }
        });

        mActivityNextBinding.topNextToolBar.tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to the final share screen.");
                //upload the image to firebase
                Toast.makeText(NextActivity.this, "Attempting to upload new photo", Toast.LENGTH_SHORT).show();
                String caption = mActivityNextBinding.caption.getText().toString();

                if (intent.hasExtra(getString(R.string.selected_image))) {
                    imgUrl = intent.getStringExtra(getString(R.string.selected_image));
                    mNextActivityViewModel.getFirebaseMethods().uploadNewPhoto(NextActivity.this, getString(R.string.new_photo), caption, mNextActivityViewModel.getImageCount(), imgUrl, null);
                } else if (intent.hasExtra(getString(R.string.selected_bitmap))) {
                    imgUrl = intent.getStringExtra(getString(R.string.selected_bitmap));
                    mNextActivityViewModel.getFirebaseMethods().uploadNewPhoto(NextActivity.this, getString(R.string.new_photo), caption, mNextActivityViewModel.getImageCount(), imgUrl, null);
                }
                finish();
            }
        });

        setImage();
    }


    private void setImage() {
        intent = getIntent();
        String mAppend = "file:/";
        if (intent.hasExtra(getString(R.string.selected_image))) {
            imgUrl = intent.getStringExtra(getString(R.string.selected_image));
            Log.d(TAG, "setImage: got new image url: " + imgUrl);
            UniversalImageLoader.setImage(getImageLoader(), imgUrl, mActivityNextBinding.imageShare, null, mAppend);
        } else if (intent.hasExtra(getString(R.string.selected_bitmap))) {
            imgUrl = intent.getStringExtra(getString(R.string.selected_bitmap));
            Log.d(TAG, "setImage: got new bitmap");
            UniversalImageLoader.setImage(getImageLoader(), imgUrl, mActivityNextBinding.imageShare, null, mAppend);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mNextActivityViewModel.startFireBaseAuth();
    }

    @Override
    public void onStop() {
        super.onStop();
        mNextActivityViewModel.stopFireBaseAuth();

    }
}
