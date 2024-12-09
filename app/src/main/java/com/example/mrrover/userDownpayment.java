package com.example.mrrover;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mrrover.model.DriverHistoryModel;
import com.example.mrrover.model.GcashModel;
import com.example.mrrover.utils.AndroidUtil;
import com.example.mrrover.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class userDownpayment extends AppCompatActivity {

    TextView gcashName, gcashNumber, payment;
    Button uploadProof, submit;
    Dialog dialog;
    Button home;

    ActivityResultLauncher<Intent> imagePickLauncher1;
    Uri gcashProof;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    private String bookingId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_downpayment);

        gcashName = findViewById(R.id.driverName);
        gcashNumber = findViewById(R.id.driverNumber);
        payment = findViewById(R.id.PAY143);
        uploadProof = findViewById(R.id.uploadImageButton);
        submit = findViewById(R.id.submitButton);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        dialog = new Dialog(this);
        dialog.setContentView(R.layout.user_proofofpayment);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.custom_dialog_bg));
        dialog.setCancelable(false);

        home = dialog.findViewById(R.id.pay_installment);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new instance of HomeFragment
                Intent intent = new Intent(userDownpayment.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


        Intent intent = getIntent();
        bookingId = intent.getStringExtra("bookingId");
        String driversUID = intent.getStringExtra("driversUID");

        if (bookingId != null) {
            setBookingType(bookingId);
        }

        if (driversUID != null) {
            fetchDriverGcashDetails(driversUID);
        }

        imagePickLauncher1 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            gcashProof = data.getData();
                        }
                    }
                }
        );

        uploadProof.setOnClickListener(v -> {
            ImagePicker.with(userDownpayment.this)
                    .cropSquare()
                    .compress(512)
                    .maxResultSize(512, 512)
                    .createIntent(intent1 -> {
                        imagePickLauncher1.launch(intent1);
                        return null;
                    });
        });

        submit.setOnClickListener(v -> {
            if (gcashProof != null) {
                uploadGcashProofAndSavePayment(driversUID);
            } else {
                Toast.makeText(userDownpayment.this, "Please select an image to upload", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setBookingType(String bookingId) {
        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("acceptbookings").child(bookingId);

        bookingRef.child("Type").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.getValue(String.class);
                if ("Drop Off".equals(type)) {
                    payment.setText("100.00");
                } else if ("Round Trip".equals(type)) {
                    payment.setText("200.00");
                } else {
                    payment.setText("Unknown Type");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(userDownpayment.this, "Failed to fetch booking details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDriverGcashDetails(String driversUID) {
        DocumentReference documentReference = firestore.collection("driver's gcash").document(driversUID);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("Gcash Name");
                    String number = documentSnapshot.getString("Gcash Number");
                    gcashName.setText(name);
                    gcashNumber.setText(number);
                } else {
                    Toast.makeText(userDownpayment.this, "Driver's GCash details not found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(userDownpayment.this, "Failed to fetch GCash details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadGcashProofAndSavePayment(String driversUID) {
        StorageReference storageRef = FirebaseUtil.getLicenseID().child("GcashReceipt_" + System.currentTimeMillis() + ".jpg");

        storageRef.putFile(gcashProof).addOnCompleteListener(uploadTask -> {
            if (uploadTask.isSuccessful()) {
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String proofUrl = uri.toString();
                    savePaymentDetails(driversUID, proofUrl);
                }).addOnFailureListener(e -> {
                    Toast.makeText(userDownpayment.this, "Failed to get GCash Receipt URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(userDownpayment.this, "Image upload failed: " + uploadTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void savePaymentDetails(String driversUID, String proofUrl) {
        String userID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = firestore.collection("gcashpayment").document();

        Map<String, Object> gcashPaymentData = new HashMap<>();
        gcashPaymentData.put("Vehicle Owner UID", userID);
        gcashPaymentData.put("Driver UID", driversUID);
        gcashPaymentData.put("Gcash Proof", proofUrl);
        gcashPaymentData.put("Booking ID", bookingId);
        gcashPaymentData.put("Status", "Paid");

        documentReference.set(gcashPaymentData).addOnSuccessListener(unused -> {
            Toast.makeText(userDownpayment.this, "Payment Information Saved Successfully", Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(getApplicationContext(), MR_Login_Driver.class));
            navigate();
        }).addOnFailureListener(e -> {
            Toast.makeText(userDownpayment.this, "Failed to save payment information: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
    void navigate() {
        dialog.show();
    }
}