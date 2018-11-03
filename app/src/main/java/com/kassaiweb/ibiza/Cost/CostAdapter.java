package com.kassaiweb.ibiza.Cost;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
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
import com.kassaiweb.ibiza.Constant;
import com.kassaiweb.ibiza.MainActivity;
import com.kassaiweb.ibiza.R;
import com.kassaiweb.ibiza.Data.User;
import com.kassaiweb.ibiza.Util.SPUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class CostAdapter extends RecyclerView.Adapter<CostAdapter.ViewHolder> {

    private ArrayList<Cost> costs = new ArrayList<>();
    private HashMap<String, User> users = new HashMap<>();
    private String userId;
    private MainActivity activity;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView descriptionTextView;
        public TextView totalTextView;
        public LinearLayout mainLayout;
        public ImageView deleteButton;

        public ViewHolder(View v) {
            super(v);
            descriptionTextView = v.findViewById(R.id.cost_description);
            totalTextView = v.findViewById(R.id.cost_total);
            mainLayout = v.findViewById(R.id.cost_main_layout);
            deleteButton = v.findViewById(R.id.cost_delete_button);
        }
    }


    public CostAdapter(Activity activity) {

        this.activity = (MainActivity)activity;

        userId = SPUtil.getString(Constant.USER_ID, null);

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

                CostAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Users", "Failed to read value.", error.toException());
            }
        });

        DatabaseReference tasksRef = database.getReference("costs");

        tasksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                costs.clear();

                for (DataSnapshot costDataSnapshot : dataSnapshot.getChildren()) {
                    costs.add(costDataSnapshot.getValue(Cost.class));
                }

                CostAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Costs", "Failed to read value.", error.toException());
            }
        });


    }


    @Override
    public CostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cost_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        holder.descriptionTextView.setText(costs.get(position).getDescription());
        holder.totalTextView.setText(costs.get(position).getTotal()+" Ft");
        holder.descriptionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CostCreateFragment fragment = new CostCreateFragment();
                Bundle args = new Bundle();
                args.putString("costId", costs.get(position).getId());
                fragment.setArguments(args);
                activity.replaceFragment(fragment);
            }
        });


        holder.deleteButton.setTag(position);
        holder.deleteButton.setVisibility(View.GONE);

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int position = (Integer) view.getTag();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference costRef = database.getReference("costs").child(costs.get(position).getId());
                costRef.removeValue();

            }
        });

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.deleteButton.getVisibility()==View.VISIBLE) {
                    holder.deleteButton.setVisibility(View.GONE);
                }else{
                    holder.deleteButton.setVisibility(View.VISIBLE);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return costs.size();
    }

}
