package com.example.mrrover;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MR_Reservation extends AppCompatActivity {

    TextView service, typeService, date, time, location, destination, vehicle, model, driverName, gender;
    Button book;
    TextView rating;

    private String fname111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mr_reservation);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        String usersUid = currentUser.getUid();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        DocumentReference documentReference = firestore.collection("users").document(usersUid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        // Assuming the fields exist in Firestore
                        String first1 = documentSnapshot.getString("Firstname");
                        String last1 = documentSnapshot.getString("Lastname");

                        // Set the values in your EditText fields
                        fname111  = first1+ " " + last1;
                    } else {
                        Log.d("MR_Reservation", "Document does not exist");
                    }
                } else {
                    Log.d("MR_Reservation", "Failed with: ", task.getException());
                }
            }
        });

        Intent intent2 = getIntent();
        Double driverRating = intent2.getDoubleExtra("driverRatings", 0.0);
        String driverUID = intent2.getStringExtra("DRIVER'S ID");
        String fullname = intent2.getStringExtra("FULLNAME");
        String genders = intent2.getStringExtra("GENDER");
        String typeofservice = intent2.getStringExtra("SERVICE_TYPE");
        String res_date = intent2.getStringExtra("DATE");
        String res_time = intent2.getStringExtra("TIME");
        String from = intent2.getStringExtra("FROM");
        String to = intent2.getStringExtra("TO");
        String res_vehicle = intent2.getStringExtra("VEHICLE_TYPE");
        String res_model = intent2.getStringExtra("MODEL");

        driverName = findViewById(R.id.driverName);
        rating = findViewById(R.id.driverRating1);
        gender =  findViewById(R.id.driverLocation);
        service = findViewById(R.id.service1);
        typeService = findViewById(R.id.type_Service);
        date = findViewById(R.id.dateee);
        time = findViewById(R.id.time_reserve);
        location = findViewById(R.id.loc);
        destination = findViewById(R.id.desti);
        vehicle = findViewById(R.id.vehicle123);
        model = findViewById(R.id.model123);

        book = findViewById(R.id.bookButton);


        driverName.setText(fullname);
        rating.setText(String.valueOf(driverRating));
        gender.setText(genders);
        service.setText("Short-Term Service");
        typeService.setText(typeofservice);
        date.setText(res_date);
        time.setText(res_time);
        location.setText(from);
        destination.setText(to);
        vehicle.setText(res_vehicle);
        model.setText(res_model);

        //FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String service1 = service.getText().toString().trim();
                String type1 = typeService.getText().toString().trim();
                String date1 = date.getText().toString().trim();
                String time1 = time.getText().toString().trim();
                String location1 = location.getText().toString().trim();
                String destination1 = destination.getText().toString().trim();
                String vehicle1 = vehicle.getText().toString().trim();
                String model1 = model.getText().toString().trim();

                Map<String, Object> bookingDetails = new HashMap<>();
                bookingDetails.put("Vehicle Owner's Name", fname111);
                bookingDetails.put("Vehicle Owner's UID", usersUid);
                bookingDetails.put("Driver's UID", driverUID);
                bookingDetails.put("Driver's Name", fullname);
                bookingDetails.put("Gender", genders);
                bookingDetails.put("Service", service1);
                bookingDetails.put("Type", type1);
                bookingDetails.put("Date", date1);
                bookingDetails.put("Time", time1);
                bookingDetails.put("Location", location1);
                bookingDetails.put("Destination", destination1);
                bookingDetails.put("Vehicle", vehicle1);
                bookingDetails.put("Model", model1);
                bookingDetails.put("Status", "Pending");

                // Save the booking details in Firestore
                firestore.collection("bookings").add(bookingDetails).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MR_Reservation.this, "Booking Success!", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "Booking ID: " + documentReference.getId());
                        Intent intent = new Intent(MR_Reservation.this, AfterBooking.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MR_Reservation.this, "Failed to Save Booking: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }
}