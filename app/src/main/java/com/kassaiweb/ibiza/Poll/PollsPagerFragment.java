package com.kassaiweb.ibiza.Poll;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kassaiweb.ibiza.MainActivity;
import com.kassaiweb.ibiza.R;

import java.util.ArrayList;

public class PollsPagerFragment extends Fragment {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ArrayList<Poll> polls = new ArrayList<>();
    private boolean firstInit = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        view.findViewById(R.id.progress).setVisibility(View.GONE);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        mViewPager = (ViewPager) getView().findViewById(R.id.container);
        mTabLayout = getView().findViewById(R.id.container_tabLayout);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference pollsRef = database.getReference("polls");

        pollsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (firstInit) {

                    polls.clear();

                    for (DataSnapshot pollDataSnapshot : dataSnapshot.getChildren()) {
                        polls.add(pollDataSnapshot.getValue(Poll.class));
                    }

                    if (polls.size() == 0) {
                        MainActivity activity = (MainActivity) getActivity();
                        activity.replaceFragment(new PollCreateFragment());
                    }

                    //getView().findViewById(R.id.progress).setVisibility(View.GONE);
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    firstInit = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("PollsPager", "Failed to read value.", error.toException());
            }
        });


    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment f = new PollFragment();
            Bundle args = new Bundle();
            args.putString("pollId", polls.get(polls.size() - 1 - position).getId());
            f.setArguments(args);
            return f;
        }

        @Override
        public int getCount() {
            return polls.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return (position + 1) + ".";
        }
    }
}
