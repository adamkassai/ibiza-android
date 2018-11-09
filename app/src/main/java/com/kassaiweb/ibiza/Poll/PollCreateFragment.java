package com.kassaiweb.ibiza.Poll;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kassaiweb.ibiza.Constant;
import com.kassaiweb.ibiza.MainActivity;
import com.kassaiweb.ibiza.R;
import com.kassaiweb.ibiza.Util.NotificationUtil;
import com.kassaiweb.ibiza.Util.SPUtil;

import java.util.ArrayList;

public class PollCreateFragment extends Fragment {

    private ProgressBar progressBar;
    private TextView tvQuestion;
    private TextView tvOption;
    private ImageView btnAddOption;
    private CheckBox cbMultiple;
    private CheckBox cbPublic;
    private Button btnCreate;

    private ArrayList<Answer> answers = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private String currentUserId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_poll_create, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        progressBar = view.findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);

        currentUserId = SPUtil.getString(Constant.CURRENT_USER_ID, null);

        tvQuestion = view.findViewById(R.id.poll_question);
        tvOption = view.findViewById(R.id.poll_option);
        btnAddOption = view.findViewById(R.id.poll_add_option);
        cbMultiple = view.findViewById(R.id.poll_multiple);
        cbPublic = view.findViewById(R.id.poll_public);
        btnCreate = view.findViewById(R.id.poll_save);

        btnAddOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvOption.getText() != null && !tvOption.getText().toString().isEmpty()) {
                    answers.add(new Answer(tvOption.getText().toString()));
                    adapter.notifyItemInserted(answers.size() - 1);
                    tvOption.setText("");
                }
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvQuestion.getText() == null || tvQuestion.getText().toString().isEmpty()) {
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            "Kérdés megadása kötelező", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (answers.isEmpty()) {
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            "Legalább egy válaszlehetőség megadása kötelező", Snackbar.LENGTH_LONG).show();
                    return;
                }

                String choice;
                if (cbMultiple.isChecked()) {
                    choice = Constant.CHOICE_MULTIPLE;
                } else {
                    choice = Constant.CHOICE_SINGLE;
                }

                boolean publicResult;
                if (cbPublic.isChecked()) {
                    publicResult = true;
                } else {
                    publicResult = false;
                }

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference newPollRef = database.getReference("polls").push();

                String groupId = SPUtil.getString(Constant.CURRENT_GROUP_ID, "");
                Poll newPoll = new Poll(newPollRef.getKey(), groupId,
                        tvQuestion.getText().toString(), choice, answers, publicResult, currentUserId);
                newPollRef.setValue(newPoll);

                MainActivity activity = (MainActivity) getActivity();
                NotificationUtil.sendNotification("Új szavazás", newPoll.getQuestion(), currentUserId);
                activity.replaceFragment(new PollsListFragment());
            }
        });

        recyclerView = view.findViewById(R.id.poll_options_recycler_view);
        recyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new PollOptionsAdapter(answers);
        recyclerView.setAdapter(adapter);
    }
}
