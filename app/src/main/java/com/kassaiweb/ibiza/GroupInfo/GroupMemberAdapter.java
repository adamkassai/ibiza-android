package com.kassaiweb.ibiza.GroupInfo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kassaiweb.ibiza.Data.FbUser;
import com.kassaiweb.ibiza.Data.GroupMember;
import com.kassaiweb.ibiza.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.ViewHolder> {

    private List<GroupMember> data;

    public GroupMemberAdapter(List<GroupMember> data) {
        this.data = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView ivImage;
        public TextView tvName;

        public ViewHolder(View v) {
            super(v);
            ivImage = v.findViewById(R.id.row_group_member_image);
            tvName = v.findViewById(R.id.row_group_member_name);
        }
    }

    @Override
    public GroupMemberAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_group_member, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        GroupMember member = data.get(position);

        FirebaseDatabase.getInstance().getReference("fb_users").child(member.getUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        FbUser user = dataSnapshot.getValue(FbUser.class);

                        holder.tvName.setText(user.getName());
                        ImageLoader.getInstance().displayImage(user.getImageUrl(), holder.ivImage);
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
