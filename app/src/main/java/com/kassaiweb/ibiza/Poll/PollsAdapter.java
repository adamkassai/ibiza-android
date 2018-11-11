package com.kassaiweb.ibiza.Poll;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kassaiweb.ibiza.Constant;
import com.kassaiweb.ibiza.Data.Group;
import com.kassaiweb.ibiza.Data.GroupMember;
import com.kassaiweb.ibiza.R;
import com.kassaiweb.ibiza.Util.SPUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PollsAdapter extends RecyclerView.Adapter<PollsAdapter.ViewHolder> {

    public interface PollsAdapterListener {
        void onPollSelected(Poll poll);
    }

    private List<Poll> data;
    private PollsAdapterListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView ivCreator;
        public TextView tvQuestion;
        public TextView tvParticipants;

        public ViewHolder(View v) {
            super(v);
            ivCreator = v.findViewById(R.id.row_poll_creator);
            tvQuestion = v.findViewById(R.id.row_poll_name);
            tvParticipants = v.findViewById(R.id.row_poll_participants);
        }
    }

    public PollsAdapter(List<Poll> data, PollsAdapterListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_poll, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Poll poll = data.get(position);
        FirebaseDatabase.getInstance().getReference("fb_users").child(poll.getCreatorId())
                .child("imageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String imageUrl = dataSnapshot.getValue(String.class);
                ImageLoader.getInstance().displayImage(imageUrl, holder.ivCreator);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // TODO
            }
        });
        holder.tvQuestion.setText(poll.getQuestion());
        if(poll.isClosed()) {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.poll_green));
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.poll_red));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPollSelected(poll);
            }
        });

        FirebaseDatabase.getInstance().getReference("groups")
                .child(SPUtil.getString(Constant.CURRENT_GROUP_ID, null))
                .child("members")
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int voteCount = poll.getAnswers().get(0).getVotes().size();
                int groupSize = (int) dataSnapshot.getChildrenCount();
                        holder.tvParticipants.setText(voteCount + "/" + groupSize);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }
}
