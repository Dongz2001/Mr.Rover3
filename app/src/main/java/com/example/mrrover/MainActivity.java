package com.example.mrrover;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mrrover.model.DriverHistoryModel;
import com.example.mrrover.model.UserModel;
import com.example.mrrover.utils.AndroidUtil;
import com.example.mrrover.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String userId = FirebaseUtil.currentUserId();
                    if (userId == null) {
                        // If no user is logged in, navigate to registration screen
                        startActivity(new Intent(MainActivity.this, RegisterAs.class));
                        finish();
                        return;
                    }

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    // Check if the user is in the "users" collection
                    db.collection("users").document(userId).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                                        // If the user is in the "users" collection
                                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                    } else {
                                        // Check if the user is in the "drivers" collection
                                        db.collection("drivers").document(userId).get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                                                            // If the user is in the "drivers" collection
                                                            startActivity(new Intent(MainActivity.this, home_Driver.class));
                                                        } else {
                                                            // If the user is not in either collection, navigate to the registration screen
                                                            startActivity(new Intent(MainActivity.this, RegisterAs.class));
                                                        }
                                                        finish();
                                                    }
                                                });
                                    }
                                }
                            });
                }
            }, 3000); // Delay for 3 seconds



    }

}