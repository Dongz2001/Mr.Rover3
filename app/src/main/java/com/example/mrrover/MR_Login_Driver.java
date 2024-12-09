package com.example.mrrover;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class MR_Login_Driver extends AppCompatActivity {

    TextView registerHERE;
    EditText emaillog;
    EditText passwordlog;
    Button loginBTN;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mr_login_driver);

        registerHERE = findViewById(R.id.register_here);
        emaillog = findViewById(R.id.editTextUsername);
        passwordlog = findViewById(R.id.editTextPassword);
        loginBTN = findViewById(R.id.loginBTN);



        registerHERE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MR_Login_Driver.this, MR_Register_Driver.class);
                startActivity(intent);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emaillog.getText().toString().trim();
                String password = passwordlog.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    emaillog.setError("This field is required");
                    return;
                } else if (!email.contains("@")) {
                    emaillog.setError("Please enter a valid email address");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    passwordlog.setError("This field is required");
                    return;
                }

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference driversRef = db.collection("drivers");

                // Check if the driver exists in Firestore
                driversRef.whereEqualTo("Email", email).whereEqualTo("Password", password).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        // Driver exists in Firestore; proceed with Firebase Auth login
                                        firebaseAuth.signInWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> authTask) {
                                                        if (authTask.isSuccessful()) {
                                                            Toast.makeText(MR_Login_Driver.this, "Successfully Login", Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(getApplicationContext(), home_Driver.class));
                                                        } else {
                                                            Toast.makeText(MR_Login_Driver.this, "Login Failed: " + authTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        // Driver does not exist in Firestore
                                        Toast.makeText(MR_Login_Driver.this, "No driver found with these credentials.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(MR_Login_Driver.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}