package com.kassaiweb.ibiza.Poll;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kassaiweb.ibiza.Constant;
import com.kassaiweb.ibiza.MainActivity;
import com.kassaiweb.ibiza.R;
import com.kassaiweb.ibiza.Util.SPUtil;

import java.util.ArrayList;

public class PollFragment extends Fragment {

    private static PollFragment instance;
    private static Poll poll;
    private String userId;

    private RecyclerView recyclerView;
    private PollOptionsAdapter adapter;

    private TextView question;
    private Button send;
    private Button closeButton;
    private Button removeButton;
    private TextView multipleTextView;

    public static PollFragment getInstance(Poll poll) {
        PollFragment.poll = poll;
        if (instance == null) {
            instance = new PollFragment();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_poll, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {

        recyclerView = view.findViewById(R.id.poll_options_recycler_view);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userId = SPUtil.getString(Constant.CURRENT_USER_ID, null);

        question = view.findViewById(R.id.poll_question);
        send = view.findViewById(R.id.poll_send);
        closeButton = view.findViewById(R.id.poll_close);
        removeButton = view.findViewById(R.id.poll_remove);
        multipleTextView = view.findViewById(R.id.poll_multiple);
        question.setText(poll.getQuestion());

        adapter = new PollOptionsAdapter(poll);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference answersRef = FirebaseDatabase.getInstance().getReference("polls")
                        .child(poll.getFirebaseId()).child("answers");
                ArrayList<Boolean> selections = adapter.getSelections();

                for (int i = 0; i < selections.size(); i++) {
                    answersRef.child(Integer.toString(i)).child("votes")
                            .child(userId).setValue(selections.get(i));
                }
                adapter.notifyDataSetChanged();

                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        "Sikeres szavazás!", Snackbar.LENGTH_LONG).show();
            }
        });

        if (poll.getChoice().equals(Constant.CHOICE_MULTIPLE)) {
            multipleTextView.setVisibility(View.VISIBLE);
        }

        if (poll.isClosed()) {
            multipleTextView.setText(R.string.poll_closed);
            multipleTextView.setVisibility(View.VISIBLE);
            send.setVisibility(View.GONE);
        }

        if (poll.getCreatorId().equals(userId)) {
            closeButton.setVisibility(View.VISIBLE);

            if (poll.isClosed()) {
                closeButton.setText(R.string.open_poll);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference closedRef = database.getReference("polls")
                                .child(poll.getFirebaseId()).child("closed");
                        closedRef.setValue(false);
                        // adapter.notifyDataSetChanged();

                        MainActivity activity = (MainActivity) getActivity();
                        activity.replaceFragment(new PollsListFragment());
                    }
                });
            } else {
                closeButton.setText(R.string.close_poll);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference closedRef = database.getReference("polls")
                                .child(poll.getFirebaseId()).child("closed");
                        closedRef.setValue(true);
                        // adapter.notifyDataSetChanged();

                        MainActivity activity = (MainActivity) getActivity();
                        activity.replaceFragment(new PollsListFragment());
                    }
                });
            }

        }

        if (poll.getCreatorId().equals(userId)) {
            removeButton.setVisibility(View.VISIBLE);
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new AlertDialog.Builder(getContext())
                            .setTitle("Biztosan törlöd?")
                            .setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    database.getReference("polls").child(poll.getFirebaseId()).removeValue();

                                    MainActivity activity = (MainActivity) getActivity();
                                    activity.replaceFragment(new PollsListFragment());
                                }
                            })
                            .setNegativeButton("Mégsem", null)
                            .show();

                }
            });
        }
    }
}
