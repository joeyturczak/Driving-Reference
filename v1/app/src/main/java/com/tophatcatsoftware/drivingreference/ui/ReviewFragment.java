package com.tophatcatsoftware.drivingreference.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tophatcatsoftware.drivingreference.R;
import com.tophatcatsoftware.drivingreference.adapters.ReviewListAdapter;
import com.tophatcatsoftware.drivingreference.models.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnTestSelectedListener} interface
 * to handle interaction events.
 * Use the {@link ReviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewFragment extends Fragment {

    private OnTestSelectedListener mListener;

    private RecyclerView mRecyclerView;

    private ReviewListAdapter mReviewListAdapter;

    private List<Test> mTests;

    public ReviewFragment() {
        // Required empty public constructor
    }

    public static ReviewFragment newInstance(ArrayList<Test> tests) {
        ReviewFragment fragment = new ReviewFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("Tests", tests);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTests = getArguments().getParcelableArrayList("Tests");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_review, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_review_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        View emptyView = rootView.findViewById(R.id.recyclerview_review_list_empty);

        mReviewListAdapter = new ReviewListAdapter(getContext(), new ReviewListAdapter.ReviewListOnClickHandler() {
            @Override
            public void onClick(Test test, RecyclerView.ViewHolder vh) {
                // Launch detail fragment
                onTestSelected(test);
            }
        }, emptyView);

        mRecyclerView.setAdapter(mReviewListAdapter);

        mReviewListAdapter.swapData(mTests);

        return rootView;
    }

    public void onTestSelected(Test test) {
        if (mListener != null) {
            mListener.onTestSelected(test);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTestSelectedListener) {
            mListener = (OnTestSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTestCompleteListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTestSelectedListener {
        void onTestSelected(Test test);
    }
}
