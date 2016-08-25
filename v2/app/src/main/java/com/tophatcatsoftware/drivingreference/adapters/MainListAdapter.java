package com.tophatcatsoftware.drivingreference.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tophatcatsoftware.drivingreference.R;
import com.tophatcatsoftware.drivingreference.models.Manual;
import com.tophatcatsoftware.drivingreference.models.Test;
import com.tophatcatsoftware.drivingreference.utils.ItemChoiceManager;
import com.tophatcatsoftware.drivingreference.utils.TestUtility;
import com.tophatcatsoftware.drivingreference.utils.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016 Joey Turczak
 *
 *
 * Takes in data for different viewTypes and displays them with their respective layouts.
 */
public class MainListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_TITLE = 0;
    public static final int VIEW_TYPE_MANUAL = 1;
    public static final int VIEW_TYPE_NEW_TEST = 2;
    public static final int VIEW_TYPE_CONTINUE_TEST = 3;
    public static final int VIEW_TYPE_REVIEW_TEST = 4;

    private List<Integer> mViewTypes;
    private List<Object> mData;

    private boolean mIsLargeLayout;

    final private Context mContext;
    final private MainListOnClickHandler mClickHandler;
    final private View mEmptyView;
    final private ItemChoiceManager mItemChoiceManager;

    public MainListAdapter(Context context, MainListOnClickHandler mainListOnClickHandler, View emptyView, int choiceMode) {

        mViewTypes = new ArrayList<>();

        mIsLargeLayout = context.getResources().getBoolean(R.bool.large_layout);

        mContext = context;
        mClickHandler = mainListOnClickHandler;
        mEmptyView = emptyView;
        mItemChoiceManager = new ItemChoiceManager(this);
        mItemChoiceManager.setChoiceMode(choiceMode);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (parent instanceof RecyclerView) {
            int layoutId = -1;
            switch (viewType) {
                case VIEW_TYPE_TITLE: {
                    layoutId = R.layout.title_list_item;
                    return new TitleViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
                }
                case VIEW_TYPE_MANUAL: {
                    layoutId = R.layout.manual_list_item;
                    view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
                    view.setFocusable(true);
                    return new ManualViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
                }
                case VIEW_TYPE_CONTINUE_TEST:
                case VIEW_TYPE_NEW_TEST: {
                    layoutId = R.layout.test_list_item;
                    view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
                    view.setFocusable(true);
                    return new TestViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
                }
                case VIEW_TYPE_REVIEW_TEST: {
                    layoutId = R.layout.test_list_item;
                    view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
                    view.setFocusable(true);
                    return new ReviewTestViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
                }
                default:
                    return new TitleViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
            }
        } else {
            throw new RuntimeException(mContext.getString(R.string.recycler_view_error));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_TITLE:
                bindTitleView(holder, position);
                break;
            case VIEW_TYPE_MANUAL:
                bindManualView(holder, position);
                break;
            case VIEW_TYPE_CONTINUE_TEST:
                bindContinueTestView(holder, position);
                break;
            case VIEW_TYPE_NEW_TEST:
                bindNewTestView(holder, position);
                break;
            case VIEW_TYPE_REVIEW_TEST:
                bindReviewTestView(holder, position);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mViewTypes.get(position);
    }

    @Override
    public int getItemCount() {
        if(mData == null) {
            return 0;
        }
        return mData.size();
    }

    private void bindTitleView(RecyclerView.ViewHolder viewHolder, int position) {

        TitleViewHolder titleViewHolder = (TitleViewHolder) viewHolder;

        titleViewHolder.mTitleView.setText((String) mData.get(position));

        if(mIsLargeLayout) {
            mItemChoiceManager.onBindViewHolder(titleViewHolder, position);
        }
    }

    private void bindManualView(RecyclerView.ViewHolder viewHolder, int position) {

        Manual manual = (Manual) mData.get(position);

        String type = manual.getType();
        String displayName = manual.getDisplayName();

        // Get icon resource ID
        int imageId = Utility.getResourceIconId(type);

        // Set the views

        ManualViewHolder manualViewHolder = (ManualViewHolder) viewHolder;

        manualViewHolder.mManualIconView.setImageResource(imageId);

        // this enables better animations. even if we lose state due to a device rotation,
        // the animator can use this to re-find the original view
        ViewCompat.setTransitionName(manualViewHolder.mManualIconView, mContext.getString(R.string.transition_list_icon) + position);

        manualViewHolder.mManualNameView.setText(displayName);

        if(mIsLargeLayout) {
            mItemChoiceManager.onBindViewHolder(manualViewHolder, position);
        }
    }

    private void bindNewTestView(RecyclerView.ViewHolder viewHolder, int position) {

        Test test = (Test) mData.get(position);

        String type = test.getType();
        String displayName = TestUtility.getNewTestDisplayName(test);

        int imageId = Utility.getResourceIconId(type);

        TestViewHolder testViewHolder = (TestViewHolder) viewHolder;

        testViewHolder.mTestIconView.setImageResource(imageId);

        ViewCompat.setTransitionName(testViewHolder.mTestIconView, mContext.getString(R.string.transition_list_icon) + position);

        testViewHolder.mTestNameView.setText(displayName);

        if(mIsLargeLayout) {
            mItemChoiceManager.onBindViewHolder(testViewHolder, position);
        }
    }

    private void bindContinueTestView(RecyclerView.ViewHolder viewHolder, int position) {

        String displayName = "Resume Incomplete Practice Test";

        TestViewHolder testViewHolder = (TestViewHolder) viewHolder;

        testViewHolder.mTestIconView.setImageResource(R.drawable.ic_assignment_late_black_24dp);

        ViewCompat.setTransitionName(testViewHolder.mTestIconView, mContext.getString(R.string.transition_list_icon) + position);

        testViewHolder.mTestNameView.setText(displayName);

        if(mIsLargeLayout) {
            mItemChoiceManager.onBindViewHolder(testViewHolder, position);
        }
    }

    private void bindReviewTestView(RecyclerView.ViewHolder viewHolder, int position) {

        String displayName = "Review Completed Practice Tests";

        ReviewTestViewHolder reviewTestViewHolder = (ReviewTestViewHolder) viewHolder;

        reviewTestViewHolder.mTestIconView.setImageResource(R.drawable.ic_assignment_black_24dp);

        ViewCompat.setTransitionName(reviewTestViewHolder.mTestIconView, mContext.getString(R.string.transition_list_icon) + position);

        reviewTestViewHolder.mTestNameView.setText(displayName);

        if(mIsLargeLayout) {
            mItemChoiceManager.onBindViewHolder(reviewTestViewHolder, position);
        }
    }

    public class ManualViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mManualIconView;
        public final TextView mManualNameView;

        public ManualViewHolder(View view) {
            super(view);
            mManualIconView = (ImageView) view.findViewById(R.id.manual_icon);
            mManualNameView = (TextView) view.findViewById(R.id.manual_name);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int adapterPosition = getAdapterPosition();

            Manual manual = (Manual) mData.get(adapterPosition);

            mClickHandler.onClick(manual, this);
            if(mIsLargeLayout) {
                mItemChoiceManager.onClick(this);
            }
        }
    }

    public class TestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mTestIconView;
        public final TextView mTestNameView;

        public TestViewHolder(View view) {
            super(view);
            mTestIconView = (ImageView) view.findViewById(R.id.test_icon);
            mTestNameView = (TextView) view.findViewById(R.id.test_name);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int adapterPosition = getAdapterPosition();

            Test test = (Test) mData.get(adapterPosition);

            mClickHandler.onClick(test, this);
            if(mIsLargeLayout) {
                mItemChoiceManager.onClick(this);
            }
        }
    }

    public class ReviewTestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mTestIconView;
        public final TextView mTestNameView;

        public ReviewTestViewHolder(View view) {
            super(view);
            mTestIconView = (ImageView) view.findViewById(R.id.test_icon);
            mTestNameView = (TextView) view.findViewById(R.id.test_name);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int adapterPosition = getAdapterPosition();


            List<Test> tests = new ArrayList<>();
            List<?> data = (List<?>) mData.get(adapterPosition);
            for(Object object : data) {
                if(object instanceof Test) {
                    tests.add((Test) object);
                }
            }

            mClickHandler.onClick(tests, this);
            if(mIsLargeLayout) {
                mItemChoiceManager.onClick(this);
            }
        }
    }

    public class TitleViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitleView;

        public TitleViewHolder(View view) {
            super(view);
            mTitleView = (TextView) view.findViewById(R.id.list_title);
        }
    }

    public interface MainListOnClickHandler {
        void onClick(Object object, RecyclerView.ViewHolder vh);
    }

    public void swapData(List<Integer> viewTypes, List<Object> data) {
        mViewTypes = viewTypes;
        mData = data;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mItemChoiceManager.onRestoreInstanceState(savedInstanceState);
    }

    public void onSaveInstanceState(Bundle outState) {
        mItemChoiceManager.onSaveInstanceState(outState);
    }

    public int getSelectedItemPosition() {
        return mItemChoiceManager.getSelectedItemPosition();
    }

    public void selectView(RecyclerView.ViewHolder viewHolder) {
        if ( viewHolder instanceof ManualViewHolder) {
            ManualViewHolder holder = (ManualViewHolder) viewHolder;
            holder.onClick(holder.itemView);
        }
    }
}
