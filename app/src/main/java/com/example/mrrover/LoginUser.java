package com.example.mrrover;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mrrover.model.UserModel;
import com.example.mrrover.utils.AndroidUtil;
import com.example.mrrover.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginUser extends AppCompatActivity {

    EditText firstName;
    EditText lastName;
    Button confirmBtn;
    String phoneNumber;
    UserModel userModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_user);


        /*firstName = findViewById(R.id.login_firstname);
        lastName = findViewById(R.id.login_lastname);
        confirmBtn = findViewById(R.id.login_confirm_btn);

        phoneNumber = getIntent().getExtras().getString("phone");

        firstName.setText("");
        lastName.setText("");
        getFirstnameAndLastname();

        confirmBtn.setOnClickListener((v -> {

            setFirstnameAndLastname();
        }));


    }
//STARTTTTTTTTTTTT

    void setFirstnameAndLastname(){

        String firstname = firstName.getText().toString();
        String lastname = lastName.getText().toString();
        if(firstname.isEmpty() && lastname.isEmpty()){
            firstName.setError("Fill this field");
            lastName.setError("Fill this field");
            return;
        }

        if(userModel!=null){
            userModel.setFirstname(firstname);
            userModel.setLastname(lastname);
        }else{
            userModel = new UserModel(phoneNumber,firstname,lastname, Timestamp.now(),FirebaseUtil.currentUserId());
        }

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    Intent intent = new Intent(LoginUser.this,HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else{
                    AndroidUtil.showToast(getApplicationContext(), "You Exist");

                }
            }
        });

    }


    void getFirstnameAndLastname(){

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    userModel = task.getResult().toObject(UserModel.class);
                    if(userModel !=null){
                        firstName.setText(userModel.getFirstname());
                        lastName.setText(userModel.getLastname());
                    }

                }

            }
        });*/

    }
}