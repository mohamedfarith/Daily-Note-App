package com.example.farith.dailynotes;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farith.dailynotes.ModelClass.NoteClass;
import com.example.farith.dailynotes.ModelClass.NotesDatabaseList;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView notesRecyclerView;
    private FloatingActionButton addButton;
    Context context;
    ArrayList<NotesDatabaseList> notesDatabaseData = new ArrayList<>();
    SQLiteDatabase database;
    NoteDb dbReference;
    NotesAdapter adapter;
    NotesDatabaseList databaseList = new NotesDatabaseList();
    TextView emptyText;
    Boolean isGrid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        addButton.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.convert_to_grid, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (isGrid) {
            menu.findItem(R.id.change_to_list).setVisible(true);
            menu.findItem(R.id.change_to_grid).setVisible(false);
        } else {
            menu.findItem(R.id.change_to_list).setVisible(false);
            menu.findItem(R.id.change_to_grid).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
         if(item.getItemId()==R.id.change_to_list){
             invalidateOptionsMenu();
             isGrid = false;
             adapter = new NotesAdapter(MainActivity.this, notesDatabaseData);
             notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
             notesRecyclerView.setAdapter(adapter);
             if (notesDatabaseData.size() == 0) {
                 emptyText.setVisibility(View.VISIBLE);
             } else {
                 adapter.notifyDataSetChanged();
             }
         }else{
             isGrid  = true;
             invalidateOptionsMenu();
             adapter = new NotesAdapter(MainActivity.this, notesDatabaseData);
             notesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false));
             notesRecyclerView.setAdapter(adapter);
             if (notesDatabaseData.size() == 0) {
                 emptyText.setVisibility(View.VISIBLE);
             } else {
                 adapter.notifyDataSetChanged();
             }
         }
         return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        dbReference = new NoteDb(MainActivity.this);
        notesDatabaseData = dbReference.readValues(database);
        adapter = new NotesAdapter(MainActivity.this, notesDatabaseData);
        notesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false));
        notesRecyclerView.setAdapter(adapter);
        if (notesDatabaseData.size() == 0) {
            emptyText.setVisibility(View.VISIBLE);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void initView() {
        notesRecyclerView = findViewById(R.id.notes_recyclerview);
        addButton = findViewById(R.id.add_button);
        emptyText = findViewById(R.id.emptyText);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_button:
                final AlertDialog.Builder alerDialog = new AlertDialog.Builder(this);
                final View view = LayoutInflater.from(this).inflate(R.layout.alert_dialog_layout, null);
                alerDialog.setView(view);
                alerDialog.setMessage("Add Notes").setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText text = view.findViewById(R.id.edt_alert_text);
                        String time = getCurrentTime();
                        databaseList.setDate(time);
                        if(!TextUtils.isEmpty(text.getText().toString().trim())){
                        databaseList.setNotes(text.getText().toString());
                        notesDatabaseData.add(databaseList);
                        database = dbReference.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(NoteClass.NOTE_CONTENT, text.getText().toString());
                        values.put(NoteClass.DATE,time);
                        database.insert(NoteClass.TABLE_NAME, null, values);
                        emptyText.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                        }


                    }
                });
                alerDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alerDialog.create().show();

        }
    }

    private String getCurrentTime() {
        DateFormat df = new SimpleDateFormat("h:mm a EEE MMM d yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        Log.d(TAG, "getCurrentTime: date " + date);
        return date;
    }
}
