package com.kassaiweb.ibiza.GroupInfo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kassaiweb.ibiza.Constant;
import com.kassaiweb.ibiza.Data.Group;
import com.kassaiweb.ibiza.Data.GroupMember;
import com.kassaiweb.ibiza.R;
import com.kassaiweb.ibiza.Util.DateUtil;
import com.kassaiweb.ibiza.Util.SPUtil;

import java.util.ArrayList;
import java.util.List;

public class GroupInfoFragment extends Fragment {

    private TextView tvCreatedAt;
    private RecyclerView recyclerView;
    private GroupMemberAdapter adapter;
    private List<GroupMember> groupMembers = new ArrayList<>();

    private String groupId = SPUtil.getString(Constant.CURRENT_GROUP_ID, "");

    public GroupInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ImageView ivQR = view.findViewById(R.id.group_info_mini_barcode);
        tvCreatedAt = view.findViewById(R.id.group_info_created_at);
        recyclerView = view.findViewById(R.id.group_info_members);

        ivQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("groups").child(groupId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Group group = dataSnapshot.getValue(Group.class);
                                new QRCodeDialog(getContext(), groupId, group.getPassword()).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // TODO
                            }
                        });
            }
        });

        FirebaseDatabase.getInstance().getReference("groups").child(groupId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Group group = dataSnapshot.getValue(Group.class);
                        String createdDay = DateUtil.formatDay(DateUtil.convert(group.getCreatedAt()));
                        tvCreatedAt.setText("Csoport létrehozásának dátuma: " + createdDay);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // TODO
                    }
                });

        FirebaseDatabase.getInstance().getReference("groups").child(groupId)
                .child("members").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                GroupMember member = dataSnapshot.getValue(GroupMember.class);
                groupMembers.add(member);
                adapter.notifyItemInserted(groupMembers.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                GroupMember newTask = dataSnapshot.getValue(GroupMember.class);
                for (int i = 0; i < groupMembers.size(); ++i) {
                    GroupMember member = groupMembers.get(i);
                    if (member.getFirebaseId().equals(dataSnapshot.getKey())) {
                        groupMembers.remove(member);
                        groupMembers.add(i, newTask);
                        adapter.notifyItemChanged(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                for (int i = 0; i < groupMembers.size(); ++i) {
                    GroupMember task = groupMembers.get(i);
                    if (dataSnapshot.getKey().equals(task.getFirebaseId())) {
                        groupMembers.remove(task);
                        adapter.notifyItemRemoved(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new GroupMemberAdapter(groupMembers);
        recyclerView.setAdapter(adapter);
    }
}
