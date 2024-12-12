package com.example.mrrover.component;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class InitializeNotification {

    private static InitializeNotification instance;

    public static InitializeNotification getInstance() {
        if (instance == null) {
            instance = new InitializeNotification();
        }
        return instance;
    }

    public void testLoadNotification(Context context, String currentUserUID) {

        System.out.println("currentUserUID: " + currentUserUID);
        // Create notification channel
        NotificationHelper.createNotificationChannel(context);

        try {
            System.out.println("Test loading notification");
            Log.d("Test loading notification", "Test loading notification");

            FirebaseFirestore database = FirebaseFirestore.getInstance();

            database.collection("notification").addSnapshotListener((value, error) -> {
                if (error != null) {
                    Log.e("Notification", "Error loading notification", error);
                    return;
                }

                for (QueryDocumentSnapshot document : value) {
                    Boolean isNotificationSeen = document.getBoolean("isNotificationSeen");
                    String vechicleUserUid = document.getString("Vehicle Owner's UID");

                    System.out.println("vechicleUserUid: " + vechicleUserUid);

                    if (vechicleUserUid.equals(currentUserUID)) {
                      if (isNotificationSeen != null && !isNotificationSeen) {

                          String id = document.getId();
                          String bookingId = document.getString("BookingId");
                          String driverName = document.getString("DriverName");
                          String userUid = document.getString("userUid");
                          String service = document.getString("Service");
                          String date = document.getString("Date");
                          String time = document.getString("Time");
                          String timeOfCancellation = document.getString("Time of cancellation");
                          String status = document.getString("Status");
                          String description = document.getString("description");

                          // Update the notification as seen
                          database.collection("notification").document(id)
                                  .update("isNotificationSeen", true)
                                  .addOnSuccessListener(aVoid -> {
                                      Log.d("Notification", "Notification updated as seen");

                                      // Display the notification
                                      NotificationHelper.sendNotification(context, "Mr.Rover", "Booking " + bookingId + " has been cancelled by " + driverName + " at " + timeOfCancellation);
                                  })
                                  .addOnFailureListener(e -> {
                                      Log.e("Notification", "Error updating notification as seen", e);
                                  });

                      }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}