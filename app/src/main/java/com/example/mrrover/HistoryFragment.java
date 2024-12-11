package com.example.mrrover;

import static android.app.ProgressDialog.show;

import static java.security.AccessController.getContext;

import android.app.Dialog;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mrrover.adapter.DriverAdapter;
import com.example.mrrover.adapter.UserBookingAdapter;
import com.example.mrrover.model.DriverHistoryModel;
import com.example.mrrover.model.DriverModel;
import com.example.mrrover.model.UserBookingModel;
import com.example.mrrover.model.UserModel;
import com.example.mrrover.utils.AndroidUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
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


public class HistoryFragment extends Fragment {

    FirebaseFirestore database = FirebaseFirestore.getInstance();

    RecyclerView bookingView;

    List<UserBookingModel> bookingList;

    UserBookingAdapter userBookingAdapter;

    ImageView history;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        history = view.findViewById(R.id.history);

        history.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), Mr_user_History.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookingView = view.findViewById(R.id.recycleView1);
        bookingView.setLayoutManager(new LinearLayoutManager(requireContext())); // Use requireContext() in fragments
        bookingList = new ArrayList<>();
        userBookingAdapter = new UserBookingAdapter(bookingList);

        userBookingAdapter.setOnBookListener(position -> deleteBooking(position));
        bookingView.setAdapter(userBookingAdapter);
        LoadDriver();
    }
    void LoadDriver(){

        String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database.collection("bookings").whereEqualTo("Vehicle Owner's UID", currentUserUID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e!=null){
                    Toast.makeText(requireContext(), "No data  found", Toast.LENGTH_SHORT).show();
                    return;
                }
                bookingList.clear();
                for (QueryDocumentSnapshot document: queryDocumentSnapshots){
                    String userUid = document.getId();
                    String driverName = document.getString("Driver's Name");
                    String service = document.getString("Service");
                    String  vehicle = document.getString("Vehicle");
                    String  date = document.getString("Date");
                    String  time = document.getString("Time");
                    String  status = document.getString("Status");
                    String bookingId = document.getId();

                    UserBookingModel booking = new UserBookingModel(bookingId,userUid, driverName, service, vehicle, date, time, status);
                    bookingList.add(booking);
                }
                userBookingAdapter.notifyDataSetChanged();
            }
        });
    }
    private void deleteBooking(int position) {
        if (position < 0 || position >= bookingList.size()) {
            Toast.makeText(requireContext(), "Invalid position", Toast.LENGTH_SHORT).show();
            return;
        }

        UserBookingModel booking = bookingList.get(position);
        String bookingId = booking.getBookingId(); // Assuming this is the document ID

        // Create a map with cancellation details to add to the "notification" collection
        LocalTime time = LocalTime.now();
        Map<String, Object> cancellationNotification = new HashMap<>();
        cancellationNotification.put("BookingId", bookingId);
        cancellationNotification.put("DriverName", booking.getDriverName());
        cancellationNotification.put("userUid", booking.getUserUid());
        cancellationNotification.put("Service", booking.getService());
        cancellationNotification.put("Date", booking.getDdate());
        cancellationNotification.put("Time", booking.getTtime());
        cancellationNotification.put("Time of cancellation", time.toString());
        cancellationNotification.put("Status", "Cancelled");
        cancellationNotification.put("description", "Booking has been cancelled the driver");
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

        // Remove booking from Firestore
        database.collection("bookings").document(bookingId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Check if position is still valid after successful deletion
                    if (position < bookingList.size()) {
                        bookingList.remove(position);  // Remove from the list
                        userBookingAdapter.notifyItemRemoved(position);  // Notify adapter
                    }
                    Toast.makeText(requireContext(), "Booking deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error deleting booking", Toast.LENGTH_SHORT).show();
                });
    }



}


