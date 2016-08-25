package com.tophatcatsoftware.drivingreference.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tophatcatsoftware.drivingreference.R;
import com.tophatcatsoftware.drivingreference.adapters.ReviewDetailListAdapter;
import com.tophatcatsoftware.drivingreference.models.Test;


public class ReviewDetailFragment extends Fragment {

    private Test mTest;

    private RecyclerView mRecyclerView;
    private ReviewDetailListAdapter mReviewDetailListAdapter;

    private TextView mScoreView;

    public ReviewDetailFragment() {
        // Required empty public constructor
    }

    public static ReviewDetailFragment newInstance(Test test) {
        ReviewDetailFragment fragment = new ReviewDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("Test", test);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTest = getArguments().getParcelable("Test");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_review_detail, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_question_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        View emptyView = rootView.findViewById(R.id.recyclerview_question_list_empty);

        mReviewDetailListAdapter = new ReviewDetailListAdapter(getContext(), emptyView);

        mRecyclerView.setAdapter(mReviewDetailListAdapter);

        mReviewDetailListAdapter.swapData(mTest);

        mScoreView = (TextView) rootView.findViewById(R.id.score);

        mScoreView.setText("Score: " + mTest.getScore() + "/" + Test.QUESTION_MAX);

        return rootView;
    }
}
