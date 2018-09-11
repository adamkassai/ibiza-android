package com.kassaiweb.ibiza.Cost;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import java.util.ArrayList;

public class CostCreateFragment extends Fragment {

    private String userId;

    private RecyclerView mRecyclerView;
    private CostPersonAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String costId;
    private Cost cost;

    private EditText description;
    private EditText total;
    private Button send;
    private Button divide;

    private EditText totalPaid;
    private EditText totalSum;
    private EditText totalPersonal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();
        if (args!=null) {
            costId = args.getString("costId", null);
        }


        return inflater.inflate(R.layout.fragment_cost_create, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {

        totalPaid = view.findViewById(R.id.cost_total_paid);
        totalSum = view.findViewById(R.id.cost_total_sum);
        totalPersonal = view.findViewById(R.id.cost_total_personal);

        description = view.findViewById(R.id.cost_description);
        total = view.findViewById(R.id.cost_total);

        if (costId !=null) {

            mAdapter = new CostPersonAdapter(null, totalPaid, totalSum, totalPersonal);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference pollsRef = database.getReference("costs").child(costId);

            pollsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {

                    cost = dataSnapshot.getValue(Cost.class);
                    mAdapter.setCost(cost);
                    description.setText(cost.getDescription());
                    total.setText(Integer.toString(cost.getTotal()));

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w("TaskCreateFragment", "Failed to read value.", error.toException());
                }
            });

        }else{

            cost = new Cost();
            mAdapter = new CostPersonAdapter(cost, totalPaid, totalSum, totalPersonal);


            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference usersRef = database.getReference("users");

            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    ArrayList<CostPerson> costPersons = new ArrayList<>();

                    for (DataSnapshot userDataSnapshot : dataSnapshot.getChildren()) {
                        User user = userDataSnapshot.getValue(User.class);
                        costPersons.add(new CostPerson(user.getId()));
                    }

                    cost.setCostPersons(costPersons);
                    mAdapter.setCost(cost);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("Users", "Failed to read value.", error.toException());
                }
            });

        }



        mRecyclerView = view.findViewById(R.id.cost_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        userId = SPUtil.getString(Constant.USERID, null);

        send = view.findViewById(R.id.cost_save);
        divide  = view.findViewById(R.id.cost_divide);


        mRecyclerView.setAdapter(mAdapter);

        total.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (total.getText().toString()!=null && !total.getText().toString().equals("")) {
                        cost.setTotal(Integer.parseInt(total.getText().toString()));

                        for (CostPerson costPerson : cost.getCostPersons()) {
                            costPerson.setNewValue(false);
                            costPerson.setFix(false);
                        }
                        mAdapter.setCost(cost);
                    }
                }
            }
        });



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference costRef;

                if (costId!=null) {
                    costRef = database.getReference("costs").child(costId);
                }else{
                    costRef = database.getReference("costs").push();
                }


                if (description.getText()==null || description.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Megnevezés megadása kötelező", Toast.LENGTH_LONG).show();
                    return;
                }

                if (total.getText()==null || total.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Teljes összeg megadása kötelező", Toast.LENGTH_LONG).show();
                    return;
                }

                int totalInt = Integer.parseInt(total.getText().toString());
                int paid = Integer.parseInt(totalPaid.getText().toString());
                int sum = Integer.parseInt(totalSum.getText().toString());
                int personal = Integer.parseInt(totalPersonal.getText().toString());

                if (paid!=totalInt) {
                    Toast.makeText(getContext(), "A fizetett összeg nem egyezik a teljes összeggel", Toast.LENGTH_LONG).show();
                    return;
                }

                if (sum+personal!=totalInt) {
                    Toast.makeText(getContext(), "A végösszeg nem egyezik a teljes összeggel", Toast.LENGTH_LONG).show();
                    return;
                }

                if (costId!=null) {
                    cost.setId(costId);
                }else{
                    cost.setId(costRef.getKey());
                }
                cost.setDescription(description.getText().toString());
                cost.setTotal(totalInt);
                cost.setCostPersons(mAdapter.getCost().getCostPersons());
                costRef.setValue(cost);

                MainActivity activity = (MainActivity)getActivity();
                activity.replaceFragment(new CostPagerFragment());

            }
        });


        divide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAdapter.reDivide();

            }
        });


    }

}
