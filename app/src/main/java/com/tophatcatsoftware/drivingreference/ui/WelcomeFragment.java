package com.tophatcatsoftware.drivingreference.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.tophatcatsoftware.drivingreference.R;
import com.tophatcatsoftware.drivingreference.adapters.LocationSpinnerAdapter;
import com.tophatcatsoftware.drivingreference.utils.LocationUtility;
import com.tophatcatsoftware.drivingreference.utils.Utility;

import java.util.Arrays;
import java.util.List;

public class WelcomeFragment extends Fragment implements View.OnClickListener {

    private OnWelcomeCompleted mListener;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    public static WelcomeFragment newInstance() {
        return new WelcomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_welcome, container, false);

        Spinner locationSpinner = (Spinner) rootView.findViewById(R.id.location_spinner);

        Button startButton = (Button) rootView.findViewById(R.id.next_button);
        startButton.setOnClickListener(this);

        List<String> locations = Arrays.asList(getResources().getStringArray(R.array.locations));

        LocationSpinnerAdapter locationSpinnerAdapter = new LocationSpinnerAdapter(getContext(),
                R.layout.location_spinner_item, R.id.location_title, locations, true);

        String location = LocationUtility.getLocationConfig(getContext());

        locationSpinner.setAdapter(locationSpinnerAdapter);
        locationSpinner.setSelection(Utility.getSpinnerValue(getContext(), location));
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String location = parent.getItemAtPosition(position).toString().replace(" ", "_");
                LocationUtility.setLocationConfig(parent.getContext(), location);
                Log.d("Selected", "Selected");
//                locationSpinner.requestFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWelcomeCompleted) {
            mListener = (OnWelcomeCompleted) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnWelcomeCompleted");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onWelcomeCompleted();
        }
    }

    public interface OnWelcomeCompleted {
        void onWelcomeCompleted();
    }
}
