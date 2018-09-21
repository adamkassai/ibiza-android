package com.kassaiweb.ibiza.Poll;

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
import com.google.gson.Gson;
import com.kassaiweb.ibiza.Constant;
import com.kassaiweb.ibiza.MainActivity;
import com.kassaiweb.ibiza.R;
import com.kassaiweb.ibiza.Util.NotificationUtil;
import com.kassaiweb.ibiza.Util.SPUtil;

import java.util.ArrayList;

public class PollCreateFragment extends Fragment {

    private Gson gson = new Gson();
    private ArrayList<Answer> answers = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String userId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_poll_create, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        view.findViewById(R.id.progress).setVisibility(View.GONE);

        userId = SPUtil.getString(Constant.USERID, null);

        final TextView questionTextView = view.findViewById(R.id.poll_question);
        final TextView optionTextView = view.findViewById(R.id.poll_option);
        ImageView addOptionButton = view.findViewById(R.id.poll_add_option);
        final CheckBox multipleCheckbox = view.findViewById(R.id.poll_multiple);
        final CheckBox publicCheckbox = view.findViewById(R.id.poll_public);
        Button createButton = view.findViewById(R.id.poll_save);

        addOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (optionTextView.getText()!=null && !optionTextView.getText().toString().equals("")) {

                    answers.add(new Answer(optionTextView.getText().toString()));
                    mAdapter.notifyDataSetChanged();
                    optionTextView.setText("");

                }
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (questionTextView.getText()==null || questionTextView.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Kérdés megadása kötelező", Toast.LENGTH_LONG).show();
                    return;
                }

                if (answers.size()==0) {
                    Toast.makeText(getContext(), "Legalább egy válaszlehetőség megadása kötelező", Toast.LENGTH_LONG).show();
                    return;
                }

                String choice;
                if (multipleCheckbox.isChecked()) {
                    choice=Constant.CHOICE_MULTIPLE;
                }else{
                    choice=Constant.CHOICE_SINGLE;
                }

                boolean publicResult;
                if (publicCheckbox.isChecked()) {
                    publicResult=true;
                }else{
                    publicResult=false;
                }

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference newPollRef = database.getReference("polls").push();

                Poll newPoll = new Poll(newPollRef.getKey(), questionTextView.getText().toString(), choice, answers, publicResult, userId);
                newPollRef.setValue(newPoll);

                MainActivity activity = (MainActivity)getActivity();
                NotificationUtil.sendNotification("Új szavazás", newPoll.getQuestion(), userId);
                activity.replaceFragment(new PollsPagerFragment());

            }
        });



        mRecyclerView = (RecyclerView) view.findViewById(R.id.poll_options_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new PollOptionsAdapter(answers, "");
        mRecyclerView.setAdapter(mAdapter);



    }


}
