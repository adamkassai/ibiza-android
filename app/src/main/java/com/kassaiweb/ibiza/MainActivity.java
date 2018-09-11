package com.kassaiweb.ibiza;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kassaiweb.ibiza.Cost.CostPagerFragment;
import com.kassaiweb.ibiza.Place.PlacesFragment;
import com.kassaiweb.ibiza.Poll.PollsPagerFragment;
import com.kassaiweb.ibiza.Task.TaskListFragment;
import com.kassaiweb.ibiza.User.ChangeUserFragment;
import com.kassaiweb.ibiza.Util.ConnectionUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private OkHttpClient client = new OkHttpClient();
    private String username;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeImageLoader();

        FirebaseMessaging.getInstance().subscribeToTopic("messages");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .build();
        mGoogleApiClient.connect();

        new VersionCheck().execute();
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences(Constant.APP_NAME, MODE_PRIVATE);
        username = prefs.getString(Constant.USERNAME, null);

        if (username == null) {
            Fragment newFragment = new ChangeUserFragment();
            replaceFragment(newFragment);
        } else {
            Fragment newFragment = new FrontPageFragment();
            replaceFragment(newFragment);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_poll) {
            replaceFragment(new PollsPagerFragment());
            return true;
        } else if (id == R.id.action_front) {
            replaceFragment(new FrontPageFragment());
            return true;
        } else if (id == R.id.action_change_user) {
            replaceFragment(new ChangeUserFragment());
            return true;
        } else if (id == R.id.action_random) {
            replaceFragment(new PickOneFragment());
            return true;
        } else if (id == R.id.action_tasks) {
            replaceFragment(new TaskListFragment());
            return true;
        } else if (id == R.id.action_costs) {
            replaceFragment(new CostPagerFragment());
            return true;
        } else if (id == R.id.action_notification) {
            replaceFragment(new NotificationFragment());
            return true;
        } else if (id == R.id.action_places) {
            replaceFragment(new PlacesFragment());
            return true;
        } else if (id == R.id.action_shopping) {
            replaceFragment(new ShoppingListFragment());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "GoogleApiClient.OnConnectionFailed has been called");
    }

    private class VersionCheck extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try {

                Request request = new Request.Builder()
                        .url("http://kassaiweb.com/version_ibiza.txt")
                        .build();

                Response response = client.newCall(request).execute();
                return response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                SharedPreferences prefs = getSharedPreferences(Constant.APP_NAME, MODE_PRIVATE);
                int version = prefs.getInt("version", 1);
                int newVersion = Integer.parseInt(result);

                if (newVersion > version) {
                    Fragment updateFragment = new UpdateFragment();
                    Bundle args = new Bundle();
                    args.putInt("version", newVersion);
                    updateFragment.setArguments(args);

                    replaceFragment(updateFragment);
                }
            }
        }
    }

    private void initializeImageLoader() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheOnDisk(true)
                .build();


        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(options)
                .build();

        ImageLoader.getInstance().init(config);
    }

    public void replaceFragment(final Fragment fragment) {

        if (ConnectionUtil.isNetworkAvailable()) {
            //findViewById(R.id.main_fragment).setVisibility(View.VISIBLE);
            findViewById(R.id.main_error).setVisibility(View.GONE);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_fragment, fragment).addToBackStack(null).commit();
        }
        //findViewById(R.id.main_fragment).setVisibility(View.GONE);
        findViewById(R.id.main_error).setVisibility(View.VISIBLE);
        findViewById(R.id.button_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(fragment);
            }
        });
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

}
