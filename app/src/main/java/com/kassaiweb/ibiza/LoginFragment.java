package com.kassaiweb.ibiza;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kassaiweb.ibiza.Movie.Movie;
import com.kassaiweb.ibiza.User.ChangeUserFragment;
import com.nostra13.universalimageloader.core.ImageLoader;

import static android.content.Context.MODE_PRIVATE;

public class LoginFragment extends Fragment {

    private Movie movie;
    private Gson gson = new Gson();
    private int version;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        SharedPreferences prefs = getActivity().getSharedPreferences(Constant.APP_NAME, MODE_PRIVATE);
        String user = prefs.getString(Constant.USERNAME, null);
        String image = prefs.getString(Constant.USER_IMAGE, null);

        TextView usernameTextView = view.findViewById(R.id.username);
        if (user!=null) {
            usernameTextView.setText(user);
        }

        ImageView userImage = view.findViewById(R.id.userImage);
        ImageLoader.getInstance().displayImage(image, userImage);

        view.findViewById(R.id.button_changeUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity activity = (MainActivity)getActivity();
                activity.replaceFragment(new ChangeUserFragment());
            }
        });

        view.findViewById(R.id.button_frontPage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity activity = (MainActivity)getActivity();
                activity.replaceFragment(new FrontPageFragment());
            }
        });


    }

}