package com.example.mrrover;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mrrover.model.DriverHistoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Map;

public class OnBookingRate_Owner extends AppCompatActivity {

    TextView driverName;
    RatingBar ratingBar;
    EditText comments;
    Button submitbtn;

    FirebaseFirestore database = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_on_booking_rate_owner);

        driverName = findViewById(R.id.profile_name);
        ratingBar = findViewById(R.id.ratingBar1);
        comments = findViewById(R.id.comment_box);
        submitbtn = findViewById(R.id.submit_button);

        Intent intent = getIntent();
        String bookingId = intent.getStringExtra("bookingId");

        submitbtn.setOnClickListener(v -> saveRatingAndComment(bookingId));

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        DocumentReference documentReference = firestore.collection("acceptbookings").document(bookingId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        // Assuming the fields exist in Firestore
                        String name = documentSnapshot.getString("Driver's Name");
                        driverName.setText(name);
                    } else {
                        Log.d("OnBookingRate_Owner", "Document does not exist");
                    }
                } else {
                    Log.d("OnBookingRate_Owner", "Failed with: ", task.getException());
                }
            }
        });


    }
    private void saveRatingAndComment(String bookingId) {
        // Retrieve ratings and comments
        float userRating = ratingBar.getRating();
        String userComments = comments.getText().toString();

        if (bookingId == null || bookingId.isEmpty()) {
            Toast.makeText(this, "Invalid Booking ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve fields from "acceptbookings" and store them in "finishbookings"
        database.collection("acceptbookings")
                .document(bookingId)
                .get() // Use get() to retrieve the document snapshot
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        // Get all fields from the document
                        Map<String, Object> acceptBookingData = task.getResult().getData();

                        if (acceptBookingData != null) {
                            // Add rating and comments to the data
                            acceptBookingData.put("rating", userRating);
                            acceptBookingData.put("comments", userComments);

                            // Save to "finishbookings"
                            database.collection("finishbookings")
                                    .document(bookingId)
                                    .set(acceptBookingData) // Save the entire map
                                    .addOnCompleteListener(saveTask -> {
                                        if (saveTask.isSuccessful()) {
                                            // Delete the document from "acceptbookings"
                                            database.collection("acceptbookings")
                                                    .document(bookingId)
                                                    .delete()
                                                    .addOnCompleteListener(deleteTask -> {
                                                        if (deleteTask.isSuccessful()) {
                                                            Toast.makeText(this, "Rating and Comments Submitted!", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(OnBookingRate_Owner.this, HomeActivity.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(intent); // Close activity
                                                        } else {
                                                            Toast.makeText(this, "Failed to delete 'acceptbookings' entry.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(this, "Error deleting 'acceptbookings': " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    });
                                        } else {
                                            Toast.makeText(this, "Failed to submit rating.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Error saving to 'finishbookings': " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(this, "Booking not found in 'acceptbookings'.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}