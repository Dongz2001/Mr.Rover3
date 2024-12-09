package com.example.mrrover;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Payment_Method extends AppCompatActivity {

    EditText gname;
    EditText gnumber;
    Button confirm;
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_method);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        gname = findViewById(R.id.gcash_name);
        gnumber = findViewById(R.id.gcash_number);
        confirm = findViewById(R.id.confirm_payment_button);
        back = findViewById(R.id.back111);

        String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Limit gnumber to 11 digits only
        gnumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        gnumber.setInputType(InputType.TYPE_CLASS_NUMBER);

        // Add a TextWatcher to check for 11 digits
        gnumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 11) {
                    confirm.setEnabled(true);  // Enable confirm button if 11 digits are entered
                } else {
                    confirm.setEnabled(false); // Disable confirm button if not 11 digits
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Firestore reference
        DocumentReference docRef = db.collection("driver's gcash").document(currentUserUID);

        // Check if data already exists for this user
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Data exists, make fields non-editable and set existing values
                String savedGcashName = documentSnapshot.getString("Gcash Name");
                String savedGcashNumber = documentSnapshot.getString("Gcash Number");

                gname.setText(savedGcashName);
                gnumber.setText(savedGcashNumber);

                gname.setFocusable(false);
                gnumber.setFocusable(false);
                confirm.setEnabled(false); // Disable confirm button if data already exists

                Toast.makeText(this, "Gcash information already saved.", Toast.LENGTH_SHORT).show();
            } else {
                // Allow user to enter information if not saved
                confirm.setOnClickListener(v -> {
                    String gcashName = gname.getText().toString();
                    String gcashNumber = gnumber.getText().toString();

                    if (!gcashName.isEmpty() && gcashNumber.length() == 11) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("Gcash Name", gcashName);
                        data.put("Gcash Number", gcashNumber);

                        // Save under unique driver ID
                        docRef.set(data)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Gcash information saved successfully!", Toast.LENGTH_SHORT).show();
                                    gname.setFocusable(false);
                                    gnumber.setFocusable(false);
                                    confirm.setEnabled(false); // Disable confirm button after saving
                                    finish();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(this, "Please enter an 11-digit Gcash number.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Error loading data: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        back.setOnClickListener(v -> {

            finish();
        });
    }


}