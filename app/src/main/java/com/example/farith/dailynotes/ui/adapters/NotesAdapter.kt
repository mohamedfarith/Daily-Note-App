package com.example.farith.dailynotes.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.farith.dailynotes.R
import com.example.farith.dailynotes.databinding.NotesViewBinding
import com.example.farith.dailynotes.modelClass.NoteClass
import com.example.farith.dailynotes.ui.adapters.NotesAdapter.MyViewHolder
import com.example.farith.dailynotes.ui.interfaces.AdapterClickInterface
import com.example.farith.dailynotes.ui.viewModels.MainViewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class NotesAdapter(
    private var noteDbList: ArrayList<NoteClass>,
    private var viewModel: MainViewModel,
    private var adapterInterface: AdapterClickInterface
) : RecyclerView.Adapter<MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val noteViewBinding: NotesViewBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.notes_view,
                parent,
                false
            )
        return MyViewHolder(noteViewBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val noteValue = noteDbList[holder.bindingAdapterPosition].noteContent
        holder.binding.txtNotes.text = noteValue
        holder.binding.currentTime.text = noteDbList[position].date
        holder.itemView.tag = noteDbList[position]
    }

    override fun getItemCount(): Int {
        return noteDbList.size
    }

    inner class MyViewHolder(var binding: NotesViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                viewModel.transferNote(it.tag as NoteClass)
                adapterInterface.onAdapterItemClick()
            }
            itemView.setOnLongClickListener { true }
        }
    }

    //The date is formatted from milliseconds to Simple date format
    private fun getCurrentTime(dbStoredDate: String?): String {
        val df: DateFormat = SimpleDateFormat("EEE, d MMM yyyy, h:mm a", Locale.getDefault())
        val dbDate = dbStoredDate!!.toLong()
        val date = Date(dbDate)
        val formattedDate = df.format(date)
        Log.d(TAG, "getCurrentTime: date $date")
        return formattedDate
    }

    fun updateList(list: List<NoteClass>) {
        noteDbList = ArrayList()
        noteDbList.addAll(list)
        notifyDataSetChanged()
    }

    companion object {
        private val TAG = NotesAdapter::class.java.simpleName
    }

}