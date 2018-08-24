package com.instaapp.home;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.eschao.android.widget.elasticlistview.ElasticListView;
import com.eschao.android.widget.elasticlistview.LoadFooter;
import com.eschao.android.widget.elasticlistview.OnLoadListener;
import com.eschao.android.widget.elasticlistview.OnUpdateListener;
import com.instaapp.BR;
import com.instaapp.R;
import com.instaapp.adapter.MainFeedListAdapter;
import com.instaapp.base.BaseFragment;
import com.instaapp.databinding.FragmentHomeBinding;
import com.instaapp.models.Photo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by jiveksha on 8/7/18.
 */

public class HomeFragment extends BaseFragment<FragmentHomeBinding, HomeFragmentViewModel>
        implements HomeFragmentNavigator, OnUpdateListener, OnLoadListener {

    private static final String TAG = HomeFragment.class.getSimpleName();

    @Inject
    @Named("HomeFragment")
    ViewModelProvider.Factory mViewModelFactory;

    private FragmentHomeBinding mFragmentHomeBinding;
    private HomeFragmentViewModel mHomeFragmentViewModel;

    private ArrayList<Photo> mPaginatedPhotos;
    private ArrayList<Photo> mPhotos;

    private MainFeedListAdapter adapter;
    private int resultsCount = 0;

    @Override
    public void onUpdate() {
        Log.d(TAG, "ElasticListView: updating list view...");
        mHomeFragmentViewModel.getFollowing();
    }


    @Override
    public void onLoad() {
        Log.d(TAG, "ElasticListView: loading...");
        // Notify load is done
        mListView.notifyLoaded();
    }


    private ElasticListView mListView;


    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public HomeFragmentViewModel getViewModel() {
        mHomeFragmentViewModel = ViewModelProviders.of(this, mViewModelFactory).get(HomeFragmentViewModel.class);
        return mHomeFragmentViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHomeFragmentViewModel.setNavigator(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentHomeBinding = getViewDataBinding();
        setUp();
    }

    private void setUp() {
        mListView = mFragmentHomeBinding.listView;
        initListViewRefresh();
        mHomeFragmentViewModel.getFollowing();

    }

    private void initListViewRefresh() {
        mListView.setHorizontalFadingEdgeEnabled(true);
        mListView.setAdapter(adapter);
        mListView.enableLoadFooter(true)
                .getLoadFooter().setLoadAction(LoadFooter.LoadAction.RELEASE_TO_LOAD);
        mListView.setOnUpdateListener(this)
                .setOnLoadListener(this);
    }

    private void displayPhotos() {
        mPhotos = mHomeFragmentViewModel.getUserPhotos();
        if (mPhotos != null) {

            try {

                //sort for newest to oldest
                Collections.sort(mPhotos, new Comparator<Photo>() {
                    public int compare(Photo o1, Photo o2) {
                        return o2.getDate_created().compareTo(o1.getDate_created());
                    }
                });

                //we want to load 10 at a time. So if there is more than 10, just load 10 to start
                int iterations = mPhotos.size();
                if (iterations > 10) {
                    iterations = 10;
                }
//
                resultsCount = 0;
                for (int i = 0; i < iterations; i++) {
                    mPaginatedPhotos.add(mPhotos.get(i));
                    resultsCount++;
                    Log.d(TAG, "displayPhotos: adding a photo to paginated list: " + mPhotos.get(i).getPhoto_id());
                }


                adapter = new MainFeedListAdapter(getContext(), mHomeFragmentViewModel.getImageLoader(),R.layout.layout_mainfeed_listitem, mPaginatedPhotos);
                mListView.setAdapter(adapter);

                // Notify update is done
                mListView.notifyUpdated();

            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException:" + e.getMessage());
            } catch (NullPointerException e) {
                Log.e(TAG, "displayPhotos: NullPointerException:" + e.getMessage());
            }
        }
    }

    public void displayMorePhotos() {
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try {
            ArrayList<Photo> photos = mHomeFragmentViewModel.getUserPhotos();
            if (photos.size() > resultsCount && photos.size() > 0) {
                int iterations;
                if (photos.size() > (resultsCount + 10)) {
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 10;
                } else {
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = photos.size() - resultsCount;
                }
                //add the new photos to the paginated list
                for (int i = resultsCount; i < resultsCount + iterations; i++) {
                    mPaginatedPhotos.add(photos.get(i));
                }

                resultsCount = resultsCount + iterations;
                adapter.notifyDataSetChanged();
            }
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "displayPhotos: IndexOutOfBoundsException:" + e.getMessage());
        } catch (NullPointerException e) {
            Log.e(TAG, "displayPhotos: NullPointerException:" + e.getMessage());
        }
    }

    /**
     * Display photos from
     */
    @Override
    public void callDisplayPhotos() {
        displayPhotos();
    }

    @Override
    public String getStringValue(int id) {
        return getString(id);
    }

    /**
     * Clear all data from lists and adapter
     */
    @Override
    public void refreshUi() {
        if (adapter != null) {
            adapter.clear();
            adapter.notifyDataSetChanged();
        }
        if (mPaginatedPhotos != null) {
            mPaginatedPhotos.clear();
        }
        if (mPhotos != null) {
            mPhotos.clear();
        }
        mPhotos = new ArrayList<>();
        mPaginatedPhotos = new ArrayList<>();
    }
}
