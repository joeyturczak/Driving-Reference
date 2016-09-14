package com.tophatcatsoftware.drivingreference.ui;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tophatcatsoftware.drivingreference.R;
import com.tophatcatsoftware.drivingreference.adapters.MainListAdapter;
import com.tophatcatsoftware.drivingreference.data.DrivingContract;
import com.tophatcatsoftware.drivingreference.models.RealmManual;
import com.tophatcatsoftware.drivingreference.models.Test;
import com.tophatcatsoftware.drivingreference.utils.LocationUtility;
import com.tophatcatsoftware.drivingreference.utils.TestUtility;
import com.tophatcatsoftware.drivingreferencelib.DrivingValues;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Copyright (C) 2016 Joey Turczak
 */
public class MainListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener {

//    public static final int LOADER_MANUAL = 1;
    public static final int LOADER_TEST = 2;

    private MainListAdapter mMainListAdapter;
    private RecyclerView mRecyclerView;

    private boolean mIsLargeLayout;

//    private Cursor mManualsCursor;
    private Cursor mTestsCursor;

    private List<Integer> mViewTypes;
    private List<Object> mData;

    private List<RealmManual> mManuals;

//    private Context mContext;

    private DatabaseReference mRef;

    private Realm mRealm;

    private ChildEventListener mManualListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public MainListFragment() {
        // Required empty public constructor
    }

    public static MainListFragment newInstance() {
        return new MainListFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        getLoaderManager().initLoader(LOADER_MANUAL, null, this);
        mManuals = new ArrayList<>();
        getLoaderManager().initLoader(LOADER_TEST, null, this);
    }

    /**
     * Restarts the loader if a different location was selected
     */
    public void onLocationChanged() {
//        mManualsCursor = null;
        mTestsCursor = null;
//        getLoaderManager().restartLoader(LOADER_MANUAL, null, this);
        getLoaderManager().restartLoader(LOADER_TEST, null, this);

        if(mIsLargeLayout) {
            ((MainActivity)getActivity()).removeDetailFragment();
        }

        updateWidget();
        updateFirebaseRef();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mIsLargeLayout) {
            mMainListAdapter.onSaveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);
        mRealm = Realm.getDefaultInstance();

        updateFirebaseRef();

//        mManualsCursor = null;
//        mTestsCursor = null;
//        getLoaderManager().restartLoader(LOADER_MANUAL, null, this);
//        getLoaderManager().restartLoader(LOADER_TEST, null, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.unregisterOnSharedPreferenceChangeListener(this);
        mRealm.close();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsLargeLayout = getResources().getBoolean(R.bool.large_layout);
//        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

        View rootView = inflater.inflate(R.layout.fragment_main_list, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_menu);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        View emptyView = rootView.findViewById(R.id.recyclerview_menu_empty);

        mMainListAdapter = new MainListAdapter(getActivity(), new MainListAdapter.MainListOnClickHandler() {
            @Override
            public void onClick(Object object, RecyclerView.ViewHolder viewHolder) {
                int fragmentId = -1;
                if(viewHolder instanceof MainListAdapter.ManualViewHolder) {
                    fragmentId = MainActivity.PDF_FRAGMENT;
                } else if(viewHolder instanceof MainListAdapter.TestViewHolder) {
                    fragmentId = MainActivity.TEST_FRAGMENT;
                } else if(viewHolder instanceof MainListAdapter.ReviewTestViewHolder) {
                    fragmentId = MainActivity.REVIEW_FRAGMENT;
                }
                ((OnItemSelectedListener) getActivity())
                        .onItemSelected(object, fragmentId,
                                viewHolder
                        );
            }
        }, emptyView, AbsListView.CHOICE_MODE_SINGLE);

        mRecyclerView.setAdapter(mMainListAdapter);

        if (savedInstanceState != null) {
            mMainListAdapter.onRestoreInstanceState(savedInstanceState);
        }

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

//        mManualsCursor = null;
//        mTestsCursor = null;

        switch (id) {
//            case LOADER_MANUAL:
//                return getManualsLoader();
            case LOADER_TEST:
                return getTestsLoader();
            default:
                return null;
        }

    }

