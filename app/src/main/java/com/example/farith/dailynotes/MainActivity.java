package com.example.farith.dailynotes;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farith.dailynotes.ModelClass.NotesDatabaseList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, LongPressActionListener, SearchView.OnQueryTextListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView notesRecyclerView;
    private FloatingActionButton addButton;
    ArrayList<NotesDatabaseList> notesDatabaseData = new ArrayList<>();
    SQLiteDatabase database;
    NoteDb dbReference;
    NotesAdapter adapter;
    TextView emptyText;
    Boolean isGrid = true;
    public static final String NOTIFICATION = "com.example.farith.dailynotes.MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        dbReference = new NoteDb(MainActivity.this);
        dbReference.insertStatusData(database, "true");
        addButton.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.convert_to_grid, menu);
        MenuItem search = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) search.getActionView();
        if (searchView != null)
            searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//        if (isGrid) {
//            menu.findItem(R.id.change_to_list).setVisible(true);
//            menu.findItem(R.id.change_to_grid).setVisible(false);
//        } else {
//            menu.findItem(R.id.change_to_list).setVisible(false);
//            menu.findItem(R.id.change_to_grid).setVisible(true);
//        }
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.change_to_list) {
            isGrid = false;
            dbReference = new NoteDb(MainActivity.this);
            notesDatabaseData = dbReference.readValues(database);
            Log.d(TAG, "onOptionsItemSelected:readvalues from db " + notesDatabaseData.get(0).getNotificationID());
            dbReference.updateStatusData(database, isGrid.toString());
            adapter = new NotesAdapter(MainActivity.this, notesDatabaseData);
            notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        } else {
            isGrid = true;
            dbReference.updateStatusData(database, isGrid.toString());
//            invalidateOptionsMenu();
            adapter = new NotesAdapter(MainActivity.this, notesDatabaseData);
            notesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false));
        }
        notesRecyclerView.setAdapter(adapter);
        if (notesDatabaseData.size() == 0) {
            emptyText.setVisibility(View.VISIBLE);
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        notesDatabaseData = dbReference.readValues(database);
        String data = dbReference.readStatusData(database);
        if (data.equals("true")) {
            adapter = new NotesAdapter(MainActivity.this, notesDatabaseData);
            notesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false));
            notesRecyclerView.setAdapter(adapter);
            if (notesDatabaseData.size() == 0) {
                emptyText.setVisibility(View.VISIBLE);


            }
        } else {
            adapter = new NotesAdapter(MainActivity.this, notesDatabaseData);
            notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            notesRecyclerView.setAdapter(adapter);
            if (notesDatabaseData.size() == 0) {
                emptyText.setVisibility(View.VISIBLE);
            }
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
                Intent noteActivityIntent = new Intent(MainActivity.this, NoteActivity.class);
                startActivity(noteActivityIntent);
        }
    }

    private String getCurrentTime() {
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, h:mm a");
        String date = df.format(Calendar.getInstance().getTime());
        Log.d(TAG, "getCurrentTime: date " + date);
        return date;
    }

    @Override
    public void updateRecyclerView(int position, String notificationId) {
        notesDatabaseData.remove(position);
        adapter.notifyItemRemoved(position);
        //Cancelling the notification when the item is deleted from the list
        Intent cancelNotificationIntent = new Intent(this, CancelNotification.class);
        cancelNotificationIntent.putExtra("cancel_noti", notificationId);
        sendBroadcast(cancelNotificationIntent);
        if (notesDatabaseData.size() == 0) {
            emptyText.setVisibility(View.VISIBLE);
            notesDatabaseData.clear();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        ArrayList<NotesDatabaseList> tempList = new ArrayList<>();
        for (int i = 0; i < notesDatabaseData.size(); i++) {
            if (notesDatabaseData.get(i).getNotes().toLowerCase().contains(s.toLowerCase())) {
                tempList.add(notesDatabaseData.get(i));
            }
        }
        adapter.updateList(tempList);
        return true;
    }
}
