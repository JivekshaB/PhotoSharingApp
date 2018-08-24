package com.instaapp.profile;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.instaapp.BR;
import com.instaapp.R;
import com.instaapp.adapter.GridImageAdapter;
import com.instaapp.base.BaseFragment;
import com.instaapp.databinding.FragmentProfileBinding;
import com.instaapp.models.Photo;
import com.instaapp.models.UserAccountSettings;
import com.instaapp.models.UserSettings;
import com.instaapp.utils.BottomNavigationViewHelper;
import com.instaapp.utils.UniversalImageLoader;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 */

public class ProfileFragment extends BaseFragment<FragmentProfileBinding, ProfileFragmentViewModel> implements ProfileFragmentNavigator {

    private static final String TAG = ProfileFragment.class.getSimpleName();


    public interface OnGridImageSelectedListener {
        void onGridImageSelected(Photo photo, int activityNumber);
    }

    OnGridImageSelectedListener mOnGridImageSelectedListener;

    private static final int ACTIVITY_NUM = 3;
    private static final int NUM_GRID_COLUMNS = 3;


    @Inject
    @Named("ProfileFragment")
    ViewModelProvider.Factory mViewModelFactory;

    private FragmentProfileBinding mFragmentProfileBinding;

    private ProfileFragmentViewModel mProfileFragmentViewModel;


    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_profile;
    }

    @Override
    public ProfileFragmentViewModel getViewModel() {
        mProfileFragmentViewModel = ViewModelProviders.of(this, mViewModelFactory).get(ProfileFragmentViewModel.class);
        return mProfileFragmentViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProfileFragmentViewModel.setNavigator(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentProfileBinding = getViewDataBinding();
        setUp();
    }

    private void setUp() {

        setupBottomNavigationView();
        setupToolbar();

        mProfileFragmentViewModel.getUserSettings();
        mProfileFragmentViewModel.getFollowersCount();
        mProfileFragmentViewModel.getFollowingCount();
        mProfileFragmentViewModel.getPostsCount();
        mProfileFragmentViewModel.getPhotosForGrid();

        mFragmentProfileBinding.layoutCenterProfile.layoutTopProfile.textEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: navigating to " + getString(R.string.edit_profile_fragment));
                Intent intent = new Intent(getContext(), AccountSettingsActivity.class);
                intent.putExtra(getString(R.string.calling_activity), getString(R.string.profile_activity));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        try {
            mOnGridImageSelectedListener = (OnGridImageSelectedListener) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
        super.onAttach(context);
    }

    @Override
    public void setupImageGrid(final ArrayList<Photo> photos) {
        Log.d(TAG, "setupGridView: Setting up image grid.");
        if (isAdded()) {
            //setup our image grid
            int gridWidth = getResources().getDisplayMetrics().widthPixels;
            int imageWidth = gridWidth / NUM_GRID_COLUMNS;
            mFragmentProfileBinding.layoutCenterProfile.imageGridView.setColumnWidth(imageWidth);

            ArrayList<String> imgUrls = new ArrayList<>();
            for (int i = 0; i < photos.size(); i++) {
                imgUrls.add(photos.get(i).getImage_path());
            }
            GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview,
                    "", imgUrls);
            mFragmentProfileBinding.layoutCenterProfile.imageGridView.setAdapter(adapter);

            mFragmentProfileBinding.layoutCenterProfile.imageGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mOnGridImageSelectedListener.onGridImageSelected(photos.get(position), ACTIVITY_NUM);
                }
            });

        }
    }

    @Override
    public void setProfileWidgets(UserSettings userSettings) {
        UserAccountSettings settings = userSettings.getSettings();
        UniversalImageLoader.setImage(mProfileFragmentViewModel.getImageLoader(), settings.getProfile_photo(), mFragmentProfileBinding.layoutCenterProfile.profilePhoto, null, "");
        mFragmentProfileBinding.layoutCenterProfile.displayName.setText(settings.getDisplay_name());
        mFragmentProfileBinding.layoutTopProfileBar.username.setText(settings.getUsername());
        mFragmentProfileBinding.layoutCenterProfile.website.setText(settings.getWebsite());
        mFragmentProfileBinding.layoutCenterProfile.description.setText(settings.getDescription());
        mFragmentProfileBinding.profileProgressBar.setVisibility(View.GONE);
    }


    /**
     * Responsible for
     * setting up
     * the profile
     * toolbar
     */

    private void setupToolbar() {

        getBaseActivity().setSupportActionBar(mFragmentProfileBinding.layoutTopProfileBar.profileToolBar);

        mFragmentProfileBinding.layoutTopProfileBar.profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to account settings.");
                Intent intent = new Intent(getBaseActivity(), AccountSettingsActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }


    @Override
    public String getStringValue(int id) {
        return getString(id);
    }

    @Override
    public void setFollowersCount(int count) {
        mFragmentProfileBinding.layoutCenterProfile.layoutTopProfile.textFollowers.setText(String.valueOf(count));
    }

    @Override
    public void setFollowingCount(int count) {
        mFragmentProfileBinding.layoutCenterProfile.layoutTopProfile.textFollowing.setText(String.valueOf(count));

    }

    @Override
    public void setPostsCount(int count) {
        mFragmentProfileBinding.layoutCenterProfile.layoutTopProfile.textPosts.setText(String.valueOf(count));

    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(mFragmentProfileBinding.layoutBottomNav.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(getContext(), getActivity(), mFragmentProfileBinding.layoutBottomNav.bottomNavViewBar);
        Menu menu = mFragmentProfileBinding.layoutBottomNav.bottomNavViewBar.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


}