package com.example.mrrover;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mrrover.model.UserModel;
import com.example.mrrover.utils.AndroidUtil;
import com.example.mrrover.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;


public class ProfileFragment extends Fragment {

    Button myAccount;
    Button logout;
    ImageView profilePic;
    TextView firstName;
    TextView lastName;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser currentUser;

    UserModel currentUserModel;


    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        myAccount = view.findViewById(R.id.my_account);
        logout = view.findViewById(R.id.logout);
        profilePic = view.findViewById(R.id.profile_pic);
        firstName = view.findViewById(R.id.firstname_profile);
        lastName = view.findViewById(R.id.lastname_profile);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        getUserData();


        myAccount.setOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            ProfileFragment2 profileFragment = new ProfileFragment2();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, profileFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();;

        });

        //LOG-OUT BUTTON
        logout.setOnClickListener(v -> {



                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), MR_Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

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

        DocumentReference documentReference = firestore.collection("users").document(userID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        // Assuming the fields exist in Firestore
                        String firstname = documentSnapshot.getString("Firstname");
                        String lastname = documentSnapshot.getString("Lastname");

                        // Set the values in your EditText fields
                        firstName.setText(firstname);
                        lastName.setText(lastname);
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