package com.example.mrrover;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mrrover.adapter.FinishBookHistoryAdapter;
import com.example.mrrover.adapter.UserFinishBookHistoryAdapter;
import com.example.mrrover.model.DriverHistoryModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Mr_user_History extends AppCompatActivity {

    FirebaseFirestore database = FirebaseFirestore.getInstance();

    RecyclerView bookingView;

    List<DriverHistoryModel> bookingList;

    UserFinishBookHistoryAdapter userFinishBookHistoryAdapter;

    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mr_user_history);

        back = findViewById(R.id.back_arrow1);

        back.setOnClickListener(v -> {
            Intent intent = new Intent(Mr_user_History.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });


        bookingView = findViewById(R.id.recyclerViewHistoryuser);
        bookingView.setLayoutManager(new LinearLayoutManager(this)); // Use 'this' for context in an Activity
        bookingList = new ArrayList<>();
        userFinishBookHistoryAdapter = new UserFinishBookHistoryAdapter(bookingList);
        bookingView.setAdapter(userFinishBookHistoryAdapter);
        //finishBookHistoryAdapter.setOnBookListener(this);
        LoadDriver();
    }

    void LoadDriver(){

        String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database.collection("finishbookings").whereEqualTo("Vehicle Owner's UID", currentUserUID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e!=null){
                    //Toast.makeText(getContext(), "No data found", Toast.LENGTH_SHORT).show();
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
                userFinishBookHistoryAdapter.notifyDataSetChanged();
            }

        });
    }
}