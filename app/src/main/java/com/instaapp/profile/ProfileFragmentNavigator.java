package com.instaapp.profile;

import com.instaapp.models.Photo;
import com.instaapp.models.UserSettings;

import java.util.ArrayList;

public interface ProfileFragmentNavigator {

    String getStringValue(int id);

    void setFollowersCount(int count);

    void setFollowingCount(int count);

    void setPostsCount(int count);

    void setProfileWidgets(UserSettings userSettings);

    void setupImageGrid(final ArrayList<Photo> photos);

}
