package com.tophatcatsoftware.drivingreference.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tophatcatsoftware.drivingreference.R;
import com.tophatcatsoftware.drivingreference.adapters.LocationSpinnerAdapter;
import com.tophatcatsoftware.drivingreference.adapters.MainListAdapter;
import com.tophatcatsoftware.drivingreference.models.Manual;
import com.tophatcatsoftware.drivingreference.models.RealmManual;
import com.tophatcatsoftware.drivingreference.models.Test;
import com.tophatcatsoftware.drivingreference.utils.LocationUtility;
import com.tophatcatsoftware.drivingreference.utils.TestUtility;
import com.tophatcatsoftware.drivingreference.utils.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Copyright (C) 2016 Joey Turczak
 */
public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener,
        MainListFragment.OnItemSelectedListener, TestFragment.OnTestCompleteListener,
        ReviewFragment.OnTestSelectedListener, WelcomeFragment.OnWelcomeCompleted, FirebaseAuth.AuthStateListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String MENU_FRAGMENT_TAG = "MF";
    private static final String PDF_FRAGMENT_TAG = "PF";
    private static final String TEST_FRAGMENT_TAG = "TF";
    private static final String REVIEW_FRAGMENT_TAG = "RF";
    private static final String REVIEW_DETAIL_FRAGMENT_TAG = "RDF";
    private static final String WELCOME_FRAGMENT_TAG = "WF";
    private static final String LOGO_FRAGMENT_TAG = "LF";

    public static final int PDF_FRAGMENT = 0;
    public static final int TEST_FRAGMENT = 1;
    public static final int REVIEW_FRAGMENT = 2;
    public static final int REVIEW_DETAIL_FRAGMENT = 3;


    private boolean mIsLargeLayout;

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    private TextView mToolbarTitle;

    private LocationSpinnerAdapter mLocationSpinnerAdapter;

    private Spinner mLocationSpinner;

    private int mLastPercent = 100;

    private String mTitle;

    private boolean mIsDbEmpty;

    private boolean mBackPressed = false;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mRef;

    private Realm mRealm;

    private ChildEventListener mManualListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            RealmManual manual = dataSnapshot.getValue(RealmManual.class);
            manual.setId(dataSnapshot.getKey());
            addToRealm(manual);
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

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAppBar();
        setupFirebase();
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeFirebaseListener();
        mRealm.close();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.main_activity_title_key), mTitle);
        outState.putBundle(getString(R.string.bundle_main_activity_state_key), bundle);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null) {
            Bundle bundle = savedInstanceState.getBundle(getString(R.string.bundle_main_activity_state_key));
            if(bundle != null) {
                mTitle = bundle.getString(getString(R.string.main_activity_title_key));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuth.signInAnonymously();

        mIsLargeLayout = getResources().getBoolean(R.bool.large_layout);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);

        ViewCompat.setTransitionName(mAppBarLayout, getString(R.string.transition_main_activity_image));
        supportPostponeEnterTransition();

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        mLocationSpinner = (Spinner) findViewById(R.id.location_spinner);

        displayUpButton();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("welcome", false);
        editor.apply();

//        initializeSpinner();
//
//        initializeAppBar();
//
//        mAdView = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder()
//                .build();
//        mAdView.loadAd(adRequest);
//
//        initInterstitial();

        if(savedInstanceState == null) {
            loadContent();
        }

        // Recieve intent from widget and load appropriate content
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            Manual manual = bundle.getParcelable(getString(R.string.widget_manual_key));
            if (manual != null) {
                onItemSelected(manual, PDF_FRAGMENT, null);
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            getSupportFragmentManager().popBackStack();
            return true;
        }

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mBackPressed = true;
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onBackStackChanged() {
        displayUpButton();
        updateAppBar();
    }

    /**
     * Displays up button if there's a fragment backStack
     */
    public void displayUpButton() {
        boolean back = getSupportFragmentManager().getBackStackEntryCount() > 0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(back);
    }

    @Override
    public void onItemSelected(Object object, int fragmentId, RecyclerView.ViewHolder viewHolder) {
        mBackPressed = false;

        switch (fragmentId) {
            case PDF_FRAGMENT:
                Manual manual = (Manual) object;
                mTitle = manual.getDisplayName();
                PDFFragment pdfFragment = PDFFragment.newInstance();

                Bundle bundle = new Bundle();
                bundle.putParcelable(getString(R.string.pdf_fragment_manual_key), manual);

                pdfFragment.setArguments(bundle);

                if (mIsLargeLayout) {
                    getSupportFragmentManager().beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.detail_container, pdfFragment, PDF_FRAGMENT_TAG)
                            .commit();
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.main_container, pdfFragment, PDF_FRAGMENT_TAG)
                            .addToBackStack(null)
                            .commit();
                }
                break;
            case TEST_FRAGMENT:
                final Test test = (Test) object;

                boolean newTest = false;
                boolean inProgress = false;

                if(viewHolder.getItemViewType() == MainListAdapter.VIEW_TYPE_NEW_TEST) {
                    newTest = true;

                }
                if(TestUtility.getTestInProgress(this)) {
                    inProgress = true;
                }

                if(newTest && inProgress) {
                    AlertDialog alertDialog = new AlertDialog.Builder(this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Practice Test in Progress")
                            .setMessage("Starting a new test will delete all progress on your unfinished test.")
                            .setPositiveButton("Start New Test", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startTestFragment(test);
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .create();

                    alertDialog.show();
                } else {
                    startTestFragment(test);
                }

                break;
            case REVIEW_FRAGMENT:
                ArrayList<Test> tests = new ArrayList<>();
                List<?> objects = (List<?>) object;
                for (Object thisObject : objects) {
                    if(thisObject instanceof Test) {
                        tests.add((Test) thisObject);
                    }
                }

                mTitle = "Review Practice Tests";
                ReviewFragment reviewFragment = ReviewFragment.newInstance(tests);

                if (mIsLargeLayout) {
                    getSupportFragmentManager().beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.detail_container, reviewFragment, REVIEW_FRAGMENT_TAG)
                            .commit();
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_container, reviewFragment, REVIEW_FRAGMENT_TAG)
                            .addToBackStack(null)
                            .commit();
                }
                break;
        }
    }

    /**
     * Sets up the spinner list and item selected listener
     */
    private void initializeSpinner() {

        String location = LocationUtility.getLocationConfig(this);

        mLocationSpinner = (Spinner) findViewById(R.id.location_spinner);

        List<String> locations = Arrays.asList(getResources().getStringArray(R.array.locations));

        mLocationSpinnerAdapter = new LocationSpinnerAdapter(this, R.layout.location_spinner_item, R.id.location_title, locations, mIsLargeLayout);

        mLocationSpinner.setAdapter(mLocationSpinnerAdapter);
        mLocationSpinner.setSelection(Utility.getSpinnerValue(this, location));
        mLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String location = parent.getItemAtPosition(position).toString().replace(" ", "_");
                LocationUtility.setLocationConfig(parent.getContext(), location);
                mLocationSpinner.requestFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mLocationSpinner.requestFocus();
    }

    /**
     * Sets up AppBarLayout for each orientation
     */
    public void initializeAppBar() {
        if (getResources().getBoolean(R.bool.landscape)) {
            mAppBarLayout.setFitsSystemWindows(false);
        } else {
            if (mAppBarLayout != null) {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    mAppBarLayout.setExpanded(false);
                }

                mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                        int scrollRange = appBarLayout.getTotalScrollRange();

                        // 0 doesn't like to be divided
                        if (scrollRange > 0) {
                            int percent = (verticalOffset + scrollRange) * 100 / scrollRange;
                            if (mLastPercent >= 20 && percent <= 20 || mLastPercent <= 20 && percent >= 20) {
                                if (percent < 20) {
                                    mLocationSpinnerAdapter.setViewType(LocationSpinnerAdapter.VIEW_TYPE_SMALL);
                                } else {
                                    mLocationSpinnerAdapter.setViewType(LocationSpinnerAdapter.VIEW_TYPE_LARGE);
                                }
                            }

                            mLastPercent = percent;
                        }
                    }
                });
            }
            ImageView imageView = (ImageView) findViewById(R.id.toolbar_image);
            loadImage(imageView);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getCacheDir().delete();
    }

    /**
     * Sets the title to the appropriate toolbar depending on orientation
     */
    private void setToolbarTitle(String title) {

        if (getResources().getBoolean(R.bool.landscape)) {
            setTitle(title);
        } else {
            if(!mIsLargeLayout) {
                mToolbarTitle.setText(title);
            }
            mCollapsingToolbarLayout.setTitle(title);
        }
    }

    private void showTitle(boolean show) {
        if(!mIsLargeLayout) {
            if (show) {
                mToolbarTitle.setVisibility(View.VISIBLE);
            } else {
                mToolbarTitle.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Shows or hides spinner
     */
    private void showSpinner(boolean show) {
        if(show) {
            mLocationSpinner.setVisibility(View.VISIBLE);
        } else {
            mLocationSpinner.setVisibility(View.GONE);
        }
    }

    /**
     * Shows the appropriate Toolbar based on the current fragment
     */
    private void updateAppBar() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            lockAppBarClosed();
            setToolbarTitle(mTitle);
            showSpinner(false);
        } else {
            unlockAppBarOpen();
            setToolbarTitle(getString(R.string.app_name));
            showSpinner(true);
        }
    }

    /**
     * Loads a random image into imageView with Glide
     */
    private void loadImage(ImageView imageView) {
        int randomNumber = Utility.getRandomNumber(1, 13, true);
        String resourceName = "background_" + String.valueOf(randomNumber);
        int id = getResources().getIdentifier(resourceName, getString(R.string.drawable_identifier), getString(R.string.package_name));
        Glide.with(this).load(id).fitCenter().into(imageView);
    }

    private void addMainListFragment() {

        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.main_container, MainListFragment.newInstance(), MENU_FRAGMENT_TAG)
                .commit();
    }

    private void addDetailFragment() {
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.detail_container, new Fragment())
                .commit();
    }

    public void removeDetailFragment() {
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .replace(R.id.detail_container, new Fragment())
                .commit();
    }

    private void loadContent() {

//        mIsDbEmpty = Utility.isDbEmpty(this, DrivingContract.DrivingManualEntry.CONTENT_URI);
//        if(mIsDbEmpty) {
//            checkNetworkAndUpdate();
//        } else {
//            updateData();
//        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean welcome = sharedPreferences.getBoolean("welcome", false);
        if(welcome) {
            initializeSpinner();

            initializeAppBar();

            mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            mAdView.loadAd(adRequest);

            initInterstitial();

            addFragments();
        } else {
            startWelcomeFragment();
        }
    }

