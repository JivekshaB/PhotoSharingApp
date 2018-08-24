package com.instaapp.search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.instaapp.R;
import com.instaapp.base.BaseViewModel;
import com.instaapp.models.User;

/**
 * Created by jiveksha on 8/18/18.
 */

public class SearchActivityViewModel extends BaseViewModel<SearchActivityNavigator> {

    private static final String TAG = SearchActivityViewModel.class.getSimpleName();

    private final DatabaseReference databaseReference;

    /**
     * Init View Model to provide FirebasAuth, FirebaseDatabase, FirebaseStorage, FirebaseMethods, ImageLoader
     *
     * @param context          :{@link Context}
     * @param firebaseAuth     :{@link FirebaseAuth}
     * @param firebaseDatabase :{@link FirebaseDatabase}
     * @param firebaseStorage  :{@link FirebaseStorage}
     */
    public SearchActivityViewModel(Context context, FirebaseAuth firebaseAuth, FirebaseDatabase firebaseDatabase, FirebaseStorage firebaseStorage) {
        super(context, firebaseAuth, firebaseDatabase, firebaseStorage);
        databaseReference = firebaseDatabase.getReference();
    }

    public void searchProfile(String keyword) {
        //update the users list view
        if (keyword.length() == 0) {
            //do nothing
        } else {
            DatabaseReference reference = getFirebaseDatabase().getReference();
            Query query = reference.child(getNavigator().getStringValue(R.string.dbname_users))
                    .orderByChild(getNavigator().getStringValue(R.string.field_username)).equalTo(keyword);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: found user:" + singleSnapshot.getValue(User.class).toString());
                        getNavigator().updateUserSearchList(singleSnapshot.getValue(User.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
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
