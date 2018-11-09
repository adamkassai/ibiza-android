package com.kassaiweb.ibiza.Task;

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
import com.kassaiweb.ibiza.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    public interface TaskAdapterListener {
        void onTaskClicked(Task task);

        void onTaskCompleteClicked(Task task);
    }

    private List<Task> tasks;
    private TaskAdapterListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView ivCreator;
        public TextView tvName, tvDescription;
        public ImageView ivPriority, ivMakeDone;

        public ViewHolder(View v) {
            super(v);
            ivCreator = v.findViewById(R.id.row_task_creator);
            tvName = v.findViewById(R.id.row_task_name);
            tvDescription = v.findViewById(R.id.row_task_description);
            ivPriority = v.findViewById(R.id.row_task_priority);
            ivMakeDone = v.findViewById(R.id.row_task_done);
        }
    }

    public TaskAdapter(List<Task> tasks, TaskAdapterListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_task, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Task task = tasks.get(position);

        FirebaseDatabase.getInstance().getReference("fb_users").child(task.getCreatorId()).child("imageUrl")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String imgUrl = dataSnapshot.getValue(String.class);
                        ImageLoader.getInstance().displayImage(imgUrl, holder.ivCreator);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        holder.tvName.setText(task.getName());
        holder.tvDescription.setText(task.getDescription());
        if (!task.isReady()) {
            holder.ivMakeDone.setImageResource(R.drawable.baseline_check_circle_black_36);
            holder.ivMakeDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onTaskCompleteClicked(task);
                }
            });
        }
        switch (task.getPriority()) {
            case 1:
                holder.ivPriority.setImageResource(R.drawable.dark_green);
                break;
            case 2:
                holder.ivPriority.setImageResource(R.drawable.green);
                break;
            case 3:
                holder.ivPriority.setImageResource(R.drawable.yellow);
                break;
            case 4:
                holder.ivPriority.setImageResource(R.drawable.orange);
                break;
            case 5:
                holder.ivPriority.setImageResource(R.drawable.red);
                break;
            default:
                holder.ivPriority.setVisibility(View.GONE);
                break;
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onTaskClicked(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks != null ? tasks.size() : 0;
    }

}
