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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileFragment2 extends Fragment {

    ImageView profilePic;
    EditText firstName;
    EditText lastName;
    EditText phone;
    Button updateProfile;

    UserModel currentUserModel;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;

    public ProfileFragment2() {
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
        View view = inflater.inflate(R.layout.fragment_profile2, container, false);

        profilePic = view.findViewById(R.id.profile_image);
        firstName = view.findViewById(R.id.profile_firstname);
        lastName = view.findViewById(R.id.profile_lastname);
        phone = view.findViewById(R.id.profile_phone);
        updateProfile = view.findViewById(R.id.update_btn);


        getUserData();

        updateProfile.setOnClickListener((v -> {
            updateBtnClick();
        }));

        profilePic.setOnClickListener((v) ->{

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

    void updateBtnClick(){

        String newFirstname = firstName.getText().toString();
        String newLastname = lastName.getText().toString();

        if(newFirstname.isEmpty() && newLastname.isEmpty()){
            firstName.setError("Fill this field");
            lastName.setError("Fill this field");
            return;
        }
        currentUserModel.setFirstname(newFirstname);
        currentUserModel.setLastname(newLastname);

        if(selectedImageUri!=null){

            FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                    .addOnCompleteListener(task -> {
                        updateToFirestore();
                    });
        }else{
            updateToFirestore();
        }


        navigateToProfileFragment();


    }

    void updateToFirestore(){

        FirebaseUtil.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        AndroidUtil.showToast(getContext(), "Update Successfully");
                    }else{
                        AndroidUtil.showToast(getContext(), "Update Failed");
                    }
                });


    }

    void getUserData() {

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
            phone.setText(currentUserModel.getPhone());

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
}