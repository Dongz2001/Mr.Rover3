package com.example.mrrover;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mrrover.utils.AndroidUtil;
import com.example.mrrover.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MR_Register_Driver extends AppCompatActivity {

    DatePickerDialog datePickerDialog;
    EditText fname, lname, ADDress,mobileNumber, pword;
    EditText EMAIL;
    Button birthDATE;
    Spinner GENDER;
    ImageView uploadFront, uploadBack, validID, birthC;
    TextView loginHERE;
    Button registerBTN;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String userID;

    Button button1;
    Button button2;
    Button button3;
    Button button4;

    ActivityResultLauncher<Intent> imagePickLauncher1;
    ActivityResultLauncher<Intent> imagePickLauncher2;
    ActivityResultLauncher<Intent> imagePickLauncher3;
    ActivityResultLauncher<Intent> imagePickLauncher4;
    Uri LicenseFront;
    Uri LicenseBack;
    Uri valid_ID;
    Uri birthCerth;

    private String selectedVehicleType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mr_register_driver);

        initDatePicker();
        fname = findViewById(R.id.firstname);
        lname = findViewById(R.id.lastname);
        EMAIL = findViewById(R.id.driver_email1);
        pword = findViewById(R.id.password1);
        ADDress = findViewById(R.id.address);
        //mobileNumber = findViewById(R.id.mobilenumber);
        birthDATE = findViewById(R.id.date);
        GENDER = findViewById(R.id.gender);
        uploadFront = findViewById(R.id.drivers_licenseFront);
        uploadBack = findViewById(R.id.drivers_licenseBack);
        validID = findViewById(R.id.valid_ID_driver);
        birthC = findViewById(R.id.birth_Certh);
        registerBTN = findViewById(R.id.registerBTN1);
        loginHERE = findViewById(R.id.login_here);

        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButtonBackgrounds();
                v.setBackgroundResource(R.drawable.clicked_kinds);
                selectedVehicleType = "Motorcycle";
            }
        };
        View.OnClickListener listener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButtonBackgrounds();
                v.setBackgroundResource(R.drawable.clicked_kind1);
                selectedVehicleType = "Car";
            }
        };
        View.OnClickListener listener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButtonBackgrounds();
                v.setBackgroundResource(R.drawable.clicked_kind2);
                selectedVehicleType = "Van";
            }
        };
        View.OnClickListener listener3 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButtonBackgrounds();
                v.setBackgroundResource(R.drawable.clicked_kind3);
                selectedVehicleType = "Truck";
            }
        };
        button1.setOnClickListener(listener);
        button2.setOnClickListener(listener1);
        button3.setOnClickListener(listener2);
        button4.setOnClickListener(listener3);

        loginHERE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MR_Register_Driver.this, MR_Login_Driver.class);
                startActivity(intent2);
            }
        });

        imagePickLauncher1 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data!=null && data.getData()!=null){
                            LicenseFront = data.getData();
                        }
                    }
                }
        );
        imagePickLauncher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data!=null && data.getData()!=null){
                            LicenseBack = data.getData();
                        }
                    }
                }
        );
        imagePickLauncher3 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data!=null && data.getData()!=null){
                            valid_ID = data.getData();
                        }
                    }
                }
        );
        imagePickLauncher4 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data!=null && data.getData()!=null){
                            birthCerth = data.getData();
                        }
                    }
                }
        );



        uploadFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(MR_Register_Driver.this).cropSquare().compress(512).maxResultSize(512, 512)
                        .createIntent(new Function1<Intent, Unit>() {
                            @Override
                            public Unit invoke(Intent intent) {
                                imagePickLauncher1.launch(intent);
                                return null;
                            }
                        });
            }
        });

        uploadBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(MR_Register_Driver.this).cropSquare().compress(512).maxResultSize(512, 512)
                        .createIntent(new Function1<Intent, Unit>() {
                            @Override
                            public Unit invoke(Intent intent) {
                                imagePickLauncher2.launch(intent);
                                return null;
                            }
                        });
            }
        });

        validID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(MR_Register_Driver.this).cropSquare().compress(512).maxResultSize(512, 512)
                        .createIntent(new Function1<Intent, Unit>() {
                            @Override
                            public Unit invoke(Intent intent) {
                                imagePickLauncher3.launch(intent);
                                return null;
                            }
                        });
            }
        });

        birthC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(MR_Register_Driver.this).cropSquare().compress(512).maxResultSize(512, 512)
                        .createIntent(new Function1<Intent, Unit>() {
                            @Override
                            public Unit invoke(Intent intent) {
                                imagePickLauncher4.launch(intent);
                                return null;
                            }
                        });
            }
        });

        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstname = fname.getText().toString().trim();
                String lastname = lname.getText().toString().trim();
                String email = EMAIL.getText().toString().trim();
                String password = pword.getText().toString().trim();
                String address = ADDress.getText().toString().trim();
                //String phone = mobileNumber.getText().toString().trim();
                String birthdate = birthDATE.getText().toString().trim();
                String gender = GENDER.getSelectedItem().toString().trim();

                // Validation logic...

                if (TextUtils.isEmpty(firstname)) {
                    fname.setError("This field is required");
                }
                if (TextUtils.isEmpty(lastname)) {
                    lname.setError("This field is required");
                }
                if (TextUtils.isEmpty(address)) {
                    ADDress.setError("This field is required");
                }
                if (TextUtils.isEmpty(birthdate)) {
                    birthDATE.setError("This field is required");
                }
                if (TextUtils.isEmpty(email)) {
                    EMAIL.setError("This field is required");
                } else if (!email.contains("@")) {
                    EMAIL.setError("Please enter a valid email address");
                }
                if (TextUtils.isEmpty(gender) || gender.equals("Item 1")) {
                    Toast.makeText(getApplicationContext(), "Please select a gender", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(password)) {
                    pword.setError("This field is required");
                }

                if (LicenseFront != null && LicenseBack != null && valid_ID != null && birthCerth != null) {
                    // Upload License Front
                    StorageReference frontRef = FirebaseUtil.getLicenseID().child("LicenseFront_" + System.currentTimeMillis() + ".jpg");
                    frontRef.putFile(LicenseFront).addOnCompleteListener(frontTask -> {
                        if (frontTask.isSuccessful()) {
                            frontRef.getDownloadUrl().addOnSuccessListener(frontUri -> {
                                String frontUrl = frontUri.toString();

                                // Upload License Back
                                StorageReference backRef = FirebaseUtil.getLicenseID().child("LicenseBack_" + System.currentTimeMillis() + ".jpg");
                                backRef.putFile(LicenseBack).addOnCompleteListener(backTask -> {
                                    if (backTask.isSuccessful()) {
                                        backRef.getDownloadUrl().addOnSuccessListener(backUri -> {
                                            String backUrl = backUri.toString();

                                            // Upload Valid ID
                                            StorageReference idRef = FirebaseUtil.getLicenseID().child("ValidID_" + System.currentTimeMillis() + ".jpg");
                                            idRef.putFile(valid_ID).addOnCompleteListener(idTask -> {
                                                if (idTask.isSuccessful()) {
                                                    idRef.getDownloadUrl().addOnSuccessListener(idUri -> {
                                                        String idUrl = idUri.toString();

                                                        // Upload Birth Certificate
                                                        StorageReference birthRef = FirebaseUtil.getLicenseID().child("BirthCertificate_" + System.currentTimeMillis() + ".jpg");
                                                        birthRef.putFile(birthCerth).addOnCompleteListener(birthTask -> {
                                                            if (birthTask.isSuccessful()) {
                                                                birthRef.getDownloadUrl().addOnSuccessListener(birthUri -> {
                                                                    String birthUrl = birthUri.toString();

                                                                    // Now proceed to save all user data including image URLs
                                                                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                                            if (task.isSuccessful()) {
                                                                                Toast.makeText(MR_Register_Driver.this, "Successfully Created", Toast.LENGTH_SHORT).show();
                                                                                userID = firebaseAuth.getCurrentUser().getUid();
                                                                                DocumentReference documentReference = firestore.collection("drivers").document(userID);

                                                                                Map<String, Object> user = new HashMap<>();
                                                                                user.put("Firstname", firstname);
                                                                                user.put("Lastname", lastname);
                                                                                user.put("Email", email);
                                                                                user.put("Password", password);
                                                                                user.put("Birthdate", birthdate);
                                                                                user.put("Address", address);
                                                                                user.put("Gender", gender);
                                                                                user.put("License Front", frontUrl);
                                                                                user.put("License Back", backUrl);
                                                                                user.put("Valid ID", idUrl);

                                                                                user.put("Birth Certificate", birthUrl);
                                                                                user.put("Preffered Type", selectedVehicleType);

                                                                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {
                                                                                        Log.d("TAG", "onSuccess: user profile is created for " + userID);
                                                                                    }
                                                                                });
                                                                                startActivity(new Intent(getApplicationContext(), MR_Login_Driver.class));
                                                                            } else {
                                                                                Toast.makeText(MR_Register_Driver.this, "Registration Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                                }).addOnFailureListener(e -> {
                                                                    AndroidUtil.showToast(MR_Register_Driver.this, "Failed to get Birth Certificate URL");
                                                                });
                                                            } else {
                                                                AndroidUtil.showToast(MR_Register_Driver.this, "Birth Certificate Upload Failed");
                                                            }
                                                        });
                                                    }).addOnFailureListener(e -> {
                                                        AndroidUtil.showToast(MR_Register_Driver.this, "Failed to get Valid ID URL");
                                                    });
                                                } else {
                                                    AndroidUtil.showToast(MR_Register_Driver.this, "Valid ID Upload Failed");
                                                }
                                            });
                                        }).addOnFailureListener(e -> {
                                            AndroidUtil.showToast(MR_Register_Driver.this, "Failed to get License Back URL");
                                        });
                                    } else {
                                        AndroidUtil.showToast(MR_Register_Driver.this, "License Back Upload Failed");
                                    }
                                });
                            }).addOnFailureListener(e -> {
                                AndroidUtil.showToast(MR_Register_Driver.this, "Failed to get License Front URL");
                            });
                        } else {
                            AndroidUtil.showToast(MR_Register_Driver.this, "License Front Upload Failed");
                        }
                    });
                } else {
                    Toast.makeText(MR_Register_Driver.this, "Please select all required images", Toast.LENGTH_SHORT).show();
                }
            }
        });


// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);

// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Apply the adapter to the spinner
        GENDER.setAdapter(adapter);
    }

    private String getTodaysDate() {

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker() {

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = makeDateString(dayOfMonth, month, year);

                birthDATE.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this,style, dateSetListener, year,month,day);

    }

    private String makeDateString(int dayOfMonth, int month, int year) {

        return getMonthFormat(month) + " " + (dayOfMonth) + " " + (year);
    }

    private String getMonthFormat(int month) {

        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        return "JAN";

    }

    public void openDatePicker(View view){

        datePickerDialog.show();
    }
    void resetButtonBackgrounds() {
        button1.setBackgroundResource(R.drawable.kinds);
        button2.setBackgroundResource(R.drawable.kind1);
        button3.setBackgroundResource(R.drawable.kind2);
        button4.setBackgroundResource(R.drawable.kind3);
    }

    /*if (selectedImageUri != null) {
                    FirebaseUtil.getLicenseID().putFile(selectedImageUri)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    //updateToFirestore(newFirstname, newLastname);
                                } else {
                                    AndroidUtil.showToast(MR_Register_Driver.this, "Image Upload Failed");
                                }
                            });
                }*/
}