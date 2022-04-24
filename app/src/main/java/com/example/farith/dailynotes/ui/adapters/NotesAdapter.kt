package com.example.farith.dailynotes.ui.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.farith.dailynotes.R
import com.example.farith.dailynotes.databinding.NotesViewBinding
import com.example.farith.dailynotes.modelClass.NoteClass
import com.example.farith.dailynotes.ui.NoteActivity
import com.example.farith.dailynotes.ui.adapters.NotesAdapter.MyViewHolder
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class NotesAdapter(
    private var noteDbList: ArrayList<NoteClass>
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
    }

    override fun getItemCount(): Int {
        return noteDbList.size
    }

    inner class MyViewHolder(var binding: NotesViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                Log.d(TAG, "onClick: " + noteDbList[bindingAdapterPosition])
                val intent = Intent(it.context, NoteActivity::class.java)
                Log.d(TAG, "onClick: adapter postition$bindingAdapterPosition")
                intent.putExtra("notes", noteDbList[bindingAdapterPosition].noteContent)
                val value = (bindingAdapterPosition + 1).toString()
                intent.putExtra("position", value)
                intent.putExtra("previousTime", noteDbList[bindingAdapterPosition].date)
                intent.putExtra("noti", noteDbList[bindingAdapterPosition].notificationId)
                intent.putExtra("reminder_time", noteDbList[bindingAdapterPosition].reminderTime)
                it.context.startActivity(intent)
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