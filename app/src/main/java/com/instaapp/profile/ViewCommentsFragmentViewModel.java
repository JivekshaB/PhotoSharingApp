package com.instaapp.profile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
import com.instaapp.models.Photo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by jiveksha on 8/18/18.
 */

public class ViewCommentsFragmentViewModel extends BaseViewModel<ViewCommentsFragmentNavigator> {

    private static final String TAG = ViewCommentsFragmentViewModel.class.getSimpleName();

    private final DatabaseReference databaseReference;
    private ArrayList<Comment> mComments;
    private Photo mPhoto;

    /**
     * Init View Model to provide FirebasAuth, FirebaseDatabase, FirebaseStorage, FirebaseMethods, ImageLoader
     *
     * @param context          :{@link Context}
     * @param firebaseAuth     :{@link FirebaseAuth}
     * @param firebaseDatabase :{@link FirebaseDatabase}
     * @param firebaseStorage  :{@link FirebaseStorage}
     */
    public ViewCommentsFragmentViewModel(Context context, FirebaseAuth firebaseAuth, FirebaseDatabase firebaseDatabase, FirebaseStorage firebaseStorage) {
        super(context, firebaseAuth, firebaseDatabase, firebaseStorage);
        databaseReference = firebaseDatabase.getReference();
        mComments = new ArrayList<>();
    }


    public void addNewComment(String newComment) {
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);

        String commentID = databaseReference.push().getKey();

        Comment comment = new Comment();
        comment.setComment(newComment);
        comment.setDate_created(getTimestamp());
        comment.setUser_id(getFirebaseAuth().getCurrentUser().getUid());

        if (null == mPhoto) {
           mPhoto = getNavigator().getPhotoFromBundle();
        }

        //insert into photos node
        databaseReference.child(getNavigator().getStringValue(R.string.dbname_photos))
                .child(mPhoto.getPhoto_id())
                .child(getNavigator().getStringValue(R.string.field_comments))
                .child(commentID)
                .setValue(comment);

        //insert into user_photos node
        databaseReference.child(getNavigator().getStringValue(R.string.dbname_user_photos))
                .child(mPhoto.getUser_id()) //should be mphoto.getUser_id()
                .child(mPhoto.getPhoto_id())
                .child(getNavigator().getStringValue(R.string.field_comments))
                .child(commentID)
                .setValue(comment);

    }


    public void setUpComments(final Photo photo) {
        Log.d(TAG, "setUpComments: photo:" + photo.getCaption());
        if (photo.getComments().size() == 0) {
            mComments.clear();
            Comment firstComment = new Comment();
            firstComment.setComment(photo.getCaption());
            firstComment.setUser_id(photo.getUser_id());
            firstComment.setDate_created(photo.getDate_created());
            mComments.add(firstComment);
            photo.setComments(mComments);
            getNavigator().setUpWidgets(mComments);
        }


        databaseReference.child(getNavigator().getStringValue(R.string.dbname_photos))
                .child(photo.getPhoto_id())
                .child(getNavigator().getStringValue(R.string.field_comments))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d(TAG, "onChildAdded: child added.");

                        Query query = databaseReference
                                .child(getNavigator().getStringValue(R.string.dbname_photos))
                                .orderByChild(getNavigator().getStringValue(R.string.field_photo_id))
                                .equalTo(photo.getPhoto_id());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                                    Photo photo = new Photo();
                                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                                    photo.setCaption(objectMap.get(getNavigator().getStringValue(R.string.field_caption)).toString());
                                    photo.setTags(objectMap.get(getNavigator().getStringValue(R.string.field_tags)).toString());
                                    photo.setPhoto_id(objectMap.get(getNavigator().getStringValue(R.string.field_photo_id)).toString());
                                    photo.setUser_id(objectMap.get(getNavigator().getStringValue(R.string.field_user_id)).toString());
                                    photo.setDate_created(objectMap.get(getNavigator().getStringValue(R.string.field_date_created)).toString());
                                    photo.setImage_path(objectMap.get(getNavigator().getStringValue(R.string.field_image_path)).toString());


                                    mComments.clear();
                                    Comment firstComment = new Comment();
                                    firstComment.setComment(photo.getCaption());
                                    firstComment.setUser_id(photo.getUser_id());
                                    firstComment.setDate_created(photo.getDate_created());
                                    mComments.add(firstComment);

                                    for (DataSnapshot dSnapshot : singleSnapshot
                                            .child(getNavigator().getStringValue(R.string.field_comments)).getChildren()) {
                                        Comment comment = new Comment();
                                        comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                                        comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                                        comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                                        mComments.add(comment);
                                    }

                                    photo.setComments(mComments);
                                    mPhoto = photo;
                                    getNavigator().setUpWidgets(mComments);
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d(TAG, "onCancelled: query cancelled.");
                            }
                        });
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

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


    private String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
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
