package com.yopachara.health.demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
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

import org.angmarch.views.NiceSpinner;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import io.apptik.widget.MultiSlider;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class SignupFragment extends Fragment {
    SnackBar mSnackBar;
    private LoginActivity lActicvity;
    RadioButton male;
    RadioButton female;
    String API = "http://pachara.me:3000";
    int day = 0;
    int month = 0;
    int year = 0;
    EditText et_date;
    EditText password;
    EditText username;
    int exePos = 0;
    String birthdate;
    NiceSpinner spinnerWeight;
    NiceSpinner spinnerHeight;
    NiceSpinner spinnerExercise;
    NiceSpinner spinnerPlan;
    TextView result_text;
    TextView carboRatio;
    TextView proteinRatio;
    TextView fatRatio;
    MultiSlider multiSlider;

    public static SignupFragment newInstance() {
        SignupFragment fragment = new SignupFragment();

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_signup, container, false);

        et_date = (EditText) v.findViewById(R.id.birthdate);

        et_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    et_date.setInputType(InputType.TYPE_NULL);
                    datePicker(view);
                }
            }
        });

        username = (EditText) v.findViewById(R.id.username);
        password = (EditText) v.findViewById(R.id.password);


        male = (RadioButton) v.findViewById(R.id.switches_rb1);
        female = (RadioButton) v.findViewById(R.id.switches_rb2);


        result_text = (TextView) v.findViewById(R.id.result_text);

        String[] itemsHeight = new String[50];
        for (int i = 0; i < itemsHeight.length; i++) {
            itemsHeight[i] = String.valueOf(i + 151);
        }

        String[] itemsWeight = new String[75];
        for (int i = 0; i < itemsWeight.length; i++) {
            itemsWeight[i] = String.valueOf(i + 31);
        }
        String[] itemsExercise = new String[]{"ออกกำลังกายน้อยมาก", "1-3 ครั้งต่อสัปดาห์", "4-5 ครั้งต่อสัปดาห์", "6-7 ครั้งต่อสัปดาห์", "วันละ 2 ครั้งขึ้นไป"};
        String[] itemsPlans = new String[]{"คงสภาพ", "สร้างกล้ามเนื้อ", "ลดน้ำหนัก", "กำหนดเอง"};


        spinnerHeight = (NiceSpinner) v.findViewById(R.id.spinner_height);
        spinnerWeight = (NiceSpinner) v.findViewById(R.id.spinner_weight);
        spinnerExercise = (NiceSpinner) v.findViewById(R.id.spinner_exercise);
        spinnerPlan = (NiceSpinner) v.findViewById(R.id.spinner_plans);


        List<String> datasHeight = new LinkedList<>(Arrays.asList(itemsHeight));
        List<String> datasWeight = new LinkedList<>(Arrays.asList(itemsWeight));
        List<String> datasExercise = new LinkedList<>(Arrays.asList(itemsExercise));
        List<String> datasPlan = new LinkedList<>(Arrays.asList(itemsPlans));


        spinnerHeight.attachDataSource(datasHeight);
        spinnerWeight.attachDataSource(datasWeight);
        spinnerExercise.attachDataSource(datasExercise);
        spinnerPlan.attachDataSource(datasPlan);

        spinnerListener();


        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    male.setChecked(male == buttonView);
                    female.setChecked(female == buttonView);
                    getResult();
                }

            }

        };

        male.setOnCheckedChangeListener(listener);
        female.setOnCheckedChangeListener(listener);

        Button back = (Button) v.findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin(v);

            }
        });

        Button signup = (Button) v.findViewById(R.id.signup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postSignup();
            }
        });


        multiSlider = (MultiSlider) v.findViewById(R.id.multiSlider);

        carboRatio = (TextView) v.findViewById(R.id.value1);
        fatRatio = (TextView) v.findViewById(R.id.value2);
        proteinRatio = (TextView) v.findViewById(R.id.value3);

        multiSlider.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
                int carboR = multiSlider.getThumb(0).getValue();
                int farR = multiSlider.getThumb(1).getValue() - carboR;
                int proteinR = 100 - (carboR + farR);
                carboRatio.setText("คาร์โบไฮเดรต "+String.valueOf(carboR)+" %");
                fatRatio.setText("ไขมัน "+String.valueOf(farR)+" %");
                proteinRatio.setText("โปรตีน "+String.valueOf(proteinR)+" %");
                spinnerPlan.setSelectedIndex(3);

            }
        });
        lActicvity = (LoginActivity) getActivity();
        mSnackBar = ((LoginActivity)getActivity()).getSnackBar();

        return v;
    }


    public void datePicker(final View v) {
        Dialog.Builder builder = null;

        boolean isLightTheme = ThemeManager.getInstance().getCurrentTheme() == 0;

        builder = new DatePickerDialog.Builder
                (isLightTheme ? R.style.Material_App_Dialog_DatePicker_Light : R.style.Material_App_Dialog_DatePicker) {
            @Override

            public void onPositiveActionClicked(DialogFragment fragment) {
                DatePickerDialog dialog = (DatePickerDialog) fragment.getDialog();
                String date = dialog.getFormattedDate(SimpleDateFormat.getDateInstance());
                day = dialog.getDay();
                month = dialog.getMonth() + 1;
                year = dialog.getYear();
                birthdate = dialog.getDate()+"";
                Toast.makeText(lActicvity, "Date is " + day + " " + month + " " + year, Toast.LENGTH_SHORT).show();
                EditText test = (EditText) getView().findViewById(R.id.birthdate);
                test.setText(date);
                getResult();
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                Toast.makeText(lActicvity, "Cancelled", Toast.LENGTH_SHORT).show();
                super.onNegativeActionClicked(fragment);
            }
        };
        builder.positiveAction("OK")
                .negativeAction("CANCEL");


        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
    }

    public void goToLogin(View v) {
        Fragment loginFragment = new LoginFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, loginFragment);
        fragmentTransaction.commit();
    }

    private int calculateAge() {
        if (year != 0 && month != 0 && day != 0) {
            LocalDate birthdate = new LocalDate(year, month, day);          //Birth date
            LocalDate now = new LocalDate();                    //Today's date
            Period period = new Period(birthdate, now, PeriodType.yearMonthDay());
            //Now access the values as below
            Log.d("Age", "Year " + period.getYears() + " Month " + period.getMonths() + " Day " + period.getDays());
            return period.getYears();
        } else {
            return 0;
        }

    }

    private double calculateBmr(int weight, int height, int age) {
        double bmr;
        if (male.isChecked() == true) {
            bmr = 66.5 + (13.75 * weight) + (5.003 * height) - (6.775 * age);
        } else {
            bmr = 655.1 + (9.563 * weight) + (1.85 * height) - (4.676 * age);
        }
        return bmr;
    }

    private double calculateBmi(int weight, int height) {
        double bmi;
        float dHeight = (float) height / 100;
        Log.d("dHeight", dHeight + "");
        bmi = weight / (dHeight * dHeight);
        return bmi;
    }

    private double calculateTdee(int index, double bmr) {
        double tdee = 0;
        switch (index) {
            case 0:
                tdee = 1.2 * bmr;
                break;
            case 1:
                tdee = 1.375 * bmr;
                break;
            case 2:
                tdee = 1.55 * bmr;
                break;
            case 3:
                tdee = 1.7 * bmr;
                break;
            case 4:
                tdee = 1.9 * bmr;
                break;
            default:
                tdee = bmr;
                break;

        }
        return tdee;
    }

    private void postSignup(String username, String password, String sex, int weight, int height, String birthdate
            , double bmr, double bmi, int carbo, int protein, int fat) {
        String type = "Android";
        Log.d("Signup", username);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API).build();
        HealthService api = restAdapter.create(HealthService.class);
        api.postUser(username, password, sex, weight, height, birthdate, type, bmr, bmi, carbo, protein, fat
                , new Callback<UserModel>() {
            @Override
            public void success(UserModel userModel, Response response) {
                Log.d("Success", "");
                mSnackBar.applyStyle(R.style.SnackBarSingleLine)
                        .text("Signup Complete")
                        .show();
                goToLogin();

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Fail", error.toString());
            }
        });
    }

    private void getResult() {
        String usernameText = username.getText().toString();
        String passwordText = password.getText().toString();
        String sex = getSex();
        int height = Integer.parseInt(spinnerHeight.getText().toString());
        int weight = Integer.parseInt(spinnerWeight.getText().toString());
        String birthdate = et_date.getText().toString();
        double bmr = calculateBmr(weight, height, calculateAge());
        double bmi = calculateBmi(weight, height);
        int carbo = multiSlider.getThumb(0).getValue();
        int fat = multiSlider.getThumb(1).getValue() - carbo;
        int protein = 100 - (carbo + fat);

        double tdee = calculateTdee(exePos, bmr);
        Log.d("Signup", "Username : " + username.getText() + " Password : " + password.getText());
        Log.d("Radio But", "Male: " + male.isChecked() + " Female: " + female.isChecked());
        Log.d("Height", height + "");
        Log.d("Weight", weight + "");
        Log.d("Age", "" + calculateAge());
        Log.d("birthdate", birthdate);
        Log.d("BMR", bmr + "");
        Log.d("BMI", bmi + "");
        Log.d("TDEE", tdee + "");
        result_text.setText("พลังงานที่ต้องการ " + Math.round(tdee) + " แคลอรี่");

        //postSignup(usernameText,passwordText,sex,weight,height,"13 July 1993",bmr,bmi,carbo,protein,fat);
    }

    private void postSignup() {
        String usernameText = username.getText().toString();
        String passwordText = password.getText().toString();
        String sex = getSex();
        int height = Integer.parseInt(spinnerHeight.getText().toString());
        int weight = Integer.parseInt(spinnerWeight.getText().toString());
        double bmr = calculateBmr(weight, height, calculateAge());
        double bmi = calculateBmi(weight, height);
        int carbo = multiSlider.getThumb(0).getValue();
        int fat = multiSlider.getThumb(1).getValue() - carbo;
        int protein = 100 - (carbo + fat);

        double tdee = calculateTdee(spinnerExercise.getSelectedIndex(), bmr);
        Log.d("Signup", "Username : " + username.getText() + " Password : " + password.getText());
        Log.d("Radio But", "Male: " + male.isChecked() + " Female: " + female.isChecked());
        Log.d("Height", height + "");
        Log.d("Weight", weight + "");
        Log.d("Age", "" + calculateAge());
        Log.d("birthdate", birthdate);
        Log.d("BMR", bmr + "");
        Log.d("BMI", bmi + "");
        Log.d("TDEE", tdee + "");
        result_text.setText("พลังงานที่ต้องการ " + Math.round(tdee) + " แคลอรี่");

        postSignup(usernameText, passwordText, sex, weight, height, birthdate, bmr, bmi, carbo, protein, fat);
    }

    private String getSex() {
        String sex;
        if (male.isChecked()) {
            sex = "male";
        } else {
            sex = "female";
        }
        return sex;
    }

    private void changePlan(int planNo) {
        switch (planNo) {
            case 0: {
                multiSlider.getThumb(0).setValue(30);
                multiSlider.getThumb(1).setValue(70);
                break;
            }
            case 1: {
                multiSlider.getThumb(0).setValue(20);
                multiSlider.getThumb(1).setValue(80);
                break;
            }
            case 2: {
                multiSlider.getThumb(0).setValue(10);
                multiSlider.getThumb(1).setValue(90);
                break;
            }

        }

    }

    private void spinnerListener() {
        spinnerPlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("spinnerPlan Index", "" + position);
                changePlan(position);
                getResult();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerHeight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getResult();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerWeight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getResult();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerExercise.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                exePos = position;
                getResult();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void goToLogin() {
        Fragment loginFragment = new LoginFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, loginFragment);
        fragmentTransaction.commit();
    }


}
