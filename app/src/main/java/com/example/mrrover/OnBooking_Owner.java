package com.example.mrrover;

import static android.app.PendingIntent.getActivity;
import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class OnBooking_Owner extends AppCompatActivity {

    EditText from;
    EditText to;
    TextView timeS;
    TextView timeE;
    TextView payment;
    Button rateNow;
    Button confirm;
    Button cancel;
    Dialog dialog;
    Button pay;

    private String bookingId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_on_booking_owner);

        from = findViewById(R.id.location_text);
        to = findViewById(R.id.destination_text);
        //timeS = findViewById(R.id.timeSTART);
        //timeE = findViewById(R.id.timeEND);
        payment = findViewById(R.id.paymentinfo);
        rateNow = findViewById(R.id.rate_now_button);
        pay = findViewById(R.id.paybtn189);

        dialog = new Dialog(OnBooking_Owner.this);
        dialog.setContentView(R.layout.bookingfullpayment);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(OnBooking_Owner.this, R.drawable.custom_dialog_bg));
        dialog.setCancelable(false);

        confirm = dialog.findViewById(R.id.yes);
        cancel = dialog.findViewById(R.id.no);

        confirm.setOnClickListener(v -> {
            Intent intent1 = new Intent(this, OnBookingRate_Owner.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent1.putExtra("bookingId", bookingId);
            startActivity(intent1);
            dialog.dismiss();
        });

        cancel.setOnClickListener(v -> {

            dialog.dismiss();
        });

        rateNow.setOnClickListener(v -> {
            navigate();
        });

        from.setFocusable(false);
        to.setFocusable(false);


        Intent intent = getIntent();
        bookingId = intent.getStringExtra("bookingId");

        pay.setOnClickListener(v -> {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            // Fetch booking location and destination
            DocumentReference documentReference = firestore.collection("acceptbookings").document(bookingId);
            documentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        String loc = documentSnapshot.getString("Location");
                        String des = documentSnapshot.getString("Destination");

                        from.setText(loc);
                        to.setText(des);
                    } else {
                        Log.d("OnBooking_Owner", "Document does not exist");
                    }
                } else {
                    Log.d("OnBooking_Owner", "Failed with: ", task.getException());
                }
            });

            // Fetch and calculate payment based on TimeStarted and TimeEnd
            firestore.collection("BookingFullPayment")
                    .whereEqualTo("BookingID", bookingId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);

                                String timeStart = documentSnapshot.getString("TimeStarted");
                                String timeEnd = documentSnapshot.getString("TimeEnd");

                                if (timeStart != null && timeEnd != null) {
                                    try {
                                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                                        Date startTime = timeFormat.parse(timeStart);
                                        Date endTime = timeFormat.parse(timeEnd);

                                        long diffInMillis = endTime.getTime() - startTime.getTime();
                                        long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);

                                        // Calculate the bill
                                        long intervals = minutes / 10; // Count of 10-minute intervals
                                        long bill = intervals * 25;

                                        // Display the calculated bill
                                        Log.d("OnBooking_Owner", "Total Bill: " + bill + " PHP");
                                        payment.setText(String.valueOf(bill));
                                    } catch (ParseException e) {
                                        Log.e("OnBooking_Owner", "Error parsing times: ", e);
                                    }
                                } else {
                                    // Display system message if TimeEnd is null or missing
                                    Log.d("OnBooking_Owner", "TimeEnd is null or missing");
                                    new AlertDialog.Builder(v.getContext())
                                            .setTitle("Incomplete Booking")
                                            .setMessage("You haven't yet reached the destination.")
                                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                            .show();
                                }
                            } else {
                                Log.d("OnBooking_Owner", "No document matching the query");
                            }
                        } else {
                            Log.d("OnBooking_Owner", "Failed with: ", task.getException());
                        }
                    });
        });

    }
    void navigate() {
        dialog.show();
    }
}