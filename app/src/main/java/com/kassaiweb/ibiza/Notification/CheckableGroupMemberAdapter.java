package com.kassaiweb.ibiza.Notification;

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
import com.kassaiweb.ibiza.Data.CheckableGroupMember;
import com.kassaiweb.ibiza.Data.FbUser;
import com.kassaiweb.ibiza.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CheckableGroupMemberAdapter extends RecyclerView.Adapter<CheckableGroupMemberAdapter.ViewHolder> {

    private List<CheckableGroupMember> data;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView ivImage;
        public TextView tvName;

        public ViewHolder(View v) {
            super(v);
            ivImage = v.findViewById(R.id.row_group_member_image);
            tvName = v.findViewById(R.id.row_group_member_name);
        }
    }

    public CheckableGroupMemberAdapter(List<CheckableGroupMember> data) {
        this.data = data;
    }

    @Override
    public CheckableGroupMemberAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_group_member, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final CheckableGroupMember cgm = data.get(position);

        holder.tvName.setText(cgm.getName());
        if(cgm.isChecked()) {
            holder.ivImage.setImageResource(R.drawable.baseline_check_circle_outline_black_36);
        } else {
            ImageLoader.getInstance().displayImage(cgm.getImageUrl(), holder.ivImage);
        }
        holder.ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cgm.isChecked()) {
                    ImageLoader.getInstance().displayImage(cgm.getImageUrl(), holder.ivImage);
                } else {
                    holder.ivImage.setImageResource(R.drawable.baseline_check_circle_outline_black_36);
                }
                cgm.setChecked(!cgm.isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }
}
