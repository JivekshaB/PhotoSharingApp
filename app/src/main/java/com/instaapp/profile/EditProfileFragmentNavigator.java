package com.instaapp.profile;

import com.google.firebase.database.DataSnapshot;

public interface EditProfileFragmentNavigator {

    void showToast(int stringId);

    void setProfileWidgets(DataSnapshot dataSnapshot);

    String getEmail();

    String getStringValue(int id);


}
