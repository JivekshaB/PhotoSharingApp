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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.instaapp.BR;
import com.instaapp.R;
import com.instaapp.adapter.GridImageAdapter;
import com.instaapp.base.BaseFragment;
import com.instaapp.databinding.FragmentViewProfileBinding;
import com.instaapp.models.Photo;
import com.instaapp.models.User;
import com.instaapp.models.UserAccountSettings;
import com.instaapp.models.UserSettings;
import com.instaapp.utils.BottomNavigationViewHelper;
import com.instaapp.utils.UniversalImageLoader;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by User on 6/29/2017.
 */

public class ViewProfileFragment extends BaseFragment<FragmentViewProfileBinding, ViewProfileFragmentViewModel> implements ViewProfileFragmentNavigator {

    private static final String TAG = ViewProfileFragment.class.getSimpleName();

    private static final int ACTIVITY_NUM = 3;
    private static final int NUM_GRID_COLUMNS = 3;


    //widgets
    private TextView mPosts, mFollowers, mFollowing, mDisplayName, mUsername, mWebsite, mDescription,
            mFollow, mUnfollow;
    private CircleImageView mProfilePhoto;
    private GridView gridView;
    private BottomNavigationViewEx bottomNavigationView;
    private TextView editProfile;


    //vars
    private User mUser;
    //widgets
    private ImageView mBackArrow;


    @Inject
    @Named("ViewProfileFragment")
    ViewModelProvider.Factory mViewModelFactory;

    private FragmentViewProfileBinding mFragmentViewProfileBinding;

    private ViewProfileFragmentViewModel mViewProfileFragmentViewModel;

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_view_profile;
    }

    @Override
    public ViewProfileFragmentViewModel getViewModel() {
        mViewProfileFragmentViewModel = ViewModelProviders.of(this, mViewModelFactory).get(ViewProfileFragmentViewModel.class);
        return mViewProfileFragmentViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewProfileFragmentViewModel.setNavigator(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentViewProfileBinding = getViewDataBinding();
        setUp();
    }

    private void setUp() {
        mDisplayName = getView().findViewById(R.id.display_name);
        mUsername = getView().findViewById(R.id.username);
        mWebsite = getView().findViewById(R.id.website);
        mDescription = getView().findViewById(R.id.description);
        mProfilePhoto = getView().findViewById(R.id.profile_photo);
        mPosts = getView().findViewById(R.id.tvPosts);
        mFollowers = getView().findViewById(R.id.tvFollowers);
        mFollowing = getView().findViewById(R.id.tvFollowing);
        gridView = getView().findViewById(R.id.gridView);
        bottomNavigationView = getView().findViewById(R.id.bottomNavViewBar);
        mFollow = getView().findViewById(R.id.follow);
        mUnfollow = getView().findViewById(R.id.unfollow);
        editProfile = getView().findViewById(R.id.textEditProfile);
        mBackArrow = getView().findViewById(R.id.backArrow);


        try {
            mUser = getUserFromBundle();
            mViewProfileFragmentViewModel.profileSetup(mUser);
            setupBottomNavigationView();
            mViewProfileFragmentViewModel.isFollowing(mUser);
            mViewProfileFragmentViewModel.getFollowingCount(mUser);
            mViewProfileFragmentViewModel.getFollowersCount(mUser);
            mViewProfileFragmentViewModel.getPostsCount(mUser);

        } catch (NullPointerException e) {
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage());
            Toast.makeText(getContext(), "something went wrong", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().popBackStack();
        }


        mFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: now following: " + mUser.getUsername());
                mViewProfileFragmentViewModel.onClickFollow(mUser);

            }
        });


        mUnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: now unfollowing: " + mUser.getUsername());
                mViewProfileFragmentViewModel.onClickUnfollow(mUser);
            }
        });


        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to " + getString(R.string.edit_profile_fragment));
                Intent intent = new Intent(getActivity().getApplicationContext(), AccountSettingsActivity.class);
                intent.putExtra(getString(R.string.calling_activity), getString(R.string.profile_activity));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


    }

    @Override
    public void setFollowing() {
        Log.d(TAG, "setFollowing: updating UI for following this user");
        mFollow.setVisibility(View.GONE);
        mUnfollow.setVisibility(View.VISIBLE);
        editProfile.setVisibility(View.GONE);
    }

    @Override
    public void setUnfollowing() {
        Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
        mFollow.setVisibility(View.VISIBLE);
        mUnfollow.setVisibility(View.GONE);
        editProfile.setVisibility(View.GONE);
    }

    @Override
    public void setupImageGrid(final ArrayList<Photo> photos) {
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        ArrayList<String> imgUrls = new ArrayList<String>();
        for (int i = 0; i < photos.size(); i++) {
            imgUrls.add(photos.get(i).getImage_path());
        }
        GridImageAdapter adapter = new GridImageAdapter(getContext(), R.layout.layout_grid_imageview,
                "", imgUrls);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mOnGridImageSelectedListener.onGridImageSelected(photos.get(position), ACTIVITY_NUM);
            }
        });
    }

    private User getUserFromBundle() {
        Log.d(TAG, "getUserFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getParcelable(getString(R.string.intent_user));
        } else {
            return null;
        }
    }

    @Override
    public void onAttach(Context context) {
        try {
            mOnGridImageSelectedListener = (OnGridImageSelectedListener) (getContext());
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
        super.onAttach(context);
    }

    @Override
    public void setProfileWidgets(UserSettings userSettings) {
        UserAccountSettings settings = userSettings.getSettings();

        UniversalImageLoader.setImage(mViewProfileFragmentViewModel.getImageLoader(), settings.getProfile_photo(), mProfilePhoto, null, "");

        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mWebsite.setText(settings.getWebsite());
        mDescription.setText(settings.getDescription());
        mPosts.setText(String.valueOf(settings.getPosts()));
        mFollowing.setText(String.valueOf(settings.getFollowing()));
        mFollowers.setText(String.valueOf(settings.getFollowers()));
        mFragmentViewProfileBinding.profileProgressBar.setVisibility(View.GONE);

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back");
                getActivity().getSupportFragmentManager().popBackStack();
                getActivity().finish();
            }
        });
    }

    @Override
    public void setFollowersCount(int count) {
        mFollowers.setText(String.valueOf(count));
    }

    @Override
    public void setFollowingCount(int count) {
        mFollowing.setText(String.valueOf(count));
    }

    @Override
    public void setPostsCount(int count) {
        mPosts.setText(String.valueOf(count));
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(getContext(), getActivity(), bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    @Override
    public String getStringValue(int id) {
        return getString(id);
    }

    public interface OnGridImageSelectedListener {
        void onGridImageSelected(Photo photo, int activityNumber);
    }

    OnGridImageSelectedListener mOnGridImageSelectedListener;


}
