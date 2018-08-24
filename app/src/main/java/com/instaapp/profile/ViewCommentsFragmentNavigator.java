package com.instaapp.profile;

import com.instaapp.models.Comment;
import com.instaapp.models.Photo;

import java.util.ArrayList;

public interface ViewCommentsFragmentNavigator {

    String getStringValue(int id);

    void setUpWidgets(ArrayList<Comment> comments);

    Photo getPhotoFromBundle();

    String getCallingActivityFromBundle();

}
