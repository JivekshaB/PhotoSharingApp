package com.instaapp.login;

/**
 * Created by jiveksha on 8/20/18.
 */

public interface RegistrationNavigator {

    void showToast(int messageId);

    void finishActivity();

    String getUserName();

    String getEmail();

    String getStringValue(int id);

}
