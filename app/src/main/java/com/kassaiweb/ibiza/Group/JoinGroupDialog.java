package com.kassaiweb.ibiza.Group;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kassaiweb.ibiza.R;
import com.kassaiweb.ibiza.Util.EncryptUtil;

public class JoinGroupDialog extends Dialog {

    public interface JoinGroupListener {
        /**
         * Implementations should authenticate the user with:
         * @param groupId ID of the group
         * @param groupPass password of the group
         */
        void joinGroup(String groupId, String groupPass);
    }

    private JoinGroupListener listener;

    private EditText etGroupId, etGroupPass;
    private TextView ok;

    public JoinGroupDialog(@NonNull Context context, JoinGroupListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_group_dialog);

        etGroupId = findViewById(R.id.join_group_dialog_name);
        etGroupPass = findViewById(R.id.join_group_dialog_password);
        ok = findViewById(R.id.join_group_dialog_ok);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.joinGroup(EncryptUtil.reverse(etGroupId.getText().toString()),
                        etGroupPass.getText().toString());
            }
        });
    }
}
