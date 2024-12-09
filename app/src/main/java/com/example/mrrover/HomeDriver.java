package com.example.mrrover;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.mrrover.adapter.BookAcceptedAdapter;
import com.example.mrrover.adapter.DriverHistoryAdapter;
import com.example.mrrover.model.CustomerIDModel;
import com.example.mrrover.model.DriverHistoryModel;
import com.example.mrrover.model.DriverModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeDriver extends Fragment {

    Dialog dialog;
    Button confirm;

    FirebaseFirestore database = FirebaseFirestore.getInstance();

    RecyclerView bookingView;

    List<DriverHistoryModel> bookingList;

    BookAcceptedAdapter bookAcceptedAdapter;

    private String bookingId;
    private String currentUserUID;

    private Double lat;
    private Double lon;

    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE=100;

    public HomeDriver() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_driver, container, false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        getLastLocation();


        dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.driver_payment_confirmation);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.custom_dialog_bg));
        dialog.setCancelable(false);

        confirm = dialog.findViewById(R.id.confirmation123);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Firestore instance
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Query the 'gcashpayment' collection to find the document with the specified Booking ID
                db.collection("gcashpayment")
                        .whereEqualTo("Booking ID", bookingId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                // Update the 'Status' field in the found document(s)
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    db.collection("gcashpayment")
                                            .document(document.getId())
                                            .update("Status", "Confirmed")
                                            .addOnSuccessListener(aVoid -> {
                                                // Success: Perform any further actions if necessary
                                                //Toast.makeText(getActivity(), "Status updated to Confirmed!", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                // Handle the error
                                                Toast.makeText(getActivity(), "Failed to update status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                }
                            } else {
                                // Handle the case where no matching document is found
                                Toast.makeText(getActivity(), "No document found with Booking ID: " + bookingId, Toast.LENGTH_SHORT).show();
                            }
                        });

                // Proceed with other actions (e.g., navigating to a new activity)
                Intent intent = new Intent(getActivity(), OnBookDriver.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("bookingId", bookingId);
                startActivity(intent);
                dialog.dismiss();
            }
        });


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookingView = view.findViewById(R.id.recycleView5);
        bookingView.setLayoutManager(new LinearLayoutManager(requireContext()));
        bookingList = new ArrayList<>();
        bookAcceptedAdapter = new BookAcceptedAdapter(bookingList);
        bookAcceptedAdapter.setOnBookListener(position -> booking(position));
        bookAcceptedAdapter.setOnChatListener(position -> chat(position));
        bookingView.setAdapter(bookAcceptedAdapter);
        LoadDriver();


    }


    void LoadDriver(){

        currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database.collection("acceptbookings").whereEqualTo("Driver's UID", currentUserUID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e!=null){
                    Toast.makeText(requireContext(), "No data  found", Toast.LENGTH_SHORT).show();
                    return;
                }
                bookingList.clear();
                for (QueryDocumentSnapshot document: queryDocumentSnapshots){
                    String userUid = document.getId();
                    String driverName = document.getString("Vehicle Owner's Name");
                    String service = document.getString("Service");
                    String  vehicle = document.getString("Vehicle");
                    String  date = document.getString("Date");
                    String  time = document.getString("Time");
                    String  status = document.getString("Status");
                    String bookingId = document.getId();
                    String vehicleowner = document.getString("Vehicle Owner's UID");
                    String driverUID = document.getString("Driver's UID");
                    String driver = document.getString("Driver's Name");
                    String loc = document.getString("Location");
                    String desti = document.getString("Destination");
                    String gender = document.getString("Gender");
                    String model = document.getString("Model");
                    String type = document.getString("Type");
                    Double star = document.getDouble("rating");
                    String comments = document.getString("comments");

                    DriverHistoryModel booking = new DriverHistoryModel(driver, loc, desti, gender,  model, type, driverName, service, vehicle, date, time, status,bookingId,vehicleowner,driverUID,star,comments);
                    bookingList.add(booking);

                }

                bookAcceptedAdapter.notifyDataSetChanged();
            }

        });
    }
    private void booking(int position) {
        // Validate the position
        if (position < 0 || position >= bookingList.size()) {
            Toast.makeText(requireContext(), "Invalid position", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the selected booking
        DriverHistoryModel booking = bookingList.get(position);
        bookingId = booking.getBookingId();

        // Query Firestore for the booking
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection("gcashpayment")
                .whereEqualTo("Booking ID", bookingId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        // Get the first matching document
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);

                        // Check the Status field
                        String status = document.getString("Status");
                        if ("Paid".equals(status)) {
                            // Navigate if Status is "Paid"
                            navigate();
                        } else if ("Confirmed".equals(status)) {
                            // Navigate to OnBookDriver if Status is "Confirmed"
                            Intent intent = new Intent(requireContext(), OnBookDriver.class);
                            intent.putExtra("bookingId", bookingId);
                            startActivity(intent);
                        } else {
                            // If Status is missing or unknown
                            Toast.makeText(requireContext(), "The Vehicle Owner has not yet paid", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // If no documents found or query failed
                        Toast.makeText(requireContext(), "The Vehicle Owner has not yet paid", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void chat(int position) {
        if (position < 0 || position >= bookingList.size()) {
            Toast.makeText(requireContext(), "Invalid position", Toast.LENGTH_SHORT).show();
            return;
        }

        DriverHistoryModel booking = bookingList.get(position);
        String bookingId = booking.getBookingId();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch data from "acceptbookings" collection
        db.collection("acceptbookings")
                .document(bookingId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String driverName = documentSnapshot.getString("Vehicle Owner's Name");

                        if (driverName != null) {
                            Intent intent = new Intent(requireContext(), ChatActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("Vehicle Owner's Name", driverName);
                            intent.putExtra("bookingId", bookingId);
                            startActivity(intent);
                        } else {
                            Toast.makeText(requireContext(), "Driver's name not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Booking not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to fetch booking details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            if (location != null) {

                                Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                                    lat = addresses.get(0).getLatitude();
                                    lon = addresses.get(0).getLongitude();
                                    String address = addresses.get(0).getAddressLine(0);

                                    //latitude.setText("Latitude: " + lat);
                                    //longitude.setText("Longitude: " + lon);
                                    //currentLoc.setText("Address: " + address);

                                    // Save to Firebase
                                    saveLocationToDatabase(lat, lon);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

        } else {
            askPermission();
        }
    }

    private void askPermission(){
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }
            else {
                Toast.makeText(requireContext(), "Required Permission", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void saveLocationToDatabase(double lat, double lon) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String currentUserUID = firebaseAuth.getCurrentUser() != null ? firebaseAuth.getCurrentUser().getUid() : null;

        if (currentUserUID == null || currentUserUID.isEmpty()) {
            // Log or display error: Invalid user ID
            return;
        }

        // Reference to the user's document
        DocumentReference userDocRef = database.collection("drivers").document(currentUserUID);

        // Retrieve the user's document
        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                // Create a map to store the updated location
                Map<String, Object> updatedLocation = new HashMap<>();
                updatedLocation.put("Current Latitude", lat);
                updatedLocation.put("Current Longitude", lon);

                // Update the document with new data
                userDocRef.update(updatedLocation)
                        .addOnSuccessListener(aVoid -> {
                            // Log or display success message
                        })
                        .addOnFailureListener(e -> {
                            // Log or display error message
                        });
            } else {
                // Log or display error: Document not found
            }
        }).addOnFailureListener(e -> {
            // Log or display error: Failed to retrieve document
        });
    }



    void navigate() {

        dialog.show();
    }
}