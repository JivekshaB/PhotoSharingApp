package com.instaapp.login;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.instaapp.base.BaseViewModel;
import com.instaapp.R;

/**
 * Created by jiveksha on 8/20/18.
 */

public class LoginViewModel extends BaseViewModel<LoginNavigator> {

    private static final String TAG = LoginViewModel.class.getSimpleName();

    /**
     * Init View Model to provide FirebasAuth, FirebaseDatabase, FirebaseStorage, FirebaseMethods, ImageLoader
     *
     * @param context          :{@link Context}
     * @param firebaseAuth     :{@link FirebaseAuth}
     * @param firebaseDatabase :{@link FirebaseDatabase}
     * @param firebaseStorage  :{@link FirebaseStorage}
     */
    public LoginViewModel(Context context, FirebaseAuth firebaseAuth, FirebaseDatabase firebaseDatabase, FirebaseStorage firebaseStorage) {
        super(context, firebaseAuth, firebaseDatabase, firebaseStorage);
    }


    public void doLogin(final LoginActivity loginActivity, String email, String password) {
        getFirebaseAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(loginActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        FirebaseUser user = getFirebaseAuth().getCurrentUser();

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());

                            Toast.makeText(loginActivity, loginActivity.getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                            getNavigator().toggleProgress(false);
                        } else {
                            try {
                                if (user.isEmailVerified()) {
                                    Log.d(TAG, "onComplete: success. email is verified.");
                                    getNavigator().redirectToHome();
                                } else {
                                    Toast.makeText(loginActivity, "Email is not verified \n check your email inbox.", Toast.LENGTH_SHORT).show();
                                    getNavigator().toggleProgress(false);
                                    getFirebaseAuth().signOut();
                                }
                            } catch (NullPointerException e) {
                                Log.e(TAG, "onComplete: NullPointerException: " + e.getMessage());
                            }
                        }

                    }
                });
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //check if the user is logged in
        if (user != null) {
            // User is signed in
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }
    }

    public FirebaseUser getCurrentUser() {
        return getFirebaseAuth().getCurrentUser();
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
}
