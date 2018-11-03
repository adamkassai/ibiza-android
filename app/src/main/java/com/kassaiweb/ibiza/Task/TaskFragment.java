package com.kassaiweb.ibiza.Task;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.kassaiweb.ibiza.Constant;
import com.kassaiweb.ibiza.R;
import com.kassaiweb.ibiza.Util.SPUtil;

public class TaskFragment extends Fragment {

    private String taskId;
    private Task task;
    private Gson gson = new Gson();
    private String userId;

    private RecyclerView mRecyclerView;
    private VolunteerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView description;
    private TextView volunteerNumber;
    private Button send;
    private Button closeButton;
    private Button removeButton;
    private TextView multipleTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();
        taskId = args.getString("taskId", null);



        return inflater.inflate(R.layout.fragment_task_create, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {

        mRecyclerView = view.findViewById(R.id.task_volunteers_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        userId = SPUtil.getString(Constant.USER_ID, null);

        description = view.findViewById(R.id.task_description);
        volunteerNumber = view.findViewById(R.id.task_volunteerNumber);

        send = view.findViewById(R.id.poll_send);
        closeButton = view.findViewById(R.id.poll_close);
        removeButton = view.findViewById(R.id.poll_remove);
        multipleTextView = view.findViewById(R.id.poll_multiple);


        if (taskId !=null) {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference pollsRef = database.getReference("tasks").child(taskId);

            pollsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {

                    task = dataSnapshot.getValue(Task.class);

                    if (task !=null) {

                        description.setText(task.getDescription());

                        mAdapter = new VolunteerAdapter(task);
                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();

                        send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference taskRef = database.getReference("tasks").child(taskId);
                                task.setVolunteers(mAdapter.getVolunteers());
                                taskRef.setValue(task);

                            }
                        });



                            /*if (task.isReady()) {
                                closeButton.setText(R.string.open_task);
                                closeButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference closedRef = database.getReference("tasks").child(taskId).child("ready");
                                        closedRef.setValue(false);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                            }else{
                                closeButton.setText(R.string.close_task);
                                closeButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference closedRef = database.getReference("tasks").child(taskId).child("ready");
                                        closedRef.setValue(true);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                            }*/



                            /*removeButton.setVisibility(View.VISIBLE);
                            removeButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    database.getReference("tasks").child(taskId).removeValue();

                                    MainActivity activity = (MainActivity)getActivity();
                                    activity.replaceFragment(new FrontPageFragment());

                                }
                            });*/




                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w("TaskCreateFragment", "Failed to read value.", error.toException());
                }
            });
        }


    }

}
