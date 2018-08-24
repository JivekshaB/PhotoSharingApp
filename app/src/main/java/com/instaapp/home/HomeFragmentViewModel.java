package com.instaapp.home;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.instaapp.base.BaseViewModel;
import com.instaapp.R;
import com.instaapp.models.Comment;
import com.instaapp.models.Photo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jiveksha on 8/18/18.
 */

public class HomeFragmentViewModel extends BaseViewModel<HomeFragmentNavigator> {

    private static final String TAG = HomeFragmentViewModel.class.getSimpleName();

    private ArrayList<Photo> mPhotos;
    private ArrayList<String> mFollowing;

    /**
     * Init View Model to provide FirebasAuth, FirebaseDatabase, FirebaseStorage, FirebaseMethods, ImageLoader
     *
     * @param context          :{@link Context}
     * @param firebaseAuth     :{@link FirebaseAuth}
     * @param firebaseDatabase :{@link FirebaseDatabase}
     * @param firebaseStorage  :{@link FirebaseStorage}
     */
    public HomeFragmentViewModel(Context context, FirebaseAuth firebaseAuth, FirebaseDatabase firebaseDatabase, FirebaseStorage firebaseStorage) {
        super(context, firebaseAuth, firebaseDatabase, firebaseStorage);
    }


    /**
     * Retrieve all user id's that current user is following
     */
    public void getFollowing() {
        Log.d(TAG, "getFollowing: searching for following");

        refreshViewUi();
        //also add your own id to the list
        mFollowing.add(getFirebaseAuth().getCurrentUser().getUid());

        Query query = getFirebaseDatabase().getReference()
                .child(getNavigator().getStringValue(R.string.dbname_following))
                .child(getFirebaseAuth().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "getFollowing: found user: " + singleSnapshot
                            .child(getNavigator().getStringValue(R.string.field_user_id)).getValue());

                    mFollowing.add(singleSnapshot
                            .child(getNavigator().getStringValue(R.string.field_user_id)).getValue().toString());
                }

                getPhotos();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    private void getPhotos() {
        Log.d(TAG, "getPhotos: getting list of photos");

        for (int i = 0; i < mFollowing.size(); i++) {
            final int count = i;
            Query query = getFirebaseDatabase().getReference()
                    .child(getNavigator().getStringValue(R.string.dbname_user_photos))
                    .child(mFollowing.get(i))
                    .orderByChild(getNavigator().getStringValue(R.string.field_user_id))
                    .equalTo(mFollowing.get(i));
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

                        Log.d(TAG, "getPhotos: photo: " + newPhoto.getPhoto_id());
                        List<Comment> commentsList = new ArrayList<>();
                        for (DataSnapshot firebaseDataSnapshot : singleSnapshot
                                .child(getNavigator().getStringValue(R.string.field_comments)).getChildren()) {
                            Map<String, Object> object_map = (HashMap<String, Object>) firebaseDataSnapshot.getValue();
                            Comment comment = new Comment();
                            comment.setUser_id(object_map.get(getNavigator().getStringValue(R.string.field_user_id)).toString());
                            comment.setComment(object_map.get(getNavigator().getStringValue(R.string.field_comment)).toString());
                            comment.setDate_created(object_map.get(getNavigator().getStringValue(R.string.field_date_created)).toString());
                            commentsList.add(comment);
                        }
                        newPhoto.setComments(commentsList);
                        mPhotos.add(newPhoto);
                    }
                    if (count >= mFollowing.size() - 1) {
                        //display the photos
                        getNavigator().callDisplayPhotos();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: query cancelled.");
                }
            });

        }
    }

    public ArrayList<Photo> getUserPhotos() {
        return mPhotos;
    }

    private void refreshViewUi() {
        if (mFollowing != null) {
            mFollowing.clear();
        }
        if (mPhotos != null) {
            mPhotos.clear();
        }

        mFollowing = new ArrayList<>();
        mPhotos = new ArrayList<>();
        getNavigator().refreshUi();

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
}
