package com.kassaiweb.ibiza.Task;

import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kassaiweb.ibiza.Constant;
import com.kassaiweb.ibiza.FrontPageFragment;
import com.kassaiweb.ibiza.MainActivity;
import com.kassaiweb.ibiza.R;
import com.kassaiweb.ibiza.User.User;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class VolunteerAdapter extends RecyclerView.Adapter<VolunteerAdapter.ViewHolder> {

    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<String> volunteers = new ArrayList<>();
    private Task task;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;
        public CheckBox userCheckBox;
        public ImageView userImageView;

        public ViewHolder(View v) {
            super(v);
            nameTextView = v.findViewById(R.id.username);
            userCheckBox = v.findViewById(R.id.volunteer_checkBox);
            userImageView = v.findViewById(R.id.userImage);
        }
    }


    public VolunteerAdapter(Task task) {

        this.task = task;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    users.clear();

                    for (DataSnapshot userDataSnapshot : dataSnapshot.getChildren()) {
                        users.add(userDataSnapshot.getValue(User.class));
                    }

                    VolunteerAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Volunteers", "Failed to read value.", error.toException());
            }
        });


    }



    @Override
    public VolunteerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_volunter, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.nameTextView.setText(users.get(position).getName());
        holder.userCheckBox.setTag(position);
        ImageLoader.getInstance().displayImage(users.get(position).getImage(), holder.userImageView);

        if (task!=null && task.getVolunteers().contains(users.get(position).getId())) {
            volunteers.add(users.get(position).getId());
            holder.userCheckBox.setChecked(true);
        }

        holder.userCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CheckBox checkBox = (CheckBox) view;

                if (checkBox.isChecked()) {

                    if (!volunteers.contains(users.get((Integer)checkBox.getTag()).getId()))
                    volunteers.add(users.get((Integer)checkBox.getTag()).getId());
                    checkBox.setChecked(true);

                } else {

                    volunteers.remove(users.get((Integer)checkBox.getTag()).getId());
                    checkBox.setChecked(false);

                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public ArrayList<String> getVolunteers() {
        return volunteers;
    }

    public ArrayList<String> getRandomVolunteers(int n) {

        volunteers.clear();
        Random random = new Random();

        while(volunteers.size()<Math.min(n, users.size())) {

            int r = random.nextInt(users.size());

            if (!volunteers.contains(users.get(r).getId())) {
                volunteers.add(users.get(r).getId());
            }

        }

        return volunteers;
    }

}
