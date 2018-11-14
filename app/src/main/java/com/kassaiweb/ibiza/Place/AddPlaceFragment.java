package com.kassaiweb.ibiza.Place;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kassaiweb.ibiza.Constant;
import com.kassaiweb.ibiza.MainActivity;
import com.kassaiweb.ibiza.R;
import com.kassaiweb.ibiza.Util.SPUtil;

public class AddPlaceFragment extends Fragment {

    private static Place place;
    private GoogleMap map;
    private Marker selectedMarker;

    private EditText etName;
    private MapView mapView;

    public AddPlaceFragment() {
    }

    public static AddPlaceFragment newInstance(Place place) {
        AddPlaceFragment.place = place;
        return new AddPlaceFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_place, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etName = view.findViewById(R.id.add_place_name);
        mapView = view.findViewById(R.id.add_place_map);
        TextView tvAdd = view.findViewById(R.id.add_place_add);

        // Creates the MapView
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.setMinZoomPreference(12);
                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        if(selectedMarker != null) {
                            selectedMarker.setPosition(latLng);
                        }
                    }
                });

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(place.getLatLng());
                selectedMarker = map.addMarker(markerOptions);

                map.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
            }
        });
        /*map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);*/

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Updates the location and zoom of the MapView
        /*CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12);
        map.animateCamera(cameraUpdate);*/

        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedMarker != null) {
                    if(etName.getText() != null && !etName.getText().toString().isEmpty()) {
                        double latitude = selectedMarker.getPosition().latitude;
                        double longitude = selectedMarker.getPosition().longitude;
                        place.setLatitude(String.valueOf(latitude));
                        place.setLongitude(String.valueOf(longitude));

                        place.setName(etName.getText().toString());
                        place.setGroupId(SPUtil.getString(Constant.CURRENT_GROUP_ID, ""));
                        place.setCreatorId(SPUtil.getString(Constant.CURRENT_USER_ID, ""));

                        DatabaseReference newPlaceRef = FirebaseDatabase.getInstance().getReference("places").push();
                        newPlaceRef.setValue(place);
                        ((MainActivity) getActivity()).onBackPressed();
                    } else {
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                "Adj meg valamilyen nevet!",
                                Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
