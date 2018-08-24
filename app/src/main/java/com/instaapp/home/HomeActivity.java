package com.instaapp.home;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.instaapp.BR;
import com.instaapp.R;
import com.instaapp.adapter.MainFeedListAdapter;
import com.instaapp.base.BaseActivity;
import com.instaapp.databinding.ActivityHomeBinding;
import com.instaapp.login.LoginActivity;
import com.instaapp.models.Photo;
import com.instaapp.profile.ViewCommentsFragment;
import com.instaapp.utils.BottomNavigationViewHelper;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import javax.inject.Inject;
import javax.inject.Named;

public class HomeActivity extends BaseActivity<ActivityHomeBinding, HomeViewModel> implements HomeNavigator,
        MainFeedListAdapter.OnLoadMoreItemsListener {


    private static final String TAG = HomeActivity.class.getSimpleName();

    private static final int ACTIVITY_NUM = 0;
    private static final int HOME_FRAGMENT = 1;


    @Inject
    @Named("HomeActivity")
    ViewModelProvider.Factory mViewModelFactory;

    private ActivityHomeBinding mActivityMainBinding;

    private HomeViewModel mHomeViewModel;

    @Override
    public void onLoadMoreItems() {
        Log.d(TAG, "onLoadMoreItems: displaying more photos");
        HomeFragment fragment = (HomeFragment) getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.viewpager_container + ":" + mActivityMainBinding.layoutCenterViewPager.viewpagerContainer.getCurrentItem());
        if (fragment != null) {
            fragment.displayMorePhotos();
        }
    }


    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    public HomeViewModel getViewModel() {
        mHomeViewModel = ViewModelProviders.of(this, mViewModelFactory).get(HomeViewModel.class);
        return mHomeViewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: starting.");
        setUp();

    }

    private void setUp() {
        mActivityMainBinding = getViewDataBinding();
        mHomeViewModel.setNavigator(this);
        setupBottomNavigationView();
        setupViewPager();
    }

    public void onCommentThreadSelected(Photo photo, String callingActivity) {
        Log.d(TAG, "onCommentThreadSelected: selected a coemment thread");

        ViewCommentsFragment fragment = new ViewCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        args.putString(callingActivity, getString(R.string.home_activity));
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();

    }

    public void hideLayout() {
        Log.d(TAG, "hideLayout: hiding layout");
        mActivityMainBinding.relLayoutParent.setVisibility(View.GONE);
        mActivityMainBinding.container.setVisibility(View.VISIBLE);
    }


    public void showLayout() {
        Log.d(TAG, "hideLayout: showing layout");
        mActivityMainBinding.relLayoutParent.setVisibility(View.VISIBLE);
        mActivityMainBinding.container.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mActivityMainBinding.container.getVisibility() == View.VISIBLE) {
            showLayout();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: incoming result.");
    }

    /**
     * Responsible for adding the 3 tabs: Camera, Home, Messages
     */
    private void setupViewPager() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        //  adapter.addFragment(new PhotoFragment()); //index 0
        adapter.addFragment(new HomeFragment()); //index 1
        // adapter.addFragment(new MessagesFragment()); //index 2
        mActivityMainBinding.layoutCenterViewPager.viewpagerContainer.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager( mActivityMainBinding.layoutCenterViewPager.viewpagerContainer);

        //   tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_instagram_black);
        // tabLayout.getTabAt(2).setIcon(R.drawable.ic_arrow);
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(this, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


    /**
     * Redirect to Login Activity
     */
    @Override
    public void redirectToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


    /**
     * on Activity Start
     */
    @Override
    public void onStart() {
        super.onStart();
        mHomeViewModel.startFireBaseAuth();
        mActivityMainBinding.layoutCenterViewPager.viewpagerContainer.setCurrentItem(HOME_FRAGMENT);
        mHomeViewModel.checkCurrentUser(mHomeViewModel.getFirebaseUser());
    }

    /**
     * on Activity Stop
     */
    @Override
    public void onStop() {
        super.onStop();
        mHomeViewModel.stopFireBaseAuth();
    }


}
