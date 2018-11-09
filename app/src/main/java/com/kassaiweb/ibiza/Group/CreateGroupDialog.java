package com.kassaiweb.ibiza.Group;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kassaiweb.ibiza.Constant;
import com.kassaiweb.ibiza.R;
import com.kassaiweb.ibiza.Data.Group;
import com.kassaiweb.ibiza.Data.GroupInUser;
import com.kassaiweb.ibiza.Data.GroupMember;
import com.kassaiweb.ibiza.Util.DateUtil;
import com.kassaiweb.ibiza.Util.SPUtil;

import java.util.Calendar;

public class CreateGroupDialog extends Dialog {

    public interface CreateGroupListener {
        void onGroupAdded();
    }

    private CreateGroupListener listener;
    private EditText etName, etPassword;
    private TextView ok;

    public CreateGroupDialog(@NonNull Context context, CreateGroupListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group_dialog);

        etName = findViewById(R.id.create_group_dialog_name);
        etPassword = findViewById(R.id.create_group_dialog_password);
        ok = findViewById(R.id.create_group_dialog_ok);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String groupName = etName.getText().toString();
                String groupPass = etPassword.getText().toString();
                // TODO: encrypt password???

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                String firebaseUserId = SPUtil.getString(Constant.CURRENT_USER_ID, null);

                // groups push
                DatabaseReference newGroupRef = database.getReference("groups").push();
                Group newGroup = new Group(
                        groupName,
                        DateUtil.convert(Calendar.getInstance()),
                        firebaseUserId,
                        groupPass,
                        newGroupRef.getKey()
                );
                newGroupRef.setValue(newGroup);

                // groups/{newGroupId}/members push
                DatabaseReference newGroupMemberRef = database.getReference("groups")
                        .child(newGroupRef.getKey()).child("members").push();
                GroupMember newGroupMember = new GroupMember(
                        0,
                        firebaseUserId,
                        newGroupMemberRef.getKey()
                );
                newGroupMemberRef.setValue(newGroupMember);

                // fb_users/{userId}/groups push
                DatabaseReference userGroupsRef = database
                        .getReference("fb_users").child(firebaseUserId).child("groups").push();

                GroupInUser groupInUser = new GroupInUser(
                        newGroupRef.getKey(),
                        groupName
                );
                userGroupsRef.setValue(groupInUser);

                listener.onGroupAdded();
            }
        });
    }
}
