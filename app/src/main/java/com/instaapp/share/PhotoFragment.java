package com.instaapp.share;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.cameraview.CameraView;
import com.instaapp.BR;
import com.instaapp.R;
import com.instaapp.base.BaseFragment;
import com.instaapp.databinding.FragmentPhotoBinding;
import com.instaapp.profile.AccountSettingsActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;


/**
 * Created by User on 5/28/2017.
 */

public class PhotoFragment extends BaseFragment<FragmentPhotoBinding, PhotoFragmentViewModel> implements PhotoFragmentNavigator {

    private static final String TAG = PhotoFragment.class.getSimpleName();

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

    @Inject
    @Named("PhotoFragment")
    ViewModelProvider.Factory mViewModelFactory;

    private FragmentPhotoBinding mFragmentPhotoBinding;

    private PhotoFragmentViewModel mPhotoFragmentViewModel;

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_photo;
    }

    @Override
    public PhotoFragmentViewModel getViewModel() {
        mPhotoFragmentViewModel = ViewModelProviders.of(this, mViewModelFactory).get(PhotoFragmentViewModel.class);
        return mPhotoFragmentViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoFragmentViewModel.setNavigator(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentPhotoBinding = getViewDataBinding();
        setUp();
    }


    private void setUp() {
        setHasOptionsMenu(true);
        if (null != mFragmentPhotoBinding.camera) {
            mFragmentPhotoBinding.camera.addCallback(mCallback);
        }
        if (mFragmentPhotoBinding.takePicture != null) {
            mFragmentPhotoBinding.takePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != mFragmentPhotoBinding.camera) {
                        mFragmentPhotoBinding.camera.takePicture();
                    }
                }
            });
        }
        getBaseActivity().setSupportActionBar(mFragmentPhotoBinding.toolbar);
        ActionBar actionBar = ((AppCompatActivity) getContext()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mFragmentPhotoBinding.camera.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mFragmentPhotoBinding.camera.stop();

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
                if (null != mFragmentPhotoBinding.camera) {
                    mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.length;
                    item.setTitle(FLASH_TITLES[mCurrentFlash]);
                    item.setIcon(FLASH_ICONS[mCurrentFlash]);
                    mFragmentPhotoBinding.camera.setFlash(FLASH_OPTIONS[mCurrentFlash]);
                }
                return true;
            case R.id.switch_camera:
                if (mFragmentPhotoBinding.camera != null) {
                    int facing = mFragmentPhotoBinding.camera.getFacing();
                    mFragmentPhotoBinding.camera.setFacing(facing == CameraView.FACING_FRONT ?
                            CameraView.FACING_BACK : CameraView.FACING_FRONT);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private boolean isRootTask() {
        return ((ShareActivity) getContext()).getTask() == 0;
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
                        Intent intent = new Intent(getContext(), NextActivity.class);
                        intent.putExtra(getString(R.string.selected_bitmap), destination.getAbsolutePath());
                        getContext().startActivity(intent);
                    } catch (NullPointerException e) {
                        Log.d(TAG, "onActivityResult: NullPointerException: " + e.getMessage());
                    }
                } else {
                    try {
                        Log.d(TAG, "onActivityResult: received new bitmap from camera: " + destination.getAbsolutePath());
                        Intent intent = new Intent(getContext(), AccountSettingsActivity.class);
                        intent.putExtra(getString(R.string.selected_bitmap), destination.getAbsolutePath());
                        intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile_fragment));
                        startActivity(intent);
                    } catch (NullPointerException e) {
                        Log.d(TAG, "onActivityResult: NullPointerException: " + e.getMessage());
                    }
                }

                ((AppCompatActivity) getContext()).finish();

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "onPictureTaken: while creation error...");
                Toast.makeText(getContext(), "Unable to take picture. Please try again later", Toast.LENGTH_LONG).show();
            }
        }

    };


}