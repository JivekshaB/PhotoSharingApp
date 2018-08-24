package com.instaapp.profile;

import com.instaapp.models.Photo;
import com.instaapp.models.UserAccountSettings;

public interface ViewPostFragmentNavigator {

    String getStringValue(int id);

    void setupWidgets(Photo photo, UserAccountSettings userAccountSettings);

    Photo getPhotoFromBundle();

    void toggleHeartLike();

    int getActivityNumFromBundle();

}
