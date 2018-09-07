package com.kassaiweb.ibiza;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.kassaiweb.ibiza.Movie.Movie;
import com.kassaiweb.ibiza.User.ChangeUserAdapter;
import com.kassaiweb.ibiza.User.ChangeUserFragment;
import com.kassaiweb.ibiza.User.User;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class PickOneFragment extends Fragment {

    private ArrayList<User> users = new ArrayList<>();
    private TextView usernameTextView;
    private ImageView userImage;
    private Button button;
    private View progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_pick_one, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

         usernameTextView = view.findViewById(R.id.username);
         userImage = view.findViewById(R.id.userImage);
         button = view.findViewById(R.id.button_again);
         progress = view.findViewById(R.id.progress);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                users.clear();

                for (DataSnapshot userDataSnapshot : dataSnapshot.getChildren()) {
                    users.add(userDataSnapshot.getValue(User.class));
                }


                int random = new Random().nextInt(users.size());
                String user = users.get(random).getName();
                String image = users.get(random).getImage();

                if (user!=null) {
                    usernameTextView.setText(user);
                }


                ImageLoader.getInstance().displayImage(image, userImage);
                progress.setVisibility(View.GONE);
                userImage.setVisibility(View.VISIBLE);


                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int random = new Random().nextInt(users.size());
                        String user = users.get(random).getName();
                        String image = users.get(random).getImage();

                        if (user!=null) {
                            usernameTextView.setText(user);
                        }

                        ImageLoader.getInstance().displayImage(image, userImage);
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ChangeUser", "Failed to read value.", error.toException());
            }
        });




    }

}
