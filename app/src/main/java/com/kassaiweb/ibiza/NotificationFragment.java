package com.kassaiweb.ibiza;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kassaiweb.ibiza.User.User;
import com.kassaiweb.ibiza.Util.NotificationUtil;
import com.kassaiweb.ibiza.Util.SPUtil;

public class NotificationFragment extends Fragment {

    private String userId;
    private EditText body;
    private Button send;
    private CheckBox anonim;
    private String title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {

        body = view.findViewById(R.id.notification_body);
        send = view.findViewById(R.id.notification_send);
        anonim = view.findViewById(R.id.notification_anonim);

        userId = SPUtil.getString(Constant.USERID, null);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users").child(userId);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final User user = dataSnapshot.getValue(User.class);



                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (anonim.isChecked()) {
                            title = "Majdnem Ibiza";
                        }else{
                            title = user.getName()+" üzenete Siófok lakóinak";
                        }

                        if (body.getText().toString()!=null && !body.getText().toString().equals("")) {
                            NotificationUtil.sendNotification(title, body.getText().toString(), userId);
                            Toast.makeText(getContext(), "Értesítés kiküldve", Toast.LENGTH_LONG).show();
                            body.setText("");
                        }else{
                            Toast.makeText(getContext(), "Szöveg megadása kötelező", Toast.LENGTH_LONG).show();
                        }

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("User", "Failed to read value.", error.toException());
                Toast.makeText(getContext(), "Hálózati hiba", Toast.LENGTH_LONG).show();
            }
        });


    }

}
