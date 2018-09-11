package com.kassaiweb.ibiza.User;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kassaiweb.ibiza.Constant;
import com.kassaiweb.ibiza.FrontPageFragment;
import com.kassaiweb.ibiza.MainActivity;
import com.kassaiweb.ibiza.R;
import com.kassaiweb.ibiza.Util.SPUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class ChangeUserAdapter extends RecyclerView.Adapter<ChangeUserAdapter.ViewHolder> {

    private ArrayList<User> users = new ArrayList<>();
    private String userId;
    private RadioButton lastChecked;
    MainActivity activity;
    DatabaseReference usersRef;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;
        public RadioButton userRadioButton;
        public ImageView userImageView;

        public ViewHolder(View v) {
            super(v);
            nameTextView = v.findViewById(R.id.changeUser_user);
            userRadioButton = v.findViewById(R.id.changeUser_radioButton);
            userImageView = v.findViewById(R.id.userImage);
        }
    }


    public ChangeUserAdapter(MainActivity activity) {

        this.activity = activity;
        this.userId = SPUtil.getString(Constant.USERID, null);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        /*
        Ádám
        Ági
        Balázs
        Bálint
        Bumbi
        Lajos
        Linda
        Máté
        Meli
        Olivér
        Petra
        Zsolt


        User person = new User();
        person.setName("Ádám");
        person.setImage("https://scontent.fbud1-1.fna.fbcdn.net/v/t1.0-1/p160x160/27749944_1564434653592828_1520500069784407906_n.jpg?_nc_cat=0&oh=8d56283547a166565896ac32320407a3&oe=5C02357D");
        DatabaseReference ref = usersRef.push();
        person.setId(ref.getKey());
        ref.setValue(person);

                person = new User();
        person.setName("Ági");
        person.setImage("https://scontent.fbud1-1.fna.fbcdn.net/v/t1.0-1/c1.0.160.160/p160x160/37675587_2001092589924778_6054675311964979200_n.jpg?_nc_cat=0&oh=15eb0ab463cc428a9a7eeb97defe5357&oe=5BEED3E6");
        ref = usersRef.push();
        person.setId(ref.getKey());
        ref.setValue(person);

                person = new User();
        person.setName("Balázs");
        person.setImage("https://scontent.fbud1-1.fna.fbcdn.net/v/t1.0-1/p160x160/38770413_2139612759585943_2370776699911536640_n.jpg?_nc_cat=0&oh=327779ceea2a53b4147e2e0bd7642ccd&oe=5BF55394");
        ref = usersRef.push();
        person.setId(ref.getKey());
        ref.setValue(person);

                person = new User();
        person.setName("Bálint");
        person.setImage("https://scontent.fbud1-1.fna.fbcdn.net/v/t1.0-1/c0.17.160.160/p160x160/11738043_1025215290846140_5058467031997662535_n.jpg?_nc_cat=0&oh=e2c337be852670932beae02c77c591ae&oe=5C07B61C");
        ref = usersRef.push();
        person.setId(ref.getKey());
        ref.setValue(person);

        person = new User();
        person.setName("Bumbi");
        person.setImage("https://scontent.fzag3-1.fna.fbcdn.net/v/t1.0-1/c0.10.160.160/p160x160/13726827_1247983251880480_3634491173847390178_n.jpg?_nc_cat=0&oh=d4ea6b56eab5a23641687f7230f025d3&oe=5C063BC1");
        ref = usersRef.push();
        person.setId(ref.getKey());
        ref.setValue(person);

        person = new User();
        person.setName("Lajos");
        person.setImage("https://scontent.fbud1-1.fna.fbcdn.net/v/t1.0-1/p160x160/13669745_1172704026114391_5599808636308855014_n.jpg?_nc_cat=0&oh=5e1c715373103c4f296be8068f66d69f&oe=5C378968");
        ref = usersRef.push();
        person.setId(ref.getKey());
        ref.setValue(person);

                person = new User();
        person.setName("Linda");
        person.setImage("https://scontent.fbud1-1.fna.fbcdn.net/v/t1.0-1/p160x160/18057165_1373895362649431_9076326643586491855_n.jpg?_nc_cat=0&oh=9923719a117184282491470f59fd4e60&oe=5BEF6FB4");
        ref = usersRef.push();
        person.setId(ref.getKey());
        ref.setValue(person);

                person = new User();
        person.setName("Máté");
        person.setImage("https://scontent.fbud1-1.fna.fbcdn.net/v/t1.0-1/c0.0.160.160/p160x160/21751385_1148991158569195_4269775411145295139_n.jpg?_nc_cat=0&oh=99e21b7e5dd7d7728037a016b700e073&oe=5BF51014");
        ref = usersRef.push();
        person.setId(ref.getKey());
        ref.setValue(person);

                person = new User();
        person.setName("Meli");
        person.setImage("https://scontent.fbud1-1.fna.fbcdn.net/v/t1.0-1/c0.1.160.160/p160x160/31189621_1763318667039692_5033245579744903168_n.jpg?_nc_cat=0&oh=8be3cf09f24dbdf02081956295270bdc&oe=5BF45B8C");
        ref = usersRef.push();
        person.setId(ref.getKey());
        ref.setValue(person);

                person = new User();
        person.setName("Olivér");
        person.setImage("https://scontent.fbud1-1.fna.fbcdn.net/v/t1.0-1/c45.45.557.557/s160x160/424687_236826086445509_99986428_n.jpg?_nc_cat=0&oh=1015e698ead6d83eeeca94cca203c19c&oe=5BF4EF4C");
        ref = usersRef.push();
        person.setId(ref.getKey());
        ref.setValue(person);

                person = new User();
        person.setName("Petra");
        person.setImage("https://scontent.fbud1-1.fna.fbcdn.net/v/t1.0-1/p160x160/33076842_1908230522544230_2461749132352552960_n.jpg?_nc_cat=0&oh=69d22b52df663730532c32ac4e863113&oe=5C05A953");
        ref = usersRef.push();
        person.setId(ref.getKey());
        ref.setValue(person);

                person = new User();
        person.setName("Zsolt");
        person.setImage("https://scontent.fbud1-1.fna.fbcdn.net/v/t1.0-1/c27.0.160.160/p160x160/36481166_1973747219323132_6588387863929290752_n.jpg?_nc_cat=0&oh=6b5bf68d48e3bd73aaa41c28383d8a34&oe=5C35649B");
        ref = usersRef.push();
        person.setId(ref.getKey());
        ref.setValue(person);
        */

        // check firebase connection
        /*DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    System.out.println("connected");
                } else {
                    System.out.println("not connected");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });*/

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                users.clear();

                for (DataSnapshot userDataSnapshot : dataSnapshot.getChildren()) {
                    users.add(userDataSnapshot.getValue(User.class));
                }

                ChangeUserAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ChangeUser", "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public ChangeUserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_change_user, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.nameTextView.setText(users.get(position).getName());
        holder.userRadioButton.setTag(position);
        ImageLoader.getInstance().displayImage(users.get(position).getImage(), holder.userImageView);

        if (users.get(position).getId().equals(userId)) {
            holder.userRadioButton.setChecked(true);
            lastChecked = holder.userRadioButton;
        }

        holder.userRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RadioButton radioButton = (RadioButton) view;

                if (radioButton.isChecked()) {

                    if (lastChecked != null && !lastChecked.equals(radioButton)) {
                        lastChecked.setChecked(false);
                    }

                    lastChecked = radioButton;


                    SPUtil.putString(Constant.USERID, users.get((Integer) radioButton.getTag()).getId());
                    SPUtil.putString(Constant.USERNAME, users.get((Integer) radioButton.getTag()).getName());
                    SPUtil.putString(Constant.USER_IMAGE, users.get((Integer) radioButton.getTag()).getImage());

                    activity.replaceFragment(new FrontPageFragment());


                } else {
                    lastChecked = null;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
