package com.instaapp.search;

import com.instaapp.models.User;

public interface SearchActivityNavigator {

    String getStringValue(int id);

     void updateUserSearchList(User user);
}
