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
import com.rey.material.widget.FloatingActionButton;
import com.rey.material.widget.SnackBar;


public class LoginFragment extends Fragment {

    SnackBar mSnackBar;
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



        button_bt_raise.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mSnackBar.applyStyle(R.style.SnackBarSingleLine)
                        .show();
                goToSignup(v);

            }
        });

        button_bt_raise_color.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                goToMainActivity(v);

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
