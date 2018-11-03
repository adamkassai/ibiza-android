package com.kassaiweb.ibiza.Group;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kassaiweb.ibiza.Data.Group;
import com.kassaiweb.ibiza.R;
import com.kassaiweb.ibiza.Util.DateUtil;

import java.util.Calendar;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private List<Group> data;
    private GroupAdapterListener listener;

    public interface GroupAdapterListener {
        void onGroupItemClick(Group item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvCreatedAt;


        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.row_group_name);
            tvCreatedAt = itemView.findViewById(R.id.row_group_created_at);
        }

        public void bind(final Group group, final GroupAdapterListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onGroupItemClick(group);
                }
            });

            tvName.setText(group.getName());
            Calendar createdAt = DateUtil.convert(group.getCreatedAt());
            tvCreatedAt.setText(DateUtil.format(createdAt));
        }
    }

    public GroupAdapter(List<Group> data, GroupAdapterListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_group, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(data.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }
}
