package com.example.mrrover;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mrrover.email.JavaMailAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OTPActivity extends AppCompatActivity {

    private int code = 0;
    private int time = 60;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String birthDate;
    private TextView timer;
    private EditText verficationCode;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otpactivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
        firstName = intent.getStringExtra("firstname");
        lastName = intent.getStringExtra("lastname");
        phoneNumber = intent.getStringExtra("phone");
        birthDate = intent.getStringExtra("birthdate");
        System.out.println(email + " " + password + " " + firstName + " " + lastName + " " + phoneNumber + " " + birthDate);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        generateOtp();
        verficationCode = findViewById(R.id.verfication);
        timer = findViewById(R.id.textView3);
        timer.setText("");

        Button Confirm = findViewById(R.id.registerBTN);
        Confirm.setOnClickListener(v -> {
            String codeString = verficationCode.getText().toString();
            int otp = Integer.parseInt(codeString);
            if (otp == code) {
                InserrtUser();
            } else {
                Toast.makeText(OTPActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
            }
        });

        Button Resend = findViewById(R.id.registerBTN2);
        Resend.setOnClickListener(v -> {
            if (time == 0) {
                generateOtp();
            }
            else{
                Toast.makeText(OTPActivity.this, "Time out", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateOtp() {

        Random random = new Random();
        code = random.nextInt(900000) + 100000; // Generate a 6-digit OTP
        startTimer(60000); // Start a 60-second timer

        JavaMailAPI mailAPI = new JavaMailAPI(OTPActivity.this, email, "Rover Email Verification", "The verification code is:" + code, String.valueOf(code));
        Toast.makeText(this, " Email Sended", Toast.LENGTH_SHORT).show();
        mailAPI.execute();

    }

    private void startTimer(long duration) {
        new CountDownTimer(duration, 1000) {
            public void onTick(long millisUntilFinished) {
                time = (int) (millisUntilFinished / 1000);
                timer.setText("Time left: " + time);
            }

            public void onFinish() {
                time = 0;
                timer.setText("");
                verficationCode.setText("");
            }
        }.start();
    }
    private void InserrtUser(){

        ProgressDialog progressDialog = new ProgressDialog(OTPActivity.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("Please wait while we are creating your account");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        try{
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(OTPActivity.this, "Successfully Created", Toast.LENGTH_SHORT).show();
                        userID = firebaseAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = firestore.collection("users").document(userID);
                        Map<String,Object> user = new HashMap<>();
                        user.put("Firstname",firstName);
                        user.put("Lastname",lastName);
                        user.put("Email",email);
                        user.put("Password",password);
                        user.put("Phone",phoneNumber);
                        user.put("Birthdate",birthDate);
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("TAG","onSuccess: user profile is created for " + userID);
                            }
                        });
                        startActivity(new Intent(getApplicationContext(), MR_Login.class));
                    }else{
                        Toast.makeText(OTPActivity.this, "Registration Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
            progressDialog.dismiss();
        }
    }
}