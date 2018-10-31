package com.kassaiweb.ibiza;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

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
            startNextAct();
        } else {
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressBar.setVisibility(View.VISIBLE);
                    LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile"/*, "user_friends"*/));

                    LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            progressBar.setVisibility(View.INVISIBLE);
                            startNextAct();
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

    private void startNextAct() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
