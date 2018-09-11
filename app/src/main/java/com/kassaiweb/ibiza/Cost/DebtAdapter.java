package com.kassaiweb.ibiza.Cost;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class DebtAdapter extends RecyclerView.Adapter<DebtAdapter.ViewHolder> {

    private ArrayList<Payment> payments = new ArrayList<>();
    private ArrayList<Cost> costs = new ArrayList<>();
    private HashMap<String, User> users = new HashMap<>();
    private HashMap<String, Integer> balances = new HashMap<>();
    private ArrayList<String> balancedUsers = new ArrayList<>();
    private ArrayList<Payment> debts = new ArrayList<>();
    private String userId;
    private MainActivity activity;
    private boolean firstInit = true;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView fromTextView;
        public TextView toTextView;
        public ImageView fromImageView;
        public ImageView toImageView;
        public TextView sumTextView;
        public LinearLayout mainLayout;
        public ImageView payButton;
        public AlertDialog alertDialog;

        public ViewHolder(View v) {
            super(v);
            fromTextView = v.findViewById(R.id.debt_from_name);
            toTextView = v.findViewById(R.id.debt_to_name);
            fromImageView = v.findViewById(R.id.debt_from_image);
            toImageView = v.findViewById(R.id.debt_to_image);

            sumTextView = v.findViewById(R.id.debt_sum);

            mainLayout = v.findViewById(R.id.debt_main_layout);
            payButton = v.findViewById(R.id.debt_pay_button);
        }
    }


    public DebtAdapter(Activity activity) {

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
                    balances.put(user.getId(), 0);
                }

                DebtAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Users", "Failed to read value.", error.toException());
            }
        });

        DatabaseReference paymentsRef = database.getReference("payments");
        paymentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                debts.clear();
                payments.clear();
                balances.clear();
                balancedUsers.clear();

                for (DataSnapshot paymentDataSnapshot : dataSnapshot.getChildren()) {
                    payments.add(paymentDataSnapshot.getValue(Payment.class));
                }


                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference costsRef = database.getReference("costs");
                if (firstInit) {
                    costsRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            debts.clear();
                            balances.clear();
                            balancedUsers.clear();

                            Log.d("COST", "CHANGED");

                            for (DataSnapshot costDataSnapshot : dataSnapshot.getChildren()) {
                                costs.add(costDataSnapshot.getValue(Cost.class));
                            }

                            calculate();
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w("Payments", "Failed to read value.", error.toException());
                        }
                    });
                    firstInit=false;
                }

                calculate();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Payments", "Failed to read value.", error.toException());
            }
        });


    }


    @Override
    public DebtAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_debt_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        holder.fromTextView.setText(users.get(debts.get(position).getFromId()).getName());
        holder.toTextView.setText(users.get(debts.get(position).getToId()).getName());

        ImageLoader.getInstance().displayImage(users.get(debts.get(position).getFromId()).getImage(), holder.fromImageView);
        ImageLoader.getInstance().displayImage(users.get(debts.get(position).getToId()).getImage(), holder.toImageView);

        holder.sumTextView.setText(Integer.toString(debts.get(position).getSum())+" Ft");

        holder.payButton.setTag(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder
                .setTitle(users.get(debts.get(position).getFromId()).getName() + " ad "+ users.get(debts.get(position).getToId()).getName()
                        + " részére " + debts.get(position).getSum() + " Forintot?")
                .setCancelable(false)
                .setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference paymentRef = database.getReference("payments").push();

                        debts.get(position).setId(paymentRef.getKey());
                        paymentRef.setValue(debts.get(position));

                    }
                })
                .setNegativeButton("Nem",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });


        holder.alertDialog = alertDialogBuilder.create();

        holder.payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.alertDialog.show();

            }
        });



    }

    @Override
    public int getItemCount() {
        return debts.size();
    }


    private void calculate() {


        for (Cost cost : costs) {

            balances.clear();

            for (CostPerson costPerson : cost.getCostPersons()) {

                int balance;

                if (balances.containsKey(costPerson.getUserId())) {
                    balance = balances.get(costPerson.getUserId());
                } else {
                    balance = 0;
                }

                balance = balance + costPerson.getPaid() - costPerson.getSum() - costPerson.getPersonal();
                balances.put(costPerson.getUserId(), balance);

            }

            balancedUsers.clear();
            balancedUsers.addAll(balances.keySet());

            Collections.sort(balancedUsers, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return balances.get(o1).compareTo(balances.get(o2));
                }
            });


            for (int i = 0; i < balancedUsers.size(); i++) {

                if (balances.get(balancedUsers.get(i)) > 0) {

                    int positiveBalance = balances.get(balancedUsers.get(i));

                    for (int j = balancedUsers.size() - 1; j >= 0; j--) {

                        if (!balancedUsers.get(i).equals(balancedUsers.get(j)) && balances.get(balancedUsers.get(j)) < 0) {

                            int negativeBalance = balances.get(balancedUsers.get(j));
                            int transfer = Math.min(positiveBalance, Math.abs(negativeBalance));

                            if (transfer > 0) {

                                positiveBalance -= transfer;
                                negativeBalance += transfer;
                                balances.put(balancedUsers.get(i), positiveBalance);
                                balances.put(balancedUsers.get(j), negativeBalance);
                                addToDebt(balancedUsers.get(j), balancedUsers.get(i), transfer);

                            }
                        }
                    }
                }
            }


        }


        for (Payment payment : payments) {

            boolean found = false;

            for (int i = 0; i < debts.size(); i++) {

                if (debts.get(i).getFromId().equals(payment.getFromId()) && debts.get(i).getToId().equals(payment.getToId())) {

                    int sum = debts.get(i).getSum() - payment.getSum();

                    if (sum < 0) {
                        debts.get(i).setFromId(payment.getToId());
                        debts.get(i).setToId(payment.getFromId());
                        debts.get(i).setSum(Math.abs(sum));
                    } else {
                        debts.get(i).setSum(Math.abs(sum));
                    }

                    found = true;
                    break;

                } else if (debts.get(i).getFromId().equals(payment.getToId()) && debts.get(i).getToId().equals(payment.getFromId())) {

                    int sum = debts.get(i).getSum() + payment.getSum();

                    if (sum < 0) {
                        debts.get(i).setFromId(payment.getFromId());
                        debts.get(i).setToId(payment.getToId());
                        debts.get(i).setSum(Math.abs(sum));
                    } else {
                        debts.get(i).setSum(Math.abs(sum));
                    }

                    found = true;
                    break;

                }

            }


            if (!found) {
                debts.add(new Payment("", payment.getToId(), payment.getFromId(), payment.getSum()));
            }

        }

        ArrayList<Payment> zeroDebts = new ArrayList<>();

        for (Payment payment : debts)
        {

            if (payment.getSum()==0) {
                zeroDebts.add(payment);
            }

        }

        debts.removeAll(zeroDebts);


        DebtAdapter.this.notifyDataSetChanged();


    }



    private void calculateOld() {


        for (Cost cost : costs) {

            for (CostPerson costPerson : cost.getCostPersons()) {

                int balance;

                if (balances.containsKey(costPerson.getUserId())) {
                    balance = balances.get(costPerson.getUserId());
                } else {
                    balance = 0;
                }

                balance = balance + costPerson.getPaid() - costPerson.getSum() - costPerson.getPersonal();
                balances.put(costPerson.getUserId(), balance);

            }

        }

        balancedUsers.clear();
        balancedUsers.addAll(balances.keySet());

        Collections.sort(balancedUsers, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return balances.get(o1).compareTo(balances.get(o2));
            }
        });


        for (int i = 0; i < balancedUsers.size(); i++) {

            Log.d("BALANCE", users.get(balancedUsers.get(i)).getName()+" "+balances.get(balancedUsers.get(i)));

            if (balances.get(balancedUsers.get(i)) > 0) {

                int positiveBalance = balances.get(balancedUsers.get(i));

                for (int j = balancedUsers.size() - 1; j >= 0; j--) {

                    if (!balancedUsers.get(i).equals(balancedUsers.get(j)) && balances.get(balancedUsers.get(j)) < 0) {

                        int negativeBalance = balances.get(balancedUsers.get(j));
                        int transfer = Math.min(positiveBalance, Math.abs(negativeBalance));

                        if (transfer > 0) {

                            positiveBalance -= transfer;
                            negativeBalance += transfer;
                            balances.put(balancedUsers.get(i), positiveBalance);
                            balances.put(balancedUsers.get(j), negativeBalance);
                            debts.add(new Payment("", balancedUsers.get(j), balancedUsers.get(i), transfer));

                        }

                    }

                }

            }

        }

        for (Payment payment : payments) {

            boolean found = false;

            for (int i = 0; i < debts.size(); i++) {

                if (debts.get(i).getFromId().equals(payment.getFromId()) && debts.get(i).getToId().equals(payment.getToId())) {

                    int sum = debts.get(i).getSum() - payment.getSum();

                    if (sum < 0) {
                        debts.get(i).setFromId(payment.getToId());
                        debts.get(i).setToId(payment.getFromId());
                        debts.get(i).setSum(Math.abs(sum));
                    } else {
                        debts.get(i).setSum(Math.abs(sum));
                    }

                    found = true;
                    break;

                } else if (debts.get(i).getFromId().equals(payment.getToId()) && debts.get(i).getToId().equals(payment.getFromId())) {

                    int sum = debts.get(i).getSum() + payment.getSum();

                    if (sum < 0) {
                        debts.get(i).setFromId(payment.getFromId());
                        debts.get(i).setToId(payment.getToId());
                        debts.get(i).setSum(Math.abs(sum));
                    } else {
                        debts.get(i).setSum(Math.abs(sum));
                    }

                    found = true;
                    break;

                }

            }


            if (!found) {
                debts.add(new Payment("", payment.getToId(), payment.getFromId(), payment.getSum()));
            }

        }

        ArrayList<Payment> zeroDebts = new ArrayList<>();

        for (Payment payment : debts)
        {

            if (payment.getSum()==0) {
                zeroDebts.add(payment);
            }

        }

        debts.removeAll(zeroDebts);


        DebtAdapter.this.notifyDataSetChanged();


    }


    private void addToDebt(String fromId, String toId, int cost)
    {

        boolean found = false;

        for (int i = 0; i < debts.size(); i++) {

            if (debts.get(i).getFromId().equals(fromId) && debts.get(i).getToId().equals(toId)) {

                int sum = debts.get(i).getSum() + cost;

                if (sum < 0) {
                    debts.get(i).setFromId(toId);
                    debts.get(i).setToId(fromId);
                    debts.get(i).setSum(Math.abs(sum));
                } else {
                    debts.get(i).setSum(Math.abs(sum));
                }

                found = true;
                break;

            } else if (debts.get(i).getFromId().equals(toId) && debts.get(i).getToId().equals(fromId)) {

                int sum = debts.get(i).getSum() - cost;

                if (sum < 0) {
                    debts.get(i).setFromId(fromId);
                    debts.get(i).setToId(toId);
                    debts.get(i).setSum(Math.abs(sum));
                } else {
                    debts.get(i).setSum(Math.abs(sum));
                }

                found = true;
                break;

            }

        }


        if (!found) {
            debts.add(new Payment("", fromId, toId, cost));
        }

    }





}
