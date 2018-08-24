package com.instaapp.profile;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.instaapp.BR;
import com.instaapp.R;
import com.instaapp.base.BaseFragment;
import com.instaapp.databinding.FragmentEditprofileBinding;
import com.instaapp.dialogs.ConfirmPasswordDialog;
import com.instaapp.models.UserAccountSettings;
import com.instaapp.models.UserSettings;
import com.instaapp.share.ShareActivity;
import com.instaapp.utils.UniversalImageLoader;

import javax.inject.Inject;
import javax.inject.Named;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by User on 6/4/2017.
 */

public class EditProfileFragment extends BaseFragment<FragmentEditprofileBinding, EditProfileFragmentViewModel> implements EditProfileFragmentNavigator {

    private static final String TAG = EditProfileFragment.class.getSimpleName();

    @Inject
    @Named("EditProfileFragment")
    ViewModelProvider.Factory mViewModelFactory;

    private FragmentEditprofileBinding mFragmentEditprofileBinding;

    private EditProfileFragmentViewModel mEditProfileFragmentViewModel;

    private UserSettings mUserSettings;


    //EditProfile Fragment widgets
    private EditText mDisplayName, mUsername, mWebsite, mDescription, mEmail, mPhoneNumber;
    private TextView mChangeProfilePhoto;
    private CircleImageView mProfilePhoto;


    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_editprofile;
    }

    @Override
    public EditProfileFragmentViewModel getViewModel() {
        mEditProfileFragmentViewModel = ViewModelProviders.of(this, mViewModelFactory).get(EditProfileFragmentViewModel.class);
        return mEditProfileFragmentViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEditProfileFragmentViewModel.setNavigator(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentEditprofileBinding = getViewDataBinding();
        setUp();
    }

    private void setUp() {
        mProfilePhoto = getView().findViewById(R.id.profile_photo);
        mDisplayName = getView().findViewById(R.id.display_name);
        mUsername = getView().findViewById(R.id.username);
        mWebsite = getView().findViewById(R.id.website);
        mDescription = getView().findViewById(R.id.description);
        mEmail = getView().findViewById(R.id.email);
        mPhoneNumber = getView().findViewById(R.id.phoneNumber);
        mChangeProfilePhoto = getView().findViewById(R.id.changeProfilePhoto);

        mEditProfileFragmentViewModel.setupFirebaseAuth();

        //back arrow for navigating back to "ProfileActivity"
        ImageView backArrow = getView().findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to ProfileActivity");
                getActivity().finish();
            }
        });

        ImageView checkmark = getView().findViewById(R.id.saveChanges);
        checkmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to save changes.");
                saveProfileSettings();
            }
        });


    }


    /**
     * Retrieves the data contained in the widgets and submits it to the database
     * Before donig so it chekcs to make sure the username chosen is unqiue
     */
    private void saveProfileSettings() {
        final String displayName = mDisplayName.getText().toString();
        final String username = mUsername.getText().toString();
        final String website = mWebsite.getText().toString();
        final String description = mDescription.getText().toString();
        final String email = mEmail.getText().toString();
        final long phoneNumber = Long.parseLong(mPhoneNumber.getText().toString());


        //case1: if the user made a change to their username
        if (!mUserSettings.getUser().getUsername().equals(username)) {
            mEditProfileFragmentViewModel.checkIfUsernameExists(username);
        }
        //case2: if the user made a change to their email
        if (!mUserSettings.getUser().getEmail().equals(email)) {

            // step1) Reauthenticate
            //          -Confirm the password and email
            ConfirmPasswordDialog dialog = new ConfirmPasswordDialog();
            dialog.show(getFragmentManager(), getString(R.string.confirm_password_dialog));
            dialog.setTargetFragment(EditProfileFragment.this, 1);


            // step2) check if the email already is registered
            //          -'fetchProvidersForEmail(String email)'
            // step3) change the email
            //          -submit the new email to the database and authentication
        }

        /**
         * change the rest of the settings that do not require uniqueness
         */
        if (!mUserSettings.getSettings().getDisplay_name().equals(displayName)) {
            //update displayname
            mEditProfileFragmentViewModel.getFirebaseMethods().updateUserAccountSettings(displayName, null, null, 0);
        }
        if (!mUserSettings.getSettings().getWebsite().equals(website)) {
            //update website
            mEditProfileFragmentViewModel.getFirebaseMethods().updateUserAccountSettings(null, website, null, 0);
        }
        if (!mUserSettings.getSettings().getDescription().equals(description)) {
            //update description
            mEditProfileFragmentViewModel.getFirebaseMethods().updateUserAccountSettings(null, null, description, 0);
        }
        if (mUserSettings.getUser().getPhone_number() != phoneNumber) {
            //update phoneNumber
            mEditProfileFragmentViewModel.getFirebaseMethods().updateUserAccountSettings(null, null, null, phoneNumber);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mEditProfileFragmentViewModel.startFireBaseAuth();
    }

    @Override
    public void onStop() {
        super.onStop();
        mEditProfileFragmentViewModel.stopFireBaseAuth();
    }

    @Override
    public void showToast(int stringId) {
        Toast.makeText(getActivity().getApplicationContext(), getString(stringId), Toast.LENGTH_LONG).show();
    }


    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public String getStringValue(int id) {
        return getString(id);
    }

    @Override
    public void setProfileWidgets(DataSnapshot dataSnapshot) {


        mUserSettings = mEditProfileFragmentViewModel.getFirebaseMethods().getUserSettings(dataSnapshot);

        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + mUserSettings.toString());
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + mUserSettings.getUser().getEmail());
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + mUserSettings.getUser().getPhone_number());

        //User user = userSettings.getUser();
        UserAccountSettings settings = mUserSettings.getSettings();
        UniversalImageLoader.setImage(mEditProfileFragmentViewModel.getImageLoader(), settings.getProfile_photo(), mProfilePhoto, null, "");
        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mWebsite.setText(settings.getWebsite());
        mDescription.setText(settings.getDescription());
        mEmail.setText(mUserSettings.getUser().getEmail());
        mPhoneNumber.setText(String.valueOf(mUserSettings.getUser().getPhone_number()));

        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: changing profile photo");
                Intent intent = new Intent(getContext(), ShareActivity.class);
                intent.putExtra(getString(R.string.gallery_fragment), false);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
    }

}
