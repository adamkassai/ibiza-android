package com.kassaiweb.ibiza.News;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kassaiweb.ibiza.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NewsFragment extends Fragment {

    private News news;
    private Gson gson = new Gson();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();
        String json = args.getString("newsJson", null);

        if (json!=null) {
            news = gson.fromJson(json, News.class);
        }



        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


        TextView title = view.findViewById(R.id.news_title);
        if (news.getTitle()!=null) {
            title.setText(news.getTitle().trim());
        }

        TextView header = view.findViewById(R.id.news_header);
        if (news.getHeader()!=null) {
            header.setText(news.getHeader().trim());
        }

        TextView body = view.findViewById(R.id.news_body);
        if (news.getBody()!=null) {
            body.setText(news.getBody().trim());
        }


        ImageView cover = view.findViewById(R.id.news_cover);
        if (news.getCover()!=null) {
            ImageLoader.getInstance().displayImage(news.getCover(), cover);
        }


    }

}
