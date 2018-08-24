package com.instaapp.profile;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.instaapp.BR;
import com.instaapp.R;
import com.instaapp.base.BaseFragment;
import com.instaapp.databinding.FragmentViewPostBinding;
import com.instaapp.models.Photo;
import com.instaapp.models.UserAccountSettings;
import com.instaapp.utils.BottomNavigationViewHelper;
import com.instaapp.utils.LikeToggle;
import com.instaapp.utils.UniversalImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.inject.Named;


/**
 * Created by User on 8/12/2017.
 */

public class ViewPostFragment extends BaseFragment<FragmentViewPostBinding, ViewPostFragmentViewModel> implements ViewPostFragmentNavigator {

    private static final String TAG = "ViewPostFragment";

    OnCommentThreadSelectedListener mOnCommentThreadSelectedListener;


    @Inject
    @Named("ViewPostFragment")
    ViewModelProvider.Factory mViewModelFactory;

    private FragmentViewPostBinding mFragmentViewPostBinding;

    private ViewPostFragmentViewModel mViewPostFragmentViewModel;

    private LikeToggle mHeart;

    private GestureDetector mGestureDetector;


    public ViewPostFragment() {
        super();
        setArguments(new Bundle());
    }

    public interface OnCommentThreadSelectedListener {
        void onCommentThreadSelectedListener(Photo photo);
    }


    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_view_post;
    }

    @Override
    public ViewPostFragmentViewModel getViewModel() {
        mViewPostFragmentViewModel = ViewModelProviders.of(this, mViewModelFactory).get(ViewPostFragmentViewModel.class);
        return mViewPostFragmentViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewPostFragmentViewModel.setNavigator(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentViewPostBinding = getViewDataBinding();
        setUp();
    }

    private void setUp() {
        mHeart = new LikeToggle(mFragmentViewPostBinding.layoutViewPost.imageHeart, mFragmentViewPostBinding.layoutViewPost.imageHeartRed);
        mGestureDetector = new GestureDetector(getContext(), new GestureListener());
        setupBottomNavigationView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAdded()) {
            UniversalImageLoader.setImage(mViewPostFragmentViewModel.getImageLoader(), getPhotoFromBundle().getImage_path(), mFragmentViewPostBinding.layoutViewPost.postImage, null, "");
            mViewPostFragmentViewModel.init();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnCommentThreadSelectedListener = (OnCommentThreadSelectedListener) this;
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }


    public class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "onDoubleTap: double tap detected.");
            mViewPostFragmentViewModel.doubleTapClick();
            return true;
        }
    }

    @Override
    public void setupWidgets(final Photo photo, UserAccountSettings userAccountSettings) {
        String timestampDiff = getTimestampDifference(photo);
        if (!timestampDiff.equals("0")) {
            mFragmentViewPostBinding.layoutViewPost.imageTimePosted.setText(timestampDiff + " DAYS AGO");
        } else {
            mFragmentViewPostBinding.layoutViewPost.imageTimePosted.setText("TODAY");
        }
        UniversalImageLoader.setImage(mViewPostFragmentViewModel.getImageLoader(), userAccountSettings.getProfile_photo(), mFragmentViewPostBinding.layoutViewPost.profilePhoto, null, "");
        mFragmentViewPostBinding.layoutViewPost.username.setText(userAccountSettings.getUsername());
        mFragmentViewPostBinding.layoutViewPost.imageLikes.setText(mViewPostFragmentViewModel.getLikesStringValue());
        mFragmentViewPostBinding.layoutViewPost.imageCaption.setText(photo.getCaption());

        if (photo.getComments().size() > 0) {
            mFragmentViewPostBinding.layoutViewPost.imageCommentsLink.setText("View all " + photo.getComments().size() + " comments");
        } else {
            mFragmentViewPostBinding.layoutViewPost.imageCommentsLink.setText("");
        }

        mFragmentViewPostBinding.layoutViewPost.imageCommentsLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to comments thread");

                mOnCommentThreadSelectedListener.onCommentThreadSelectedListener(photo);

            }
        });

        mFragmentViewPostBinding.layoutPostToolbar.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back");
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        mFragmentViewPostBinding.layoutViewPost.imageCommentsLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back");
                mOnCommentThreadSelectedListener.onCommentThreadSelectedListener(photo);

            }
        });

        if (mViewPostFragmentViewModel.isLikedByCurrentUser()) {
            mFragmentViewPostBinding.layoutViewPost.imageHeart.setVisibility(View.GONE);
            mFragmentViewPostBinding.layoutViewPost.imageHeartRed.setVisibility(View.VISIBLE);
            mFragmentViewPostBinding.layoutViewPost.imageHeartRed.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: red heart touch detected.");
                    return mGestureDetector.onTouchEvent(event);
                }
            });
        } else {
            mFragmentViewPostBinding.layoutViewPost.imageHeart.setVisibility(View.VISIBLE);
            mFragmentViewPostBinding.layoutViewPost.imageHeartRed.setVisibility(View.GONE);
            mFragmentViewPostBinding.layoutViewPost.imageHeart.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: white heart touch detected.");
                    return mGestureDetector.onTouchEvent(event);
                }
            });
        }
    }


    private String getTimestampDifference(Photo mPhoto) {
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));//google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = mPhoto.getDate_created();
        try {
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24)));
        } catch (ParseException e) {
            Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage());
            difference = "0";
        }
        return difference;
    }

    @Override
    public int getActivityNumFromBundle() {
        Log.d(TAG, "getActivityNumFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getInt(getString(R.string.activity_number));
        } else {
            return 0;
        }
    }

    @Override
    public Photo getPhotoFromBundle() {
        Log.d(TAG, "getPhotoFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getParcelable(getString(R.string.photo));
        } else {
            return null;
        }
    }

    @Override
    public void toggleHeartLike() {
        mHeart.toggleLike();
    }

    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(mFragmentViewPostBinding.layoutViewPost.postBottomNavView.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(getContext(), getActivity(), mFragmentViewPostBinding.layoutViewPost.postBottomNavView.bottomNavViewBar);
        Menu menu = mFragmentViewPostBinding.layoutViewPost.postBottomNavView.bottomNavViewBar.getMenu();
        MenuItem menuItem = menu.getItem(getActivityNumFromBundle());
        menuItem.setChecked(true);
    }

    @Override
    public String getStringValue(int id) {
        return getString(id);
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}




















