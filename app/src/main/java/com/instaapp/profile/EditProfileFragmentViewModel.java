package com.instaapp.profile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.instaapp.R;
import com.instaapp.base.BaseViewModel;
import com.instaapp.dialogs.ConfirmPasswordDialog;
import com.instaapp.models.User;

/**
 * Created by jiveksha on 8/18/18.
 */

public class EditProfileFragmentViewModel extends BaseViewModel<EditProfileFragmentNavigator> implements ConfirmPasswordDialog.OnConfirmPasswordListener {

    private static final String TAG = EditProfileFragmentViewModel.class.getSimpleName();

    private DatabaseReference mDatabaseReference;

    /**
     * Init View Model to provide FirebasAuth, FirebaseDatabase, FirebaseStorage, FirebaseMethods, ImageLoader
     *
     * @param context          :{@link Context}
     * @param firebaseAuth     :{@link FirebaseAuth}
     * @param firebaseDatabase :{@link FirebaseDatabase}
     * @param firebaseStorage  :{@link FirebaseStorage}
     */
    public EditProfileFragmentViewModel(Context context, FirebaseAuth firebaseAuth, FirebaseDatabase firebaseDatabase, FirebaseStorage firebaseStorage) {
        super(context, firebaseAuth, firebaseDatabase, firebaseStorage);
        mDatabaseReference = firebaseDatabase.getReference();
    }


    /**
     * Setup the firebase auth object
     */
    public void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //retrieve user information from the database

                getNavigator().setProfileWidgets(dataSnapshot);
                //retrieve images for the user in question
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onConfirmPassword(String password) {
        Log.d(TAG, "onConfirmPassword: got the password: " + password);

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider.getCredential(getFirebaseAuth().getCurrentUser().getEmail(), password);

        ///////////////////// Prompt the user to re-provide their sign-in credentials
        getFirebaseAuth().getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User re-authenticated.");

                            ///////////////////////check to see if the email is not already present in the database
                            getFirebaseAuth().fetchProvidersForEmail(getNavigator().getEmail()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                    if (task.isSuccessful()) {
                                        try {
                                            if (task.getResult().getProviders().size() == 1) {
                                                Log.d(TAG, "onComplete: that email is already in use.");
                                                getNavigator().showToast(R.string.profile_email_already_in_use);
                                            } else {
                                                Log.d(TAG, "onComplete: That email is available.");

                                                //////////////////////the email is available so update it
                                                getFirebaseAuth().getCurrentUser().updateEmail(getNavigator().getEmail())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Log.d(TAG, "User email address updated.");
                                                                    getNavigator().showToast(R.string.profile_email_updated);
                                                                    getFirebaseMethods().updateEmail(getNavigator().getEmail());
                                                                }
                                                            }
                                                        });
                                            }
                                        } catch (NullPointerException e) {
                                            Log.e(TAG, "onComplete: NullPointerException: " + e.getMessage());
                                        }
                                    }
                                }
                            });


                        } else {
                            Log.d(TAG, "onComplete: re-authentication failed.");
                        }

                    }
                });
    }


    /**
     * Check is @param username already exists in teh database
     *
     * @param username
     */
    public void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernameExists: Checking if  " + username + " already exists.");

        Query query = mDatabaseReference
                .child(getNavigator().getStringValue(R.string.dbname_users))
                .orderByChild(getNavigator().getStringValue(R.string.field_username))
                .equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    //add the username
                    getFirebaseMethods().updateUsername(username);
                    getNavigator().showToast(R.string.profile_save_username);
                }
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    if (singleSnapshot.exists()) {
                        Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + singleSnapshot.getValue(User.class).getUsername());
                        getNavigator().getStringValue(R.string.profile_username_exists);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        }

    }
}
