package com.kassaiweb.ibiza.Task;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kassaiweb.ibiza.Constant;
import com.kassaiweb.ibiza.R;
import com.kassaiweb.ibiza.User.User;
import com.kassaiweb.ibiza.Util.SPUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private ArrayList<Task> tasks = new ArrayList<>();
    private HashMap<String, User> users = new HashMap<>();
    private String userId;
    private Activity activity;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView descriptionTextView;
        public LinearLayout volunteersLayout;
        public TextView dateTextView;
        public TextView typeTextView;
        public Button applyButton;
        public ImageView closeButton;
        public ImageView deleteButton;
        public LinearLayout mainLayout;

        public ViewHolder(View v) {
            super(v);
            descriptionTextView = v.findViewById(R.id.task_description);
            volunteersLayout = v.findViewById(R.id.task_volunteers_layout);
            dateTextView = v.findViewById(R.id.task_date);
            typeTextView = v.findViewById(R.id.task_type);
            applyButton = v.findViewById(R.id.task_apply_button);
            closeButton = v.findViewById(R.id.task_close_button);
            deleteButton = v.findViewById(R.id.task_delete_button);
            mainLayout = v.findViewById(R.id.task_main_layout);
        }
    }


    public TaskAdapter(Activity activity) {

        this.activity = activity;

        userId = SPUtil.getString(Constant.USERID, null);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                users.clear();

                for (DataSnapshot userDataSnapshot : dataSnapshot.getChildren()) {
                    User user = userDataSnapshot.getValue(User.class);
                    users.put(user.getId(), user);
                }

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference tasksRef = database.getReference("tasks");
                tasksRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        tasks.clear();

                        for (DataSnapshot taskDataSnapshot : dataSnapshot.getChildren()) {
                            tasks.add(taskDataSnapshot.getValue(Task.class));
                        }

                        TaskAdapter.this.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w("Tasks", "Failed to read value.", error.toException());
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Users", "Failed to read value.", error.toException());
            }
        });


    }


    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_task, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy. MM. dd. HH:mm");


        if (tasks.get(position).getDeadline() != null) {
            if (new Date().compareTo(tasks.get(position).getDeadline()) > 0) {

                tasks.get(position).setVolunteers(getRandomVolunteers(tasks.get(position).getVolunteerNumber(), tasks.get(position).getVolunteers()));
                tasks.get(position).setType(Constant.TASK_RANDOM);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference taskRef = database.getReference("tasks").child(tasks.get(position).getId());
                taskRef.setValue(tasks.get(position));


            }
        }



        holder.descriptionTextView.setText(tasks.get(position).getDescription());
        holder.dateTextView.setText(dateFormat.format(tasks.get(position).getDate()));
        holder.applyButton.setTag(position);
        holder.closeButton.setTag(position);
        holder.deleteButton.setTag(position);
        holder.mainLayout.setTag(position);

        holder.closeButton.setVisibility(View.GONE);
        holder.deleteButton.setVisibility(View.GONE);

        if (tasks.get(position).isReady()) {
            holder.closeButton.setImageResource(R.drawable.baseline_replay_black_36);
            holder.dateTextView.setText("Lezárt feladat");
            holder.volunteersLayout.setVisibility(View.GONE);
            holder.typeTextView.setVisibility(View.GONE);
        }else{
            holder.closeButton.setImageResource(R.drawable.baseline_check_circle_outline_black_36);
            holder.volunteersLayout.setVisibility(View.VISIBLE);
            holder.typeTextView.setVisibility(View.VISIBLE);
        }

        if (tasks.get(position).getType().equals(Constant.TASK_ASSIGNED)) {
            if (users.get(tasks.get(position).getCreatorId())!=null && users.get(tasks.get(position).getCreatorId()).getName()!=null) {
                holder.typeTextView.setText(users.get(tasks.get(position).getCreatorId()).getName() + " által beosztva:");
            }
        } else if (tasks.get(position).getType().equals(Constant.TASK_RANDOM)) {
            holder.typeTextView.setText("Véletlenszerűen kisorsolva:");
        } else {
            holder.typeTextView.setVisibility(View.GONE);
        }

        holder.volunteersLayout.removeAllViews();

        for (String volunteerId : tasks.get(position).getVolunteers()) {
            CircleImageView imageView = new CircleImageView(activity);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(40, 40);
            layoutParams.setMargins(5, 5, 5, 5);
            imageView.setLayoutParams(layoutParams);
            if (users.get(volunteerId)!=null && users.get(volunteerId).getImage()!=null) {
                ImageLoader.getInstance().displayImage(users.get(volunteerId).getImage(), imageView);
            }
            holder.volunteersLayout.addView(imageView);
        }


        if (tasks.get(position).getVolunteers().size() < tasks.get(position).getVolunteerNumber() && !tasks.get(position).isReady() && !tasks.get(position).getVolunteers().contains(userId)) {
            holder.applyButton.setVisibility(View.VISIBLE);
        } else {
            holder.applyButton.setVisibility(View.GONE);
        }

        holder.applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int position = (Integer) view.getTag();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference taskRef = database.getReference("tasks").child(tasks.get(position).getId());

                if (!tasks.get(position).getVolunteers().contains(userId)) {
                    tasks.get(position).getVolunteers().add(userId);
                    taskRef.setValue(tasks.get(position));
                }

            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int position = (Integer) view.getTag();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference taskRef = database.getReference("tasks").child(tasks.get(position).getId());
                taskRef.removeValue();

            }
        });

        holder.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int position = (Integer) view.getTag();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference taskRef = database.getReference("tasks").child(tasks.get(position).getId());

                if (tasks.get(position).isReady()) {
                    tasks.get(position).setReady(false);
                    holder.closeButton.setImageResource(R.drawable.baseline_check_circle_outline_black_36);
                }else{
                    tasks.get(position).setReady(true);
                    holder.closeButton.setImageResource(R.drawable.baseline_replay_black_36);
                }

                taskRef.setValue(tasks.get(position));

            }
        });

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.closeButton.getVisibility()==View.VISIBLE) {
                    holder.closeButton.setVisibility(View.GONE);
                    holder.deleteButton.setVisibility(View.GONE);
                }else{
                    holder.closeButton.setVisibility(View.VISIBLE);
                    holder.deleteButton.setVisibility(View.VISIBLE);
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public ArrayList<String> getRandomVolunteers(int n, ArrayList<String> volunteers) {

        Random random = new Random();
        Object[] userArray = users.keySet().toArray();

        while (volunteers.size() < Math.min(n, userArray.length)) {

            int r = random.nextInt(userArray.length);

            if (!volunteers.contains((String) userArray[r])) {
                volunteers.add((String) userArray[r]);
            }

        }

        return volunteers;
    }

}
