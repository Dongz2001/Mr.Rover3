package com.example.mrrover;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MR_Register extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText firstnamereg;
    EditText lastnamereg;
    EditText emailreg;
    EditText phoneNumberreg;
    EditText passwordreg;
    EditText cPassword;
    Button birthdateBTN;
    TextView loginHERE;
    Button registerBTN;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String userID;

    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mr_register);

        firstnamereg = findViewById(R.id.firstname);
        lastnamereg = findViewById(R.id.lastname);
        emailreg = findViewById(R.id.email);
        phoneNumberreg = findViewById(R.id.phonenumber);
        passwordreg = findViewById(R.id.password);
        cPassword= findViewById(R.id.confirmpassword);
        birthdateBTN = findViewById(R.id.date);
        registerBTN = findViewById(R.id.registerBTN);
        loginHERE = findViewById(R.id.login_here);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        initDatePicker();

        loginHERE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MR_Register.this, MR_Login.class);
                startActivity(intent2);
            }
        });

        if(firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MR_Login.class));
            finish();
        }


        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstname = firstnamereg.getText().toString().trim();
                String lastname = lastnamereg.getText().toString().trim();
                String email = emailreg.getText().toString().trim();
                String phone = phoneNumberreg.getText().toString().trim();
                String birthdate = birthdateBTN.getText().toString().trim();
                String password = passwordreg.getText().toString().trim();
                String confirmpassword = cPassword.getText().toString().trim();

                if(TextUtils.isEmpty(firstname)){
                    firstnamereg.setError("This field is required");
                }
                if(TextUtils.isEmpty(lastname)){
                    lastnamereg.setError("This field is required");
                }
                if (TextUtils.isEmpty(phone)) {
                    phoneNumberreg.setError("This field is required");
                } else if (!phone.matches("\\d{11}")) {
                    phoneNumberreg.setError("Phone number must contain exactly 11 digits");
                }
                if(TextUtils.isEmpty(birthdate)){
                    birthdateBTN.setError("This field is required");
                }
                if (TextUtils.isEmpty(email)) {
                    emailreg.setError("This field is required");
                } else if (!email.contains("@")) {
                    emailreg.setError("Please enter a valid email address");
                }
                if (TextUtils.isEmpty(password)) {
                    passwordreg.setError("This field is required");
                } else if (TextUtils.isEmpty(confirmpassword)) {
                    cPassword.setError("This field is required");
                } else if (!password.equals(confirmpassword)) {
                    cPassword.setError("Passwords do not match");
                }

               Intent intent = new Intent(MR_Register.this, OTPActivity.class);
                intent.putExtra("phone", phone);
                intent.putExtra("firstname", firstname);
                intent.putExtra("lastname", lastname);
                intent.putExtra("email", email);
                intent.putExtra("birthdate", birthdate);
                intent.putExtra("password", password);
                startActivity(intent);

            }
        });
    }

    private String getTodaysDate() {

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker() {

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = makeDateString(dayOfMonth, month, year);

                birthdateBTN.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this,style, dateSetListener, year,month,day);

    }

    private String makeDateString(int dayOfMonth, int month, int year) {

        return getMonthFormat(month) + " " + (dayOfMonth) + " " + (year);
    }

    private String getMonthFormat(int month) {

        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        return "JAN";

    }

    public void openDatePicker(View view){

        datePickerDialog.show();
    }
}