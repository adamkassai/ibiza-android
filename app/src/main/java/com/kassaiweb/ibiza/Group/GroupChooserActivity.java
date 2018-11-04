package com.kassaiweb.ibiza.Group;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.kassaiweb.ibiza.Constant;
import com.kassaiweb.ibiza.Data.Group;
import com.kassaiweb.ibiza.Data.GroupInUser;
import com.kassaiweb.ibiza.Data.GroupMember;
import com.kassaiweb.ibiza.MainActivity;
import com.kassaiweb.ibiza.R;
import com.kassaiweb.ibiza.Util.EncryptUtil;
import com.kassaiweb.ibiza.Util.SPUtil;

import java.util.ArrayList;
import java.util.List;

public class GroupChooserActivity extends AppCompatActivity implements
        CreateGroupDialog.CreateGroupListener, GroupAdapter.GroupAdapterListener,
        JoinGroupDialog.JoinGroupListener {

    private static final String TAG = GroupChooserActivity.class.getSimpleName();
    private TextView tvWelcome;
    private TextView tvOptions;
    private RecyclerView recyclerView;
    private TextView tvMakeNew;
    private TextView tvConnectToOther;
    private TextView tvJoinById;
    private ProgressBar progress;

    private GroupAdapter adapter;
    private List<Group> userGroups = new ArrayList<>();

    private CreateGroupDialog createGroupDialog;
    private ConfirmGroupDialog confirmGroupDialog;
    private JoinGroupDialog joinGroupDialog;

    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chooser);

        tvWelcome = findViewById(R.id.group_chooser_welcome);
        tvOptions = findViewById(R.id.group_chooser_options);

        progress = findViewById(R.id.group_chooser_progress);

        recyclerView = findViewById(R.id.group_chooser_recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(GroupChooserActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new GroupAdapter(userGroups, GroupChooserActivity.this);
        recyclerView.setAdapter(adapter);

        tvMakeNew = findViewById(R.id.group_chooser_make_new);
        tvConnectToOther = findViewById(R.id.group_chooser_connect_to_other);
        tvJoinById = findViewById(R.id.group_chooser_join);

        createGroupDialog = new CreateGroupDialog(GroupChooserActivity.this, GroupChooserActivity.this);

        tvWelcome.setText(getString(R.string.welcome_xy, SPUtil.getString(Constant.ACCOUNT_NAME, "idegen")));

        tvOptions.setText("Még nem vagy egy csoport tagja sem!");

        database = FirebaseDatabase.getInstance();

        String fbUserId = SPUtil.getString(Constant.CURRENT_USER_ID, null);
        database.getReference("fb_users").child(fbUserId)
                .child("groups")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        tvOptions.setText("Korábbi csoportok, amikhez csatlakoztál:");
                        GroupInUser groupInUser = dataSnapshot.getValue(GroupInUser.class);

                        database.getReference("groups").child(groupInUser.getGroupId())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Group group = dataSnapshot.getValue(Group.class);
                                        group.setGroupKey(dataSnapshot.getKey());
                                        userGroups.add(group);
                                        adapter.notifyItemInserted(userGroups.size() - 1);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.e(TAG, "onCancelled called when trying to get group by id!");
                                        Snackbar.make(findViewById(android.R.id.content),
                                                getString(R.string.group_connection_error_msg),
                                                Snackbar.LENGTH_LONG).show();
                                    }
                                });
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        // TODO
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        // TODO
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        // TODO
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // TODO
                    }
                });
        tvMakeNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroupDialog.show();
            }
        });
        tvConnectToOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(GroupChooserActivity.this);
                integrator.setBeepEnabled(false);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt(getString(R.string.scanner_hint));
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
            }
        });

        tvJoinById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinGroupDialog = new JoinGroupDialog(GroupChooserActivity.this, GroupChooserActivity.this);
                joinGroupDialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() != null) {
                progress.setVisibility(View.VISIBLE);
                // The QR code looks like {firebaseId.reverse},{password.reverse}
                String scanned = result.getContents();
                try {
                    final String firebaseGroupId = EncryptUtil.reverse(scanned.split(",")[0]);
                    final String firebaseGroupPass = EncryptUtil.reverse(scanned.split(",")[1]);
                    joinGroup(firebaseGroupId, firebaseGroupPass);
                } catch (Exception e) {
                    progress.setVisibility(View.GONE);
                    Snackbar.make(findViewById(android.R.id.content),
                            getString(R.string.group_connection_qr_error_msg),
                            Snackbar.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startMainAct() {
        progress.setVisibility(View.GONE);
        startActivity(new Intent(GroupChooserActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onGroupAdded() {
        createGroupDialog.dismiss();
    }

    @Override
    public void onGroupItemClick(Group item) {
        confirmGroupDialog = new ConfirmGroupDialog(GroupChooserActivity.this, item, GroupChooserActivity.this);
        confirmGroupDialog.show();
    }

    public void connectToGroup(final String groupId) {
        database.getReference("groups").child(groupId).child("members")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String currentUserId = SPUtil.getString(Constant.CURRENT_USER_ID, null);
                        boolean isCurrentUserAlreadyMemberOfGroup = false;
                        for(DataSnapshot member : dataSnapshot.getChildren()) {
                            GroupMember groupMember = member.getValue(GroupMember.class);
                            if(groupMember.getUserId().equals(currentUserId)) {
                                isCurrentUserAlreadyMemberOfGroup = true;
                                break;
                            }
                        }
                        if(!isCurrentUserAlreadyMemberOfGroup) {
                            // current user has not joined the group yet, so:
                            // 1) groups/{groupId}/members push
                            DatabaseReference newGroupMemberRef =
                                    database.getReference("groups").child(groupId).child("members").push();

                            GroupMember newGroupMember = new GroupMember(0, currentUserId);
                            newGroupMemberRef.setValue(newGroupMember);

                            database.getReference("groups").child(groupId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Group group = dataSnapshot.getValue(Group.class);

                                            String currentUserId =
                                                    SPUtil.getString(Constant.CURRENT_USER_ID, null);

                                            // 2) fb_users/{userId}/groups push
                                            DatabaseReference userGroupsRef = database
                                                    .getReference("fb_users")
                                                    .child(currentUserId)
                                                    .child("groups").push();

                                            GroupInUser groupInUser = new GroupInUser(
                                                    groupId,
                                                    group.getName()
                                            );
                                            userGroupsRef.setValue(groupInUser);

                                            SPUtil.putString(Constant.CURRENT_GROUP_ID, groupId);
                                            SPUtil.putString(Constant.CURRENT_GROUP_NAME, group.getName());
                                            startMainAct();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            progress.setVisibility(View.GONE);
                                            Snackbar.make(findViewById(android.R.id.content),
                                                    getString(R.string.group_connection_error_msg),
                                                    Snackbar.LENGTH_LONG).show();
                                        }
                                    });
                        } else {
                            database.getReference("groups").child(groupId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Group group = dataSnapshot.getValue(Group.class);

                                            SPUtil.putString(Constant.CURRENT_GROUP_ID, groupId);
                                            SPUtil.putString(Constant.CURRENT_GROUP_NAME, group.getName());
                                            startMainAct();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            progress.setVisibility(View.GONE);
                                            Snackbar.make(findViewById(android.R.id.content),
                                                    getString(R.string.group_connection_error_msg),
                                                    Snackbar.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progress.setVisibility(View.GONE);
                        Snackbar.make(findViewById(android.R.id.content),
                                getString(R.string.group_connection_error_msg),
                                Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void joinGroup(final String groupId, final String groupPass) {
        progress.setVisibility(View.VISIBLE);
        database.getReference("groups").child(groupId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Group group = dataSnapshot.getValue(Group.class);
                        if(group == null || group.getPassword() == null) {
                            Snackbar.make(findViewById(android.R.id.content),
                                    "A csoport ID nem jó!", Snackbar.LENGTH_LONG).show();
                        } else if(group.getPassword().equals(groupPass)) {
                            if(confirmGroupDialog != null) {
                                confirmGroupDialog.dismiss();
                            }
                            if(joinGroupDialog != null) {
                                joinGroupDialog.dismiss();
                            }
                            connectToGroup(groupId);
                        } else {
                            Snackbar.make(findViewById(android.R.id.content),
                                    "A csoport jelszava nem jó!", Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progress.setVisibility(View.GONE);
                        Snackbar.make(findViewById(android.R.id.content),
                                getString(R.string.group_connection_error_msg),
                                Snackbar.LENGTH_LONG).show();
                    }
                });
    }
}
