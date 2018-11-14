package com.kassaiweb.ibiza.Place;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.BasePermissionListener;
import com.kassaiweb.ibiza.Constant;
import com.kassaiweb.ibiza.MainActivity;
import com.kassaiweb.ibiza.R;
import com.kassaiweb.ibiza.Util.SPUtil;

import java.util.ArrayList;
import java.util.List;

public class PlacesListFragment extends Fragment {

    private ProgressBar progress;

    private PlaceAdapter adapter;
    private List<Place> places = new ArrayList<>();

    public PlacesListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_places_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.places_list_recyclerView);
        TextView tvShare = view.findViewById(R.id.places_list_share);
        // progress = view.findViewById(R.id.places_list_progress);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new PlaceAdapter(places);
        recyclerView.setAdapter(adapter);

        tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(getActivity())
                        .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        .withListener(new BasePermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                Location location = getLastKnownLocation();
                                Place currentPlace = new Place();
                                if(location != null) {
                                    currentPlace.setLatitude(String.valueOf(location.getLatitude()));
                                    currentPlace.setLongitude(String.valueOf(location.getLongitude()));
                                } else {
                                    currentPlace.setLatitude(Constant.DEFAUlT_LATITUDE);
                                    currentPlace.setLongitude(Constant.DEFAUlT_LONGITUDE);
                                }

                                ((MainActivity) getActivity()).replaceFragment(
                                        AddPlaceFragment.newInstance(currentPlace));
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                        "Ha meg szeretnéd osztani a pozíciódat, akkor kell az engedély!",
                                        Snackbar.LENGTH_LONG).show();
                            }
                        })
                        .onSameThread().check();
            }
        });

        String currentGroupId = SPUtil.getString(Constant.CURRENT_GROUP_ID, "");
        FirebaseDatabase.getInstance().getReference("places").orderByChild("groupId")
                .equalTo(currentGroupId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Place place = dataSnapshot.getValue(Place.class);
                place.setFirebaseId(dataSnapshot.getKey());
                places.add(place);
                adapter.notifyItemInserted(places.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Place newPlace = dataSnapshot.getValue(Place.class);
                newPlace.setFirebaseId(dataSnapshot.getKey());
                for (int i = 0; i < places.size(); ++i) {
                    Place place = places.get(i);
                    if (place.getFirebaseId().equals(dataSnapshot.getKey())) {
                        places.remove(place);
                        places.add(i, newPlace);
                        adapter.notifyItemChanged(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                for (int i = 0; i < places.size(); ++i) {
                    Place place = places.get(i);
                    if (dataSnapshot.getKey().equals(place.getFirebaseId())) {
                        places.remove(place);
                        adapter.notifyItemRemoved(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private Location getLastKnownLocation() {
        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = lm.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}
