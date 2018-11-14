package com.kassaiweb.ibiza.Place;

import android.content.Intent;
import android.net.Uri;
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

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private List<Place> places;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivNavigate;
        public TextView tvName;
        public CircleImageView ivCreator;

        public ViewHolder(View v) {
            super(v);
            ivNavigate = v.findViewById(R.id.row_place_navigate);
            tvName = v.findViewById(R.id.row_place_name);
            ivCreator = v.findViewById(R.id.row_place_creator);
        }
    }

    public PlaceAdapter(List<Place> places) {
        this.places = places;
    }

    @Override
    public PlaceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_place, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Place place = places.get(position);

        holder.ivNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse(
                        "google.navigation:q=" + place.getLatitudeD() +"," + place.getLongitudeD());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                holder.itemView.getContext().startActivity(mapIntent);
            }
        });
        holder.tvName.setText(place.getName());
        FirebaseDatabase.getInstance().getReference("fb_users").child(place.getCreatorId())
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
    }

    @Override
    public int getItemCount() {
        return places != null ? places.size() : 0;
    }

}
