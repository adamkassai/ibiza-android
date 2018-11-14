package com.kassaiweb.ibiza;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.kassaiweb.ibiza.Cost.CostPagerFragment;
import com.kassaiweb.ibiza.News.News;
import com.kassaiweb.ibiza.Notification.NotificationFragment;
import com.kassaiweb.ibiza.Poll.Poll;
import com.kassaiweb.ibiza.Poll.PollsPagerFragment;
import com.kassaiweb.ibiza.Task.Task;
import com.kassaiweb.ibiza.Task.TaskListFragment;
import com.kassaiweb.ibiza.Util.SPUtil;

public class FrontPageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_front_page, container, false);
    }

}
