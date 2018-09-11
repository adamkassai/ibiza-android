package com.kassaiweb.ibiza.Task;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.kassaiweb.ibiza.Constant;
import com.kassaiweb.ibiza.FrontPageFragment;
import com.kassaiweb.ibiza.MainActivity;
import com.kassaiweb.ibiza.R;
import com.kassaiweb.ibiza.Util.NotificationUtil;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class TaskCreateFragment extends Fragment {

    private String userId;

    private RecyclerView mRecyclerView;
    private VolunteerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView description;
    private TextView volunteerNumber;
    private TextView deadline;
    private Button send;

    private RadioButton assignRadioButton;
    private RadioButton volunteerRadioButton;
    private RadioButton randomRadioButton;

    private View deadlineLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_task_create, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {

        mRecyclerView = view.findViewById(R.id.task_volunteers_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        SharedPreferences prefs = getActivity().getSharedPreferences(Constant.APP_NAME, MODE_PRIVATE);
        userId = prefs.getString(Constant.USERID, null);

        description = view.findViewById(R.id.task_description);
        volunteerNumber = view.findViewById(R.id.task_volunteerNumber);
        deadline = view.findViewById(R.id.task_deadline);
        deadlineLayout = view.findViewById(R.id.task_deadline_layout);

        send = view.findViewById(R.id.task_save);

        assignRadioButton = view.findViewById(R.id.task_assign_RadioButton);
        volunteerRadioButton = view.findViewById(R.id.task_volunteer_RadioButton);
        randomRadioButton = view.findViewById(R.id.task_random_RadioButton);

        mAdapter = new VolunteerAdapter(null);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        assignRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (assignRadioButton.isChecked()) {
                    volunteerRadioButton.setChecked(false);
                    randomRadioButton.setChecked(false);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    deadlineLayout.setVisibility(View.GONE);
                }else {
                    mRecyclerView.setVisibility(View.GONE);
                }

            }
        });

        volunteerRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (volunteerRadioButton.isChecked()) {
                    assignRadioButton.setChecked(false);
                    randomRadioButton.setChecked(false);
                    mRecyclerView.setVisibility(View.GONE);
                    deadlineLayout.setVisibility(View.VISIBLE);
                }else {
                    deadlineLayout.setVisibility(View.VISIBLE);
                }

            }
        });

        randomRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (randomRadioButton.isChecked()) {
                    volunteerRadioButton.setChecked(false);
                    assignRadioButton.setChecked(false);
                    mRecyclerView.setVisibility(View.GONE);
                    deadlineLayout.setVisibility(View.GONE);
                }

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference taskRef = database.getReference("tasks").push();

                if (description.getText()==null || description.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Leírás megadása kötelező", Toast.LENGTH_LONG).show();
                    return;
                }

                if (volunteerNumber.getText()==null || volunteerNumber.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Résztvevők számának megadása kötelező", Toast.LENGTH_LONG).show();
                    return;
                }

                int volunteerN = Integer.parseInt(volunteerNumber.getText().toString());

                Task task = new Task(taskRef.getKey(), description.getText().toString(), userId, volunteerN);



                if (assignRadioButton.isChecked()) {

                    if (mAdapter.getVolunteers().size()!=volunteerN) {
                        Toast.makeText(getContext(), "A hozzárendelt résztvevők száma nem egyezik a szükséges létszámmal", Toast.LENGTH_LONG).show();
                        return;
                    }

                    task.setVolunteers(mAdapter.getVolunteers());
                    task.setType(Constant.TASK_ASSIGNED);
                }

                if (volunteerRadioButton.isChecked()) {

                    if (deadline.getText()==null || deadline.getText().toString().equals("")) {
                        Toast.makeText(getContext(), "Határidő megadása kötelező", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(task.getDate());
                    cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(deadline.getText().toString()));

                    task.setDeadline(cal.getTime());
                    task.setType(Constant.TASK_VOLUNTEERS);
                }

                if (randomRadioButton.isChecked()) {
                    task.setVolunteers(mAdapter.getRandomVolunteers(volunteerN));
                    task.setType(Constant.TASK_RANDOM);
                }


                taskRef.setValue(task);

                MainActivity activity = (MainActivity)getActivity();
                NotificationUtil.sendNotification("Új feladat", task.getDescription(), userId);
                activity.replaceFragment(new TaskListFragment());

            }
        });




    }

}
