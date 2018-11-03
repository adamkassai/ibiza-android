package com.kassaiweb.ibiza;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kassaiweb.ibiza.Data.FbUser;
import com.kassaiweb.ibiza.Group.GroupChooserActivity;
import com.kassaiweb.ibiza.Util.SPUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();

    private CallbackManager callbackManager;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView loginButton = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.login_progress);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn) {
            String currentGroupId = SPUtil.getString(Constant.CURRENT_GROUP_ID, null);
            if(currentGroupId == null) {
                // we are logged in, but we are not in a group
                startGroupChooserActivity();
            } else {
                startMainAct();
            }
        } else {
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressBar.setVisibility(View.VISIBLE);
                    LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile"/*, "user_friends"*/));

                    LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            // we have to get the user data
                            GraphRequest request = GraphRequest.newMeRequest(
                                    AccessToken.getCurrentAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject object, GraphResponse response) {
                                            try {
                                                SPUtil.putString(Constant.ACCOUNT_ID, object.getString("id"));
                                                SPUtil.putString(Constant.ACCOUNT_NAME, object.getString("name"));
                                                SPUtil.putString(Constant.ACCOUNT_IMAGE_URL, object.getJSONObject("picture").getJSONObject("data").getString("url"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            menageFirebaseDB();
                                        }
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,picture");
                            request.setParameters(parameters);
                            request.executeAsync();
                        }

                        @Override
                        public void onCancel() {
                            progressBar.setVisibility(View.INVISIBLE);
                            Snackbar.make(findViewById(R.id.login_root), "Nem sikerült a bejelentkezés", Snackbar.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Snackbar.make(findViewById(R.id.login_root), "Valami hiba történt bejelentkezés közben!", Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            });

            callbackManager = CallbackManager.Factory.create();
        }
    }

    private void menageFirebaseDB() {
        // meg kell keresni a user-t a firebase-ben
        FirebaseDatabase.getInstance().getReference("fb_users")
                .orderByChild("id")
                .equalTo(SPUtil.getString(Constant.ACCOUNT_ID, ""))
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if(dataSnapshot.getValue() == null) {
                            // új fb felhasználó elmentése a db-ben
                            DatabaseReference newUserRef = FirebaseDatabase.getInstance().getReference("fb_users").push();
                            FbUser newUser = new FbUser(
                                    SPUtil.getString(Constant.ACCOUNT_ID, ""),
                                    AccessToken.getCurrentAccessToken().getToken(),
                                    SPUtil.getString(Constant.ACCOUNT_IMAGE_URL, ""),
                                    SPUtil.getString(Constant.ACCOUNT_NAME, "")
                            );
                            newUserRef.setValue(newUser);
                            SPUtil.putString(Constant.CURRENT_USER_ID, newUserRef.getKey());
                        } else {
                            HashMap<String, String> asdf = (HashMap) dataSnapshot.getValue();
                            if(asdf.keySet().size() == 1) {
                                for(String key : asdf.keySet()) {
                                    SPUtil.putString(Constant.CURRENT_USER_ID, key);
                                }
                            } else {
                                Log.e(TAG, "there are multiple users with the same \"id\"!!! This is a huge problem!");
                            }
                        }
                        startGroupChooserActivity();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressBar.setVisibility(View.VISIBLE);
                        Snackbar.make(findViewById(R.id.login_root), "Valami hiba történt bejelentkezés közben!", Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private void startMainAct() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    /**
     * Starting the group chooser activity. At this point, the user is logged in, but not a member of any groups.
     */
    private void startGroupChooserActivity() {
        startActivity(new Intent(LoginActivity.this, GroupChooserActivity.class));
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
