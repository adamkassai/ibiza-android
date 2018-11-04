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
import com.kassaiweb.ibiza.Util.EncryptUtil;
import com.kassaiweb.ibiza.Util.SPUtil;

public class ConfirmGroupDialog extends Dialog {

    private TextView tvTitle;
    private EditText etPassword;
    private TextView btnOk;

    private Group group;

    private JoinGroupDialog.JoinGroupListener listener;

    public ConfirmGroupDialog(@NonNull Context context, Group group, JoinGroupDialog.JoinGroupListener listener) {
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
                listener.joinGroup(group.getGroupKey(), etPassword.getText().toString());
            }
        });
    }
}
