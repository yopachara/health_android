package com.rey.material.demo;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

        Button bt_flat = (Button)v.findViewById(R.id.button_bt_flat);
        Button bt_flat_color = (Button)v.findViewById(R.id.button_bt_flat_color);



        bt_flat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                mSnackBar.applyStyle(R.style.SnackBarSingleLine)
//                        .show();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);

            }
        });

        bt_flat_color.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), MainActivity.class);
//                startActivity(intent);
                mSnackBar.applyStyle(R.style.SnackBarSingleLine)
                        .show();

            }
        });

        mSnackBar = ((LoginActivity)getActivity()).getSnackBar();
        return v;
    }





}
