package com.instaapp.materialcamera;

import android.app.Fragment;
import android.support.annotation.NonNull;

import com.instaapp.materialcamera.internal.BaseCaptureActivity;
import com.instaapp.materialcamera.internal.CameraFragment;


public class CaptureActivity extends BaseCaptureActivity {

  @Override
  @NonNull
  public Fragment getFragment() {
    return CameraFragment.newInstance();
  }
}
