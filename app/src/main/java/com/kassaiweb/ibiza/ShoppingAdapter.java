package com.kassaiweb.ibiza;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kassaiweb.ibiza.Cost.Payment;
import com.kassaiweb.ibiza.User.User;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.ViewHolder> {

    private ArrayList<ShoppingItem> items = new ArrayList<>();
    private MainActivity activity;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemTextView;
        public LinearLayout mainLayout;
        public ImageView deleteButton;
        public ImageView doneButton;

        public ViewHolder(View v) {
            super(v);
            itemTextView = v.findViewById(R.id.shopping_item);
            doneButton = v.findViewById(R.id.shopping_done_button);

            mainLayout = v.findViewById(R.id.shopping_main_layout);
            deleteButton = v.findViewById(R.id.shopping_delete_button);
        }
    }


    public ShoppingAdapter(Activity activity) {

        this.activity = (MainActivity)activity;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference shoppingRef = database.getReference("shopping");

        shoppingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                items.clear();

                for (DataSnapshot itemDataSnapshot : dataSnapshot.getChildren()) {
                    items.add(itemDataSnapshot.getValue(ShoppingItem.class));
                }

                ShoppingAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ShoppingItems", "Failed to read value.", error.toException());
            }
        });


    }


    @Override
    public ShoppingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_shopping_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        holder.itemTextView.setText(items.get(position).getItem());

        holder.deleteButton.setTag(position);
        holder.deleteButton.setVisibility(View.GONE);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int position = (Integer) view.getTag();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference paymentRef = database.getReference("shopping").child(items.get(position).getId());
                paymentRef.removeValue();

            }
        });

        if (items.get(position).isReady()) {
            holder.doneButton.setImageResource(R.drawable.baseline_check_circle_black_36);
            holder.itemTextView.setPaintFlags(holder.itemTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else{
            holder.doneButton.setImageResource(R.drawable.baseline_check_circle_outline_black_36);
            holder.itemTextView.setPaintFlags(holder.itemTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        holder.doneButton.setTag(position);
        holder.doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int position = (Integer) view.getTag();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference paymentRef = database.getReference("shopping").child(items.get(position).getId());

                if (items.get(position).isReady()) {
                    items.get(position).setReady(false);
                    holder.itemTextView.setPaintFlags(holder.itemTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }else{
                    items.get(position).setReady(true);
                    holder.itemTextView.setPaintFlags(holder.itemTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }

                paymentRef.setValue(items.get(position));

            }
        });

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.deleteButton.getVisibility()==View.VISIBLE) {
                    holder.deleteButton.setVisibility(View.GONE);
                }else{
                    holder.deleteButton.setVisibility(View.VISIBLE);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
