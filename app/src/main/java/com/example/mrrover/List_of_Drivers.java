package com.example.mrrover;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mrrover.adapter.DriverAdapter;
import com.example.mrrover.model.DriverModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class List_of_Drivers extends AppCompatActivity implements DriverAdapter.onBookListener {

    Button allBtn;
    Button nearbyBtn;
    Button starBtn;
    Button maleBtn;
    Button femaleBtn;
    Button choose;

    FirebaseFirestore database = FirebaseFirestore.getInstance();

    RecyclerView driverView;

    List<DriverModel> driverList;

    DriverAdapter driverAdapter;

    String location;
    String destination;
    String date;
    String time;
    String model;
    String serviceType;
    String vehicleType;
    Double rating;

    private Double userLon;
    private Double userlat;

    List<FilterCategories> filterCategories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_of_drivers);



        allBtn = findViewById(R.id.button1);
        nearbyBtn = findViewById(R.id.button2);
        starBtn = findViewById(R.id.button3);
        maleBtn = findViewById(R.id.button4);
        femaleBtn = findViewById(R.id.button5);

        Intent intent = getIntent();
        location = intent.getStringExtra("FROM");
        destination = intent.getStringExtra("TO");
        date = intent.getStringExtra("DATE");
        time = intent.getStringExtra("TIME");
        model = intent.getStringExtra("MODEL");
        serviceType = intent.getStringExtra("SERVICE_TYPE");
        vehicleType = intent.getStringExtra("VEHICLE_TYPE");




        View.OnClickListener onClick_All = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterCategories = new ArrayList<>();
                updatebuttonbackgrounds();
                v.setBackgroundResource(R.drawable.clickedradius);
                LoadDriver();

            }
        };
        View.OnClickListener onClick_Neaby = new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (filterCategories.contains(FilterCategories.NEARBY)){
                    filterCategories.remove(FilterCategories.NEARBY);
                } else {
                    filterCategories.add(FilterCategories.NEARBY);
                }
                updatebuttonbackgrounds();
                LoadDriver();

            }
        };
        View.OnClickListener onClick_Star = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filterCategories.contains(FilterCategories.STAR)){
                    filterCategories.remove(FilterCategories.STAR);
                } else {
                    filterCategories.add(FilterCategories.STAR);
                }
                updatebuttonbackgrounds();
                LoadDriver();
            }
        };
        View.OnClickListener onClick_Male = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filterCategories.contains(FilterCategories.MALE)){
                    filterCategories.remove(FilterCategories.MALE);
                } else {
                    filterCategories.add(FilterCategories.MALE);
                }

                if (filterCategories.contains(FilterCategories.FEMALE)){
                    filterCategories.remove(FilterCategories.FEMALE);
                }
                updatebuttonbackgrounds();
                LoadDriver();
            }
        };
        View.OnClickListener onClick_Female = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filterCategories.contains(FilterCategories.FEMALE)){
                    filterCategories.remove(FilterCategories.FEMALE);
                } else {
                    filterCategories.add(FilterCategories.FEMALE);
                }

                if (filterCategories.contains(FilterCategories.MALE)){
                    filterCategories.remove(FilterCategories.MALE);
                }
                updatebuttonbackgrounds();
                LoadDriver();
            }
        };
        allBtn.setOnClickListener(onClick_All);
        nearbyBtn.setOnClickListener(onClick_Neaby);
        starBtn.setOnClickListener(onClick_Star);
        maleBtn.setOnClickListener(onClick_Male);
        femaleBtn.setOnClickListener(onClick_Female);


        driverView = findViewById(R.id.recycleView);
        driverView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        driverList = new ArrayList<>();
        driverAdapter = new DriverAdapter(driverList);
        driverView.setAdapter(driverAdapter);
        driverAdapter.setOnBookListener(this);

        getUserLoc(this::LoadDriver);
    }

    void updatebuttonbackgrounds() {

        if (!filterCategories.isEmpty()){
            allBtn.setBackgroundResource(R.drawable.radiusbutton);

        }
        if (filterCategories.contains(FilterCategories.NEARBY)){
            nearbyBtn.setBackgroundResource(R.drawable.clickedradius);
        }else {
            nearbyBtn.setBackgroundResource(R.drawable.radiusbutton);

        }

        if  (filterCategories.contains(FilterCategories.STAR)){
            starBtn.setBackgroundResource(R.drawable.clickedradius);

        }else {
            starBtn.setBackgroundResource(R.drawable.radiusbutton);

        }
        if  (filterCategories.contains(FilterCategories.MALE)){
            maleBtn.setBackgroundResource(R.drawable.clickedradius);

        }else {
            maleBtn.setBackgroundResource(R.drawable.radiusbutton);


        }
        if  (filterCategories.contains(FilterCategories.FEMALE)){
            femaleBtn.setBackgroundResource(R.drawable.clickedradius);

        }else {
            femaleBtn.setBackgroundResource(R.drawable.radiusbutton);

        }
    }




    //  This is for the book listener
    @Override
    public void onBook(int position) {
        DriverModel driverdata = driverList.get(position);
        String driverUid = driverdata.getUid();
        String fullname = driverdata.getFullName();
        String gender = driverdata.getGender();
        Intent intent3 = new Intent(List_of_Drivers.this, MR_Reservation.class);
        intent3.putExtra("driverRatings", rating);
        intent3.putExtra("DRIVER'S ID", driverUid);
        intent3.putExtra("FULLNAME", fullname);
        intent3.putExtra("GENDER", gender);
        intent3.putExtra("FROM", location);
        intent3.putExtra("TO", destination);
        intent3.putExtra("DATE", date);
        intent3.putExtra("TIME", time);
        intent3.putExtra("MODEL", model);
        intent3.putExtra("SERVICE_TYPE", serviceType);
        intent3.putExtra("VEHICLE_TYPE", vehicleType);

        startActivity(intent3);
    }

    void LoadDriver() {
        String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        List_of_Drivers bubble = this;

        // Step 1: Retrieve drivers' names booked by the current user
        database.collection("bookings")
                .whereEqualTo("Vehicle Owner's UID", currentUserUID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> bookedDriverIDs = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String bookedDriverID = document.getString("Driver's UID"); // Assume this field exists in bookings
                            if (bookedDriverID != null) {
                                bookedDriverIDs.add(bookedDriverID);
                            }
                        }


                        Query query = database.collection("drivers").whereEqualTo("Preffered Type", vehicleType);
                        if (filterCategories.contains(FilterCategories.MALE)){

                            String gendermale = "Male";
                            query = query.whereEqualTo("Gender", gendermale);

                        }
                        if (filterCategories.contains(FilterCategories.FEMALE)){
                            String genderfemale = "Female";
                            query = query.whereEqualTo("Gender", genderfemale);

                        }
                        if (filterCategories.contains(FilterCategories.STAR)){
                            query = query.orderBy("driverRatings", Query.Direction.DESCENDING);
                        }


                                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                                if (e != null) {
                                    //Toast.makeText(getBaseContext(), "No data found", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                driverList.clear();
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    String uid = document.getId();
                                    String firstname = document.getString("Firstname");
                                    String lastname = document.getString("Lastname");
                                    String gender = document.getString("Gender");
                                    rating = document.getDouble("driverRatings");
                                    Double driverLon = document.getDouble("Current Longitude");
                                    Double driverLat = document.getDouble("Current Latitude");


                                    String fullname = firstname + " " + lastname;



                                    // Only add drivers not booked by the current user
                                    if (!bookedDriverIDs.contains(uid)) {

                                        Double distance = haversine(userLon, userlat, driverLon, driverLat);

                                        DriverModel driver = new DriverModel(uid, fullname, gender,rating, distance);
                                        driverList.add(driver);
                                    }

                                }
                                if (filterCategories.contains(FilterCategories.NEARBY)){

                                    driverList = driverList.stream().sorted((p1,p2)->p1.getDistance().compareTo (p2.getDistance())).collect(Collectors.toList());
                                    driverAdapter = new DriverAdapter(driverList);
                                    driverView.setAdapter(driverAdapter);
                                    driverAdapter.setOnBookListener(bubble);

                                }
                                driverAdapter.notifyDataSetChanged();

                            }
                        });
                    } else {
                        Toast.makeText(getBaseContext(), "Failed to load bookings", Toast.LENGTH_SHORT).show();
                    }


                });
    }

    void getUserLoc (Runnable callback){

        String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database.collection("users")
                .document(currentUserUID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        Object longitude = documentSnapshot.get("Current Longitude");
                        Object latitude = documentSnapshot.get("Current Latitude");

                        if (longitude instanceof Double && latitude instanceof Double){

                            userLon = (Double) longitude;
                            userlat = (Double) latitude;

                        }
                    }

                    callback.run();
                });


    }


        static double haversine(double lat1, double lon1,
                                double lat2, double lon2)
        {
            // distance between latitudes and longitudes
            double dLat = Math.toRadians(lat2 - lat1);
            double dLon = Math.toRadians(lon2 - lon1);

            // convert to radians
            lat1 = Math.toRadians(lat1);
            lat2 = Math.toRadians(lat2);

            // apply formulae
            double a = Math.pow(Math.sin(dLat / 2), 2) +
                    Math.pow(Math.sin(dLon / 2), 2) *
                            Math.cos(lat1) *
                            Math.cos(lat2);
            double rad = 6371;
            double c = 2 * Math.asin(Math.sqrt(a));
            return rad * c;
        }


}

enum FilterCategories {

    NEARBY, STAR, MALE,FEMALE
}