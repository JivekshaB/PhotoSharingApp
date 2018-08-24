package com.instaapp.profile;

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
import com.instaapp.models.Comment;
import com.instaapp.models.Like;
import com.instaapp.models.Photo;
import com.instaapp.models.User;
import com.instaapp.models.UserAccountSettings;
import com.instaapp.utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jiveksha on 8/18/18.
 */

public class ViewPostFragmentViewModel extends BaseViewModel<ViewPostFragmentNavigator> {

    private static final String TAG = ViewPostFragmentViewModel.class.getSimpleName();

    private final DatabaseReference databaseReference;
    private Boolean mLikedByCurrentUser;
    private User mCurrentUser;
    private UserAccountSettings mUserAccountSettings;
    private StringBuilder mUsers;
    private String mLikesString = "";
    private Photo mPhoto;


    /**
     * Init View Model to provide FirebasAuth, FirebaseDatabase, FirebaseStorage, FirebaseMethods, ImageLoader
     *
     * @param context          :{@link Context}
     * @param firebaseAuth     :{@link FirebaseAuth}
     * @param firebaseDatabase :{@link FirebaseDatabase}
     * @param firebaseStorage  :{@link FirebaseStorage}
     */
    public ViewPostFragmentViewModel(Context context, FirebaseAuth firebaseAuth, FirebaseDatabase firebaseDatabase, FirebaseStorage firebaseStorage) {
        super(context, firebaseAuth, firebaseDatabase, firebaseStorage);
        databaseReference = firebaseDatabase.getReference();
    }

    public void init() {
        try {
            //mPhoto = getPhotoFromBundle();
            String photo_id = getNavigator().getPhotoFromBundle().getPhoto_id();

            Query query = FirebaseDatabase.getInstance().getReference()
                    .child(getNavigator().getStringValue(R.string.dbname_photos))
                    .orderByChild(getNavigator().getStringValue(R.string.field_photo_id))
                    .equalTo(photo_id);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        Photo newPhoto = new Photo();
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                        newPhoto.setCaption(objectMap.get(getNavigator().getStringValue(R.string.field_caption)).toString());
                        newPhoto.setTags(objectMap.get(getNavigator().getStringValue(R.string.field_tags)).toString());
                        newPhoto.setPhoto_id(objectMap.get(getNavigator().getStringValue(R.string.field_photo_id)).toString());
                        newPhoto.setUser_id(objectMap.get(getNavigator().getStringValue(R.string.field_user_id)).toString());
                        newPhoto.setDate_created(objectMap.get(getNavigator().getStringValue(R.string.field_date_created)).toString());
                        newPhoto.setImage_path(objectMap.get(getNavigator().getStringValue(R.string.field_image_path)).toString());

                        List<Comment> commentsList = new ArrayList<>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getNavigator().getStringValue(R.string.field_comments)).getChildren()) {
                            Comment comment = new Comment();
                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                            commentsList.add(comment);
                        }
                        newPhoto.setComments(commentsList);

                        mPhoto = newPhoto;

