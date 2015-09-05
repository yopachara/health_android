package com.rey.material.demo;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rey.material.widget.Button;
import com.rey.material.widget.EditText;
import com.rey.material.widget.FloatingActionButton;
import com.rey.material.widget.SnackBar;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import com.rey.material.demo.API.healthAPI;
import com.rey.material.demo.Model.healthModel;
import com.rey.material.widget.EditText;

public class LoginFragment extends Fragment {

    SnackBar mSnackBar;

    String API = "http://pachara.me:3000";

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
        final EditText textTest = (EditText)v.findViewById(R.id.textfield_et_label);
        Button testBut = (Button)v.findViewById(R.id.testBut);



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
                goToMainActivity(v);

            }
        });

        testBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setEndpoint(API).build();
                healthAPI api = restAdapter.create(healthAPI.class);

                api.getFeeds(new Callback<healthModel>() {

                    @Override
                    public void success(healthModel healthModel, Response response) {
                        textTest.setText(healthModel.getText()+healthModel.toString());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        textTest.setText(error.getUrl()+error.getMessage());
                    }
                });
            }
        });




        mSnackBar = ((LoginActivity)getActivity()).getSnackBar();
        return v;
    }


    public void goToMainActivity(View v)
    {
        Intent intent = new Intent(getActivity(), MainActivity.class);
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


}
