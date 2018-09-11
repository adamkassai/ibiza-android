package com.kassaiweb.ibiza;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.kassaiweb.ibiza.Cost.CostPagerFragment;
import com.kassaiweb.ibiza.News.News;
import com.kassaiweb.ibiza.Place.PlacesFragment;
import com.kassaiweb.ibiza.Poll.Poll;
import com.kassaiweb.ibiza.Poll.PollsPagerFragment;
import com.kassaiweb.ibiza.Task.Task;
import com.kassaiweb.ibiza.Task.TaskListFragment;
import com.kassaiweb.ibiza.User.ChangeUserFragment;
import com.kassaiweb.ibiza.Util.SPUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FrontPageFragment extends Fragment {

    private News news;
    private Gson gson = new Gson();

    private LinearLayout taskBox;
    private LinearLayout pollBox;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_front_page, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        final String user = SPUtil.getString(Constant.USERNAME, null);
        userId = SPUtil.getString(Constant.USERID, null);
        String image = SPUtil.getString(Constant.USER_IMAGE, null);

        TextView usernameTextView = view.findViewById(R.id.username);
        if (user!=null) {
            usernameTextView.setText(user);
        }

        ImageView userImage = view.findViewById(R.id.userImage);
        ImageLoader.getInstance().displayImage(image, userImage);

        view.findViewById(R.id.button_changeUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity activity = (MainActivity)getActivity();
                activity.replaceFragment(new ChangeUserFragment());
            }
        });

        view.findViewById(R.id.front_cost_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity activity = (MainActivity)getActivity();
                activity.replaceFragment(new CostPagerFragment());
            }
        });

        view.findViewById(R.id.front_places_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity activity = (MainActivity)getActivity();
                activity.replaceFragment(new PlacesFragment());
            }
        });

        view.findViewById(R.id.front_random_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity activity = (MainActivity)getActivity();
                activity.replaceFragment(new PickOneFragment());
            }
        });

        view.findViewById(R.id.front_notification_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity activity = (MainActivity)getActivity();
                activity.replaceFragment(new NotificationFragment());
            }
        });

        pollBox = view.findViewById(R.id.front_poll_box);

        pollBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity activity = (MainActivity)getActivity();
                activity.replaceFragment(new PollsPagerFragment());
            }
        });

        taskBox = view.findViewById(R.id.front_task_box);

        taskBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity activity = (MainActivity)getActivity();
                activity.replaceFragment(new TaskListFragment());
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tasksRef = database.getReference("tasks");
        tasksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot taskDataSnapshot : dataSnapshot.getChildren()) {
                    Task task = taskDataSnapshot.getValue(Task.class);
                    if (!task.isReady() && (task.getVolunteers().contains(userId) || task.getType().equals(Constant.TASK_VOLUNTEERS))) {
                        taskBox.setVisibility(View.VISIBLE);
                        ((TextView)taskBox.findViewById(R.id.front_task_text)).setText(task.getDescription());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Task", "Failed to read value.", error.toException());
            }
        });

        DatabaseReference pollsRef = database.getReference("polls");
        pollsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot pollDataSnapshot : dataSnapshot.getChildren()) {
                    Poll poll = pollDataSnapshot.getValue(Poll.class);
                    if (!poll.isClosed()) {
                        pollBox.setVisibility(View.VISIBLE);
                        ((TextView)pollBox.findViewById(R.id.front_poll_text)).setText(poll.getQuestion());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Task", "Failed to read value.", error.toException());
            }
        });

    }

}
