package com.kassaiweb.ibiza.Place;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.kassaiweb.ibiza.Constant;
import com.kassaiweb.ibiza.MainActivity;
import com.kassaiweb.ibiza.R;
import com.kassaiweb.ibiza.Util.SPUtil;

import java.util.ArrayList;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private ArrayList<Place> places;
    private String userId;
    private MainActivity activity;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView address;
        public TextView website;
        public LinearLayout main;

        public ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            address = v.findViewById(R.id.address);
            website = v.findViewById(R.id.website);
            main = v.findViewById(R.id.place_main_layout);
        }
    }


    public PlaceAdapter(Activity activity, ArrayList places) {

        this.activity = (MainActivity)activity;
        this.places = places;

        userId = SPUtil.getString(Constant.USER_ID, null);

    }


    @Override
    public PlaceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_place, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.name.setText(places.get(position).getName());
        holder.address.setText(places.get(position).getAddress());
        if (places.get(position).getWebsiteUri()!=null) {
            holder.website.setText(places.get(position).getWebsiteUri().toString());
        }else{
            holder.website.setVisibility(View.GONE);
        }

        holder.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("placeId", places.get(position).getId());
                Fragment fragment = new MapFragment();
                fragment.setArguments(bundle);
                activity.replaceFragment(fragment);

            }
        });

    }

    @Override
    public int getItemCount() {
        return places.size();
    }

}
