package com.instaapp.home;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.instaapp.base.BaseViewModel;

/**
 * Created by jiveksha on 8/18/18.
 */

public class HomeViewModel extends BaseViewModel<HomeNavigator> {

    private static final String TAG = HomeViewModel.class.getSimpleName();

    /**
     * Init View Model to provide FirebasAuth and FirebaseDatabae
     *
     * @param context
     * @param firebaseAuth     :{@link FirebaseAuth}
     * @param firebaseDatabase :{@link FirebaseDatabase}
     * @param firebaseStorage
     */
    public HomeViewModel(Context context, FirebaseAuth firebaseAuth, FirebaseDatabase firebaseDatabase, FirebaseStorage firebaseStorage) {
        super(context, firebaseAuth, firebaseDatabase, firebaseStorage);
    }


    /**
     * checks to see if the @param 'user' is logged in
     */
    public void checkCurrentUser(FirebaseUser user) {
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");
        if (user == null) {
            getNavigator().redirectToLoginActivity();
        }
    }

    /**
     * Check to see if the firebase user's state has changed
     *
     * @param firebaseAuth
     */
    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //check if the user is logged in
        checkCurrentUser(user);

        if (user != null) {
            // User is signed in
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }
    }

    /**
     * Remove firebase state listener
     */
    public void stopFireBaseAuth() {
        getFirebaseAuth().removeAuthStateListener(this);
    }

    /**
     * Add firebase state listener
     */
    public void startFireBaseAuth() {
        getFirebaseAuth().addAuthStateListener(this);
    }

    /**
     * Get Firebase User
     *
     * @return
     */
    public FirebaseUser getFirebaseUser() {
        return getFirebaseAuth().getCurrentUser();
    }

}
