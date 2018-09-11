package com.kassaiweb.ibiza.Place;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.kassaiweb.ibiza.R;

import java.util.ArrayList;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private String userId;
    private String placeId;
    private ArrayList<String> placeIds;
    private String placeIdsJson;

    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;

    private GoogleMap map;
    private Button navigation;
    private Button home;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        placeId = bundle.getString("placeId", null);
        placeIdsJson  = bundle.getString("placeIds", null);

        navigation=view.findViewById(R.id.map_navigation);
        home=view.findViewById(R.id.map_home);

        if (placeIdsJson!=null) {

            placeIds = new Gson().fromJson(placeIdsJson, ArrayList.class);

        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.map = googleMap;

        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);

            mGeoDataClient.getPlaceById("ChIJXeSBOEvAaUcR8_S29z8gY-I").addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {

                    PlaceBufferResponse placeBufferResponse = task.getResult();
                    for (final Place place : placeBufferResponse) {

                        map.addMarker(new MarkerOptions().position(place.getLatLng()).title("Mókus Nyaralóház")).showInfoWindow();
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 11.0f));


                        /*if (placeId!=null && placeIds==null) {

                            home.setVisibility(View.VISIBLE);
                            home.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + place.getLatLng().latitude + "," + place.getLatLng().longitude);
                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                    mapIntent.setPackage("com.google.android.apps.maps");
                                    startActivity(mapIntent);

                                }
                            });
                        }*/

                    }
                }
            });



        if (placeId!=null) {


            mGeoDataClient.getPlaceById(placeId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {

                    PlaceBufferResponse placeBufferResponse = task.getResult();
                    for (final Place place : placeBufferResponse) {

                        map.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString())).showInfoWindow();
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 11.0f));

                        navigation.setVisibility(View.VISIBLE);
                        navigation.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Uri gmmIntentUri = Uri.parse("google.navigation:q="+place.getLatLng().latitude+","+place.getLatLng().longitude);
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                startActivity(mapIntent);

                            }
                        });

                    }
                }
            });

        }

        if (placeIds!=null && placeIds.size()>0) {

            for (String placeId : placeIds) {

                mGeoDataClient.getPlaceById(placeId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceBufferResponse> task) {

                        PlaceBufferResponse placeBufferResponse = task.getResult();
                        for (Place place : placeBufferResponse) {

                            map.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
                        }
                    }
                });

            }

        }


    }
}
