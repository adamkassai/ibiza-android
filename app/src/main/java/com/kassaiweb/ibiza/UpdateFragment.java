package com.kassaiweb.ibiza;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.kassaiweb.ibiza.Movie.Movie;
import com.kassaiweb.ibiza.Movie.MoviePagerFragment;

import static android.content.Context.MODE_PRIVATE;

public class UpdateFragment extends Fragment {

    private Movie movie;
    private Gson gson = new Gson();
    private int version;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();
        if (args!=null) {
            version = args.getInt("version", 0);
        }


        return inflater.inflate(R.layout.fragment_update, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        view.findViewById(R.id.button_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = getActivity().getSharedPreferences("nyaralas2018", MODE_PRIVATE).edit();
                editor.putInt("version", version);
                editor.apply();

                MainActivity activity = (MainActivity)getActivity();
                activity.replaceFragment(new FrontPageFragment());

                String url = "http://kassaiweb.com/revfulop.apk";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }
        });

    }

}
