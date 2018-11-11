package com.kassaiweb.ibiza.Task;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class TaskListFragment extends Fragment implements TaskAdapter.TaskAdapterListener {

    private List<Task> tasks = new ArrayList<>();
    private RecyclerView recyclerView;
    private TaskAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_task_list, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.task_recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.listview_divider));
        recyclerView.addItemDecoration(itemDecorator);

        adapter = new TaskAdapter(tasks, TaskListFragment.this);
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.task_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity activity = (MainActivity) getActivity();
                activity.replaceFragment(TaskInfoFragment.newInstance(new Task()));
            }
        });

        String currentGroupId = SPUtil.getString(Constant.CURRENT_GROUP_ID, "");
        FirebaseDatabase.getInstance().getReference("tasks")
                .orderByChild("groupId").equalTo(currentGroupId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Task task = dataSnapshot.getValue(Task.class);
                        task.setFirebaseId(dataSnapshot.getKey());
                        tasks.add(task);
                        adapter.notifyItemInserted(tasks.size() - 1);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Task newTask = dataSnapshot.getValue(Task.class);
                        newTask.setFirebaseId(dataSnapshot.getKey());
                        for (int i = 0; i < tasks.size(); ++i) {
                            Task task = tasks.get(i);
                            if (task.getFirebaseId().equals(dataSnapshot.getKey())) {
                                tasks.remove(task);
                                tasks.add(i, newTask);
                                adapter.notifyItemChanged(i);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        for (int i = 0; i < tasks.size(); ++i) {
                            Task task = tasks.get(i);
                            if (dataSnapshot.getKey().equals(task.getFirebaseId())) {
                                tasks.remove(task);
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

                    }
                });
    }

    @Override
    public void onTaskClicked(Task task) {
        MainActivity activity = (MainActivity) getActivity();
        activity.replaceFragment(TaskInfoFragment.newInstance(task));
    }

    @Override
    public void onTaskCompleteClicked(Task task) {
        task.setReady(true);
        FirebaseDatabase.getInstance().getReference("tasks").child(task.getFirebaseId()).setValue(task);
        Snackbar.make(getActivity().findViewById(android.R.id.content), "Feladat kész!", Snackbar.LENGTH_SHORT).show();
        // és a UI frissül magától
    }
}
