package com.kassaiweb.ibiza.News;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kassaiweb.ibiza.R;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsPagerFragment extends Fragment {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private ArrayList<News> news = new ArrayList<>();
    private OkHttpClient client = new OkHttpClient();
    private Gson gson = new Gson();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        new DownloadData().execute();

    }



    private class DownloadData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try {

                Request request = new Request.Builder()
                        .url("http://kassaiweb.com/news.json")
                        .build();

                Response response = client.newCall(request).execute();
                return response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            Button retry = getView().findViewById(R.id.button_retry);
            retry.setVisibility(View.GONE);
            getView().findViewById(R.id.progress).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {

            if (getView()!=null)
            {

                getView().findViewById(R.id.progress).setVisibility(View.GONE);

                if (result!=null) {
                    news = gson.fromJson(result, new TypeToken<ArrayList<News>>(){}.getType());

                    mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
                    mViewPager = (ViewPager) getView().findViewById(R.id.container);
                    mViewPager.setAdapter(mSectionsPagerAdapter);

                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "Hálózati hiba", Toast.LENGTH_LONG).show();

                    Button retry = getView().findViewById(R.id.button_retry);
                    retry.setVisibility(View.VISIBLE);
                    retry.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new NewsPagerFragment.DownloadData().execute();
                        }
                    });

                }

            }

        }

    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment f = new NewsFragment();
            Bundle args = new Bundle();
            args.putString("newsJson", gson.toJson(news.get(position)));
            f.setArguments(args);
            return f;
        }

        @Override
        public int getCount() {
            return news.size();
        }
    }



}
