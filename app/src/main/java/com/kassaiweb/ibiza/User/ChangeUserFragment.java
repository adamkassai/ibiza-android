package com.kassaiweb.ibiza.User;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kassaiweb.ibiza.Constant;
import com.kassaiweb.ibiza.MainActivity;
import com.kassaiweb.ibiza.Poll.Answer;
import com.kassaiweb.ibiza.Poll.Poll;
import com.kassaiweb.ibiza.Poll.PollOptionsAdapter;
import com.kassaiweb.ibiza.Poll.PollsPagerFragment;
import com.kassaiweb.ibiza.R;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class ChangeUserFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_change_user, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


        mRecyclerView = (RecyclerView) view.findViewById(R.id.poll_options_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ChangeUserAdapter((MainActivity)getActivity());
        mRecyclerView.setAdapter(mAdapter);



    }


}
