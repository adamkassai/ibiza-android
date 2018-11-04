package com.kassaiweb.ibiza.Task;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kassaiweb.ibiza.Data.FbUser;
import com.kassaiweb.ibiza.Data.GroupMember;
import com.kassaiweb.ibiza.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ViewHolder> {

    private Task task;
    private List<FbUser> data;
    private ParticipantAdapterListener listener;

    public interface ParticipantAdapterListener {
        void onUserChecked(FbUser user, boolean isChecked);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public CheckBox checkBox;
        public ImageView ivProfilePic;

        public ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.row_participant_name);
            checkBox = v.findViewById(R.id.row_participant_cb);
            ivProfilePic = v.findViewById(R.id.row_participant_profile_pic);
        }
    }


    public ParticipantAdapter(Task task, List<FbUser> data, ParticipantAdapterListener listener) {
        this.task = task;
        this.data = data;
        this.listener = listener;
    }

    @Override
    public ParticipantAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_participant, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final FbUser user = data.get(position);

        holder.tvName.setText(user.getName());
        ImageLoader.getInstance().displayImage(user.getImageUrl(), holder.ivProfilePic);
        if(task.getParticipants() != null && task.getParticipants().contains(user.getFirebaseId())) {
            holder.checkBox.setChecked(true);
        }
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listener.onUserChecked(user, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }
}
