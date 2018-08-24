package com.instaapp.share;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.instaapp.base.BaseViewModel;

/**
 * Created by jiveksha on 8/18/18.
 */

public class NextActivityViewModel extends BaseViewModel<NextActivityNavigator> {

    private static final String TAG = NextActivityViewModel.class.getSimpleName();

    private final DatabaseReference databaseReference;

    private int imageCount = 0;

    /**
     * Init View Model to provide FirebasAuth, FirebaseDatabase, FirebaseStorage, FirebaseMethods, ImageLoader
     *
     * @param context          :{@link Context}
     * @param firebaseAuth     :{@link FirebaseAuth}
     * @param firebaseDatabase :{@link FirebaseDatabase}
     * @param firebaseStorage  :{@link FirebaseStorage}
     */
    public NextActivityViewModel(Context context, FirebaseAuth firebaseAuth, FirebaseDatabase firebaseDatabase, FirebaseStorage firebaseStorage) {
        super(context, firebaseAuth, firebaseDatabase, firebaseStorage);
        databaseReference = firebaseDatabase.getReference();
    }

    public void init() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imageCount = getFirebaseMethods().getImageCount(dataSnapshot);
                Log.d(TAG, "onDataChange: image count: " + imageCount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public int getImageCount() {
        return imageCount;
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

        }

    }
}
