package com.kassaiweb.ibiza.Cost;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kassaiweb.ibiza.R;
import com.kassaiweb.ibiza.Data.User;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;

public class CostPersonAdapter extends RecyclerView.Adapter<CostPersonAdapter.ViewHolder> {

    private HashMap<String, User> users = new HashMap<>();
    private Cost cost;
    private TextView totalPaid;
    private TextView totalSum;
    private TextView totalPersonal;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;
        public ImageView userImageView;
        public EditText paid;
        public EditText sum;
        public EditText personal;
        public CheckBox fix;

        public ViewHolder(View v) {
            super(v);
            nameTextView = v.findViewById(R.id.username);
            userImageView = v.findViewById(R.id.userImage);
            paid = v.findViewById(R.id.cost_paid);
            sum = v.findViewById(R.id.cost_sum);
            personal = v.findViewById(R.id.cost_personal);
            fix = v.findViewById(R.id.cost_fix);
        }
    }


    public CostPersonAdapter(Cost cost, TextView totalPaid, TextView totalSum, TextView totalPersonal) {

        this.cost = cost;
        this.totalPaid = totalPaid;
        this.totalSum = totalSum;
        this.totalPersonal = totalPersonal;

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

                    CostPersonAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Users", "Failed to read value.", error.toException());
            }
        });


    }



    @Override
    public CostPersonAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cost, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (cost!=null) {

            refreshTotal();

            CostPerson costPerson = cost.getCostPersons().get(position);

            holder.nameTextView.setText(users.get(costPerson.getUserId()).getName());
            ImageLoader.getInstance().displayImage(users.get(costPerson.getUserId()).getImage(), holder.userImageView);
            holder.fix.setTag(position);

            if (costPerson.isFix()) {
                holder.fix.setChecked(true);
            }else{
                holder.fix.setChecked(false);
            }

            if (costPerson.isNewValue()) {
                holder.paid.setText(Integer.toString(costPerson.getPaid()));
                holder.personal.setText(Integer.toString(costPerson.getPersonal()));
                holder.sum.setText(Integer.toString(costPerson.getSum()));

            }else{
                int sum = (int)((double)cost.getTotal() / cost.getCostPersons().size());
                holder.sum.setText(Integer.toString(sum));
                cost.getCostPersons().get(position).setSum(sum);
                refreshTotal();
            }

            holder.fix.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    CheckBox cb = (CheckBox)view;
                    int position = (Integer)cb.getTag();

                    if (cb.isChecked()) {
                        cost.getCostPersons().get(position).setFix(true);
                    }else{
                        cost.getCostPersons().get(position).setFix(false);
                    }


                }
            });

            holder.personal.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (holder.personal.getText().toString()!=null && !holder.personal.getText().toString().equals("")) {


                            int personal = Integer.parseInt(holder.personal.getText().toString());
                            cost.getCostPersons().get(position).setPersonal(personal);

                            refreshTotal();


                        }
                    }
                }
            });


            holder.sum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (holder.sum.getText().toString()!=null && !holder.sum.getText().toString().equals("")) {

                            int sum = Integer.parseInt(holder.sum.getText().toString());
                            cost.getCostPersons().get(position).setSum(sum);

                            refreshTotal();
                        }
                    }
                }
            });

            holder.paid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (holder.paid.getText().toString()!=null && !holder.paid.getText().toString().equals("")) {

                            int paid = Integer.parseInt(holder.paid.getText().toString());
                            cost.getCostPersons().get(position).setPaid(paid);
                            refreshTotal();
                        }
                    }
                }
            });

        }

    }

    @Override
    public int getItemCount() {

        if (cost==null) {
            return 0;
        }

        return cost.getCostPersons().size();
    }

    public void setCost(Cost cost) {
        this.cost = cost;
        notifyDataSetChanged();
    }

    public void refreshTotal()
    {
        int totalPaidInt=0;
        int totalSumInt=0;
        int totalPersonalInt=0;

        for (CostPerson costPerson : cost.getCostPersons()) {

            totalPaidInt+=costPerson.getPaid();
            totalSumInt+=costPerson.getSum();
            totalPersonalInt+=costPerson.getPersonal();

        }

        totalPaid.setText(Integer.toString(totalPaidInt));
        totalSum.setText(Integer.toString(totalSumInt));
        totalPersonal.setText(Integer.toString(totalPersonalInt));
    }

    public Cost getCost() {
        return cost;
    }

    public void reDivide() {

        int personalTotal=0;
        int fixed=0;
        int fixPersonNumber=0;

        for (CostPerson costP : cost.getCostPersons())
        {

            personalTotal+=costP.getPersonal();

            if (costP.isFix()) {
                fixed+=costP.getSum();
                fixPersonNumber++;
            }

        }

        int newSum = (int)((double)(cost.getTotal() - personalTotal - fixed) / (cost.getCostPersons().size()-fixPersonNumber));

        for (int i=0; i<cost.getCostPersons().size(); i++) {

            if (!cost.getCostPersons().get(i).isFix()) {
                cost.getCostPersons().get(i).setSum(newSum);
            }
            cost.getCostPersons().get(i).setNewValue(true);

        }

        notifyDataSetChanged();
        refreshTotal();

    }
}
