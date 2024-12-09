package com.example.mrrover;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Calendar;

public class Registration extends AppCompatActivity {

    Button button;
    DatePickerDialog datePickerDialog;
    EditText fname, lname, mail, pnumber, signupUsername,signupPassword,signupConfirmpassword;
    Button regbtn;
    TextView textView;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        textView = findViewById(R.id.login_here);
        fname = findViewById(R.id.firstname);
        lname = findViewById(R.id.lastname);
        mail = findViewById(R.id.email);
        pnumber = findViewById(R.id.phonenumber);
        signupUsername = findViewById(R.id.username);
        signupPassword = findViewById(R.id.password);
        signupConfirmpassword = findViewById(R.id.confirmpassword);
        button = findViewById(R.id.date);
        regbtn = findViewById(R.id.registerBTN);
        initDatePicker();

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Registration.this, newLogin.class);
                startActivity(intent);
            }
        });
        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                database = FirebaseDatabase.getInstance();
                reference = database.getReference("Users");

                String firstname = fname.getText().toString();
                String lastname = lname.getText().toString();
                String email = mail.getText().toString();
                String birthdate = button.getText().toString();
                String phonenumber = pnumber.getText().toString();
                String username = signupUsername.getText().toString();
                String password = signupPassword.getText().toString();

                String userId = reference.push().getKey();

                HelperClass helperClass = new HelperClass(firstname, lastname, email, birthdate, username, password, phonenumber);
                reference.child(userId).setValue(helperClass);

                Toast.makeText(Registration.this, "Signup Successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Registration.this, newLogin.class);
                startActivity(intent);
            }
        });

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

                button.setText(date);
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
}