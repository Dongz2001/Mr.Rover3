package com.example.mrrover;

import static android.app.PendingIntent.getActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

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
    int hour, minute;

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
        button.setText(getTodaysDate());

        confirm.setOnClickListener((v -> {

            navigate();
        }));
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButtonBackgrounds();
                v.setBackgroundResource(R.drawable.clicked_kinds);
            }
        };
        View.OnClickListener listener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButtonBackgrounds();
                v.setBackgroundResource(R.drawable.clicked_kind1);
            }
        };
        View.OnClickListener listener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButtonBackgrounds();
                v.setBackgroundResource(R.drawable.clicked_kind2);
            }
        };
        View.OnClickListener listener3 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButtonBackgrounds();
                v.setBackgroundResource(R.drawable.clicked_kind3);
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

    void navigate() {
        Intent intent = new Intent(this , setLocation.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    void resetButtonBackgrounds() {
        button1.setBackgroundResource(R.drawable.kinds);
        button2.setBackgroundResource(R.drawable.kind1);
        button3.setBackgroundResource(R.drawable.kind2);
        button4.setBackgroundResource(R.drawable.kind3);
    }
}