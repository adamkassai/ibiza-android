package com.kassaiweb.ibiza.Movie;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kassaiweb.ibiza.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MovieFragment extends Fragment {

    private Movie movie;
    private Gson gson = new Gson();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();
        String json = args.getString("movieJson", null);

        if (json!=null) {
            movie = gson.fromJson(json, Movie.class);
        }



        return inflater.inflate(R.layout.fragment_movie, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        Log.d("MOVIE", new Gson().toJson(movie));

        TextView title = view.findViewById(R.id.movie_title);
        if (movie.getTitle()!=null) {
            title.setText(movie.getTitle().trim());
        }

        TextView genre = view.findViewById(R.id.movie_genre);
        if (movie.getGenre()!=null) {
            genre.setText(movie.getGenre().trim().replace(" ", ", ").toLowerCase());
        }

        TextView description = view.findViewById(R.id.movie_description);
        if (movie.getDescription()!=null) {
            description.setText(movie.getDescription().trim());
        }

        TextView cast = view.findViewById(R.id.movie_cast);
        if (movie.getCast()!=null) {
            cast.setText(movie.getCast().trim());
            view.findViewById(R.id.cast_header).setVisibility(View.VISIBLE);
        }


        ImageView cover = view.findViewById(R.id.movie_cover);
        if (movie.getCover()!=null) {
            ImageLoader.getInstance().displayImage(movie.getCover(), cover);
        }


    }

}
