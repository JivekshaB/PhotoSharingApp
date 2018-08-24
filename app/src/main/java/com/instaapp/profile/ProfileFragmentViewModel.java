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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jiveksha on 8/18/18.
 */

public class ProfileFragmentViewModel extends BaseViewModel<ProfileFragmentNavigator> {

    private static final String TAG = ProfileFragmentViewModel.class.getSimpleName();

    private DatabaseReference mDatabaseReference;
    private int mFollowersCount = 0;
    private int mFollowingCount = 0;
    private int mPostsCount = 0;


    /**
     * Init View Model to provide FirebasAuth, FirebaseDatabase, FirebaseStorage, FirebaseMethods, ImageLoader
     *
     * @param context          :{@link Context}
     * @param firebaseAuth     :{@link FirebaseAuth}
     * @param firebaseDatabase :{@link FirebaseDatabase}
     * @param firebaseStorage  :{@link FirebaseStorage}
     */
    public ProfileFragmentViewModel(Context context, FirebaseAuth firebaseAuth, FirebaseDatabase firebaseDatabase, FirebaseStorage firebaseStorage) {
        super(context, firebaseAuth, firebaseDatabase, firebaseStorage);
        mDatabaseReference = firebaseDatabase.getReference();
    }


    public void getPhotosForGrid() {
        final ArrayList<Photo> photos = new ArrayList<>();
        Query query = mDatabaseReference
                .child(getNavigator().getStringValue(R.string.dbname_user_photos))
                .child(getFirebaseAuth().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                    Photo photo = new Photo();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                    try {

                        photo.setCaption(objectMap.get(getNavigator().getStringValue(R.string.field_caption)).toString());
                        photo.setTags(objectMap.get(getNavigator().getStringValue(R.string.field_tags)).toString());
                        photo.setPhoto_id(objectMap.get(getNavigator().getStringValue(R.string.field_photo_id)).toString());
                        photo.setUser_id(objectMap.get(getNavigator().getStringValue(R.string.field_user_id)).toString());
                        photo.setDate_created(objectMap.get(getNavigator().getStringValue(R.string.field_date_created)).toString());
                        photo.setImage_path(objectMap.get(getNavigator().getStringValue(R.string.field_image_path)).toString());

                        ArrayList<Comment> comments = new ArrayList<>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getNavigator().getStringValue(R.string.field_comments)).getChildren()) {
                            Comment comment = new Comment();
                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                            comments.add(comment);
                        }

                        photo.setComments(comments);

                        List<Like> likesList = new ArrayList<Like>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getNavigator().getStringValue(R.string.field_likes)).getChildren()) {
                            Like like = new Like();
                            like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
                            likesList.add(like);
                        }
                        photo.setLikes(likesList);
                        photos.add(photo);
                        getNavigator().setupImageGrid(photos);

                    } catch (NullPointerException e) {
                        Log.e(TAG, "onDataChange: NullPointerException: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Setup the firebase auth object
     */
    void getUserSettings() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //retrieve user information from the database
                getNavigator().setProfileWidgets(getFirebaseMethods().getUserSettings(dataSnapshot));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void getFollowersCount() {
        mFollowersCount = 0;

        Query query = mDatabaseReference.child(getNavigator().getStringValue(R.string.dbname_followers))
                .child(getFirebaseAuth().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found follower:" + singleSnapshot.getValue());
                    mFollowersCount++;
                }
                getNavigator().setFollowersCount(mFollowingCount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    void getFollowingCount() {
        mFollowingCount = 0;

        Query query = mDatabaseReference.child(getNavigator().getStringValue(R.string.dbname_following))
                .child(getFirebaseAuth().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found following user:" + singleSnapshot.getValue());
                    mFollowingCount++;
                }
                getNavigator().setFollowingCount(mFollowingCount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void getPostsCount() {
        mPostsCount = 0;

        Query query = mDatabaseReference.child(getNavigator().getStringValue(R.string.dbname_user_photos))
                .child(getFirebaseAuth().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found post:" + singleSnapshot.getValue());
                    mPostsCount++;
                    getNavigator().setPostsCount(mPostsCount);
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
