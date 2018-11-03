package com.kassaiweb.ibiza.Group;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kassaiweb.ibiza.Data.Group;
import com.kassaiweb.ibiza.R;

public class ConfirmGroupDialog extends Dialog {

    public interface ConfirmGroupListener {
        void onGroupConfirmed(String groupId);

        void onGroupConfirmError(String errorMsg);
    }

    private TextView tvTitle;
    private EditText etPassword;
    private TextView btnOk;

    private Group group;

    private ConfirmGroupListener listener;

    public ConfirmGroupDialog(@NonNull Context context, Group group, ConfirmGroupListener listener) {
        super(context);
        this.group = group;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_group_dialog);

        tvTitle = findViewById(R.id.confirm_group_title);
        etPassword = findViewById(R.id.confirm_group_pass);
        btnOk = findViewById(R.id.confirm_group_ok);

        tvTitle.setText(getContext().getString(R.string.connect_to_group, group.getName()));

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (group != null && etPassword.getText().toString().equals(group.getPassword())) {
                    listener.onGroupConfirmed(group.getGroupKey());
                } else {
                    listener.onGroupConfirmError("The password is not correct!");
                }
            }
        });
    }
}
