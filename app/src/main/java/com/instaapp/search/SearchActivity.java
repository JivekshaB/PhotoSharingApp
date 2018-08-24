package com.instaapp.search;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import com.instaapp.BR;
import com.instaapp.R;
import com.instaapp.adapter.UserListAdapter;
import com.instaapp.base.BaseActivity;
import com.instaapp.databinding.ActivitySearchBinding;
import com.instaapp.models.User;
import com.instaapp.profile.ProfileActivity;
import com.instaapp.utils.BottomNavigationViewHelper;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by User on 5/28/2017.
 */

public class SearchActivity extends BaseActivity<ActivitySearchBinding, SearchActivityViewModel> implements SearchActivityNavigator {

    private static final String TAG = SearchActivity.class.getSimpleName();
    private static final int ACTIVITY_NUM = 1;
    //vars
    private List<User> mUserList;

    @Inject
    @Named("SearchActivity")
    ViewModelProvider.Factory mViewModelFactory;

    private ActivitySearchBinding mActivitySearchBinding;

    private SearchActivityViewModel mSettingsActivityViewModel;


    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    public SearchActivityViewModel getViewModel() {
        mSettingsActivityViewModel = ViewModelProviders.of(this, mViewModelFactory).get(SearchActivityViewModel.class);
        return mSettingsActivityViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: started SearchActivity");
        mSettingsActivityViewModel.setNavigator(this);
        setUp();

    }

    private void setUp() {
        mActivitySearchBinding = getViewDataBinding();
        hideSoftKeyboard();
        setupBottomNavigationView();
        initTextListener();
    }

    private void initTextListener() {
        Log.d(TAG, "initTextListener: initializing");

        mUserList = new ArrayList<>();


        mActivitySearchBinding.layoutSnippetSearch.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String text = mActivitySearchBinding.layoutSnippetSearch.search.getText().toString().toLowerCase(Locale.getDefault());
                searchForMatch(text);
            }
        });


    }

    private void searchForMatch(String keyword) {
        Log.d(TAG, "searchForMatch: searching for a match: " + keyword);
        mUserList.clear();
        mSettingsActivityViewModel.searchProfile(keyword);
    }

    @Override
    public String getStringValue(int id) {
        return getString(id);
    }

    @Override
    public void updateUserSearchList(User user) {
        Log.d(TAG, "updateUsersList: updating users list");
        mUserList.add(user);

        UserListAdapter mAdapter = new UserListAdapter(SearchActivity.this, R.layout.layout_user_listitem, mUserList);
        mActivitySearchBinding.listView.setAdapter(mAdapter);
        mActivitySearchBinding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected user: " + mUserList.get(position).toString());

                //navigate to profile activity
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtra(getString(R.string.calling_activity), getString(R.string.search_activity));
                intent.putExtra(getString(R.string.intent_user), mUserList.get(position));
                startActivity(intent);
            }
        });
    }


    private void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(getApplicationContext(), this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
