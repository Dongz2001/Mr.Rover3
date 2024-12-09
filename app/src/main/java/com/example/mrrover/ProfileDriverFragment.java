package com.example.mrrover;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mrrover.utils.AndroidUtil;
import com.example.mrrover.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class ProfileDriverFragment extends Fragment {

    Button myAccount;
    Button logout;
    Button payment;
    ImageView profilePic;
    TextView firstName;
    TextView lastName;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    TextView ratingvalue;


    public ProfileDriverFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_driver, container, false);

        myAccount = view.findViewById(R.id.my_account1);
        logout = view.findViewById(R.id.logout);
        profilePic = view.findViewById(R.id.profile_pic);
        firstName = view.findViewById(R.id.firstname_profile);
        lastName = view.findViewById(R.id.lastname_profile);
        payment = view.findViewById(R.id.my_paymentmethod);
        ratingvalue = view.findViewById(R.id.rating_value);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        getUserData();

        payment.setOnClickListener(v -> {
            // Create an intent to start Payment_Method activity
            Intent intent = new Intent(getActivity(), Payment_Method.class);
            // Start the activity
            startActivity(intent);
        });

        myAccount.setOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            ProfileDriverFragment2 profiledriverFragment = new ProfileDriverFragment2();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, profiledriverFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        });
        logout.setOnClickListener(v -> {
            /*Intent intent = new Intent(getActivity(), Logout.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);*/

            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(getActivity(), MR_Login_Driver.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

// Get current user's UID
        String currentUserUID = auth.getCurrentUser().getUid();

// Reference to the "finishbookings" collection
        CollectionReference finishBookingsRef = db.collection("finishbookings");

// Query to get all bookings for the current driver
        finishBookingsRef.whereEqualTo("Driver's UID", currentUserUID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        double totalRatings = 0.0;
                        int count = 0;

                        // Iterate through the documents
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.contains("rating")) {
                                // Get the rating value
                                double rating = document.getDouble("rating");
                                totalRatings += rating;
                                count++;
                            }
                        }

                        // Calculate the average rating
                        if (count > 0) {
                            double averageRating = totalRatings / count;

                            // Update the driver's ratings in Firestore
                            db.collection("drivers").document(currentUserUID)
                                    .update("driverRatings", averageRating)
                                    .addOnSuccessListener(aVoid -> System.out.println("Driver ratings updated successfully."))
                                    .addOnFailureListener(e -> System.err.println("Error updating driver ratings: " + e.getMessage()));
                        } else {
                            System.out.println("No ratings available for this driver.");
                        }
                    } else {
                        // Handle the error
                        System.err.println("Error fetching ratings: " + task.getException().getMessage());
                    }
                });

        return view;
    }
    void getUserData(){
        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Uri uri = task.getResult();
                        AndroidUtil.setProfilePic(getContext(),uri,profilePic);
                    }
                });
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        DocumentReference documentReference = firestore.collection("drivers").document(userID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        // Assuming the fields exist in Firestore
                        String firstname = documentSnapshot.getString("Firstname");
                        String lastname = documentSnapshot.getString("Lastname");
                        Double rating = documentSnapshot.getDouble("driverRatings");

                        // Safely handle null values
                        if (firstname != null) {
                            firstName.setText(firstname);
                        }
                        if (lastname != null) {
                            lastName.setText(lastname);
                        }
                        if (rating != null) {
                            // Format the Double to 1 decimal place
                            String formattedRating = String.format("%.1f", rating);
                            ratingvalue.setText(formattedRating);
                        } else {
                            ratingvalue.setText("0.0");
                        }
                    } else {
                        Log.d("ProfileFragment", "Document does not exist");
                    }
                } else {
                    Log.d("ProfileFragment", "Failed with: ", task.getException());
                }
            }
        });

    }
}