                        getCurrentUser();
                        getPhotoDetails();
                        getLikesString();

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: query cancelled.");
                }
            });

        } catch (NullPointerException e) {
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage());
        }
    }


    private void getLikesString() {
        Log.d(TAG, "getLikesString: getting likes string");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getNavigator().getStringValue(R.string.dbname_photos))
                .child(mPhoto.getPhoto_id())
                .child(getNavigator().getStringValue(R.string.field_likes));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers = new StringBuilder();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Query query = reference
                            .child(getNavigator().getStringValue(R.string.dbname_users))
                            .orderByChild(getNavigator().getStringValue(R.string.field_user_id))
                            .equalTo(singleSnapshot.getValue(Like.class).getUser_id());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                Log.d(TAG, "onDataChange: found like: " +
                                        singleSnapshot.getValue(User.class).getUsername());

                                mUsers.append(singleSnapshot.getValue(User.class).getUsername());
                                mUsers.append(",");
                            }

                            String[] splitUsers = mUsers.toString().split(",");

                            if (mUsers.toString().contains(mCurrentUser.getUsername() + ",")) {
                                mLikedByCurrentUser = true;
                            } else {
                                mLikedByCurrentUser = false;
                            }

                            int length = splitUsers.length;
                            if (length == 1) {
                                mLikesString = "Liked by " + splitUsers[0];
                            } else if (length == 2) {
                                mLikesString = "Liked by " + splitUsers[0]
                                        + " and " + splitUsers[1];
                            } else if (length == 3) {
                                mLikesString = "Liked by " + splitUsers[0]
                                        + ", " + splitUsers[1]
                                        + " and " + splitUsers[2];

                            } else if (length == 4) {
                                mLikesString = "Liked by " + splitUsers[0]
                                        + ", " + splitUsers[1]
                                        + ", " + splitUsers[2]
                                        + " and " + splitUsers[3];
                            } else if (length > 4) {
                                mLikesString = "Liked by " + splitUsers[0]
                                        + ", " + splitUsers[1]
                                        + ", " + splitUsers[2]
                                        + " and " + (splitUsers.length - 3) + " others";
                            }
                            Log.d(TAG, "onDataChange: likes string: " + mLikesString);
                            getNavigator().setupWidgets(mPhoto, mUserAccountSettings);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                if (!dataSnapshot.exists()) {
                    mLikesString = "";
                    mLikedByCurrentUser = false;
                    getNavigator().setupWidgets(mPhoto, mUserAccountSettings);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getCurrentUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getNavigator().getStringValue(R.string.dbname_users))
                .orderByChild(getNavigator().getStringValue(R.string.field_user_id))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    mCurrentUser = singleSnapshot.getValue(User.class);
                }
                getLikesString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
    }

    public void doubleTapClick() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getNavigator().getStringValue(R.string.dbname_photos))
                .child(mPhoto.getPhoto_id())
                .child(getNavigator().getStringValue(R.string.field_likes));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                    String keyID = singleSnapshot.getKey();

                    //case1: Then user already liked the photo
                    if (mLikedByCurrentUser &&
                            singleSnapshot.getValue(Like.class).getUser_id()
                                    .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                        databaseReference.child(getNavigator().getStringValue(R.string.dbname_photos))
                                .child(mPhoto.getPhoto_id())
                                .child(getNavigator().getStringValue(R.string.field_likes))
                                .child(keyID)
                                .removeValue();
///
                        databaseReference.child(getNavigator().getStringValue(R.string.dbname_user_photos))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(mPhoto.getPhoto_id())
                                .child(getNavigator().getStringValue(R.string.field_likes))
                                .child(keyID)
                                .removeValue();

                        getNavigator().toggleHeartLike();
                        getLikesString();
                    }
                    //case2: The user has not liked the photo
                    else if (!mLikedByCurrentUser) {
                        //add new like
                        addNewLike();
                        break;
                    }
                }
                if (!dataSnapshot.exists()) {
                    //add new like
                    addNewLike();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void addNewLike() {
        Log.d(TAG, "addNewLike: adding new like");

        String newLikeID = databaseReference.push().getKey();
        Like like = new Like();
        like.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseReference.child(getNavigator().getStringValue(R.string.dbname_photos))
                .child(mPhoto.getPhoto_id())
                .child(getNavigator().getStringValue(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        databaseReference.child(getNavigator().getStringValue(R.string.dbname_user_photos))
                .child(mPhoto.getUser_id())
                .child(mPhoto.getPhoto_id())
                .child(getNavigator().getStringValue(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        getNavigator().toggleHeartLike();
        getLikesString();
    }


    private void getPhotoDetails() {
        Log.d(TAG, "getPhotoDetails: retrieving photo details.");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getNavigator().getStringValue(R.string.dbname_user_account_settings))
                .orderByChild(getNavigator().getStringValue(R.string.field_user_id))
                .equalTo(mPhoto.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    mUserAccountSettings = singleSnapshot.getValue(UserAccountSettings.class);
                }
                //setupWidgets();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
    }

    public boolean isLikedByCurrentUser() {
        return mLikedByCurrentUser;
    }

    public String getLikesStringValue() {
        return mLikesString;
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
