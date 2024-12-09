package com.example.mrrover;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentSnapshot;


public class ProfileFragment extends Fragment {

    Button myAccount;
    Button logout;
    ImageView profilePic;
    TextView firstName;
    TextView lastName;

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


        getUserData();


        myAccount.setOnClickListener(v -> {

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            ProfileFragment2 profileFragment = new ProfileFragment2();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, profileFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();;

        });

        logout.setOnClickListener((v) -> {

            FirebaseUtil.logout();
            Intent intent = new Intent(getContext(),MainActivity.class);
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

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {

            currentUserModel = task.getResult().toObject(UserModel.class);
            firstName.setText(currentUserModel.getFirstname());
            lastName.setText(currentUserModel.getLastname());

        });

    }

}