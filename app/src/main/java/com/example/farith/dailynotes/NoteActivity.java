package com.example.farith.dailynotes;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NoteActivity extends AppCompatActivity {
    EditText noteText;
    NoteDb db;
    String notes;
    String adapterPosition;
    Button saveBtn;
    NoteDb finalDatabase;
    SQLiteDatabase database;
    private static final String TAG = NoteActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        noteText = findViewById(R.id.note_text);
        saveBtn = findViewById(R.id.save_btn);
        finalDatabase= new NoteDb(this);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: save button is clicked "+noteText.getText().toString()+" "+adapterPosition);
                finalDatabase.updateDb(database,noteText.getText().toString(),adapterPosition,getCurrentTime());
                Intent backToMainActivity = new Intent(NoteActivity.this,MainActivity.class);
                backToMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(backToMainActivity);
            }
        });
        Intent intent = getIntent();
        notes = intent.getStringExtra("notes");
        adapterPosition = intent.getStringExtra("position");
        Log.d(TAG, "onCreate:notes " + notes);
        Log.d(TAG, "onCreate: adapterPosition "+adapterPosition);
        if (TextUtils.isEmpty(notes)) {

        } else {
            noteText.setText(notes);
            noteText.setSelection(noteText.getText().toString().length());

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }
    private String getCurrentTime() {
        DateFormat df = new SimpleDateFormat("h:mm a EEE MMM d yy");
        String date = df.format(Calendar.getInstance().getTime());
        Log.d(TAG, "getCurrentTime: date "+date);
        return date;
    }
}
