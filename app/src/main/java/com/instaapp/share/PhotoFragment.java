package com.instaapp.share;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.cameraview.CameraView;
import com.instaapp.BaseFragment;
import com.instaapp.R;
import com.instaapp.profile.AccountSettingsActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by User on 5/28/2017.
 */

public class PhotoFragment extends BaseFragment {
    private static final String TAG = "PhotoFragment";

    //constant
    private static final int PHOTO_FRAGMENT_NUM = 1;
    private static final int GALLERY_FRAGMENT_NUM = 2;
    private static final int CAMERA_REQUEST_CODE = 5;

    private CameraView mCameraView;
    private int mCurrentFlash;

    private static final int[] FLASH_OPTIONS = {
            CameraView.FLASH_AUTO,
            CameraView.FLASH_OFF,
            CameraView.FLASH_ON,
    };

    private static final int[] FLASH_ICONS = {
            R.drawable.ic_flash_auto,
            R.drawable.ic_flash_off,
            R.drawable.ic_flash_on,
    };

    private static final int[] FLASH_TITLES = {
            R.string.flash_auto,
            R.string.flash_off,
            R.string.flash_on,
    };

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        Log.d(TAG, "onCreateView: started.");
        setHasOptionsMenu(true);
        mCameraView = view.findViewById(R.id.camera);
        if (mCameraView != null) {
            mCameraView.addCallback(mCallback);
        }
        FloatingActionButton fab = view.findViewById(R.id.take_picture);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mCameraView != null) {
                        mCameraView.takePicture();
                    }
                }
            });
        }
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivityComponent().getContext()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivityComponent().getContext()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCameraView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCameraView.stop();

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.camera_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.switch_flash:
                if (mCameraView != null) {
                    mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.length;
                    item.setTitle(FLASH_TITLES[mCurrentFlash]);
                    item.setIcon(FLASH_ICONS[mCurrentFlash]);
                    mCameraView.setFlash(FLASH_OPTIONS[mCurrentFlash]);
                }
                return true;
            case R.id.switch_camera:
                if (mCameraView != null) {
                    int facing = mCameraView.getFacing();
                    mCameraView.setFacing(facing == CameraView.FACING_FRONT ?
                            CameraView.FACING_BACK : CameraView.FACING_FRONT);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private boolean isRootTask() {
        return ((ShareActivity) getActivityComponent().getContext()).getTask() == 0;
    }

    private CameraView.Callback mCallback
            = new CameraView.Callback() {

        @Override
        public void onCameraOpened(CameraView cameraView) {
            Log.d(TAG, "onCameraOpened");
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
            Log.d(TAG, "onCameraClosed");
        }

        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {
            Log.d(TAG, "onPictureTaken " + data.length);

            Log.d(TAG, "onActivityResult: done taking a photo.");
            Log.d(TAG, "onActivityResult: attempting to navigate to final share screen.");
            File destination;
            try {

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "_";
                destination = new File(Environment.getExternalStorageDirectory(),
                        imageFileName);
                FileOutputStream fo;

                fo = new FileOutputStream(destination);
                fo.write(data);
                fo.close();

                if (isRootTask()) {
                    if (null != cameraView)
                        cameraView.stop();
                    try {
                        Log.d(TAG, "onActivityResult: received new bitmap from camera: " + destination.getAbsolutePath());
                        Intent intent = new Intent(getApplicationComponent().getContext(), NextActivity.class);
                        intent.putExtra(getString(R.string.selected_bitmap), destination.getAbsolutePath());
                        (getActivityComponent().getContext()).startActivity(intent);
                    } catch (NullPointerException e) {
                        Log.d(TAG, "onActivityResult: NullPointerException: " + e.getMessage());
                    }
                } else {
                    try {
                        Log.d(TAG, "onActivityResult: received new bitmap from camera: " + destination.getAbsolutePath());
                        Intent intent = new Intent(getApplicationComponent().getContext(), AccountSettingsActivity.class);
                        intent.putExtra(getString(R.string.selected_bitmap), destination.getAbsolutePath());
                        intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile_fragment));
                        startActivity(intent);
                    } catch (NullPointerException e) {
                        Log.d(TAG, "onActivityResult: NullPointerException: " + e.getMessage());
                    }
                }

                ((AppCompatActivity)getActivityComponent().getContext()).finish();

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "onPictureTaken: while creation error...");
                Toast.makeText(getContext(), "Unable to take picture. Please try again later", Toast.LENGTH_LONG).show();
            }
        }

    };


}

