//    /**
//     * Starts the intent service to check if there is new data
//     */
//    private void updateData() {
//        Intent intent = new Intent(this, UpdateDataIntentService.class);
//        intent.setAction(UpdateDataIntentService.ACTION_UPDATE_MANUALS);
//        intent.putExtra(getString(R.string.database_empty_intent_key), mIsDbEmpty);
//        startService(intent);
//    }
//
//    private void checkNetworkAndUpdate() {
//        if(Utility.isNetworkAvailable(this)) {
//            updateData();
//        } else {
//            showNetworkError();
//        }
//    }

//    private void showNetworkError() {
//        AlertDialog alertDialog = new AlertDialog.Builder(this)
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .setTitle("No Network Connection")
//                .setMessage("This app requires a content download for some features to be enabled. " +
//                        "Please connect to the Internet and restart the app for full functionality")
//                .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        checkNetworkAndUpdate();
//                    }
//                })
//                .setPositiveButton("OK", null)
//                .setCancelable(false)
//                .create();
//
//        alertDialog.show();
//    }

    private void addFragments() {
        addMainListFragment();

        if(mIsLargeLayout) {
            addDetailFragment();
        }
    }

    @Override
    public void onTestComplete(boolean complete, Test test) {
        // Remove test fragment
        // If test is complete, launch review fragment
        if(!mBackPressed && !complete) {
            return;
        }

        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        if(complete) {
            // Launch ReviewFragment
            showInterstitial();
            startReviewDetailFragment(test);
        }
    }

    private void lockAppBarClosed() {
        if(!getResources().getBoolean(R.bool.landscape)) {
            mAppBarLayout.setExpanded(false, true);
            mAppBarLayout.setActivated(false);
            showTitle(true);
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
            layoutParams.height = (int) getResources().getDimension(R.dimen.toolbar_height);
        }
    }

    private void unlockAppBarOpen() {
        if(!getResources().getBoolean(R.bool.landscape)) {
            mAppBarLayout.setExpanded(true, true);
            mAppBarLayout.setActivated(true);
            showTitle(false);
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
            layoutParams.height = (int) getResources().getDimension(R.dimen.toolbar_expanded_height);
        }
    }

    public void initInterstitial() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                loadInterstitial();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                loadInterstitial();
            }
        });

        loadInterstitial();
    }

    public void loadInterstitial() {

        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

    public void showInterstitial() {
        if(mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    @Override
    public void onTestSelected(Test test) {
        startReviewDetailFragment(test);
    }

    private void startReviewDetailFragment(Test test) {
        mTitle = TestUtility.getNewTestDisplayName(test);
        ReviewDetailFragment reviewDetailFragment = ReviewDetailFragment.newInstance(test);

        if (mIsLargeLayout) {
            getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.detail_container, reviewDetailFragment, REVIEW_DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, reviewDetailFragment, REVIEW_DETAIL_FRAGMENT_TAG)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void startTestFragment(Test test) {
        mTitle = TestUtility.getNewTestDisplayName(test);
        TestFragment testFragment = TestFragment.newInstance(test);

        if (mIsLargeLayout) {
            getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.detail_container, testFragment, TEST_FRAGMENT_TAG)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, testFragment, TEST_FRAGMENT_TAG)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void startWelcomeFragment() {
        WelcomeFragment welcomeFragment = WelcomeFragment.newInstance();

        if(mIsLargeLayout) {
            getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.detail_container, welcomeFragment, WELCOME_FRAGMENT_TAG)
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.main_container, LogoFragment.newInstance(), LOGO_FRAGMENT_TAG)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, welcomeFragment, WELCOME_FRAGMENT_TAG)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onWelcomeCompleted() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("welcome", true);
        editor.apply();

//        removeFirebaseListener();
//        setupFirebase();

        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//        addFragments();
        loadContent();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

    }

    private void addToRealm(final RealmManual manual) {
        RealmQuery<RealmManual> query = mRealm.where(RealmManual.class);
        query.equalTo("id", manual.getId());

        RealmResults<RealmManual> results = query.findAll();
        if(results.size() > 0) {
            // EXISTS
        } else {
            // ADD TO REALM
            mRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    manual.setDownloaded(false);
                    manual.setLastPage(0);
                    realm.copyToRealm(manual);
                }
            });
        }
    }

    private void setupFirebase() {
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String location = LocationUtility.getLocationConfig(this);
        String language = "English";
        mRef = FirebaseDatabase.getInstance().getReference().child("manuals").child(location).child(language);
        mRef.addChildEventListener(mManualListener);
    }

    private void removeFirebaseListener() {
        mRef.removeEventListener(mManualListener);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.shared_preference_location_key))) {
            removeFirebaseListener();
            setupFirebase();
        }
    }
}
