package com.kassaiweb.ibiza.Cost;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kassaiweb.ibiza.Constant;
import com.kassaiweb.ibiza.MainActivity;
import com.kassaiweb.ibiza.R;
import com.kassaiweb.ibiza.Task.TaskAdapter;
import com.kassaiweb.ibiza.Task.TaskCreateFragment;

import static android.content.Context.MODE_PRIVATE;

public class CostListFragment extends Fragment {

    private String userId;

    private RecyclerView mRecyclerView;
    private CostAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_cost_list, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {

        mRecyclerView = view.findViewById(R.id.cost_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        SharedPreferences prefs = getActivity().getSharedPreferences(Constant.APP_NAME, MODE_PRIVATE);
        userId = prefs.getString(Constant.USERID, null);

        mAdapter = new CostAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        view.findViewById(R.id.cost_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MainActivity activity = (MainActivity) getActivity();
                activity.replaceFragment(new CostCreateFragment());

            }
        });

    }

}