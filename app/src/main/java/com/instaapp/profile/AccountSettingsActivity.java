package com.instaapp.profile;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.instaapp.BR;
import com.instaapp.R;
import com.instaapp.adapter.SectionsStatePagerAdapter;
import com.instaapp.base.BaseActivity;
import com.instaapp.databinding.ActivityAccountsettingsBinding;
import com.instaapp.utils.BottomNavigationViewHelper;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by jiveksha on 8/9/18.
 */

public class AccountSettingsActivity extends BaseActivity<ActivityAccountsettingsBinding, AccountSettingsViewModel> implements AccountSettingsNavigator {


    private static final String TAG = AccountSettingsActivity.class.getSimpleName();

    private static final int ACTIVITY_NUM = 3;

    @Inject
    @Named("AccountSettings")
    ViewModelProvider.Factory mViewModelFactory;

    private ActivityAccountsettingsBinding mActivityAccountSettingsBinding;

    private AccountSettingsViewModel mAccountSettingsViewModel;

    public SectionsStatePagerAdapter mSectionsStatePagerAdapter;
    private ViewPager mViewPager;

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_accountsettings;
    }

    @Override
    public AccountSettingsViewModel getViewModel() {
        mAccountSettingsViewModel = ViewModelProviders.of(this, mViewModelFactory).get(AccountSettingsViewModel.class);
        return mAccountSettingsViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: started AccountSettings");
        mAccountSettingsViewModel.setNavigator(this);
        setUp();

    }

    private void setUp() {
        mActivityAccountSettingsBinding = getViewDataBinding();
        mViewPager = findViewById(R.id.viewpager_container);

        setupSettingsList();
        setupBottomNavigationView();
        setupFragments();
        getIncomingIntent();


        //set up back arrow for navigating back to profile activity
        ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to 'ProfileActivity'");
                finish();
            }
        });

    }

    private void getIncomingIntent() {
        Intent intent = getIntent();

        if (intent.hasExtra(getString(R.string.selected_image))
                || intent.hasExtra(getString(R.string.selected_bitmap))) {

            //if there is an imageUrl attached as an extra, then it was chosen from the gallery/photo fragment
            Log.d(TAG, "getIncomingIntent: New incoming imgUrl");
            if (intent.getStringExtra(getString(R.string.return_to_fragment)).equals(getString(R.string.edit_profile_fragment))) {

                if (intent.hasExtra(getString(R.string.selected_image))) {
                    //set the new profile picture
                    mAccountSettingsViewModel.getFirebaseMethods().uploadNewPhoto(AccountSettingsActivity.this, getString(R.string.profile_photo), null, 0,
                            intent.getStringExtra(getString(R.string.selected_image)), null);
                } else if (intent.hasExtra(getString(R.string.selected_bitmap))) {
                    //set the new profile picture
                    mAccountSettingsViewModel.getFirebaseMethods().uploadNewPhoto(AccountSettingsActivity.this, getString(R.string.profile_photo), null, 0,
                            intent.getStringExtra(getString(R.string.selected_bitmap)), null);
                }

            }

        }

        if (intent.hasExtra(getString(R.string.calling_activity))) {
            Log.d(TAG, "getIncomingIntent: received incoming intent from " + getString(R.string.profile_activity));
            setViewPager(mSectionsStatePagerAdapter.getFragmentNumber(getString(R.string.edit_profile_fragment)));
        }
    }

    private void setupFragments() {
        mSectionsStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        mSectionsStatePagerAdapter.addFragment(new EditProfileFragment(), getString(R.string.edit_profile_fragment));
        mSectionsStatePagerAdapter.addFragment(new SignOutFragment(), getString(R.string.sign_out_fragment));

    }

    public void setViewPager(int fragmentNumber) {
        Log.d(TAG, "setViewPager: navigating to fragment #: " + fragmentNumber);
        mActivityAccountSettingsBinding.relLayout1.setVisibility(View.GONE);
        mViewPager.setAdapter(mSectionsStatePagerAdapter);
        mViewPager.setCurrentItem(fragmentNumber);
    }

    private void setupSettingsList() {
        Log.d(TAG, "setupSettingsList: initializing 'Account Settings' list.");
        ListView listView = findViewById(R.id.lvAccountSettings);

        ArrayList<String> options = new ArrayList<>();
        options.add(getString(R.string.edit_profile_fragment)); //fragment 0
        options.add(getString(R.string.sign_out_fragment)); //fragment 1

        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, options);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: navigating to fragment#: " + position);
                setViewPager(position);
            }
        });

    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(getApplicationContext(), this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

}
