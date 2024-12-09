package com.example.mrrover.utils;

import android.widget.EditText;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;

public class FirebaseUtil {


    public static void checkUserRole(FirebaseFirestore db, OnUserRoleCheckedListener listener) {
        String userId = currentUserId();

        if (userId == null) {
            listener.onUserRoleChecked("none");
            return;
        }

        // Check if user is in "users" collection
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        listener.onUserRoleChecked("users");
                    } else {
                        // Check if user is in "drivers" collection
                        db.collection("drivers").document(userId).get()
                                .addOnCompleteListener(driverTask -> {
                                    if (driverTask.isSuccessful() && driverTask.getResult() != null && driverTask.getResult().exists()) {
                                        listener.onUserRoleChecked("drivers");
                                    } else {
                                        listener.onUserRoleChecked("none");
                                    }
                                });
                    }
                });
    }

    public interface OnUserRoleCheckedListener {
        void onUserRoleChecked(String role);
    }

    public static String currentUserId() {
        // Return current Firebase authenticated user ID
        return FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
    }




    // Reference to current user details in Firebase Realtime Database
    public static DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();

    }


    public static StorageReference getCurrentProfilePicStorageRef(){

        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(FirebaseUtil.currentUserId());
    }
    public static StorageReference getLicenseID(){

        return FirebaseStorage.getInstance().getReference().child("Registration IDs" );
    }

    public static StorageReference getOtherProfilePicStorageRef(String otherUserId){

        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(otherUserId);
    }



    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("drivers");
    }





    public static DocumentReference getChatroomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return getChatroomReference(chatroomId).collection("chats");
    }





    public static String getChatroomId(String userId1, String userId2) {
        if (userId1 == null || userId2 == null) {
            throw new IllegalArgumentException("User IDs must not be null");
        }
        if (userId1.hashCode() < userId2.hashCode()) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

    public static CollectionReference allChatroomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }



    public static DocumentReference getOtherUserFromChatroom(List<String> userIds){
        if(userIds.get(0).equals(FirebaseUtil.currentUserId())){
            return allUserCollectionReference().document(userIds.get(1));
        }else{
            return allUserCollectionReference().document(userIds.get(0));
        }
    }





    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }

}
