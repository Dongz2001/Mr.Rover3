package com.example.mrrover;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mrrover.adapter.DriverHistoryAdapter;
import com.example.mrrover.component.InitializeNotification;
import com.example.mrrover.component.NotificationHelper;
import com.example.mrrover.model.DriverHistoryModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryDriverFragment extends Fragment {

    FirebaseFirestore database = FirebaseFirestore.getInstance();

    RecyclerView bookingView;

    List<DriverHistoryModel> bookingList;

    DriverHistoryAdapter driverHistoryAdapter;

    ImageView history;

    public HistoryDriverFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history_driver, container, false);

        history = view.findViewById(R.id.history098);

        history.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Mr_driver_History.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Request notification permission for Android 13 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"}, 1);
        }

        // you can also pass an user id here to assign the notification to a specific user
        String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        InitializeNotification.getInstance().testLoadNotification(requireContext(), currentUserUID);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookingView = view.findViewById(R.id.recycleView2);
        bookingView.setLayoutManager(new LinearLayoutManager(requireContext()));
        bookingList = new ArrayList<>();
        driverHistoryAdapter = new DriverHistoryAdapter(bookingList);
        driverHistoryAdapter.setOnBookListener(position -> deleteBooking(position));
        driverHistoryAdapter.setOnAcceptListener(position -> acceptBooking(position));
        bookingView.setAdapter(driverHistoryAdapter);
        LoadDriver();
    }

    void LoadDriver() {
        String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database.collection("bookings").whereEqualTo("Driver's UID", currentUserUID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(requireContext(), "No data found", Toast.LENGTH_SHORT).show();
                    return;
                }
                bookingList.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    String userUid = document.getId();
                    String driverName = document.getString("Vehicle Owner's Name");
                    String service = document.getString("Service");
                    String vehicle = document.getString("Vehicle");
                    String date = document.getString("Date");
                    String time = document.getString("Time");
                    String status = document.getString("Status");
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

                    DriverHistoryModel booking = new DriverHistoryModel(driver, loc, desti, gender, model, type, driverName, service, vehicle, date, time, status, bookingId, vehicleowner, driverUID, star, comments);
                    bookingList.add(booking);
                }

                driverHistoryAdapter.notifyDataSetChanged();
            }
        });
    }

    private void deleteBooking(int position) {
        if (position < 0 || position >= bookingList.size()) {
            Toast.makeText(requireContext(), "Invalid position", Toast.LENGTH_SHORT).show();
            return;
        }

        DriverHistoryModel booking = bookingList.get(position);
        String bookingId = booking.getBookingId();

        // Create a map with cancellation details to add to the "notification" collection
        LocalTime time = LocalTime.now();
        Map<String, Object> cancellationNotification = new HashMap<>();
        cancellationNotification.put("BookingId", bookingId);
        cancellationNotification.put("DriverName", booking.getDriverName());
        cancellationNotification.put("Vehicle Owner's UID", booking.getVehicleowner());
        cancellationNotification.put("Service", booking.getService());
        cancellationNotification.put("Date", booking.getDdate());
        cancellationNotification.put("Time", booking.getTtime());
        cancellationNotification.put("Time of cancellation", time.toString());
        cancellationNotification.put("Status", "Cancelled");
        cancellationNotification.put("description", "Booking has been cancelled by the driver by" + booking.getDriverName());
        cancellationNotification.put("isNotificationSeen", false);

        // Add the cancellation details to the "notification" collection in Firestore
        database.collection("notification").document()
                .set(cancellationNotification)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Booking cancelled and notification stored", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error storing cancellation notification", Toast.LENGTH_SHORT).show();
                });

        database.collection("bookings").document(bookingId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    if (position < bookingList.size()) {
                        bookingList.remove(position);
                        driverHistoryAdapter.notifyItemRemoved(position);
                    }
                    Toast.makeText(requireContext(), "Booking deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error deleting booking", Toast.LENGTH_SHORT).show();
                });
    }

    private void acceptBooking(int position) {
        if (position < 0 || position >= bookingList.size()) {
            Toast.makeText(requireContext(), "Invalid position", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database.collection("driver's gcash")
                .document(currentUserUID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        DriverHistoryModel booking = bookingList.get(position);

                        Map<String, Object> acceptedBooking = new HashMap<>();
                        acceptedBooking.put("Date", booking.getDdate());
                        acceptedBooking.put("Service", booking.getService());
                        acceptedBooking.put("Status", "In-Progress");
                        acceptedBooking.put("Time", booking.getTtime());
                        acceptedBooking.put("Vehicle", booking.getVehicle());
                        acceptedBooking.put("Vehicle Owner's Name", booking.getDriverName());
                        acceptedBooking.put("Vehicle Owner's UID", booking.getVehicleowner());
                        acceptedBooking.put("Driver's UID", booking.getdriverUID());
                        acceptedBooking.put("Driver's Name", booking.getdriverNAME());
                        acceptedBooking.put("Location", booking.getloc());
                        acceptedBooking.put("Destination", booking.getdesti());
                        acceptedBooking.put("Gender", booking.getgender());
                        acceptedBooking.put("Model", booking.getmodel());
                        acceptedBooking.put("Type", booking.gettype());

                        database.collection("acceptbookings").document()
                                .set(acceptedBooking)
                                .addOnSuccessListener(aVoid -> {
                                    booking.setStatus("In Progress");
                                    driverHistoryAdapter.notifyItemChanged(position);
                                    Toast.makeText(requireContext(), "Booking accepted and stored", Toast.LENGTH_SHORT).show();

                                    database.collection("bookings").document(booking.getBookingId())
                                            .delete()
                                            .addOnSuccessListener(aVoid1 -> {
                                                // Original booking deleted
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(requireContext(), "Error deleting original booking", Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(requireContext(), "Error storing accepted booking", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(requireContext(), "You should input your GCash in your profile", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error checking GCash information", Toast.LENGTH_SHORT).show();
                });
    }


}