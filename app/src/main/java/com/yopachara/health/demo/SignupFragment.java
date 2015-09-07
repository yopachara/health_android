package com.yopachara.health.demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.ThemeManager;
import com.rey.material.widget.Button;
import com.rey.material.widget.EditText;
import com.rey.material.widget.RadioButton;
import com.rey.material.widget.SnackBar;

import java.text.SimpleDateFormat;


public class SignupFragment extends Fragment implements View.OnClickListener {
    SnackBar mSnackBar;
    private LoginActivity lActicvity;
    RadioButton male;
    RadioButton female;

    public static SignupFragment newInstance() {
        SignupFragment fragment = new SignupFragment();

        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_signup, container, false);

        Button bt_date = (Button)v.findViewById(R.id.dialog_bt_date);

        bt_date.setOnClickListener(this);

        male = (RadioButton)v.findViewById(R.id.switches_rb1);
        female = (RadioButton)v.findViewById(R.id.switches_rb2);

        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    male.setChecked(male == buttonView);
                    female.setChecked(female == buttonView);
                }

            }

        };

        male.setOnCheckedChangeListener(listener);
        female.setOnCheckedChangeListener(listener);

        Button back = (Button)v.findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin(v);

            }
        });


        lActicvity = (LoginActivity)getActivity();

        return v;
    }

    public void onClick(final View v) {
        Dialog.Builder builder = null;

        boolean isLightTheme = ThemeManager.getInstance().getCurrentTheme() == 0;
        switch (v.getId()){
            case R.id.dialog_bt_date:
                builder = new DatePickerDialog.Builder(isLightTheme ? R.style.Material_App_Dialog_DatePicker_Light :  R.style.Material_App_Dialog_DatePicker){
                    @Override

                    public void onPositiveActionClicked(DialogFragment fragment) {
                        DatePickerDialog dialog = (DatePickerDialog)fragment.getDialog();
                        String date = dialog.getFormattedDate(SimpleDateFormat.getDateInstance());
                        Toast.makeText(lActicvity, "Date is " + date, Toast.LENGTH_SHORT).show();
                        EditText test = (EditText)getView().findViewById(R.id.birthdate);
                        test.setText(date);
                        Button asd = (Button)getView().findViewById(R.id.dialog_bt_date);
                        asd.setText(date);
                        super.onPositiveActionClicked(fragment);
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        Toast.makeText(lActicvity, "Cancelled" , Toast.LENGTH_SHORT).show();
                        super.onNegativeActionClicked(fragment);
                    }
                };
                builder.positiveAction("OK")
                        .negativeAction("CANCEL");
                break;

        }
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
    }

    public void goToLogin(View v)
    {
        Fragment loginFragment = new LoginFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, loginFragment);
        fragmentTransaction.commit();
    }



}
