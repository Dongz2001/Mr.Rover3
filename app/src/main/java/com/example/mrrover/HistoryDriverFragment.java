package com.example.mrrover;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mrrover.adapter.DriverHistory1Adapter;
import com.example.mrrover.adapter.DriverHistoryAdapter;
import com.example.mrrover.adapter.UserBookingAdapter;
import com.example.mrrover.model.DriverHistoryModel;
import com.example.mrrover.model.UserBookingModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        history.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), Mr_driver_History.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

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

    void LoadDriver(){

        String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database.collection("bookings").whereEqualTo("Driver's UID", currentUserUID).addSnapshotListener(new EventListener<QuerySnapshot>() {
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

                    DriverHistoryModel booking = new DriverHistoryModel(driver, loc, desti, gender,  model, type, driverName, service, vehicle, date, time, status,bookingId,vehicleowner,driverUID, star, comments);
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

        /*String userUID = booking.getVehicleowner();

        NotificationRepository repository = new NotificationRepository();
        repository.NotifyRejectedBooking(userUID, new NotificationRepository.NotificationPushedCallback() {
            @Override
            public void onNotificationDone() {

            }
        });// Assuming this is the document ID

        // Remove booking from Firestore
        database.collection("bookings").document(bookingId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Check if position is still valid after successful deletion
                    if (position < bookingList.size()) {
                        bookingList.remove(position);  // Remove from the list
                        driverHistoryAdapter.notifyItemRemoved(position);  // Notify adapter
                    }
                    Toast.makeText(requireContext(), "Booking deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error deleting booking", Toast.LENGTH_SHORT).show();
                });*/
    }

    private void acceptBooking(int position) {
        if (position < 0 || position >= bookingList.size()) {
            Toast.makeText(requireContext(), "Invalid position", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Check if the user's UID exists in the "driver's gcash" collection
        database.collection("driver's gcash")
                .document(currentUserUID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Proceed with accepting the booking if the UID exists in "driver's gcash"
                        DriverHistoryModel booking = bookingList.get(position);

                        // Create a map with all booking details to add to the "acceptbookings" collection
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

                        // Add the booking details to the "acceptbookings" collection in Firestore
                        database.collection("acceptbookings").document()
                                .set(acceptedBooking)
                                .addOnSuccessListener(aVoid -> {
                                    booking.setStatus("In Progress");  // Update the local model
                                    driverHistoryAdapter.notifyItemChanged(position);  // Notify adapter
                                    Toast.makeText(requireContext(), "Booking accepted and stored", Toast.LENGTH_SHORT).show();

                                    // Delete the original booking from the "bookings" collection
                                    database.collection("bookings").document(booking.getBookingId())
                                            .delete()
                                            .addOnSuccessListener(aVoid1 -> {
                                                //Toast.makeText(requireContext(), "Original booking deleted", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(requireContext(), "Error deleting original booking", Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(requireContext(), "Error storing accepted booking", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // Show a message if the UID does not exist in "driver's gcash" and stop the process
                        Toast.makeText(requireContext(), "You should input your GCash in your profile", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error checking GCash information", Toast.LENGTH_SHORT).show();
                });
    }



}