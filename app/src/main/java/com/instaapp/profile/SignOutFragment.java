package com.instaapp.profile;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.instaapp.BR;
import com.instaapp.R;
import com.instaapp.base.BaseFragment;
import com.instaapp.databinding.FragmentSignoutBinding;
import com.instaapp.login.LoginActivity;

import javax.inject.Inject;
import javax.inject.Named;


/**
 * Created by User on 6/4/2017.
 */

public class SignOutFragment extends BaseFragment<FragmentSignoutBinding, SignOutFragmentViewModel> implements SignOutFragmentNavigator {

    private static final String TAG = SignOutFragment.class.getSimpleName();

    @Inject
    @Named("SignOutFragment")
    ViewModelProvider.Factory mViewModelFactory;

    private FragmentSignoutBinding mFragmentSignoutBinding;

    private SignOutFragmentViewModel mSignOutFragmentViewModel;

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_signout;
    }

    @Override
    public SignOutFragmentViewModel getViewModel() {
        mSignOutFragmentViewModel = ViewModelProviders.of(this, mViewModelFactory).get(SignOutFragmentViewModel.class);
        return mSignOutFragmentViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSignOutFragmentViewModel.setNavigator(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentSignoutBinding = getViewDataBinding();
        setUp();
    }

    private void setUp() {
        mFragmentSignoutBinding.progressBar.setVisibility(View.GONE);
        mFragmentSignoutBinding.tvSigningOut.setVisibility(View.GONE);

        mFragmentSignoutBinding.btnConfirmSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to sign out.");
                mFragmentSignoutBinding.progressBar.setVisibility(View.VISIBLE);
                mFragmentSignoutBinding.tvSigningOut.setVisibility(View.VISIBLE);
                mSignOutFragmentViewModel.signOutUser();
            }
        });
    }

    @Override
    public void redirectToLogin() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.putExtra(getString(R.string.from_signup), true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        ((AppCompatActivity) getContext()).finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        mSignOutFragmentViewModel.startFireBaseAuth();
    }

    @Override
    public void onStop() {
        super.onStop();
        mSignOutFragmentViewModel.stopFireBaseAuth();
    }


}
