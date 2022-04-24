package com.example.farith.dailynotes.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.farith.dailynotes.R
import com.example.farith.dailynotes.databinding.ActivityMainBinding
import com.example.farith.dailynotes.modelClass.NoteClass
import com.example.farith.dailynotes.ui.adapters.NotesAdapter
import com.example.farith.dailynotes.ui.viewModels.MainViewModel
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var noteList: List<NoteClass>
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        bindData()
        setUpViewModel()
    }

    private fun bindData() {
        notesAdapter = NotesAdapter( ArrayList())
        with(binding.notesRecyclerview) {
            this.layoutManager = GridLayoutManager(
                this@MainActivity,
                2, LinearLayoutManager.VERTICAL, false
            ).also { adapter = notesAdapter }
        }
        binding.addButton.visibility = View.VISIBLE
        binding.addButton.setOnClickListener {
            val noteActivityIntent = Intent(this@MainActivity, NoteActivity::class.java)
            noteActivityIntent.putExtra("ADD_BUTTON",true)
            startActivity(noteActivityIntent)
        }
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.getNoteLiveData().observe(this, { data ->
            data?.let {
                if (data.isEmpty()) {
                    binding.emptyText.visibility = View.VISIBLE
                } else {
                    notesAdapter.updateList(data)
                    notesAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        if (::viewModel.isInitialized)
            viewModel.getNotesList()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.convert_to_grid, menu)
        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as SearchView
        searchView.setOnQueryTextListener(onQueryTextListener)
        return super.onCreateOptionsMenu(menu)
    }

    private val onQueryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            val tempList = java.util.ArrayList<NoteClass>()
            if (::noteList.isInitialized && newText != null) {
                for (i in noteList.indices) {
                    val note = noteList[i].noteContent?.lowercase()
                    if (note.equals(newText)) {
                        tempList.add(noteList[i])
                    }
                }
            }
            notesAdapter.updateList(tempList)
            return true
        }

    }


}