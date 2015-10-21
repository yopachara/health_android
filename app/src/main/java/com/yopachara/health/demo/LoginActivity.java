package com.yopachara.health.demo;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.rey.material.app.ThemeManager;
import com.rey.material.widget.SnackBar;



public class LoginActivity extends AppCompatActivity  {

    //Button bt_flat = (Button)v.findViewById(R.id.button_bt_flat);

    private SnackBar mSnackBar;
    private LineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ThemeManager.init(this, 2, 0, null);

//        LoginFragment myFragment = new LoginFragment();
//
//        FragmentManager manager = getSupportFragmentManager();
//        FragmentTransaction transaction = manager.beginTransaction();
//        transaction.replace(R.id.login_fragment, myFragment);
//        transaction.commit();

        mSnackBar = (SnackBar)findViewById(R.id.main_sn);

        LoginFragment loginFragment = new LoginFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, loginFragment);
        transaction.commit();

    }


    public SnackBar getSnackBar(){
        return mSnackBar;
    }



}
