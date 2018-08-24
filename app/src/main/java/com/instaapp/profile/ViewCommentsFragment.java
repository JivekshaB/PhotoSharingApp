package com.instaapp.profile;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.instaapp.BR;
import com.instaapp.R;
import com.instaapp.adapter.CommentListAdapter;
import com.instaapp.base.BaseFragment;
import com.instaapp.databinding.FragmentViewCommentsBinding;
import com.instaapp.home.HomeActivity;
import com.instaapp.models.Comment;
import com.instaapp.models.Photo;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by User on 8/12/2017.
 */

public class ViewCommentsFragment extends BaseFragment<FragmentViewCommentsBinding, ViewCommentsFragmentViewModel> implements ViewCommentsFragmentNavigator {

    private static final String TAG = "ViewCommentsFragment";

    @Inject
    @Named("ViewCommentsFragment")
    ViewModelProvider.Factory mViewModelFactory;

    private FragmentViewCommentsBinding mFragmentViewCommentsBinding;

    private ViewCommentsFragmentViewModel mViewCommentsFragmentViewModel;

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_view_comments;
    }

    @Override
    public ViewCommentsFragmentViewModel getViewModel() {
        mViewCommentsFragmentViewModel = ViewModelProviders.of(this, mViewModelFactory).get(ViewCommentsFragmentViewModel.class);
        return mViewCommentsFragmentViewModel;
    }


    public ViewCommentsFragment() {
        setArguments(new Bundle());
    }

    //widgets
    private ImageView mBackArrow;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewCommentsFragmentViewModel.setNavigator(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentViewCommentsBinding = getViewDataBinding();
        setUp();
    }

    private void setUp() {
        mBackArrow = getView().findViewById(R.id.backArrow);
        try {
            mViewCommentsFragmentViewModel.setUpComments(getPhotoFromBundle());
        } catch (NullPointerException e) {
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage());
        }
    }

    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * retrieve the photo from the incoming bundle from profileActivity interface
     *
     * @return
     */

    @Override
    public String getCallingActivityFromBundle() {
        Log.d(TAG, "getPhotoFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getString(getString(R.string.home_activity));
        } else {
            return null;
        }
    }

    /**
     * retrieve the photo from the incoming bundle from profileActivity interface
     *
     * @return
     */
    @Override
    public Photo getPhotoFromBundle() {
        Log.d(TAG, "getPhotoFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getParcelable(getString(R.string.photo));
        } else {
            return null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public String getStringValue(int id) {
        return getString(id);
    }

    @Override
    public void setUpWidgets(ArrayList<Comment> comments) {
        CommentListAdapter adapter = new CommentListAdapter(getContext(),
                R.layout.layout_comment, comments);
        mFragmentViewCommentsBinding.listView.setAdapter(adapter);

        mFragmentViewCommentsBinding.ivPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = mFragmentViewCommentsBinding.comment.getText().toString();
                if (!comment.equals("")) {
                    Log.d(TAG, "onClick: attempting to submit new comment.");
                    mViewCommentsFragmentViewModel.addNewComment(comment);
                    mFragmentViewCommentsBinding.comment.setText("");
                    closeKeyboard();
                } else {
                    Toast.makeText(getActivity(), "you can't post a blank comment", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back");
                if (getCallingActivityFromBundle().equals(getString(R.string.home_activity))) {
                    getActivity().getSupportFragmentManager().popBackStack();
                    ((HomeActivity) getActivity()).showLayout();
                } else {
                    getActivity().getSupportFragmentManager().popBackStack();
                }

            }
        });
    }
}





















