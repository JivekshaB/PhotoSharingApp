package com.instaapp.profile;

import com.instaapp.models.Photo;
import com.instaapp.models.UserSettings;

import java.util.ArrayList;

public interface ViewProfileFragmentNavigator {

    String getStringValue(int id);

    void setFollowersCount(int count);

    void setFollowingCount(int count);

    void setPostsCount(int count);

    void setProfileWidgets(UserSettings userSettings);

    void setFollowing();

    void setUnfollowing();

    void setupImageGrid(final ArrayList<Photo> photos);

}
