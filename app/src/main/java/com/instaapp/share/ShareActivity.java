package com.instaapp.share;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.instaapp.BR;
import com.instaapp.R;
import com.instaapp.adapter.SectionsPagerAdapter;
import com.instaapp.base.BaseActivity;
import com.instaapp.databinding.ActivityShareBinding;
import com.instaapp.utils.Permissions;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by jiveksha on 8/7/18.
 */

public class ShareActivity extends BaseActivity<ActivityShareBinding, ShareActivityViewModel> implements ShareActivityNavigator {
    private static final String TAG = ShareActivity.class.getSimpleName();

    //constants
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;

    @Inject
    @Named("ShareActivity")
    ViewModelProvider.Factory mViewModelFactory;

    private ActivityShareBinding mActivityShareBinding;
    private ShareActivityViewModel mShareActivityViewModel;

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_share;
    }

    @Override
    public ShareActivityViewModel getViewModel() {
        mShareActivityViewModel = ViewModelProviders.of(this, mViewModelFactory).get(ShareActivityViewModel.class);
        return mShareActivityViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: started AccountSettings");
        mShareActivityViewModel.setNavigator(this);
        setUp();
    }

    private void setUp() {
        mActivityShareBinding = getViewDataBinding();
        if (checkPermissionsArray(Permissions.PERMISSIONS)) {
            setupViewPager();
        } else {
            verifyPermissions(Permissions.PERMISSIONS);
        }
    }

    /***
     * return the current tab number
     * 0 = GalleryFragment
     * 1 = PhotoFragment
     *
     * @return
     */
    public int getCurrentTabNumber() {
        return mActivityShareBinding.layoutCenterViewPager.viewpagerContainer.getCurrentItem();
    }

    /**
     * setup viewpager for manager the tabs
     */
    private void setupViewPager() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GalleryFragment());
        adapter.addFragment(new PhotoFragment());

        mActivityShareBinding.layoutCenterViewPager.viewpagerContainer
                .setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabsBottom);
        tabLayout.setupWithViewPager(mActivityShareBinding.layoutCenterViewPager.viewpagerContainer);

        tabLayout.getTabAt(0).setText(getString(R.string.gallery));
        tabLayout.getTabAt(1).setText(getString(R.string.photo));

    }

    public int getTask() {
        boolean isGalleryFragment = false;
        if (null != getIntent()) {
            isGalleryFragment = getIntent().getBooleanExtra(getString(R.string.gallery_fragment), false);
        }
        return isGalleryFragment ? 0 : 1;
    }

    /**
     * verifiy all the permissions passed to the array
     *
     * @param permissions
     */
    public void verifyPermissions(String[] permissions) {
        Log.d(TAG, "verifyPermissions: verifying permissions.");

        ActivityCompat.requestPermissions(
                ShareActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }

    /**
     * Check an array of permissions
     *
     * @param permissions
     * @return
     */
    public boolean checkPermissionsArray(String[] permissions) {
        Log.d(TAG, "checkPermissionsArray: checking permissions array.");

        for (int i = 0; i < permissions.length; i++) {
            String check = permissions[i];
            if (!checkPermissions(check)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check a single permission is it has been verified
     *
     * @param permission
     * @return
     */
    public boolean checkPermissions(String permission) {
        Log.d(TAG, "checkPermissions: checking permission: " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(ShareActivity.this, permission);

        if (permissionRequest != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
            return false;
        } else {
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (checkPermissionsArray(Permissions.PERMISSIONS)) {
            setupViewPager();
        } else {
            verifyPermissions(Permissions.PERMISSIONS);
        }

    }
}