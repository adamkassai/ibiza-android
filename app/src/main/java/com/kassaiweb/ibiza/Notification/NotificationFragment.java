package com.kassaiweb.ibiza.Notification;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kassaiweb.ibiza.Constant;
import com.kassaiweb.ibiza.Data.CheckableGroupMember;
import com.kassaiweb.ibiza.Data.FbUser;
import com.kassaiweb.ibiza.Data.GroupMember;
import com.kassaiweb.ibiza.R;
import com.kassaiweb.ibiza.Data.User;
import com.kassaiweb.ibiza.Util.NotificationUtil;
import com.kassaiweb.ibiza.Util.SPUtil;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    private EditText etBody;
    private RecyclerView recyclerView;
    private List<CheckableGroupMember> members = new ArrayList<>();
    private CheckableGroupMemberAdapter adapter;
    private CheckBox cbAnonym;
    private TextView tvSend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        etBody = view.findViewById(R.id.notification_body);
        recyclerView = view.findViewById(R.id.notification_recyclerView);
        cbAnonym = view.findViewById(R.id.notification_anonym);
        tvSend = view.findViewById(R.id.notification_send);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CheckableGroupMemberAdapter(members);
        recyclerView.setAdapter(adapter);

        String groupId = SPUtil.getString(Constant.CURRENT_GROUP_ID, "");
        FirebaseDatabase.getInstance().getReference("groups").child(groupId).child("members")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot data : dataSnapshot.getChildren()) {
                            final GroupMember member = data.getValue(GroupMember.class);
                            member.setFirebaseId(data.getKey());

                            FirebaseDatabase.getInstance().getReference("fb_users").child(member.getUserId())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            CheckableGroupMember checkableGroupMember = new CheckableGroupMember();
                                            checkableGroupMember.setFirebaseId(member.getFirebaseId());
                                            checkableGroupMember.setUserId(member.getUserId());
                                            checkableGroupMember.setAccountType(member.getAccountType());
                                            checkableGroupMember.setChecked(Boolean.TRUE);
                                            checkableGroupMember.setName(dataSnapshot.getValue(FbUser.class).getName());
                                            checkableGroupMember.setImageUrl(dataSnapshot.getValue(FbUser.class).getImageUrl());
                                            members.add(checkableGroupMember);
                                            adapter.notifyItemInserted(members.size() - 1);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                        setSendButtonClickListener();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void setSendButtonClickListener() {
        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title;
                String groupName = SPUtil.getString(Constant.CURRENT_GROUP_NAME, "");
                if (cbAnonym.isChecked()) {
                    title = groupName + "csoport egyik tagja üzen";
                } else {
                    String creatorName = SPUtil.getString(Constant.ACCOUNT_NAME, "");
                    title = creatorName + " üzenete " + groupName + " tagjainak";
                }

                if (etBody.getText().toString() != null && !etBody.getText().toString().isEmpty()) {
                    String userId = SPUtil.getString(Constant.CURRENT_USER_ID, "");

                    Notification notif = new Notification(title, etBody.getText().toString(), userId);
                    List<String> to = new ArrayList<>();
                    for(CheckableGroupMember member : members) {
                        if(member.isChecked()) {
                            to.add(member.getFirebaseId());
                        }
                    }
                    if(to.isEmpty()) {
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                "Legalább egy személyt ki kell választani!",
                                Snackbar.LENGTH_LONG).show();
                        return;
                    }

                    notif.setTo(to);
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference messageRef = database.getReference("messages").push();
                    messageRef.setValue(notif);

                    // NotificationUtil.sendNotification(title, etBody.getText().toString(), userId);
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            "Értesítés kiküldve", Snackbar.LENGTH_LONG).show();
                    etBody.setText("");
                } else {
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            "Szöveg megadása kötelező", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

}
