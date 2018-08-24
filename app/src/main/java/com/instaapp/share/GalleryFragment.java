package com.instaapp.share;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.instaapp.BR;
import com.instaapp.R;
import com.instaapp.adapter.GridImageAdapter;
import com.instaapp.base.BaseFragment;
import com.instaapp.databinding.FragmentGalleryBinding;
import com.instaapp.profile.AccountSettingsActivity;
import com.instaapp.utils.FilePaths;
import com.instaapp.utils.FileSearch;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;


/**
 * Created by User on 5/28/2017.
 */

public class GalleryFragment extends BaseFragment<FragmentGalleryBinding, GalleryFragmentViewModel> implements GalleryFragmentNavigator {
    private static final String TAG = GalleryFragment.class.getSimpleName();


    //constants
    private static final int NUM_GRID_COLUMNS = 3;

    //vars
    private ArrayList<String> directories;
    private String mAppend = "file:/";
    private String mSelectedImage;

    @Inject
    @Named("GalleryFragment")
    ViewModelProvider.Factory mViewModelFactory;

    private FragmentGalleryBinding mFragmentGalleryBinding;

    private GalleryFragmentViewModel mGalleryFragmentViewModel;

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_gallery;
    }

    @Override
    public GalleryFragmentViewModel getViewModel() {
        mGalleryFragmentViewModel = ViewModelProviders.of(this, mViewModelFactory).get(GalleryFragmentViewModel.class);
        return mGalleryFragmentViewModel;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGalleryFragmentViewModel.setNavigator(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentGalleryBinding = getViewDataBinding();
        setUp();
    }


    private void setUp() {
        directories = new ArrayList<>();
        mFragmentGalleryBinding.layoutGalleryTopBar.ivCloseShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the gallery fragment.");
                ((AppCompatActivity) getContext()).finish();
            }
        });


        mFragmentGalleryBinding.layoutGalleryTopBar.tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to the final share screen.");

                if (isRootTask()) {
                    Intent intent = new Intent(getContext(), NextActivity.class);
                    intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), AccountSettingsActivity.class);
                    intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                    intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile_fragment));
                    startActivity(intent);
                }
                ((AppCompatActivity) getContext()).finish();
            }
        });

        init();
    }

    private boolean isRootTask() {
        return ((ShareActivity) getContext()).getTask() == 0;
    }

    private void init() {
        FilePaths filePaths = new FilePaths();
        //check for other folders inside "/storage/emulated/0/pictures"
        directories.add(filePaths.CAMERA);
        if (FileSearch.getDirectoryPaths(filePaths.PICTURES) != null) {
            for (String picturesFilePath : FileSearch.getDirectoryPaths(filePaths.PICTURES)) {
                if (null != FileSearch.getFilePaths(picturesFilePath) && FileSearch.getFilePaths(picturesFilePath).size() > 0) {
                    directories.add(picturesFilePath);
                }
            }
        }


        ArrayList<String> directoryNames = new ArrayList<>();
        for (int i = 0; i < directories.size(); i++) {
            Log.d(TAG, "init: directory: " + directories.get(i));
            int index = directories.get(i).lastIndexOf("/");
            String string = directories.get(i).substring(index);
            directoryNames.add(string);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, directoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFragmentGalleryBinding.layoutGalleryTopBar.spinnerDirectory.setAdapter(adapter);

        mFragmentGalleryBinding.layoutGalleryTopBar.spinnerDirectory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected: " + directories.get(position));

                //setup our image grid for the directory chosen
                setupGridView(directories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void setupGridView(String selectedDirectory) {
        Log.d(TAG, "setupGridView: directory chosen: " + selectedDirectory);
        final ArrayList<String> imgURLs;
        if (selectedDirectory.contains("camera")) {
            imgURLs = (ArrayList<String>) FileSearch.getCameraImages(getContext());
        } else {
            imgURLs = FileSearch.getFilePaths(selectedDirectory);
        }

        if (null != imgURLs && imgURLs.size() > 0) {
            Collections.sort(imgURLs, Collections.<String>reverseOrder());
            //set the grid column width
            int gridWidth = getResources().getDisplayMetrics().widthPixels;
            int imageWidth = gridWidth / NUM_GRID_COLUMNS;
            mFragmentGalleryBinding.gridView.setColumnWidth(imageWidth);

            //use the grid adapter to adapter the images to gridview
            GridImageAdapter adapter = new GridImageAdapter(getContext(), R.layout.layout_grid_imageview, mAppend, imgURLs);
            mFragmentGalleryBinding.gridView.setAdapter(adapter);

            //set the first image to be displayed when the activity fragment view is inflated
            try {
                setImage(imgURLs.get(0), mFragmentGalleryBinding.galleryImageView, mAppend);
                mSelectedImage = imgURLs.get(0);
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e(TAG, "setupGridView: ArrayIndexOutOfBoundsException: " + e.getMessage());
            }

            mFragmentGalleryBinding.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "onItemClick: selected an image: " + imgURLs.get(position));

                    setImage(imgURLs.get(position), mFragmentGalleryBinding.galleryImageView, mAppend);
                    mSelectedImage = imgURLs.get(position);
                }
            });
        }

    }


    private void setImage(String imgURL, ImageView image, String append) {
        Log.d(TAG, "setImage: setting image");

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mFragmentGalleryBinding.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mFragmentGalleryBinding.progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mFragmentGalleryBinding.progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                mFragmentGalleryBinding.progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}































