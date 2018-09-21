package com.kassaiweb.ibiza.Cost;

import android.app.Activity;
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
import com.kassaiweb.ibiza.User.User;
import com.kassaiweb.ibiza.Util.SPUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {

    private ArrayList<Payment> payments = new ArrayList<>();
    private HashMap<String, User> users = new HashMap<>();
    private String userId;
    private MainActivity activity;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView fromTextView;
        public TextView toTextView;
        public ImageView fromImageView;
        public ImageView toImageView;
        public TextView sumTextView;
        public LinearLayout mainLayout;
        public ImageView deleteButton;

        public ViewHolder(View v) {
            super(v);
            fromTextView = v.findViewById(R.id.payment_from_name);
            toTextView = v.findViewById(R.id.payment_to_name);
            fromImageView = v.findViewById(R.id.payment_from_image);
            toImageView = v.findViewById(R.id.payment_to_image);

            sumTextView = v.findViewById(R.id.payment_sum);

            mainLayout = v.findViewById(R.id.payment_main_layout);
            deleteButton = v.findViewById(R.id.payment_delete_button);
        }
    }


    public PaymentAdapter(Activity activity) {

        this.activity = (MainActivity)activity;

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

                PaymentAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Users", "Failed to read value.", error.toException());
            }
        });

        DatabaseReference tasksRef = database.getReference("payments");

        tasksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                payments.clear();

                for (DataSnapshot paymentDataSnapshot : dataSnapshot.getChildren()) {
                    payments.add(paymentDataSnapshot.getValue(Payment.class));
                }

                PaymentAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Payments", "Failed to read value.", error.toException());
            }
        });


    }


    @Override
    public PaymentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_payment_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        holder.fromTextView.setText(users.get(payments.get(position).getFromId()).getName());
        holder.toTextView.setText(users.get(payments.get(position).getToId()).getName());

        ImageLoader.getInstance().displayImage(users.get(payments.get(position).getFromId()).getImage(), holder.fromImageView);
        ImageLoader.getInstance().displayImage(users.get(payments.get(position).getToId()).getImage(), holder.toImageView);

        holder.sumTextView.setText(Integer.toString(payments.get(position).getSum())+" Ft");

        holder.deleteButton.setTag(position);
        holder.deleteButton.setVisibility(View.GONE);

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int position = (Integer) view.getTag();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference paymentRef = database.getReference("payments").child(payments.get(position).getId());
                paymentRef.removeValue();

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
        return payments.size();
    }

}
