package com.example.mrrover;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.mrrover.adapter.SearchUserRecyclerAdapter;
import com.example.mrrover.model.DriverHistoryModel;
import com.example.mrrover.model.UserModel;
import com.example.mrrover.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class search_user extends AppCompatActivity {

    EditText searchInput;
    ImageButton searchButton;
    ImageButton backButton;
    RecyclerView recyclerView;

    SearchUserRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_user);

        searchInput = findViewById(R.id.searchUsernameinput);
        searchButton = findViewById(R.id.searchuserbtn);
        backButton = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.searchUserRecyclerView);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        searchInput.requestFocus();

        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        searchButton.setOnClickListener(v -> {

           String searchTerm = searchInput.getText().toString();
            if(searchTerm.isEmpty() || searchTerm.length()<3){

                searchInput.setError("Invalid Username");
                return;
            }
            setupSearchRecyclerView(searchTerm);


        });
    }
    void setupSearchRecyclerView(String searchTerm) {

        Query query = FirebaseUtil.allUserCollectionReference()
                .whereGreaterThanOrEqualTo("Firstname", searchTerm);

        FirestoreRecyclerOptions<DriverHistoryModel> options = new FirestoreRecyclerOptions.Builder<DriverHistoryModel>()
                .setQuery(query, DriverHistoryModel.class)
                .build();

        adapter = new SearchUserRecyclerAdapter(options, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


   @Override
    protected void onStart(){
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(adapter!=null)
            adapter.startListening();
    }
}