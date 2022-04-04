package com.example.farith.dailynotes.ui.adapters

import android.app.*
import android.content.*
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.farith.dailynotes.R
import com.example.farith.dailynotes.db.NoteDb
import com.example.farith.dailynotes.modelClass.NotesDatabaseList
import com.example.farith.dailynotes.ui.NoteActivity
import com.example.farith.dailynotes.ui.adapters.NotesAdapter.MyViewHolder
import com.example.farith.dailynotes.ui.interfaces.LongPressActionListener
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class NotesAdapter(
    private val mContext: Context,
    private var noteDbList: ArrayList<NotesDatabaseList>
) : RecyclerView.Adapter<MyViewHolder>() {
    //   private TextView txtnotesItem;
    //   private TextView txtCurrentTime;
    var deleteRowInDb: NoteDb? = null
    var deleteRowInDatabase: SQLiteDatabase? = null
    var longPressListener: LongPressActionListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notes_view, parent, false)
        //  initView(view);
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: " + noteDbList!![position]?.notes)
        val noteValue = noteDbList!![holder.adapterPosition]?.notes
        holder.txtnotesItem.text = noteValue
        holder.txtCurrentTime.text = getCurrentTime(noteDbList!![holder.adapterPosition]?.date)
    }

    override fun getItemCount(): Int {
        return noteDbList!!.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtnotesItem: TextView = itemView.findViewById(R.id.txt_notes)
        var txtCurrentTime: TextView = itemView.findViewById(R.id.current_time)

        init {
            itemView.setOnClickListener {
                Log.d(TAG, "onClick: " + noteDbList!![adapterPosition])
                val intent = Intent(mContext, NoteActivity::class.java)
                Log.d(TAG, "onClick: adapter postition$adapterPosition")
                intent.putExtra("notes", noteDbList!![adapterPosition]?.notes)
                val value = (adapterPosition + 1).toString()
                intent.putExtra("position", value)
                intent.putExtra("previousTime", noteDbList!![adapterPosition]?.date)
                intent.putExtra("noti", noteDbList!![adapterPosition]?.notificationID)
                intent.putExtra("reminder_time", noteDbList!![adapterPosition]?.reminder)
                mContext.startActivity(intent)
            }
            itemView.setOnLongClickListener(object : OnLongClickListener {
                override fun onLongClick(v: View): Boolean {
                    createDialog()
                    return true
                }

                private fun createDialog() {
                    val deleteAlertDialog = AlertDialog.Builder(
                        mContext
                    )
                    deleteAlertDialog.setTitle("CONFIRM DELETE")
                        .setPositiveButton("DELETE") { dialog, which ->
                            deleteRowInDb = NoteDb(mContext)
                            deleteRowInDb!!.deleteRowInDb(
                                deleteRowInDatabase,
                                adapterPosition,
                                noteDbList!![adapterPosition]?.notes
                            )
                            longPressListener = mContext as LongPressActionListener
                            longPressListener!!.updateRecyclerView(
                                adapterPosition,
                                noteDbList!![adapterPosition]?.notificationID
                            )
                        }
                    deleteAlertDialog.setNegativeButton("CANCEL") { dialog, which -> }
                    deleteAlertDialog.create().show()
                }
            })
        }
    }

    //The date is formatted from milliseconds to Simple date format
    private fun getCurrentTime(dbStoredDate: String?): String {
        val df: DateFormat = SimpleDateFormat("EEE, d MMM yyyy, h:mm a")
        val dbDate = dbStoredDate!!.toLong()
        val date = Date(dbDate)
        val formattedDate = df.format(date)
        Log.d(TAG, "getCurrentTime: date $date")
        return formattedDate
    }

    fun updateList(list: ArrayList<NotesDatabaseList>) {
        noteDbList = ArrayList()
        noteDbList.addAll(list)
        notifyDataSetChanged()
    }

    companion object {
        private val TAG = NotesAdapter::class.java.simpleName
    }
}