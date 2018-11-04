package com.kassaiweb.ibiza;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kassaiweb.ibiza.Connection.NetworkChangeReceiver;
import com.kassaiweb.ibiza.Cost.CostPagerFragment;
import com.kassaiweb.ibiza.Group.GroupChooserActivity;
import com.kassaiweb.ibiza.Notification.NotificationFragment;
import com.kassaiweb.ibiza.Place.PlacesFragment;
import com.kassaiweb.ibiza.Poll.PollsPagerFragment;
import com.kassaiweb.ibiza.Task.TaskInfoFragment;
import com.kassaiweb.ibiza.Task.TaskListFragment;
import com.kassaiweb.ibiza.Util.NetworkUtil;
import com.kassaiweb.ibiza.Util.SPUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener,
        NetworkChangeReceiver.NetworkChangeInterface {

    private static final String TAG = MainActivity.class.getSimpleName();
    private OkHttpClient client = new OkHttpClient();

    private Fragment currentFragment;

    private GoogleApiClient mGoogleApiClient;

    private ImageView headerProfilePic;
    private TextView headerName;
    private TextView headerGroup;

    private DrawerLayout drawer;
    private NavigationView navigationView;

    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkChangeReceiver.addObserver(this);

        initializeImageLoader();

        FirebaseMessaging.getInstance().subscribeToTopic("messages");

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        headerProfilePic = navigationView.getHeaderView(0).findViewById(R.id.nav_header_image);
        headerName = navigationView.getHeaderView(0).findViewById(R.id.nav_header_name);
        headerGroup = navigationView.getHeaderView(0).findViewById(R.id.nav_header_group);

        snackbar = Snackbar.make(findViewById(android.R.id.content),
                getResources().getString(R.string.internet_connection_error),
                Snackbar.LENGTH_INDEFINITE
        );

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

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

        if (SPUtil.getString(Constant.ACCOUNT_NAME, null) == null) {
            // getting the user's data from facebook at the first time
            GraphRequest request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            try {
                                SPUtil.putString(Constant.ACCOUNT_NAME, object.getString("name"));
                                SPUtil.putString(Constant.ACCOUNT_IMAGE_URL, object.getJSONObject("picture").getJSONObject("data").getString("url"));
                                fillHeader();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            onNavigationItemSelected(navigationView.getMenu().findItem(R.id.action_front));
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,picture");
            request.setParameters(parameters);
            request.executeAsync();
        } else {
            fillHeader();
            onNavigationItemSelected(navigationView.getMenu().findItem(R.id.action_front));
        }
    }

    @Override
    public void onStop() {
        NetworkChangeReceiver.removeObserver(this);
        super.onStop();
    }

    private void fillHeader() {
        ImageLoader.getInstance().displayImage(SPUtil.getString(Constant.ACCOUNT_IMAGE_URL, ""), headerProfilePic);
        headerName.setText(SPUtil.getString(Constant.ACCOUNT_NAME, ""));
        headerGroup.setText(SPUtil.getString(Constant.CURRENT_GROUP_NAME, ""));
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_front:
                getSupportActionBar().setTitle(item.getTitle());
                replaceFragment(new FrontPageFragment());
                break;
            case R.id.action_tasks:
                getSupportActionBar().setTitle(item.getTitle());
                replaceFragment(new TaskListFragment());
                break;
            case R.id.action_costs:
                getSupportActionBar().setTitle(item.getTitle());
                replaceFragment(new CostPagerFragment());
                break;
            case R.id.action_poll:
                getSupportActionBar().setTitle(item.getTitle());
                replaceFragment(new PollsPagerFragment());
                break;
            case R.id.action_random:
                getSupportActionBar().setTitle(item.getTitle());
                replaceFragment(new PickOneFragment());
                break;
            case R.id.action_places:
                getSupportActionBar().setTitle(item.getTitle());
                replaceFragment(new PlacesFragment());
                break;
            case R.id.action_shopping:
                getSupportActionBar().setTitle(item.getTitle());
                replaceFragment(new ShoppingListFragment());
                break;
            case R.id.action_notification:
                getSupportActionBar().setTitle(item.getTitle());
                replaceFragment(new NotificationFragment());
                break;
            case R.id.action_invite:
                getSupportActionBar().setTitle(item.getTitle());
                replaceFragment(new BarcodeFragment());
                break;
            case R.id.action_change_group:
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Biztosan csoportot szeretnél váltani?")
                        .setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(MainActivity.this, GroupChooserActivity.class));
                                SPUtil.putString(Constant.CURRENT_GROUP_ID, null);
                                SPUtil.putString(Constant.CURRENT_GROUP_NAME, null);
                                finish();
                            }
                        })
                        .setNegativeButton("Nem", null)
                        .show();
                break;
            case R.id.action_logout:
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Biztosan kijelentkezel?")
                        .setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LoginManager.getInstance().logOut();
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                SPUtil.putString(Constant.ACCOUNT_ID, null);
                                SPUtil.putString(Constant.ACCOUNT_NAME, null);
                                SPUtil.putString(Constant.ACCOUNT_IMAGE_URL, null);
                                SPUtil.putString(Constant.CURRENT_GROUP_ID, null);
                                SPUtil.putString(Constant.CURRENT_GROUP_NAME, null);
                                finish();
                            }
                        })
                        .setNegativeButton("Nem", null)
                        .show();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "GoogleApiClient.OnConnectionFailed has been called");
    }

    @Override
    public void networkChanged(boolean isConnected) {
        if (isConnected) {
            snackbar.dismiss();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.logged_in_main_fragment, currentFragment).commit();
        }
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
                int version = SPUtil.getInt(Constant.VERSION, 1);
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
        currentFragment = fragment;
        if (NetworkUtil.isConnected(MainActivity.this)) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.logged_in_main_fragment, currentFragment).commit();
        } else {
            snackbar.show();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(currentFragment instanceof TaskInfoFragment) {
            onNavigationItemSelected(navigationView.getMenu().findItem(R.id.action_tasks));
        } else if (!(currentFragment instanceof FrontPageFragment)) {
            onNavigationItemSelected(navigationView.getMenu().findItem(R.id.action_front));
        } else {
            super.onBackPressed();
        }
    }

}
