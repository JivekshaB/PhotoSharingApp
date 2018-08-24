package com.instaapp.login;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.instaapp.BR;
import com.instaapp.R;
import com.instaapp.base.BaseActivity;
import com.instaapp.databinding.ActivityLoginBinding;
import com.instaapp.home.HomeActivity;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by jiveksha on 8/10/18.
 */

public class LoginActivity extends BaseActivity<ActivityLoginBinding, LoginViewModel> implements LoginNavigator {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Inject
    @Named("LoginActivity")
    ViewModelProvider.Factory mViewModelFactory;

    private ActivityLoginBinding mActivityLoginBinding;

    private LoginViewModel mLoginViewModel;


    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public LoginViewModel getViewModel() {
        mLoginViewModel = ViewModelProviders.of(this, mViewModelFactory).get(LoginViewModel.class);
        return mLoginViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: started LoginActivity...");
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.package.ACTION_LOGOUT");
        sendBroadcast(broadcastIntent);
        setUp();
    }

    private void setUp() {
        mLoginViewModel.setNavigator(this);
        mActivityLoginBinding = getViewDataBinding();
        mActivityLoginBinding.pleaseWait.setVisibility(View.GONE);
        mActivityLoginBinding.progressBar.setVisibility(View.GONE);
        init();
    }


    private boolean isStringNull(String string) {
        Log.d(TAG, "isStringNull: checking string if null.");
        return string.equals("");
    }


    private void init() {
        //initialize the button for logging in
        mActivityLoginBinding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: attempting to log in.");

                String email = mActivityLoginBinding.inputEmail.getText().toString();
                String password = mActivityLoginBinding.inputPassword.getText().toString();

                if (isStringNull(email) && isStringNull(password)) {
                    Toast.makeText(getApplicationContext(), "You must fill out all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    toggleProgress(true);
                    mLoginViewModel.doLogin(LoginActivity.this, email, password);

                }
            }
        });

        TextView linkSignUp = mActivityLoginBinding.linkSignup;
        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to register screen");
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });


        /*
         If the user is logged in then navigate to HomeActivity and call 'finish()'
          */
        if (mLoginViewModel.getCurrentUser() != null) {
            redirectToHome();
        }
    }


    @Override
    public void toggleProgress(boolean show) {
        mActivityLoginBinding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        mActivityLoginBinding.pleaseWait.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void redirectToHome() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        mLoginViewModel.startFireBaseAuth();
    }

    @Override
    public void onStop() {
        super.onStop();
        mLoginViewModel.stopFireBaseAuth();
    }
}
