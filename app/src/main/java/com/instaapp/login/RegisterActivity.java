package com.instaapp.login;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.instaapp.BR;
import com.instaapp.R;
import com.instaapp.base.BaseActivity;
import com.instaapp.databinding.ActivityRegisterBinding;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by jiveksha on 8/10/18.
 */

public class RegisterActivity extends BaseActivity<ActivityRegisterBinding, RegistrationViewModel> implements RegistrationNavigator {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    @Inject
    @Named("RegisterActivity")
    ViewModelProvider.Factory mViewModelFactory;

    private ActivityRegisterBinding mActivityRegisterBinding;

    private RegistrationViewModel mRegistrationViewModel;


    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public RegistrationViewModel getViewModel() {
        mRegistrationViewModel = ViewModelProviders.of(this, mViewModelFactory).get(RegistrationViewModel.class);
        return mRegistrationViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: started RegisterActivity...");
        init();
    }

    /**
     * Initialize the activity widgets
     */
    private void init() {
        Log.d(TAG, "init.");
        mRegistrationViewModel.setNavigator(this);
        mActivityRegisterBinding = getViewDataBinding();
        mActivityRegisterBinding.progressBar.setVisibility(View.GONE);
        mActivityRegisterBinding.loadingPleaseWait.setVisibility(View.GONE);
        mActivityRegisterBinding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mActivityRegisterBinding.inputEmail.getText().toString();
                String username = mActivityRegisterBinding.inputUsername.getText().toString();
                String password = mActivityRegisterBinding.inputPassword.getText().toString();

                if (checkInputs(email, username, password)) {
                    mActivityRegisterBinding.progressBar.setVisibility(View.VISIBLE);
                    mActivityRegisterBinding.loadingPleaseWait.setVisibility(View.VISIBLE);
                    mRegistrationViewModel.getFirebaseMethods().registerNewEmail(email, password, username);
                }
            }
        });
    }

    private boolean checkInputs(String email, String username, String password) {
        Log.d(TAG, "checkInputs: checking inputs for null values.");
        if (email.equals("") || username.equals("") || password.equals("")) {
            Toast.makeText(getApplicationContext(), "All fields must be filled out.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public String getUserName() {
        return mActivityRegisterBinding.inputUsername.getText().toString();
    }

    @Override
    public String getEmail() {
        return mActivityRegisterBinding.inputEmail.getText().toString();
    }

    @Override
    public String getStringValue(int id) {
        return getString(id);
    }

    @Override
    public void showToast(int messageId) {
        Toast.makeText(getApplicationContext(), getString(messageId), Toast.LENGTH_LONG).show();
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        mRegistrationViewModel.startFireBaseAuth();
    }

    @Override
    public void onStop() {
        super.onStop();
        mRegistrationViewModel.stopFireBaseAuth();
    }
}
