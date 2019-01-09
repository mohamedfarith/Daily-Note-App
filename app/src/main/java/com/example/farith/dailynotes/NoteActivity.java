package com.example.farith.dailynotes;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.support.annotation.IntRange;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NoteActivity extends AppCompatActivity{
    EditText noteText;
    String notes;
    String adapterPosition;
    String previousTime;
    Button saveBtn;
    NoteDb finalDatabase;
    SQLiteDatabase database;
    private static final String TAG = NoteActivity.class.getSimpleName();
    Button btnRemindMe;
    String formattedDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        noteText = findViewById(R.id.note_text);
        saveBtn = findViewById(R.id.save_btn);
        btnRemindMe = findViewById(R.id.btn_remind_me);
        finalDatabase = new NoteDb(this);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //making sure the edit text is not empty
                if (TextUtils.isEmpty(noteText.getText().toString().trim())) {
                    Toast.makeText(NoteActivity.this, "the Note is empty", Toast.LENGTH_SHORT).show();
                    Intent backToMainActivity = new Intent(NoteActivity.this, MainActivity.class);
                    backToMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(backToMainActivity);
                } else {
                    //Checking whether the intent is from adapter or add button by verifying the intent extra
                    if (!TextUtils.isEmpty(adapterPosition)) {
                        Log.d(TAG, "onClick: save button is clicked " + noteText.getText().toString() + " " + adapterPosition);
                        finalDatabase.updateDb(database, noteText.getText().toString(), previousTime, getTimeInMillisconds());
                        Intent backToMainActivity = new Intent(NoteActivity.this, MainActivity.class);
                        backToMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(backToMainActivity);
                    } else {
                        finalDatabase.insertData(database, noteText.getText().toString(), getTimeInMillisconds());
                        Intent backToMainActivity = new Intent(NoteActivity.this, MainActivity.class);
                        backToMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(backToMainActivity);
                    }
                }
            }
        });
        Intent intent = getIntent();
        notes = intent.getStringExtra("notes");
        adapterPosition = intent.getStringExtra("position");
        previousTime = intent.getStringExtra("previousTime");
        Log.d(TAG, "onCreate:notes " + notes);
        Log.d(TAG, "onCreate: adapterPosition " + adapterPosition);
        if (TextUtils.isEmpty(notes)) {

        } else {
            noteText.setText(notes);
            //This line is to take the cursor to the end of the edit text field
            noteText.setSelection(noteText.getText().toString().length());

        }
        btnRemindMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(noteText.getText().toString().trim())) {
                    noteText.setError("this field is required");
                } else {
                    createDatePicker();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }

    //To get the time in milliseconds and this value is stored in the database
    public String getTimeInMillisconds() {
        Date date = new Date();
        long value = date.getTime();
        String returnTime = String.valueOf(value);
        return returnTime;
    }

    public void createDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                Log.d(TAG, "onDateSet: year " + year + " month " + month + " day " + dayOfMonth);
                createTimePicker(year, month, dayOfMonth);
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void createTimePicker(int year, int month, int dayOfMonth) {
        formattedDate = year + "/" + month + "/" + dayOfMonth;
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Log.d(TAG, "onTimeSet: hour " + hourOfDay + " minute " + minute);
                formattedDate = formattedDate + " " + hourOfDay + ":" + minute;
                Log.d(TAG, "onTimeSet: formatted date " + formattedDate);
                sendNotification(formattedDate);

            }
        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    //call to the notification class that extends Broadcast receiver to display the notification
    public void sendNotification(String formattedDate) {
        Intent intent = new Intent(NoteActivity.this, Notification.class);
        if (TextUtils.isEmpty(notes)) {
            notes = noteText.getText().toString().trim();
        }
        intent.putExtra("notes", notes);
        intent.putExtra("position", adapterPosition);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, convertToMilliseconds(formattedDate), pendingIntent);
        Toast.makeText(getApplicationContext(),"Reminder Added on "+convertToReadableDateFormat(formattedDate),Toast.LENGTH_LONG).show();
    }
    //for displaying the date and time in toast
    private String convertToReadableDateFormat(String formattedDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy, h:mm a");
        Date date = new Date(formattedDate);
        formattedDate = dateFormat.format(date);
        return formattedDate;
    }


    //converts the Date and Time content got from Date and time picker alertDialog
    private long convertToMilliseconds(String formattedDate) {
        String myDate = formattedDate;
        long milliseconds = 1;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/mm/dd hh:mm");
        try {
            Date date = simpleDateFormat.parse(myDate);
            milliseconds = date.getTime();
            Log.d(TAG, "convertToMilliseconds: " + milliseconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return milliseconds;
    }




}




