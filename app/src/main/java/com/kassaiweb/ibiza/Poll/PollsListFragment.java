package com.kassaiweb.ibiza.Poll;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.kassaiweb.ibiza.Constant;
import com.kassaiweb.ibiza.MainActivity;
import com.kassaiweb.ibiza.R;
import com.kassaiweb.ibiza.Util.SPUtil;

import java.util.ArrayList;
import java.util.List;

public class PollsListFragment extends Fragment implements PollsAdapter.PollsAdapterListener {

    private PollsAdapter adapter;
    private List<Poll> polls = new ArrayList<>();

    public PollsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_polls_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.polls_list_recyclerView);
        TextView tvCreate = view.findViewById(R.id.polls_list_create);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        /*DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.listview_divider));
        recyclerView.addItemDecoration(itemDecorator);*/

        adapter = new PollsAdapter(polls, PollsListFragment.this);
        recyclerView.setAdapter(adapter);

        String currentGroupId = SPUtil.getString(Constant.CURRENT_GROUP_ID, "");
        FirebaseDatabase.getInstance().getReference("polls").orderByChild("groupId")
                .equalTo(currentGroupId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Poll poll = dataSnapshot.getValue(Poll.class);
                poll.setFirebaseId(dataSnapshot.getKey());
                polls.add(poll);
                adapter.notifyItemInserted(polls.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Poll newPoll = dataSnapshot.getValue(Poll.class);
                newPoll.setFirebaseId(dataSnapshot.getKey());
                for (int i = 0; i < polls.size(); ++i) {
                    Poll task = polls.get(i);
                    if (task.getFirebaseId().equals(dataSnapshot.getKey())) {
                        polls.remove(task);
                        polls.add(i, newPoll);
                        adapter.notifyItemChanged(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                for (int i = 0; i < polls.size(); ++i) {
                    Poll poll = polls.get(i);
                    if (dataSnapshot.getKey().equals(poll.getFirebaseId())) {
                        polls.remove(poll);
                        adapter.notifyItemRemoved(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FIREBASE", "onCancel called with message: " + databaseError.getMessage());
            }
        });

        tvCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).replaceFragment(new PollCreateFragment());
            }
        });
    }

    @Override
    public void onPollSelected(Poll poll) {
        ((MainActivity) getActivity()).replaceFragment(PollFragment.getInstance(poll));
    }
}
