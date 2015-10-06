package com.yopachara.health.demo;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rey.material.widget.Button;
import com.rey.material.widget.EditText;
import com.rey.material.widget.SnackBar;
import com.yopachara.health.demo.Model.FoodModel;
import com.yopachara.health.demo.Model.HistoryModel;
import com.yopachara.health.demo.Model.UserModel;
import com.yopachara.health.demo.Service.HealthService;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;


public class LoginFragment extends Fragment {

    SnackBar mSnackBar;

    String API = "http://pachara.me:3000";

    ArrayList<UserModel.User> users;
    TextInputLayout usernameText;
    TextInputLayout passwordText;

    public static LoginFragment newInstance(){
        LoginFragment fragment = new LoginFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        Button button_bt_raise = (Button)v.findViewById(R.id.button_bt_raise);
        Button button_bt_raise_color = (Button)v.findViewById(R.id.button_bt_raise_color);
        Button login_but = (Button)v.findViewById(R.id.login_but);
        final TextInputLayout textTest = (TextInputLayout)v.findViewById(R.id.usernameWrapper);
        Button testBut = (Button)v.findViewById(R.id.testBut);
        usernameText = (TextInputLayout)v.findViewById(R.id.usernameWrapper);
        passwordText = (TextInputLayout)v.findViewById(R.id.passwordWrapper);

        users = new ArrayList<UserModel.User>();


        button_bt_raise.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mSnackBar.applyStyle(R.style.SnackBarSingleLine)
                        .show();
                goToSignup(v);

            }
        });

        button_bt_raise_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                goToMainActivity(v);

            }
        });

        testBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setEndpoint(API).build();
                HealthService api = restAdapter.create(HealthService.class);

                api.getFeeds(new Callback<FoodModel>() {
                    @Override
                    public void success(FoodModel foodModel, Response response) {
//                        ArrayList<UserModel.User> users = user.getObjects();
                        Log.d("Success", users.get(1).getUsername());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        textTest.setHint(error.toString());
                    }
                });
            }
        });

        login_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postLogin("yopachara","yopachara");
            }
        });




        mSnackBar = ((LoginActivity)getActivity()).getSnackBar();
        return v;
    }


    public void goToMainActivity(View v,String username,String password)
    {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        startActivity(intent);
    }

    public void goToSignup(View v)
    {
        Fragment signupFragment = new SignupFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, signupFragment);
        fragmentTransaction.commit();
    }

    public void postLogin(final String username, final String password){
        String basicAuth = "Basic " + Base64.encodeToString(String.format("%s:%s", username, password).getBytes(), Base64.NO_WRAP);
        Log.d("Auth",basicAuth);
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setConverter(new GsonConverter(gson))
                .setEndpoint(API).build();
        HealthService api = restAdapter.create(HealthService.class);
        api.getUser(basicAuth, new Callback<UserModel>() {
            @Override
            public void success(UserModel userModel, Response response) {
                users = userModel.getObjects();
                Log.d("Success", "user size " +users.size());
                int size = users.size()-1;
                Log.d("Username", users.get(size).getUsername()+" "+users.get(size).getCreateAt());

                Log.d("Username EditText",usernameText.getEditText().getText().toString());
                goToMainActivity(getView(),username,password);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Fail",error.toString());

            }
        });

    }


}
