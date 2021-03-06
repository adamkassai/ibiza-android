package com.kassaiweb.ibiza.Cost;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kassaiweb.ibiza.R;

public class CostPagerFragment extends Fragment {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        view.findViewById(R.id.progress).setVisibility(View.GONE);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        mViewPager = (ViewPager) view.findViewById(R.id.container);
        mTabLayout = view.findViewById(R.id.container_tabLayout);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment f;

            if (position == 0) {
                f = new CostListFragment();
            } else if (position == 1) {
                f = new DebtListFragment();
            } else if (position == 2) {
                f = new PaymentListFragment();
            } else {
                f = new CostListFragment();
            }

            return f;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Költségek";
                case 1:
                    return "Tartozások";
                case 2:
                    return "Fizetési előzmények";
                default:
                    return "Költségek";
            }
        }
    }


}
