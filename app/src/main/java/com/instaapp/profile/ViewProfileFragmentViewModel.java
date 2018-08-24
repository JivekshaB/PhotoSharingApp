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
import com.instaapp.models.UserSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jiveksha on 8/18/18.
 */

public class ViewProfileFragmentViewModel extends BaseViewModel<ViewProfileFragmentNavigator> {

    private static final String TAG = ViewProfileFragmentViewModel.class.getSimpleName();

    private final DatabaseReference databaseReference;
    private User mUser;
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
    public ViewProfileFragmentViewModel(Context context, FirebaseAuth firebaseAuth, FirebaseDatabase firebaseDatabase, FirebaseStorage firebaseStorage) {
        super(context, firebaseAuth, firebaseDatabase, firebaseStorage);
        databaseReference = firebaseDatabase.getReference();
    }

    public void isFollowing(User user) {
        Log.d(TAG, "isFollowing: checking if following this users.");
        getNavigator().setUnfollowing();

        Query query = databaseReference.child(getNavigator().getStringValue(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByChild(getNavigator().getStringValue(R.string.field_user_id)).equalTo(user.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user:" + singleSnapshot.getValue());

                    getNavigator().setFollowing();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getFollowersCount(User user) {
        mFollowersCount = 0;

        Query query = databaseReference.child(getNavigator().getStringValue(R.string.dbname_followers))
                .child(user.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found follower:" + singleSnapshot.getValue());
                    mFollowersCount++;
                }
                getNavigator().setFollowersCount(mFollowersCount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getFollowingCount(User user) {
        mFollowingCount = 0;

        Query query = databaseReference.child(getNavigator().getStringValue(R.string.dbname_following))
                .child(user.getUser_id());
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

    public void getPostsCount(User user) {
        mPostsCount = 0;

        Query query = databaseReference.child(getNavigator().getStringValue(R.string.dbname_user_photos))
                .child(user.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found post:" + singleSnapshot.getValue());
                    mPostsCount++;
                }
                getNavigator().setPostsCount(mPostsCount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onClickFollow(User user) {
        databaseReference
                .child(getNavigator().getStringValue(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mUser.getUser_id())
                .child(getNavigator().getStringValue(R.string.field_user_id))
                .setValue(mUser.getUser_id());

        databaseReference
                .child(getNavigator().getStringValue(R.string.dbname_followers))
                .child(mUser.getUser_id())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getNavigator().getStringValue(R.string.field_user_id))
                .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
        getNavigator().setFollowing();
    }

    public void onClickUnfollow(User user) {
       databaseReference
                .child(getNavigator().getStringValue(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mUser.getUser_id())
                .removeValue();

        databaseReference
                .child(getNavigator().getStringValue(R.string.dbname_followers))
                .child(mUser.getUser_id())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .removeValue();
        getNavigator().setUnfollowing();
    }

    void profileSetup(User user) {
        mUser = user;

        Query query1 = databaseReference.child(getNavigator().getStringValue(R.string.dbname_user_account_settings))
                .orderByChild(getNavigator().getStringValue(R.string.field_user_id)).equalTo(user.getUser_id());
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user:" + singleSnapshot.getValue(UserAccountSettings.class).toString());

                    UserSettings settings = new UserSettings();
                    settings.setUser(mUser);
                    settings.setSettings(singleSnapshot.getValue(UserAccountSettings.class));
                    getNavigator().setProfileWidgets(settings);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //get the users profile photos
        Query query2 = databaseReference
                .child(getNavigator().getStringValue(R.string.dbname_user_photos))
                .child(user.getUser_id());
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Photo> photos = new ArrayList<Photo>();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                    Photo photo = new Photo();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                    photo.setCaption(objectMap.get(getNavigator().getStringValue(R.string.field_caption)).toString());
                    photo.setTags(objectMap.get(getNavigator().getStringValue(R.string.field_tags)).toString());
                    photo.setPhoto_id(objectMap.get(getNavigator().getStringValue(R.string.field_photo_id)).toString());
                    photo.setUser_id(objectMap.get(getNavigator().getStringValue(R.string.field_user_id)).toString());
                    photo.setDate_created(objectMap.get(getNavigator().getStringValue(R.string.field_date_created)).toString());
                    photo.setImage_path(objectMap.get(getNavigator().getStringValue(R.string.field_image_path)).toString());

                    ArrayList<Comment> comments = new ArrayList<Comment>();
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
                }
                getNavigator().setupImageGrid(photos);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
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

            Log.d(TAG, "onAuthStateChanged: navigating back to login screen.");

        }

    }
}
