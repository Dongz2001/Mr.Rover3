package com.example.mrrover;

import static android.app.PendingIntent.getActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.Locale;

public class Booking extends AppCompatActivity {

    DatePickerDialog datePickerDialog;
    Button button;
    Button timeButton;
    Button confirm;
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    EditText from;
    EditText to;
    EditText model123;
    int hour, minute;
    RadioButton dropoff;
    RadioButton roundtrip;
    RadioGroup radioGroup;

    private String selectedVehicleType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking);


        initDatePicker();
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        confirm = findViewById(R.id.location123);
        timeButton = findViewById(R.id.time_btn);
        button = findViewById(R.id.date);
        model123 = findViewById(R.id.model);

        button.setText(getTodaysDate());

        from = findViewById(R.id.fromBOOKING);
        to = findViewById(R.id.toBOOKING);

        dropoff = findViewById(R.id.radioButton1);
        roundtrip = findViewById(R.id.radioButton2);



        from.setFocusable(false);
        to.setFocusable(false);


        Intent intent = getIntent();
        String fromLocationText = intent.getStringExtra("From");
        String toLocationText = intent.getStringExtra("To");


        from.setText(fromLocationText);
        to.setText(toLocationText);



        from.setOnClickListener(v -> {
            Intent newintent = new Intent(Booking.this , setLocation.class);
            newintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(newintent);
        });

        to.setOnClickListener(v -> {
            Intent newintent = new Intent(Booking.this , setLocation.class);
            newintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(newintent);
        });

        confirm.setOnClickListener((v -> {

            String location = from.getText().toString().trim();
            String destination = to.getText().toString().trim();
            String date = button.getText().toString().trim();
            String time = timeButton.getText().toString().trim();
            String model = model123.getText().toString().trim();

            if(TextUtils.isEmpty(location)){
                from.setError("This field is required");
            }
            if(TextUtils.isEmpty(destination)){
                to.setError("This field is required");
            }
            if(TextUtils.isEmpty(model)){
                to.setError("This field is required");
            }


            RadioGroup serviceTypeGroup = findViewById(R.id.radioGroup);
            int selectedId = serviceTypeGroup.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = findViewById(selectedId);
            String serviceType = selectedRadioButton.getText().toString();

            // Pass the serviceType to the next activity
            Intent intent2 = new Intent(this, List_of_Drivers.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent2.putExtra("FROM", location);
                intent2.putExtra("TO", destination);
                intent2.putExtra("DATE", date);
                intent2.putExtra("TIME", time);
                intent2.putExtra("MODEL", model);
                intent2.putExtra("SERVICE_TYPE", serviceType);
                intent2.putExtra("VEHICLE_TYPE", selectedVehicleType);
            startActivity(intent2);

        }));

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

    public void popTimePicker(View view){

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {

                hour = selectedHour;
                minute = selectedMinute;
                timeButton.setText(String .format(Locale.getDefault(), "%02d:%02d",hour,minute));

            }
        };

        int style = AlertDialog.THEME_HOLO_LIGHT;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,style, onTimeSetListener, hour,minute, true);

        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    /*void navigate() {
        Intent intent = new Intent(this , List_of_Drivers.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }*/

    void resetButtonBackgrounds() {
        button1.setBackgroundResource(R.drawable.kinds);
        button2.setBackgroundResource(R.drawable.kind1);
        button3.setBackgroundResource(R.drawable.kind2);
        button4.setBackgroundResource(R.drawable.kind3);
    }
}