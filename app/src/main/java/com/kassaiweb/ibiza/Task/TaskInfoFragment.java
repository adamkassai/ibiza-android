package com.kassaiweb.ibiza.Task;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kassaiweb.ibiza.Constant;
import com.kassaiweb.ibiza.Data.FbUser;
import com.kassaiweb.ibiza.Data.GroupMember;
import com.kassaiweb.ibiza.MainActivity;
import com.kassaiweb.ibiza.R;
import com.kassaiweb.ibiza.Util.DateUtil;
import com.kassaiweb.ibiza.Util.SPUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TaskInfoFragment extends Fragment implements ParticipantAdapter.ParticipantAdapterListener {

    private static final String TAG = TaskInfoFragment.class.getSimpleName();

    private static Task task;
    private Calendar deadline;
    private int selectedPriority = 1;
    private ParticipantAdapter adapter;
    private List<FbUser> users = new ArrayList<>();

    private EditText etName;
    private EditText etDescription;
    private ImageView ivPriority1;
    private ImageView ivPriority2;
    private ImageView ivPriority3;
    private ImageView ivPriority4;
    private ImageView ivPriority5;
    private TextView dateChooser;
    private TextView tvNotReadyYet;
    private RecyclerView rvParticipants;

    /* After user decided on a date, store those in our calendar variable and then start the TimePickerDialog immediately */
    private final DatePickerDialog.OnDateSetListener mDateDataSet = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if(deadline == null) {
                deadline = Calendar.getInstance();
            }
            deadline.set(Calendar.YEAR, year);
            deadline.set(Calendar.MONTH, monthOfYear);
            deadline.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            new TimePickerDialog(getContext(), mTimeDataSet, deadline.get(Calendar.HOUR_OF_DAY), deadline.get(Calendar.MINUTE), false).show();
        }
    };

    /* After user decided on a time, save them into our calendar instance, and now parse what our calendar has into the TextView */
    private final TimePickerDialog.OnTimeSetListener mTimeDataSet = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            deadline.set(Calendar.HOUR_OF_DAY, hourOfDay);
            deadline.set(Calendar.MINUTE, minute);
            deadline.set(Calendar.SECOND, 0);
            dateChooser.setText(DateUtil.formatMinute(deadline));
        }
    };

    /**
     * You should NOT use this, because it needs a task object!
     * You yould use {@link #newInstance(Task)}
     */
    public TaskInfoFragment() {
    }

    public static TaskInfoFragment newInstance(Task task) {
        TaskInfoFragment.task = task;
        return new TaskInfoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (task == null) {
            throw new RuntimeException("task is NULL!!!");
        }
        etName = view.findViewById(R.id.task_info_name);
        etDescription = view.findViewById(R.id.task_info_description);
        ivPriority1 = view.findViewById(R.id.task_info_1);
        ivPriority2 = view.findViewById(R.id.task_info_2);
        ivPriority3 = view.findViewById(R.id.task_info_3);
        ivPriority4 = view.findViewById(R.id.task_info_4);
        ivPriority5 = view.findViewById(R.id.task_info_5);

        dateChooser = view.findViewById(R.id.task_info_date);
        ImageView ivClearDate = view.findViewById(R.id.task_info_date_clear);
        tvNotReadyYet = view.findViewById(R.id.task_info_unready);
        rvParticipants = view.findViewById(R.id.task_info_participants);
        TextView tvSave = view.findViewById(R.id.task_info_save);

        resetPriorityBackgrounds();
        resetPriorityListeners();

        if (task.isReady()) {
            tvNotReadyYet.setVisibility(View.VISIBLE);
            tvNotReadyYet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvNotReadyYet.setVisibility(View.GONE);
                    task.setReady(false);
                }
            });
        }

        etName.setText(task.getName());
        etDescription.setText(task.getDescription());
        if (task.getDeadline() != null && !task.getDeadline().isEmpty()) {
            deadline = DateUtil.convert(task.getDeadline());
            dateChooser.setText(DateUtil.formatMinute(deadline));
        }

        selectedPriority = task.getPriority();
        switch (selectedPriority) {
            case 1:
                ivPriority1.setBackgroundResource(R.drawable.image_border);
                break;
            case 2:
                ivPriority2.setBackgroundResource(R.drawable.image_border);
                break;
            case 3:
                ivPriority3.setBackgroundResource(R.drawable.image_border);
                break;
            case 4:
                ivPriority4.setBackgroundResource(R.drawable.image_border);
                break;
            case 5:
                ivPriority5.setBackgroundResource(R.drawable.image_border);
                break;
            default:
                Log.i(TAG, "the priority is not specified!");
                break;
        }

        dateChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar tempCal;
                if(deadline == null) {
                    tempCal = Calendar.getInstance();
                } else {
                    tempCal = deadline;
                }
                new DatePickerDialog(getContext(), mDateDataSet, tempCal.get(Calendar.YEAR),
                        tempCal.get(Calendar.MONTH), tempCal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        ivClearDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deadline = null;
                dateChooser.setText(R.string.set_deadline);
            }
        });

        // participants
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvParticipants.setLayoutManager(layoutManager);
        adapter = new ParticipantAdapter(task, users, TaskInfoFragment.this);
        rvParticipants.setAdapter(adapter);

        String currentGroupId = SPUtil.getString(Constant.CURRENT_GROUP_ID, "");
        DatabaseReference groupMemberRef = FirebaseDatabase.getInstance().getReference("groups")
                .child(currentGroupId).child("members");

        groupMemberRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dsGroupMember : dataSnapshot.getChildren()) {
                    String firebaseUserId = dsGroupMember.getValue(GroupMember.class).getUserId();
                    FirebaseDatabase.getInstance().getReference("fb_users").child(firebaseUserId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    FbUser user = dataSnapshot.getValue(FbUser.class);
                                    user.setFirebaseId(dataSnapshot.getKey());
                                    users.add(user);
                                    adapter.notifyItemInserted(users.size());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // TODO
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // TODO
            }
        });

        // saving, or creating
        if (task.getFirebaseId() == null) {
            ivPriority1.setBackgroundResource(R.drawable.image_border);
            selectedPriority = 1;
            tvSave.setText("Létrehozás");
        } else {
            ImageView ivDelete = view.findViewById(R.id.task_info_delete);
            ivDelete.setVisibility(View.VISIBLE);
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getContext()).setTitle("Biztos törlöd a feladatot?")
                            .setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseDatabase.getInstance().getReference("tasks")
                                            .child(task.getFirebaseId()).removeValue()
                                            .addOnCompleteListener(getCompleteListener());
                                }
                            })
                            .setNegativeButton("Nem", null).show();
                }
            });
        }

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etName.getText().toString().isEmpty()) {
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            "A nevet kötelező kitölteni!", Snackbar.LENGTH_LONG).show();
                    return;
                }
                task.setName(etName.getText().toString());
                task.setDescription(etDescription.getText().toString());
                task.setDeadline(DateUtil.convert(deadline));
                task.setPriority(selectedPriority);

                DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference("tasks");
                if (task.getFirebaseId() != null) {
                    // only update
                    tasksRef.child(task.getFirebaseId()).setValue(task).addOnCompleteListener(getCompleteListener());
                } else {
                    // push new task
                    task.setReady(false);
                    task.setCreatedAt(DateUtil.convert(Calendar.getInstance()));
                    task.setCreatorId(SPUtil.getString(Constant.CURRENT_USER_ID, ""));
                    task.setGroupId(SPUtil.getString(Constant.CURRENT_GROUP_ID, ""));

                    DatabaseReference newTaskRef = tasksRef.push();
                    newTaskRef.setValue(task).addOnCompleteListener(getCompleteListener());
                }
            }
        });
    }

    private OnCompleteListener<Void> getCompleteListener() {
        return new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                MainActivity activity = (MainActivity) getActivity();
                activity.replaceFragment(new TaskListFragment());
            }
        };
    }

    private void resetPriorityListeners() {
        ivPriority1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPriorityBackgrounds();
                selectedPriority = 1;
                ivPriority1.setBackgroundResource(R.drawable.image_border);
            }
        });
        ivPriority2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPriorityBackgrounds();
                selectedPriority = 2;
                ivPriority2.setBackgroundResource(R.drawable.image_border);
            }
        });
        ivPriority3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPriorityBackgrounds();
                selectedPriority = 3;
                ivPriority3.setBackgroundResource(R.drawable.image_border);
            }
        });
        ivPriority4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPriorityBackgrounds();
                selectedPriority = 4;
                ivPriority4.setBackgroundResource(R.drawable.image_border);
            }
        });
        ivPriority5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPriorityBackgrounds();
                selectedPriority = 5;
                ivPriority5.setBackgroundResource(R.drawable.image_border);
            }
        });
    }

    private void resetPriorityBackgrounds() {
        ivPriority1.setBackgroundColor(Color.TRANSPARENT);
        ivPriority2.setBackgroundColor(Color.TRANSPARENT);
        ivPriority3.setBackgroundColor(Color.TRANSPARENT);
        ivPriority4.setBackgroundColor(Color.TRANSPARENT);
        ivPriority5.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void onUserChecked(FbUser user, boolean isChecked) {
        for(FbUser fbUser : users) {
            if(fbUser.getId().equals(user.getId())) {
                if(isChecked) {
                    // add
                    List<String> participantList = new ArrayList<>();
                    if(task.getParticipants() != null) {
                        participantList.addAll(task.getParticipants());
                    }
                    participantList.add(user.getFirebaseId());
                    task.setParticipants(participantList);
                } else {
                    // remove
                    List<String> participantList = new ArrayList<>();
                    if(task.getParticipants() != null) {
                        participantList.addAll(task.getParticipants());
                    }
                    participantList.remove(user.getFirebaseId());
                    task.setParticipants(participantList);
                }
                break;
            }
        }
    }
}
