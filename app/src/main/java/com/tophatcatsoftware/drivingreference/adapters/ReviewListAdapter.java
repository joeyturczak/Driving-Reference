package com.tophatcatsoftware.drivingreference.adapters;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tophatcatsoftware.drivingreference.R;
import com.tophatcatsoftware.drivingreference.models.Test;
import com.tophatcatsoftware.drivingreference.utils.TestUtility;
import com.tophatcatsoftware.drivingreference.utils.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joeyturczak on 4/27/16.
 */
public class ReviewListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ReviewListOnClickHandler mReviewListOnClickHandler;
    private View mEmptyView;

    private List<Test> mTests;

    public ReviewListAdapter(Context context, ReviewListOnClickHandler reviewListOnClickHandler, View emptyView) {

        mTests = new ArrayList<>();

        mContext = context;
        mReviewListOnClickHandler = reviewListOnClickHandler;
        mEmptyView = emptyView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(parent instanceof RecyclerView) {
            int layoutId = R.layout.review_list_item;
            return new ReviewListViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
        } else {
            throw new RuntimeException(mContext.getString(R.string.recycler_view_error));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Test test = mTests.get(position);

        String type = test.getType();
        String displayName = TestUtility.getReviewTestDisplayName(test);
        String score = TestUtility.getScoreText(test);

        // Get icon resource ID
        int imageId = Utility.getResourceIconId(type);

        // Set the views

        ReviewListViewHolder reviewListViewHolder = (ReviewListViewHolder) holder;

        reviewListViewHolder.mReviewIconView.setImageResource(imageId);

        // this enables better animations. even if we lose state due to a device rotation,
        // the animator can use this to re-find the original view
        ViewCompat.setTransitionName(reviewListViewHolder.mReviewIconView, mContext.getString(R.string.transition_list_icon) + position);

        reviewListViewHolder.mReviewNameView.setText(displayName);

        reviewListViewHolder.mScoreView.setText(score);
    }

    @Override
    public int getItemCount() {
        return mTests.size();
    }

    public interface ReviewListOnClickHandler {
        void onClick(Test test, RecyclerView.ViewHolder vh);
    }

    public class ReviewListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mReviewIconView;
        public final TextView mReviewNameView;
        public final TextView mScoreView;

        public ReviewListViewHolder(View view) {
            super(view);
            mReviewIconView = (ImageView) view.findViewById(R.id.review_icon);
            mReviewNameView = (TextView) view.findViewById(R.id.review_name);
            mScoreView = (TextView) view.findViewById(R.id.score);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int adapterPosition = getAdapterPosition();

            Test test = mTests.get(adapterPosition);

            mReviewListOnClickHandler.onClick(test, this);
        }
    }

    public void swapData(List<Test> tests) {
        mTests = tests;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

}
