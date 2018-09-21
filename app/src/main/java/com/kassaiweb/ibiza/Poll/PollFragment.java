package com.kassaiweb.ibiza.Poll;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.kassaiweb.ibiza.Constant;
import com.kassaiweb.ibiza.MainActivity;
import com.kassaiweb.ibiza.R;
import com.kassaiweb.ibiza.Util.SPUtil;

import java.util.ArrayList;

public class PollFragment extends Fragment {

    private String pollId;
    private Poll poll;
    private String userId;

    private RecyclerView mRecyclerView;
    private PollOptionsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView question;
    private Button send;
    private Button closeButton;
    private Button removeButton;
    private TextView multipleTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();
        pollId = args.getString("pollId", null);


        return inflater.inflate(R.layout.fragment_poll, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {

        mRecyclerView = (RecyclerView) view.findViewById(R.id.poll_options_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        userId = SPUtil.getString(Constant.USERID, null);

        question = view.findViewById(R.id.poll_question);
        send = view.findViewById(R.id.poll_send);
        closeButton = view.findViewById(R.id.poll_close);
        removeButton = view.findViewById(R.id.poll_remove);
        multipleTextView = view.findViewById(R.id.poll_multiple);

        view.findViewById(R.id.poll_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MainActivity activity = (MainActivity) getActivity();
                activity.replaceFragment(new PollCreateFragment());

            }
        });

        if (pollId != null) {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference pollsRef = database.getReference("polls").child(pollId);

            pollsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {

                    poll = dataSnapshot.getValue(Poll.class);

                    if (poll != null) {

                        question.setText(poll.getQuestion());

                        mAdapter = new PollOptionsAdapter(poll, userId);
                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();

                        send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference answersRef = database.getReference("polls").child(pollId).child("answers");
                                ArrayList<Boolean> selections = mAdapter.getSelections();

                                for (int i = 0; i < selections.size(); i++) {
                                    answersRef.child(Integer.toString(i)).child("votes").child(userId).setValue(selections.get(i));
                                }

                                Toast.makeText(getContext(), "Sikeres szavazás!", Toast.LENGTH_LONG).show();


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
                                        DatabaseReference closedRef = database.getReference("polls").child(pollId).child("closed");
                                        closedRef.setValue(false);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                            } else {
                                closeButton.setText(R.string.close_poll);
                                closeButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference closedRef = database.getReference("polls").child(pollId).child("closed");
                                        closedRef.setValue(true);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                            }

                        }

                        if (poll.getCreatorId().equals(userId)) {
                            removeButton.setVisibility(View.VISIBLE);
                            removeButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    database.getReference("polls").child(pollId).removeValue();

                                    MainActivity activity = (MainActivity) getActivity();
                                    activity.replaceFragment(new PollsPagerFragment());

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w("PollFragment", "Failed to read value.", error.toException());
                }
            });
        }

        /*TextView header = view.findViewById(R.id.news_header);
        if (news.getHeader()!=null) {
            header.setText(news.getHeader().trim());
        }

        TextView body = view.findViewById(R.id.news_body);
        if (news.getBody()!=null) {
            body.setText(news.getBody().trim());
        }


        ImageView cover = view.findViewById(R.id.news_cover);
        if (news.getCover()!=null) {
            ImageLoader.getInstance().displayImage(news.getCover(), cover);
        }*/
    }
}
