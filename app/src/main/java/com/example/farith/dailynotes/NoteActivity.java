package com.example.farith.dailynotes;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class NoteActivity extends AppCompatActivity {
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
    String notificationID;
    String reminderTime = "No reminder Added";
    TextView reminderText;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String cancelNotificationId = intent.getStringExtra("cancel_noti");
            Log.d(TAG, "onReceive: cancelling notification id is");
            Intent newIntent = new Intent(NoteActivity.this, Notification.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(cancelNotificationId), newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        reminderText = findViewById(R.id.reminder_text);
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
                        finalDatabase.updateDb(database, noteText.getText().toString(), previousTime, getTimeInMillisconds(), notificationID, reminderTime);
                        Intent backToMainActivity = new Intent(NoteActivity.this, MainActivity.class);
                        backToMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(backToMainActivity);
                    } else {
                        notificationID = createNotificationId();
                        Log.d(TAG, "onClick: notification id created and the id is " + notificationID);
                        finalDatabase.insertData(database, noteText.getText().toString(), getTimeInMillisconds(), notificationID, reminderTime);
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
        notificationID = intent.getStringExtra("noti");
        reminderTime = intent.getStringExtra("reminder_time");
        Log.d(TAG, "onCreate:notes " + notes + "onCreate: adapterPosition " + adapterPosition + " reminder time from intent " + reminderTime);
        if (TextUtils.isEmpty(notes)) {

        } else {
            noteText.setText(notes);
            //This line is to take the cursor to the end of the edit text field
            noteText.setSelection(noteText.getText().toString().length());
            if (TextUtils.isEmpty(reminderTime)) {
                reminderText.setText("No Reminder Added");
            } else {
                reminderText.setText("Reminder is set to " + reminderTime);
            }
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
        noteText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged: ");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: ");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(TextUtils.isEmpty(s)){
            noteText.setHint("Enter your note here");
                }
            }
        });
    }

    /*this method create notification id to send the broadcast, this id is also added in the database and
     retrieved in the main activity while deleting the note */
    private String createNotificationId() {
        Random randomNumber = new Random();
        int uniqueRandomNumber = randomNumber.nextInt(900000);
        return String.valueOf(uniqueRandomNumber);
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
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
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
        //To make sure user has saved the note before adding reminder
        if (TextUtils.isEmpty(notificationID)) {
            Toast.makeText(this, "Kindly save the Note before adding the reminder", Toast.LENGTH_LONG).show();
        } else {
            if (convertTo24hrsMilliseconds(formattedDate ) > System.currentTimeMillis()) {
                Intent intent = new Intent(NoteActivity.this, Notification.class);
                if (TextUtils.isEmpty(notes)) {
                    notes = noteText.getText().toString().trim();
                }
                intent.putExtra("notes", notes);
                intent.putExtra("position", notificationID);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(notificationID), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, convertTo24hrsMilliseconds(formattedDate), pendingIntent);
                reminderTime = convertToReadableDateFormat(formattedDate);
                reminderText.setText("Reminder is set to " + reminderTime);
                Toast.makeText(getApplicationContext(), "Reminder Added on " + convertToReadableDateFormat(formattedDate), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "The time is set to past time an so reminder cannot be set, Change it to future time to get Notification", Toast.LENGTH_LONG).show();
            }

        }
    }

    //for displaying the date and time in toast
    private String convertToReadableDateFormat(String formattedDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy, h:mm a");
        Date date = new Date(formattedDate);
        formattedDate = dateFormat.format(date);
        return formattedDate;
    }

    public long convertTo24hrsMilliseconds(String formattedDate) {
        String myDate = formattedDate;
        long milliseconds = 1;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        try {
            Date date = simpleDateFormat.parse(myDate);
            milliseconds = date.getTime();
            Log.d(TAG, "convertToMilliseconds: " + milliseconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return milliseconds;
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

    @Override
    public void onBackPressed() {

        if(!noteText.getText().toString().equals(notes)){
            final AlertDialog.Builder backPressedAlertDialog = new AlertDialog.Builder(this);
            backPressedAlertDialog.setTitle("Note is not Saved");
            backPressedAlertDialog.setMessage("Do you want to continue without saving, The changes made will not be reflected");
            backPressedAlertDialog.setPositiveButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            backPressedAlertDialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent backToMainActivity = new Intent(NoteActivity.this, MainActivity.class);
                    backToMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(backToMainActivity);
                }
            });
            backPressedAlertDialog.show();

        }
        else{
            super.onBackPressed();
        }
    }
}