//    /**
//     * Returns a CursorLoader for Manuals database selection
//     */
//    private CursorLoader getManualsLoader() {
//        String location = LocationUtility.getLocationConfig(getContext());
//
//        String sortOrder = DrivingContract.DrivingManualEntry.COLUMN_TYPE + getString(R.string.sort_order_asc);
//
//        //Show only the items from the selected location
//        String selection = DrivingContract.DrivingManualEntry.COLUMN_LOCATION + " = '" + location + "'";
//
//        return new CursorLoader(getActivity(),
//                DrivingContract.DrivingManualEntry.CONTENT_URI,
//                null,
//                selection,
//                null,
//                sortOrder);
//    }

    /**
     * Returns a CursorLoader for Tests database selection
     */
    private CursorLoader getTestsLoader() {
//        String location = LocationUtility.getLocationConfig(getContext());

        String sortOrder = DrivingContract.TestEntry.COLUMN_DATE + getString(R.string.sort_order_desc);

//        //Show only the items from the selected location
//        String selection = DrivingContract.DrivingManualEntry.COLUMN_LOCATION + " = '" + location + "'";

        return new CursorLoader(getActivity(),
                DrivingContract.TestEntry.CONTENT_URI,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()) {
//            case LOADER_MANUAL:
//                mManualsCursor = data;
//                break;
            case LOADER_TEST:
                mTestsCursor = data;
                break;
        }

//        if(mManualsCursor != null && mTestsCursor != null) {
            fillDataArrays();
//            mMainListAdapter.swapData(mViewTypes, mData);

            if ( data.getCount() == 0 ) {
                getActivity().supportStartPostponedEnterTransition();
            }
//        }
    }

    /**
     * Adds data from the cursors to the arrays that will be sent to the RecyclerView adapter
     */
    private void fillDataArrays() {

        mViewTypes = new ArrayList<>();
        mData = new ArrayList<>();

        // Add title
        mViewTypes.add(MainListAdapter.VIEW_TYPE_TITLE);
        mData.add(getString(R.string.title_practice_tests));

        List<Test> tests = new ArrayList<>();
        if(mTestsCursor != null && mTestsCursor.moveToFirst()) {
            do {
                tests.add(TestUtility.getTestFromCursor(mTestsCursor));
            } while (mTestsCursor.moveToNext());
        }

        List<Test> testsForReview = new ArrayList<>();

        if(tests.size() > 0) {
            // Check for unfinished tests
            List<Test> incompleteTests = new ArrayList<>();


            for(Test test : tests) {
                if(test.getCompleted() == Test.NOT_COMPLETED) {
                    incompleteTests.add(test);
                } else {
                    testsForReview.add(test);
                }
            }

            boolean inProgress = TestUtility.getTestInProgress(getContext());

            // Add test to continue if there is one
            if(inProgress) {
                Test incompleteTest;

                if (incompleteTests.size() > 0) {
                    incompleteTest = incompleteTests.get(0);
                    incompleteTests.remove(incompleteTest);
                    mViewTypes.add(MainListAdapter.VIEW_TYPE_CONTINUE_TEST);
                    mData.add(incompleteTest);
                }
            }

            // Delete any tests in the database that shouldn't be there
            for(Test test : incompleteTests) {
                TestUtility.removeTestFromDb(getContext(), test);
            }
        }

        // Add new tests
        // Add Driver Test
        mViewTypes.add(MainListAdapter.VIEW_TYPE_NEW_TEST);
        mData.add(TestUtility.createNewTest(getContext(), LocationUtility.getLocationConfig(getContext()),
                DrivingValues.Type.Driver.toString(), DrivingValues.Language.English.toString()));
        // Add Motorcycle Test
        mViewTypes.add(MainListAdapter.VIEW_TYPE_NEW_TEST);
        mData.add(TestUtility.createNewTest(getContext(), LocationUtility.getLocationConfig(getContext()),
                DrivingValues.Type.Motorcycle.toString(), DrivingValues.Language.English.toString()));
        // Add Commercial Test
        mViewTypes.add(MainListAdapter.VIEW_TYPE_NEW_TEST);
        mData.add(TestUtility.createNewTest(getContext(), LocationUtility.getLocationConfig(getContext()),
                DrivingValues.Type.Commercial.toString(), DrivingValues.Language.English.toString()));

        if(testsForReview.size() > 0) {
            // Add tests to review
            mViewTypes.add(MainListAdapter.VIEW_TYPE_REVIEW_TEST);
            mData.add(testsForReview);
        }

        // Add manuals
        if(mManuals.size() > 0) {
            Log.d("Manual", "Add");
            mViewTypes.add(MainListAdapter.VIEW_TYPE_TITLE);
            mData.add(getString(R.string.title_driving_manuals));
            for(RealmManual manual : mManuals) {
                Log.d("Manual", "Add2");
                mViewTypes.add(MainListAdapter.VIEW_TYPE_MANUAL);
                mData.add(manual);
            }
        }
//        if(mManualsCursor != null && mManualsCursor.moveToFirst()) {
//            mViewTypes.add(MainListAdapter.VIEW_TYPE_TITLE);
//            mData.add(getString(R.string.title_driving_manuals));
//            do {
//                Manual manual = ManualUtility.getManualFromCursor(mManualsCursor);
//
//                mViewTypes.add(MainListAdapter.VIEW_TYPE_MANUAL);
//                mData.add(manual);
//            } while(mManualsCursor.moveToNext());
//        }

        mMainListAdapter.swapData(mViewTypes, mData);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        mManualsCursor = null;
        mTestsCursor = null;
        mMainListAdapter.swapData(null, null);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getContext().getString(R.string.shared_preference_location_key))) {
            onLocationChanged();
        }
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
    public interface OnItemSelectedListener {
        void onItemSelected(Object object, int fragmentId, RecyclerView.ViewHolder viewHolder);
    }

    /**
     * Updates the widget with relevant information
     */
    private void updateWidget() {

        Context context = getContext();
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }

    private void updateFirebaseRef() {
        String location = LocationUtility.getLocationConfig(getContext());
        String language = "English";
        mRef = FirebaseDatabase.getInstance().getReference().child("manuals").child(location)
                .child(language);
        Log.d("update", location);

        mManuals = new ArrayList<>();

        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mManuals.add(dataSnapshot.getValue(RealmManual.class));
                Log.d("Add", "add");
                fillDataArrays();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
