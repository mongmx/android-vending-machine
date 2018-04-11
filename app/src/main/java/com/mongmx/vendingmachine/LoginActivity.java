package com.mongmx.vendingmachine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mongmx.vendingmachine.api.APIService;
import com.mongmx.vendingmachine.models.LoginData;
import com.mongmx.vendingmachine.models.User;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends Activity {

    private EditText editUsername;
    private EditText editPassword;
    private EditText editServerIP;
    RelativeLayout introView;

    private final Handler mHideHandler = new Handler();

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            LoginActivity.this.login();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        editUsername = (EditText) findViewById(R.id.editTextUsername);
        editPassword = (EditText) findViewById(R.id.editTextPassword);
        editServerIP = (EditText) findViewById(R.id.editTextServerIP);
        introView = (RelativeLayout) findViewById(R.id.introView);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        mHideHandler.removeCallbacks(mHideRunnable);
//        mHideHandler.postDelayed(mHideRunnable, 1000);
    }

    public void postLogin(View v) {
        SharedPreferences sp = getSharedPreferences("VM_PREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("Endpoint", "http://" + editServerIP.getText().toString() + "/api");
        editor.apply();

//        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(getString(R.string.api_endpoint)).build();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(sp.getString("Endpoint","")).build();
        APIService service = restAdapter.create(APIService.class);
        LoginData loginData = new LoginData();
        loginData.setName(editUsername.getText().toString());
        loginData.setPassword(editPassword.getText().toString());
        service.postAuthCallback(loginData, new Callback<User>() {
            @Override
            public void success(User authUser, Response response) {
                checkRole(authUser);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), "Something wrong, Cannot login!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void checkRole(User authUser) {
        editUsername.setText("");
        editPassword.setText("");
        if (authUser.getRole().equals("admin")) {
            Intent mIntent = new Intent(LoginActivity.this, QueueActivity.class);
            LoginActivity.this.startActivity(mIntent);
        } else if (authUser.getRole().equals("user")) {
            Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
            LoginActivity.this.startActivity(mIntent);
        }
    }

    public void startLogin(View v) {
        introView.setVisibility(View.INVISIBLE);
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, 1000);
    }

    private void login() {
        SharedPreferences sp = getSharedPreferences("VM_PREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("Endpoint", "http://" + editServerIP.getText().toString() + "/api");
        editor.apply();

//        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(getString(R.string.api_endpoint)).build();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(sp.getString("Endpoint","")).build();
        APIService service = restAdapter.create(APIService.class);
        LoginData loginData = new LoginData();
        loginData.setName("user");
        loginData.setPassword("cnx053");
        service.postAuthCallback(loginData,new Callback<User>() {
            @Override
            public void success(User authUser, Response response) {
                checkRole(authUser);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), "Something wrong, Cannot login!", Toast.LENGTH_LONG).show();
            }
        });
    }

}
