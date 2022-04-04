package com.example.farith.dailynotes.ui

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.farith.dailynotes.R
import com.example.farith.dailynotes.db.NoteDb
import com.example.farith.dailynotes.modelClass.NotesDatabaseList
import com.example.farith.dailynotes.receivers.CancelNotification
import com.example.farith.dailynotes.ui.adapters.NotesAdapter
import com.example.farith.dailynotes.ui.interfaces.LongPressActionListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener, LongPressActionListener,
    SearchView.OnQueryTextListener {
    private var notesRecyclerView: RecyclerView? = null
    private var addButton: FloatingActionButton? = null
    private var notesDatabaseData: ArrayList<NotesDatabaseList> = ArrayList()
    var database: SQLiteDatabase? = null
    var dbReference: NoteDb? = null
    var adapter: NotesAdapter? = null
    var emptyText: TextView? = null
    var isGrid = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        dbReference = NoteDb(this@MainActivity)
        dbReference!!.insertStatusData(database, "true")
        addButton!!.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.convert_to_grid, menu)
        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        return super.onCreateOptionsMenu(menu)
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
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if (item.itemId == R.id.change_to_list) {
            isGrid = false
            dbReference = NoteDb(this@MainActivity)
            notesDatabaseData = dbReference!!.readValues(database)
            Log.d(
                TAG,
                "onOptionsItemSelected:readvalues from db " + notesDatabaseData.get(0)
                    ?.notificationID
            )
            dbReference!!.updateStatusData(database, isGrid.toString())
            adapter = NotesAdapter(this@MainActivity, notesDatabaseData)
            notesRecyclerView!!.layoutManager = LinearLayoutManager(this)
        } else {
            isGrid = true
            dbReference!!.updateStatusData(database, isGrid.toString())
            //            invalidateOptionsMenu();
            adapter = NotesAdapter(this@MainActivity, notesDatabaseData)
            notesRecyclerView!!.layoutManager =
                GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false)
        }
        notesRecyclerView!!.adapter = adapter
        if (notesDatabaseData!!.size == 0) {
            emptyText!!.visibility = View.VISIBLE
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        notesDatabaseData = dbReference!!.readValues(database)
        val data = dbReference!!.readStatusData(database)
        if (data == "true") {
            adapter = NotesAdapter(this@MainActivity, notesDatabaseData)
            notesRecyclerView!!.layoutManager =
                GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false)
            notesRecyclerView!!.adapter = adapter
            if (notesDatabaseData.size == 0) {
                emptyText!!.visibility = View.VISIBLE
            }
        } else {
            adapter = NotesAdapter(this@MainActivity, notesDatabaseData)
            notesRecyclerView!!.layoutManager = LinearLayoutManager(this)
            notesRecyclerView!!.adapter = adapter
            if (notesDatabaseData.size == 0) {
                emptyText!!.visibility = View.VISIBLE
            }
        }
    }

    private fun initView() {
        notesRecyclerView = findViewById(R.id.notes_recyclerview)
        addButton = findViewById(R.id.add_button)
        emptyText = findViewById(R.id.emptyText)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.add_button -> {
                val noteActivityIntent = Intent(this@MainActivity, NoteActivity::class.java)
                startActivity(noteActivityIntent)
            }
        }
    }

    private val currentTime: String
        private get() {
            val df: DateFormat = SimpleDateFormat("EEE, d MMM yyyy, h:mm a")
            val date = df.format(Calendar.getInstance().time)
            Log.d(TAG, "getCurrentTime: date $date")
            return date
        }

    override fun updateRecyclerView(position: Int, notificationId: String?) {
        notesDatabaseData!!.removeAt(position)
        adapter!!.notifyItemRemoved(position)
        //Cancelling the notification when the item is deleted from the list
        val cancelNotificationIntent = Intent(this, CancelNotification::class.java)
        cancelNotificationIntent.putExtra("cancel_noti", notificationId)
        sendBroadcast(cancelNotificationIntent)
        if (notesDatabaseData!!.size == 0) {
            emptyText!!.visibility = View.VISIBLE
            notesDatabaseData!!.clear()
        }
    }

    override fun onQueryTextSubmit(s: String): Boolean {
        return false
    }

    override fun onQueryTextChange(s: String): Boolean {
        val tempList = ArrayList<NotesDatabaseList>()
        for (i in notesDatabaseData.indices) {
            if (notesDatabaseData[i].notes?.lowercase(Locale.getDefault())?.contains(
                    s.lowercase(
                        Locale.getDefault()
                    )
                ) == true
            ) {
                tempList.add(notesDatabaseData[i])
            }
        }
        adapter!!.updateList(tempList)
        return true
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        const val NOTIFICATION = "com.example.farith.dailynotes.ui.MainActivity"
    }
}