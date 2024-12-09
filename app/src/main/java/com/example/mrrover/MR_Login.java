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
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.mrrover.model.UserModel;
import com.example.mrrover.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.TimeUnit;

public class MR_Login extends AppCompatActivity {

    EditText emaillog;
    EditText passwordlog;
    Button loginBTN;
    TextView registerHERE;
    FirebaseAuth firebaseAuth;

    UserModel userModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mr_login);

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        emaillog = findViewById(R.id.editTextUsername);
        passwordlog = findViewById(R.id.editTextPassword);
        loginBTN = findViewById(R.id.loginBTN);
        registerHERE = findViewById(R.id.register_here);

        registerHERE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MR_Login.this, MR_Register.class);
                startActivity(intent2);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        MR_Login bubble = this;

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
                CollectionReference usersRef = db.collection("users");



                // Check if user exists in Firestore
                usersRef.whereEqualTo("Email", email).whereEqualTo("Password", password).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        // User exists in Firestore; proceed with Firebase Auth login
                                        firebaseAuth.signInWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> authTask) {
                                                        if (authTask.isSuccessful()) {
                                                            Toast.makeText(MR_Login.this, "Successfully Login", Toast.LENGTH_SHORT).show();

                                                            String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                                            PeriodicWorkRequest notifWorkRequest = new PeriodicWorkRequest.Builder(
                                                                    com.example.mrrover.NotificationBackgroundWorker.class, 15, TimeUnit.MINUTES)
                                                                    .setInputData(new Data.Builder()
                                                                            .putString(com.example.mrrover.NotificationConstants.RECEIVER_ID, currentUserUID)
                                                                            .build())
                                                                    .addTag(com.example.mrrover.NotificationConstants.NOTIFICATION_WORKER_TAG)
                                                                    .setConstraints(constraints)
                                                                    .build();
                                                            WorkManager.getInstance(bubble)
                                                                    .enqueueUniquePeriodicWork(com.example.mrrover.NotificationConstants.NOTIFICATION_TAG,
                                                                            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, notifWorkRequest);


                                                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                                        } else {
                                                            Toast.makeText(MR_Login.this, "Login Failed: " + authTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        // User does not exist in Firestore
                                        Toast.makeText(MR_Login.this, "No user found with these credentials.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(MR_Login.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}