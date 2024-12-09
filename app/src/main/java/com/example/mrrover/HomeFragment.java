package com.example.mrrover;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.constraintlayout.motion.widget.Debug.getLocation;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.PeriodicWorkRequest;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mrrover.adapter.BookAcceptedAdapter;
import com.example.mrrover.adapter.UserBookingAcceptedAdapter;
import com.example.mrrover.model.DriverHistoryModel;
import com.example.mrrover.model.UserBookingModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class HomeFragment extends Fragment {

    Button button;
    Dialog dialog;
    Dialog dialog1;
    Dialog dialog2;
    Button sHort;
    Button Long;
    Button pay;
    Button pay2;
    private String bookingId;
    private String driversUID;
    private String currentUserUID;
    private Double lat;
    private Double lon;
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    RecyclerView bookingView;
    List<DriverHistoryModel> bookingList;
    UserBookingAcceptedAdapter userBookingAcceptedAdapter;
    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE=100;


    /*private static final String CHANNEL_ID = "channel_id";
    private static final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;

    private ActivityResultLauncher<String> requestNotificationPermissionLauncher;*/


    public HomeFragment() {


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


       /* requestNotificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {

                        showNotification(null);
                    } else {

                        Toast.makeText(requireContext(), "Notification permission denied.", Toast.LENGTH_SHORT).show();
                    }
                }
        );*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);



        /*Button notificationButton = view.findViewById(R.id.notificationButton);
        notificationButton.setOnClickListener(this::checkAndShowNotification);

        createNotificationChannel();*/


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        getLastLocation();

        /*getLocation.setOnClickListener(v -> {

            getLastLocation();
            //latitude.setText("Latitude: " +addresses.get(0).getLatitude());
            //longitude.setText("Longitude: " +addresses.get(0).getLongitude());
            //city.setText("City: " +addresses.get(0).getLocality());
            //country.setText("Country: " +addresses.get(0).getCountryName());
            //currentLoc.setText("Address: " +addresses.get(0).getAddressLine(0));
        });*/


        dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.custom_dialog_box);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.custom_dialog_bg));
        dialog.setCancelable(false);


        dialog1 = new Dialog(requireContext());
        dialog1.setContentView(R.layout.user_pay_installment);
        dialog1.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog1.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.custom_dialog_bg));
        dialog1.setCancelable(false);

        dialog2 = new Dialog(requireContext());
        dialog2.setContentView(R.layout.user_pay200);
        dialog2.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog1.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.custom_dialog_bg));
        dialog2.setCancelable(false);


        sHort = dialog.findViewById(R.id.shortterm);
        Long = dialog.findViewById(R.id.longterm);

        pay = dialog1.findViewById(R.id.pay_installment);
        pay2 = dialog2.findViewById(R.id.pay_installment);

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), userDownpayment.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("bookingId", bookingId);
                intent.putExtra("driversUID", driversUID);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        pay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), userDownpayment.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("bookingId", bookingId);
                intent.putExtra("driversUID", driversUID);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        sHort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Booking.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        Long.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(getActivity(), Booking_Longterm.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);*/
                dialog.dismiss();
            }
        });

        button = view.findViewById(R.id.book_now);

        button.setOnClickListener((v -> {
            navigate();
        }));
        return view;
    }
    void navigate() {
        dialog.show();
    }
    void navigate1() {
        dialog1.show();
    }
    void navigate2() {
        dialog2.show();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookingView = view.findViewById(R.id.recycleView666);
        bookingView.setLayoutManager(new LinearLayoutManager(requireContext()));
        bookingList = new ArrayList<>();
        userBookingAcceptedAdapter = new UserBookingAcceptedAdapter(bookingList);
        userBookingAcceptedAdapter.setOnBookListener(position -> booking(position));
        userBookingAcceptedAdapter.setOnChatListener(position -> chat(position));
        bookingView.setAdapter(userBookingAcceptedAdapter);
        LoadDriver();


    }


    void LoadDriver(){

        currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database.collection("acceptbookings").whereEqualTo("Vehicle Owner's UID", currentUserUID).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                userBookingAcceptedAdapter.notifyDataSetChanged();
            }

        });
    }

    private void booking(int position) {
        if (position < 0 || position >= bookingList.size()) {
            Toast.makeText(requireContext(), "Invalid position", Toast.LENGTH_SHORT).show();
            return;
        }

        DriverHistoryModel booking = bookingList.get(position);
        bookingId = booking.getBookingId(); // Get the Booking ID
        driversUID = booking.getdriverUID(); // Get the Driver's UID

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query Firestore for the document in gcashpayment collection matching the Booking ID
        db.collection("gcashpayment")
                .whereEqualTo("Booking ID", bookingId) // Match the Booking ID
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Get the first document matching the query
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);

                        // Check the "Status" field
                        String status = document.getString("Status");
                        if ("Confirmed".equals(status)) {
                            // If Status is Confirmed, navigate to OnBooking_Owner
                            Intent intent = new Intent(requireContext(), OnBooking_Owner.class);
                            intent.putExtra("bookingId", bookingId);
                            startActivity(intent);
                        } else if ("Paid".equals(status)) {
                            // If Status is Paid, prompt the user to wait for driver confirmation
                            Toast.makeText(requireContext(), "Please wait for the driver to confirm.", Toast.LENGTH_SHORT).show();
                        } else {
                            // If no status or unrecognized status, proceed to acceptBooking
                            acceptBooking(booking);
                        }
                    } else {
                        // If no document found, proceed to acceptBooking
                        acceptBooking(booking);
                    }
                });
    }

    private void acceptBooking(DriverHistoryModel booking) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("acceptbookings").child(bookingId);

        String bookingType = booking.gettype();
        Map<String, Object> acceptedBooking = new HashMap<>();
        acceptedBooking.put("Type", bookingType);

        databaseRef.setValue(acceptedBooking).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if ("Drop Off".equals(bookingType)) {
                    navigate1();
                } else if ("Round Trip".equals(bookingType)) {
                    navigate2();
                } else {
                    Toast.makeText(requireContext(), "Unknown booking type", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Failed to save booking", Toast.LENGTH_SHORT).show();
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
                        String driverName = documentSnapshot.getString("Driver's Name");

                        if (driverName != null) {
                            Intent intent = new Intent(requireContext(), ChatActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("Driver's Name", driverName);
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
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

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

            return;
        }


        DocumentReference userDocRef = database.collection("users").document(currentUserUID);


        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {

                Map<String, Object> updatedLocation = new HashMap<>();
                updatedLocation.put("Current Latitude", lat);
                updatedLocation.put("Current Longitude", lon);

                // Update the document with new data
                userDocRef.update(updatedLocation)
                        .addOnSuccessListener(aVoid -> {

                        })
                        .addOnFailureListener(e -> {

                        });
            } else {

            }
        }).addOnFailureListener(e -> {

        });
    }




    /*private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Channel Name",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel Description");

            NotificationManager manager = requireContext().getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void checkAndShowNotification(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {

                showNotification(view);
            } else {

                requestNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {

            showNotification(view);
        }
    }

    public void showNotification(View view) {
        Intent intent = new Intent(requireContext(), HomeFragment.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_chat_24) // Replace with your icon
                .setContentTitle("Mr.Rover")
                .setContentText("SANDBOX TEAM")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());

        try {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } catch (SecurityException e) {
            Toast.makeText(requireContext(), "Failed to show notification. Permission denied.", Toast.LENGTH_SHORT).show();
        }
    }*/

}