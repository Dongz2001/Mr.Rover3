package com.example.mrrover;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.mrrover.model.UserModel;
import com.example.mrrover.utils.AndroidUtil;
import com.example.mrrover.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class ProfileDriverFragment2 extends Fragment {

    ImageView profilePic;
    EditText firstName;
    EditText lastName;
    EditText phone;
    EditText email;
    Button updateProfile;

    FirebaseAuth auth;

    UserModel currentUserModel;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;


    public ProfileDriverFragment2() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data!=null && data.getData()!=null){
                            selectedImageUri = data.getData();
                            AndroidUtil.setProfilePic(getContext(),selectedImageUri,profilePic);
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_driver2, container, false);

        profilePic = view.findViewById(R.id.profile_image);
        firstName = view.findViewById(R.id.firstname);
        lastName = view.findViewById(R.id.lastname);
        email = view.findViewById(R.id.email);
        phone = view.findViewById(R.id.phone);
        updateProfile = view.findViewById(R.id.updateBTN);

        firstName.setFocusable(false);
        lastName.setFocusable(false);
        email.setFocusable(false);
        phone.setFocusable(false);

        auth = FirebaseAuth.getInstance();

        getUserData();

        updateProfile.setOnClickListener((v -> {
            updateBtnClick();
        }));
        profilePic.setOnClickListener((v) ->{
            //LUNCHING THE IMAGE PICKER
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        });

        return view;
    }
    void updateBtnClick() {
        String newFirstname = firstName.getText().toString();
        String newLastname = lastName.getText().toString();

        // Validate that the fields are not empty
        if (newFirstname.isEmpty()) {
            firstName.setError("This field is required");
            return;
        }

        if (newLastname.isEmpty()) {
            lastName.setError("This field is required");
            return;
        }

        // If a profile picture is selected, upload it first and then update Firestore
        if (selectedImageUri != null) {
            FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            updateToFirestore(newFirstname, newLastname);
                        } else {
                            AndroidUtil.showToast(getContext(), "Image Upload Failed");
                        }
                    });
        } else {
            updateToFirestore(newFirstname, newLastname);
        }
    }
    void updateToFirestore(String firstname, String lastname) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        String userID = firebaseAuth.getCurrentUser().getUid(); // Get current user's UID

        // Create a map for the fields to update
        Map<String, Object> updates = new HashMap<>();
        updates.put("Firstname", firstname);
        updates.put("Lastname", lastname);

        // Update only firstname and lastname in Firestore for the logged-in user
        firestore.collection("drivers").document(userID)
                .update(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        AndroidUtil.showToast(getContext(), "Profile Updated Successfully");
                        navigateToProfileFragment();
                    } else {
                        AndroidUtil.showToast(getContext(), "Update Failed: " + task.getException().getMessage());
                    }
                });
    }
    void navigateToProfileFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        ProfileFragment profileFragment = new ProfileFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, profileFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();;
    }
    void getUserData() {

        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Uri uri = task.getResult();
                        AndroidUtil.setProfilePic(getContext(),uri,profilePic);
                    }
                });
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        DocumentReference documentReference = firestore.collection("drivers").document(userID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        // Assuming the fields exist in Firestore
                        String firstname = documentSnapshot.getString("Firstname");
                        String lastname = documentSnapshot.getString("Lastname");
                        String emailText = documentSnapshot.getString("Email");
                        String phoneText = documentSnapshot.getString("Phone");

                        // Set the values in your EditText fields
                        firstName.setText(firstname);
                        lastName.setText(lastname);
                        email.setText(emailText);
                        phone.setText(phoneText);
                    } else {
                        Log.d("ProfileFragment2", "Document does not exist");
                    }
                } else {
                    Log.d("ProfileFragment2", "Failed with: ", task.getException());
                }
            }
        });
    }
}