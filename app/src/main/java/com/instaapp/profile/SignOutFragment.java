package com.instaapp.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.instaapp.BaseFragment;
import com.instaapp.R;
import com.instaapp.login.LoginActivity;


/**
 * Created by User on 6/4/2017.
 */

public class SignOutFragment extends BaseFragment {

    private static final String TAG = "SignOutFragment";

    private FirebaseAuth.AuthStateListener mAuthListener;

    private ProgressBar mProgressBar;
    private TextView tvSignout, tvSigningOut;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signout, container, false);
        tvSignout = view.findViewById(R.id.tvConfirmSignout);
        mProgressBar = view.findViewById(R.id.progressBar);
        tvSigningOut = view.findViewById(R.id.tvSigningOut);
        Button btnConfirmSignout = view.findViewById(R.id.btnConfirmSignout);

        mProgressBar.setVisibility(View.GONE);
        tvSigningOut.setVisibility(View.GONE);

        setupFirebaseAuth();

        btnConfirmSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to sign out.");
                mProgressBar.setVisibility(View.VISIBLE);
                tvSigningOut.setVisibility(View.VISIBLE);

                getApplicationComponent().getFirebaseAuth().signOut();
            }
        });

        return view;
    }

    /**
     ------------------------------------ Firebase ---------------------------------------------
     */


    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");

                    Log.d(TAG, "onAuthStateChanged: navigating back to login screen.");
                    Intent intent = new Intent(getApplicationComponent().getContext(), LoginActivity.class);
                    intent.putExtra(getString(R.string.from_signup), true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    getActivityComponent().getActivity().finish();
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        getApplicationComponent().getFirebaseAuth().addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            getApplicationComponent().getFirebaseAuth().removeAuthStateListener(mAuthListener);
        }
    }
}
