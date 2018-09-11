package com.kassaiweb.ibiza.Place;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.kassaiweb.ibiza.Constant;
import com.kassaiweb.ibiza.MainActivity;
import com.kassaiweb.ibiza.R;
import com.kassaiweb.ibiza.Util.SPUtil;

import java.util.ArrayList;

public class PlacesFragment extends Fragment {

    private String userId;

    private RecyclerView mRecyclerView;
    private PlaceAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private LinearLayout showMap;
    private LinearLayout showHome;


    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;

    private ArrayList<Place>  places = new ArrayList<>();
    private ArrayList<String>  placeIds = new ArrayList<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_places, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {

        mRecyclerView = view.findViewById(R.id.places_recycler_view);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        userId = SPUtil.getString(Constant.USERID, null);

        mAdapter = new PlaceAdapter(getActivity(), places);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


        LatLngBounds latLngBounds = new LatLngBounds(
                new LatLng( 46.831701, 18.046708 ),
                new LatLng( 46.972339, 18.161730 ));

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                .build();


        showMap=view.findViewById(R.id.place_show_map);
        showHome=view.findViewById(R.id.place_show_home);

        if (placeIds.size()>0) {
            showMap.setVisibility(View.VISIBLE);
        }else {
            showMap.setVisibility(View.GONE);
        }


        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);

        view.findViewById(R.id.places_tobacco).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeIds = getTobaccos();
                setPlaces();
                showMap.setVisibility(View.VISIBLE);
            }
        });

        view.findViewById(R.id.places_market).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeIds = getMarkets();
                setPlaces();
                showMap.setVisibility(View.VISIBLE);
            }
        });

        view.findViewById(R.id.places_petrol).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeIds = getPetrolStations();
                setPlaces();
                showMap.setVisibility(View.VISIBLE);
            }
        });

        view.findViewById(R.id.places_train).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeIds = getTrains();
                setPlaces();
                showMap.setVisibility(View.VISIBLE);
            }
        });


       showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("placeIds", new Gson().toJson(placeIds));
                Fragment fragment = new MapFragment();
                fragment.setArguments(bundle);
                ((MainActivity)getActivity()).replaceFragment(fragment);


            }
        });


        mGeoDataClient.getPlaceById("ChIJXeSBOEvAaUcR8_S29z8gY-I").addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {

                PlaceBufferResponse placeBufferResponse = task.getResult();
                for (final Place place : placeBufferResponse) {

                        showHome.setVisibility(View.VISIBLE);
                        showHome.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + place.getLatLng().latitude + "," + place.getLatLng().longitude);
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                startActivity(mapIntent);

                            }
                        });
                    }


            }
        });


       /* mGeoDataClient.getAutocompletePredictions("bolt", latLngBounds, GeoDataClient.BoundsMode.BIAS, typeFilter).addOnCompleteListener(new OnCompleteListener<AutocompletePredictionBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<AutocompletePredictionBufferResponse> task) {

                AutocompletePredictionBufferResponse autocompletePredictions = task.getResult();
                ArrayList<AutocompletePrediction> autocompletePredictionsList = DataBufferUtils.freezeAndClose(autocompletePredictions);
                Log.d("predictions", autocompletePredictionsList.size()+" db");



                mAdapter.notifyDataSetChanged();

            }
        });*/




    }

    public void setPlaces() {

        places.clear();

        for(String placeId : placeIds) {

            mGeoDataClient.getPlaceById(placeId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {

                    PlaceBufferResponse placeBufferResponse = task.getResult();
                    for(Place place : placeBufferResponse)
                    {
                        places.add(place);
                    }
                    mAdapter.notifyDataSetChanged();
                }
            });

        }

    }

    public ArrayList<String> getTobaccos()
    {
        ArrayList<String> list = new ArrayList<>();
        list.add("ChIJWXeIq5DAaUcRKAwi6Q_AIL8");
        list.add("ChIJD90xKJrAaUcRCDv9pXYKPvU");
        list.add("ChIJI2-5yJvAaUcR-knZCCCNNDY");
        list.add("ChIJn7sE3GPBaUcRfM6GIOvmyw0");

        return list;
    }

    public ArrayList<String> getPetrolStations()
    {
        ArrayList<String> list = new ArrayList<>();
        list.add("ChIJS9Nf2VvAaUcRFGfJRlcmOV4");
        list.add("ChIJq-IQrrjAaUcRw3zFP_2V7Co");
        list.add("ChIJn3cZOL_AaUcRCULTaHlNUPo");
        list.add("ChIJk6M9F8fAaUcRzaHrXT7YfXc");
        list.add("ChIJf_FO45jAaUcRE919cX97Qiw");
        list.add("ChIJqx_bRGzqaUcRHVsG02oQqJI");

        return list;
    }

    public ArrayList<String> getTrains()
    {
        ArrayList<String> list = new ArrayList<>();
        list.add("ChIJS8-Dj8zBaUcRlckmMlGz5eQ");
        list.add("ChIJBRM5x5rAaUcRSbYJXImNzhs");

        return list;
    }

    public ArrayList<String> getMarkets()
    {
        ArrayList<String> list = new ArrayList<>();
        list.add("ChIJ501IfMvBaUcR8ZEKXA8DBfQ");
        list.add("ChIJmQ1w2L7AaUcRlAtRnyQ7e0U");
        list.add("ChIJsV2L1UvAaUcRw899onMmz7Y");
        list.add("ChIJf_FO45jAaUcRE919cX97Qiw");
        list.add("ChIJCfS9HY7AaUcR-N5yABjGHzk");
        list.add("ChIJJ3oWSJjAaUcRlrDwyR_7AvE");
        list.add("ChIJQR7yMVy_aUcRXD3kgc2FCfo");
        list.add("ChIJBzBDlLjAaUcRjIaZpN9sSJQ");


        return list;
    }

}
