package com.example.mrrover;

import static android.app.PendingIntent.getActivity;
import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OnBookDriver extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText fromLocation;
    private EditText toLocation;
    private Button start;
            Button end;

    Button finish;

    Dialog dialog;

    private String bookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_on_book_driver);

        fromLocation = findViewById(R.id.fromEditText);
        toLocation = findViewById(R.id.toEditText);
        start = findViewById(R.id.trackRideButton);
        end = findViewById(R.id.trackRideButton1);

        Intent intent = getIntent();
        bookingId = intent.getStringExtra("bookingId");

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.driver_endofbooking);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.custom_dialog_bg));
        dialog.setCancelable(false);

        finish = dialog.findViewById(R.id.confirmation789);

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                dialog.dismiss(); // Close the dialog
            }
        });


        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        DocumentReference documentReference = firestore.collection("acceptbookings").document(bookingId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        // Assuming the fields exist in Firestore
                        String loc = documentSnapshot.getString("Location");
                        String des = documentSnapshot.getString("Destination");

                        // Set the values in your EditText fields
                        fromLocation.setText(loc);
                        toLocation.setText(des);
                    } else {
                        Log.d("OnBookDriver", "Document does not exist");
                    }
                } else {
                    Log.d("OnBookDriver", "Failed with: ", task.getException());
                }
            }
        });



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Error loading map fragment", Toast.LENGTH_SHORT).show();
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        start.setOnClickListener(v -> {
            String from = fromLocation.getText().toString().trim();
            String to = toLocation.getText().toString().trim();

            if (!from.isEmpty() && !to.isEmpty()) {
                // Show the route
                showRoute(from, to);

                // Get the current time
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

                String bookingId = getIntent().getStringExtra("bookingId");

                // Reference to Firebase database
                DocumentReference bookingDocumentReference = firestore.collection("BookingFullPayment").document();

                // Create a map to store the booking details
                Map<String, String> bookingDetails = new HashMap<>();
                bookingDetails.put("BookingID", bookingId);
                bookingDetails.put("TimeStarted", currentTime);

                // Save the booking details to Firebase
                bookingDocumentReference.set(bookingDetails).addOnSuccessListener(unused -> {
                    Toast.makeText(OnBookDriver.this, "Booking Started", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(OnBookDriver.this, "Failed to save payment information: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(this, "Please enter both locations", Toast.LENGTH_SHORT).show();
            }
        });

        end.setOnClickListener(v -> {
            /*FirebaseFirestore database = FirebaseFirestore.getInstance();
            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            String bookingId = getIntent().getStringExtra("bookingId");

            if (bookingId != null && !bookingId.isEmpty()) {
                database.collection("BookingFullPayment")
                        .whereEqualTo("BookingID", bookingId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    String docId = document.getId();

                                    Map<String, Object> updateData = new HashMap<>();
                                    updateData.put("TimeEnd", currentTime);

                                    database.collection("BookingFullPayment")
                                            .document(docId)
                                            .update(updateData)
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("Firestore", "TimeEnd successfully updated!");
                                                navigate(); // Show the dialog after successful update
                                            })
                                            .addOnFailureListener(e ->
                                                    Log.e("Firestore", "Error updating TimeEnd", e));
                                }
                            } else {
                                Log.d("Firestore", "No matching documents found.");
                            }
                        })
                        .addOnFailureListener(e ->
                                Log.e("Firestore", "Error executing query", e));
            } else {
                Log.e("Firestore", "Invalid or missing bookingId");
            }*/
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng defaultLocation = new LatLng(10.3097, 123.9484);
        float zoomLevel = 15.0f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, zoomLevel));
    }

    private void showRoute(String from, String to) {
        mMap.clear();

        Geocoder geocoder = new Geocoder(this);
        LatLngBounds cebuBounds = new LatLngBounds(
                new LatLng(9.2144, 123.5897),
                new LatLng(11.229, 125.2906)
        );

        try {
            List<Address> fromAddresses = geocoder.getFromLocationName(from, 1,
                    cebuBounds.southwest.latitude, cebuBounds.southwest.longitude,
                    cebuBounds.northeast.latitude, cebuBounds.northeast.longitude);

            List<Address> toAddresses = geocoder.getFromLocationName(to, 1,
                    cebuBounds.southwest.latitude, cebuBounds.southwest.longitude,
                    cebuBounds.northeast.latitude, cebuBounds.northeast.longitude);

            if (!fromAddresses.isEmpty() && !toAddresses.isEmpty()) {
                LatLng fromLatLng = new LatLng(fromAddresses.get(0).getLatitude(), fromAddresses.get(0).getLongitude());
                LatLng toLatLng = new LatLng(toAddresses.get(0).getLatitude(), toAddresses.get(0).getLongitude());

                mMap.addMarker(new MarkerOptions()
                        .position(fromLatLng)
                        .title("From: " + from)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                mMap.addMarker(new MarkerOptions()
                        .position(toLatLng)
                        .title("To: " + to)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(fromLatLng);
                builder.include(toLatLng);
                LatLngBounds bounds = builder.build();
                int padding = 100;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.moveCamera(cu);

                // Draw a polyline between the two locations
                mMap.addPolyline(new PolylineOptions()
                        .add(fromLatLng, toLatLng)
                        .width(10)
                        .color(Color.RED));

            } else {
                Toast.makeText(this, "Unable to geocode locations within Cebu", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e("OnBookDriver", "Geocoding error", e);
            Toast.makeText(this, "An error occurred while geocoding. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    void navigate() {
        if (dialog != null) {
            dialog.show();
        } else {
            Log.e("OnBookDriver", "Dialog is null");
        }
    }
}