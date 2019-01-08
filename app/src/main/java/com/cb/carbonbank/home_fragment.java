package com.cb.carbonbank;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class home_fragment extends Fragment {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Button btnView;
    private TextView content;

    public home_fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_home_fragment, container, false);
        mSectionsPagerAdapter = new home_fragment.SectionsPagerAdapter(getActivity().getSupportFragmentManager());
        mViewPager = (ViewPager) view.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        btnView = view.findViewById(R.id.viewDetails);
        return view;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }


    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.home_viewpage, null);
            //Textview and image view from fragment.xml
            TextView titleTv = (TextView) rootView.findViewById(R.id.titleTv);
            TextView content = (TextView) rootView.findViewById(R.id.content);
            TextView description = (TextView) rootView.findViewById(R.id.description);
            Button viewDetails = (Button) rootView.findViewById(R.id.viewDetails);
            SharedPreferences sharedPreferences;


            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                titleTv.setText(R.string.carboncredit);
                content.setText(HomeActivity.cc + " cc");
                description.setText(R.string.cc_description);
                viewDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), CarbonCreditActivity.class);
                        startActivity(intent);
                    }
                });
            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                titleTv.setText(R.string.carbontax);
                content.setText(String.format("RM %d", HomeActivity.ct));
                description.setText(R.string.ct_descriptoin);
                viewDetails.setText("Pay Tax");
                viewDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), CarbonTaxActivity.class);
                        startActivity(intent);

                    }
                });
            }

            return rootView;
        }
    }
}



