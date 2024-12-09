package com.example.mrrover;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


import com.example.mrrover.adapter.RecentChatRecyclerAdapter;
import com.example.mrrover.model.ChatroomModel;
import com.example.mrrover.model.UserModel;
import com.example.mrrover.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class MessageFragment extends Fragment {

    RecyclerView recyclerView;
    RecentChatRecyclerAdapter adapter;

    ImageButton searchButton;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        searchButton = view.findViewById(R.id.mainSearchBtn);

        searchButton.setOnClickListener((v) -> {
            Intent intent = new Intent(getActivity(), search_user.class);
            startActivity(intent);
        });

        setupRecyclerView();

        return view;
    }

    void setupRecyclerView() {

        Query query = FirebaseUtil.allChatroomCollectionReference()
                .whereArrayContains("userIds",FirebaseUtil.currentUserId())
                .orderBy("lastMessageTimestamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatroomModel> options = new FirestoreRecyclerOptions.Builder<ChatroomModel>()
                .setQuery(query, ChatroomModel.class)
                .build();

        adapter = new RecentChatRecyclerAdapter(options, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStart(){
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }

    @Override
    public void onStop(){
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }
}