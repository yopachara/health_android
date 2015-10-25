package com.yopachara.health.demo;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.yopachara.health.demo.Model.UserModel;


public class ProfileFragment extends Fragment {
    private static final String USER_KEY = "user_key";
    private UserModel.User mUser;
    private EditText username_text;
    private EditText sex_text;
    private EditText height_text;
    private EditText weight_text;
    private EditText bmi_text;
    private EditText bmr_text;
    Typeface font;


    public static ProfileFragment newInstance(UserModel.User user) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER_KEY, user);
        fragment.setArguments(bundle);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        mUser = (UserModel.User) getArguments().getSerializable(USER_KEY);
        font = Typeface.createFromAsset(getContext().getAssets(), "supermarket.ttf");

        username_text = (EditText)v.findViewById(R.id.username_text);
        sex_text = (EditText)v.findViewById(R.id.sex_text);
        height_text = (EditText)v.findViewById(R.id.height_text);
        weight_text = (EditText)v.findViewById(R.id.weight_text);
        bmi_text = (EditText)v.findViewById(R.id.bmi_text);
        bmr_text = (EditText)v.findViewById(R.id.bmr_text);

        username_text.setTypeface(font);
        sex_text.setTypeface(font);
        height_text.setTypeface(font);
        weight_text.setTypeface(font);
        bmi_text.setTypeface(font);
        bmr_text.setTypeface(font);

        Log.d("mUser",mUser.getUsername()+" "+mUser.getBirthdate());
        username_text.setText(mUser.getUsername());
        sex_text.setText(mUser.getSex());
        height_text.setText(mUser.getHeight() + " cm");
        weight_text.setText(mUser.getWeight() + " kg");
        bmi_text.setText(Math.round(mUser.getBmi()) + "");
        bmr_text.setText(Math.round(mUser.getBmr())+"");

        return v;

    }


}
