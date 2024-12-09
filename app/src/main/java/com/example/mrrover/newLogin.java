package com.example.mrrover;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class newLogin extends AppCompatActivity {

    TextView textView;
    EditText user;
    EditText pass;
    Button loginBTN;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_login);

        textView = findViewById(R.id.register_here);
        user = findViewById(R.id.editTextUsername);
        pass = findViewById(R.id.editTextPassword);
        loginBTN = findViewById(R.id.loginBTN);

        auth = FirebaseAuth.getInstance();

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(newLogin.this, RegisterAs.class);
                startActivity(intent);
            }
        });

        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateUsername() | !validatePassword()) {

                } else {
                    checkUser();

                }

            }
        });
    }
    public Boolean validateUsername(){
        String val = user.getText().toString();
        if(val.isEmpty()){
            user.setError("Username cannot be empty");
            return false;
        } else {
            user.setError(null);
            return true;
        }
    }
    public Boolean validatePassword(){
        String val = pass.getText().toString();
        if(val.isEmpty()){
            pass.setError("Password cannot be empty");
            return false;
        } else {
            pass.setError(null);
            return true;
        }
    }

    public void checkUser(){
        String checkuser = user.getText().toString().trim();
        String checkpass = pass.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(checkuser);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    // Clear previous errors
                    user.setError(null);

                    // Loop through the users that match the query (in case there are multiple)
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Fetch the password from the database for this user
                        String passwordFromDB = userSnapshot.child("password").getValue(String.class);

                        // Check if the password matches the provided input
                        if (passwordFromDB != null && passwordFromDB.equals(checkpass)) {
                            user.setError(null); // Clear any errors

                            SharedPreferences sharedPreferences = getSharedPreferences("Users", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("username", checkuser);
                            editor.apply();

                            // Proceed to the next activity if login is successful
                            Intent intent = new Intent(newLogin.this, HomeActivity.class);
                            startActivity(intent);
                            finish(); // Optional: to close the login activity
                        } else {
                            // Invalid password
                            pass.setError("Invalid Credentials");
                            pass.requestFocus();
                        }
                    }
                } else {
                    // Username does not exist
                    user.setError("User does not exist");
                    user.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(newLogin.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

}