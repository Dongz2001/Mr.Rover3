package com.example.mrrover;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.mrrover.adapter.ChatRecyclerAdapter;
import com.example.mrrover.adapter.ChatRecyclerAdapter;
import com.example.mrrover.model.ChatMessageModel;
import com.example.mrrover.model.ChatroomModel;
import com.example.mrrover.model.UserModel;
import com.example.mrrover.utils.AndroidUtil;
import com.example.mrrover.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;
import java.util.function.Consumer;

public class ChatActivity extends AppCompatActivity {

    UserModel otherUser;
    String chatroomId;
    ChatroomModel chatroomModel;

    ChatRecyclerAdapter adapter;

    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    TextView driverNameTextView;

    RecyclerView recyclerView;

    ImageView profileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        String bookingId = getIntent().getStringExtra("bookingId");

        if (bookingId == null || bookingId.isEmpty()) {
            Log.e("ChatActivity", "bookingId is missing!");
            finish();
            return;
        }


        driverNameTextView = findViewById(R.id.otherUsername);
        messageInput = findViewById(R.id.chatMessageInput);
        sendMessageBtn = findViewById(R.id.msgsendbtn);
        backBtn = findViewById(R.id.backbtn);
        recyclerView = findViewById(R.id.chatrecyclerview);
        profileImageView = findViewById(R.id.profile_pic_image_view);


        backBtn.setOnClickListener(v -> {
            finish();
        });


        sendMessageBtn.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (message.isEmpty()) {
                Toast.makeText(this, "Message cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            sendMessageToUser(message);
        });
        fetchChatDetails(bookingId, this::setupChatRecyclerView);
        //setupChatRecyclerView();
    }

    void setupChatRecyclerView() {
        if (FirebaseUtil.currentUserId() == null) {
            Log.e("ChatActivity", "Current user ID is null. Cannot fetch chatroomId.");
            return;
        }


        FirebaseFirestore.getInstance().collection("chatrooms")
                .whereArrayContains("userIds", FirebaseUtil.currentUserId())
                .whereEqualTo("chatroomId", chatroomId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        for (DocumentSnapshot document : task.getResult()) {

                            chatroomId = document.getId();

                            if (chatroomId != null && !chatroomId.isEmpty()) {
                                Log.d("ChatActivity", "Chatroom ID fetched: " + chatroomId);


                                Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                                        .orderBy("timestamp", Query.Direction.DESCENDING);

                                FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                                        .setQuery(query, ChatMessageModel.class)
                                        .build();


                                adapter = new ChatRecyclerAdapter(options, getApplicationContext());
                                LinearLayoutManager manager = new LinearLayoutManager(this);
                                manager.setReverseLayout(true);
                                recyclerView.setLayoutManager(manager);
                                recyclerView.setAdapter(adapter);
                                adapter.startListening();

                                /*adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                                    @Override
                                    public void onItemRangeInserted(int positionStart, int itemCount) {
                                        super.onItemRangeInserted(positionStart, itemCount);
                                        //recyclerView.smoothScrollToPosition(0);
                                    }
                                });*/

                                break;
                            } else {
                                Log.e("ChatActivity", "ChatroomId is null or empty for document: " + document.getId());
                            }
                        }
                    } else {
                        Log.e("ChatActivity", "Failed to find chatrooms for the user", task.getException());
                    }
                });
    }


    private void fetchChatDetails(String bookingId, Runnable callback) {
        FirebaseFirestore.getInstance().collection("acceptbookings")
                .document(bookingId)
                .get()
                .addOnFailureListener(err -> {
                    System.out.println();
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot bookingDoc = task.getResult();

                        // Extract Vehicle Owner's UID and Driver's UID
                        String vehicleOwnerUid = bookingDoc.getString("Vehicle Owner's UID");
                        String driverUid = bookingDoc.getString("Driver's UID");
                        String driverName = bookingDoc.getString("Driver's Name");

                        if (vehicleOwnerUid == null || driverUid == null) {
                            Log.e("ChatActivity", "VehicleOwnerUID or DriverUID is missing!");
                            return;
                        }

                        // Update Driver's Name in the UI
                        if (driverName != null) {
                            driverNameTextView.setText(driverName);
                        } else {
                            driverNameTextView.setText("Unknown Driver");
                        }

                        // Set otherUser as the Driver's UserModel
                        otherUser = new UserModel();
                        otherUser.setUserId(driverUid);

                        // Generate chatroomId using Vehicle Owner's UID and Driver's UID
                        chatroomId = FirebaseUtil.getChatroomId(vehicleOwnerUid, driverUid);

                        // Proceed to fetch or create the chatroom
                        getOrCreateChatroomModel();

                        callback.run();

                    } else {
                        Log.e("ChatActivity", "Failed to fetch booking data", task.getException());
                    }
                });
    }

    private void getOrCreateChatroomModel() {
        if (chatroomId == null || chatroomId.isEmpty() || otherUser == null) {
            Log.e("ChatActivity", "chatroomId or otherUser is not initialized!");
            return;
        }

        FirebaseUtil.getChatroomReference(chatroomId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        chatroomModel = task.getResult().toObject(ChatroomModel.class);
                        if (chatroomModel == null) {
                            chatroomModel = new ChatroomModel(
                                    chatroomId,
                                    Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getUserId()),
                                    Timestamp.now(),
                                    "" // Add any default data if needed
                            );
                            FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel)
                                    .addOnSuccessListener(aVoid -> Log.d("ChatActivity", "Chatroom created successfully"))
                                    .addOnFailureListener(e -> Log.e("ChatActivity", "Failed to create chatroom", e));
                        }
                    } else {
                        Log.e("ChatActivity", "Failed to fetch chatroom", task.getException());
                    }
                });
    }

    void sendMessageToUser(String message) {
        if (chatroomModel == null) {
            Log.e("ChatActivity", "ChatroomModel is not initialized. Cannot send message.");
            return;
        }


        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatroomModel.setLastMessage(message);


        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel)
                .addOnFailureListener(e -> Log.e("ChatActivity", "Failed to update chatroom", e));


        ChatMessageModel chatMessageModel = new ChatMessageModel(
                message,
                FirebaseUtil.currentUserId(),
                Timestamp.now()
        );


        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        messageInput.setText(""); // Clear the input field on success
                        Log.d("ChatActivity", "Message sent successfully");
                    } else {
                        Log.e("ChatActivity", "Failed to send message", task.getException());
                    }
                });
    }

    void loadChatRoomData(){



    }
}