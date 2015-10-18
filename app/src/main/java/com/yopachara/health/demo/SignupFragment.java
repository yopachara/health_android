package com.yopachara.health.demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.rey.material.widget.Spinner;
import com.yopachara.health.demo.Model.UserModel;
import com.yopachara.health.demo.Service.HealthService;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.text.SimpleDateFormat;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class SignupFragment extends Fragment implements View.OnClickListener {
    SnackBar mSnackBar;
    private LoginActivity lActicvity;
    RadioButton male;
    RadioButton female;
    String API = "http://pachara.me:3000";
    int day;
    int month;
    int year;

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

        final EditText username = (EditText)v.findViewById(R.id.username);
        final EditText password = (EditText)v.findViewById(R.id.password);


        male = (RadioButton)v.findViewById(R.id.switches_rb1);
        female = (RadioButton)v.findViewById(R.id.switches_rb2);

        final Spinner spn_height = (Spinner)v.findViewById(R.id.spinner_height);
        final Spinner spn_weight = (Spinner)v.findViewById(R.id.spinner_weight);
        Spinner spn_exercise = (Spinner)v.findViewById(R.id.spinner_exercise);

        String[] itemsHeight = new String[50];
        for(int i = 0; i < itemsHeight.length; i++)
            itemsHeight[i] = String.valueOf(i + 151);
        ArrayAdapter<String> adapterHeight = new ArrayAdapter<>(getActivity(), R.layout.row_spn, itemsHeight);

        String[] itemsWeight = new String[75];
        for(int i = 0; i < itemsWeight.length; i++)
            itemsWeight[i] = String.valueOf(i + 31);
        ArrayAdapter<String> adapterWeight = new ArrayAdapter<>(getActivity(), R.layout.row_spn, itemsWeight);

        String[] itemsExercise = new String[] {"Little","3 times/week","4 times/week","5 times/week","Daily"};

        ArrayAdapter<String> adapterExercise = new ArrayAdapter<>(getActivity(), R.layout.row_spn, itemsExercise);

        adapterHeight.setDropDownViewResource(R.layout.row_spn_dropdown);
        adapterWeight.setDropDownViewResource(R.layout.row_spn_dropdown);
        adapterExercise.setDropDownViewResource(R.layout.row_spn_dropdown);
        spn_height.setAdapter(adapterHeight);
        spn_weight.setAdapter(adapterWeight);
        spn_exercise.setAdapter(adapterExercise);

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

        Button signup = (Button)v.findViewById(R.id.signup);

        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int height = Integer.parseInt(spn_weight.getSelectedItem().toString());
                int weight = Integer.parseInt(spn_height.getSelectedItem().toString());
                Log.d("Signup","Username : "+username.getText()+" Password : "+password.getText());
                Log.d("Radio But","Male: "+male.isChecked()+" Female: "+female.isChecked());
                Log.d("Height", spn_height.getSelectedItem().toString());
                Log.d("Age",""+calculateAge());
                Log.d("BMR", calculateBmr(height, weight, calculateAge())+"");
                Log.d("BMI", calculateBmi(weight, height)+"");
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
                        day = dialog.getDay(); month = dialog.getMonth()+1; year = dialog.getYear();
                        Toast.makeText(lActicvity, "Date is " + day + " "+ month+" "+ year, Toast.LENGTH_SHORT).show();
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

    private int calculateAge(){
        LocalDate birthdate = new LocalDate (year, month, day);          //Birth date
        LocalDate now = new LocalDate();                    //Today's date
        Period period = new Period(birthdate, now, PeriodType.yearMonthDay());
        //Now access the values as below
        System.out.println("Day "+period.getDays());
        System.out.println("Month " + period.getMonths());
        System.out.println("Year " + period.getYears());
        return  period.getYears();
    }

    private double calculateBmr(int weight, int height, int age){
        double bmr;
        if (male.isChecked()==true){
            bmr = 66.5 + (13.75*weight) + (5.003*height) - (6.775 * age);
        } else {
            bmr = 655.1 + (9.563*weight) + (1.85 * height ) - (4.676 * age );
        }
        return bmr;
    }

    private double calculateBmi(int weight, int height){
        double bmi;
        bmi = weight/((height/100.00)*(height/100.00));
        return bmi;
    }

    public void postSignup(String username, String password, String sex, int weight, int height, String birthdate, int bmr, int bmi){
        String type = "Android";
        Log.d("Signup",username);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API).build();
        HealthService api = restAdapter.create(HealthService.class);
        api.postUser(username, password, sex, weight, height, birthdate, type, bmr, bmi, new Callback<UserModel>() {
            @Override
            public void success(UserModel userModel, Response response) {
                UserModel.User user = userModel.getObjects().get(0);
                Log.d("Success",user.getUsername()+""+user.getCreateAt());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Fail",error.toString());
            }
        });
    }



}
