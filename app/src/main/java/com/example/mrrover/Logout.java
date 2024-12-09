package com.example.mrrover;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class Logout extends AppCompatActivity {

    Button confirm;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_logout);

        confirm = findViewById(R.id.btn_confirm);
        logout = findViewById(R.id.btn_cancel);

        confirm.setOnClickListener(v ->  {

            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Logout.this, MR_Login_Driver.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        logout.setOnClickListener(v -> {

            finish();
        });
    }
}