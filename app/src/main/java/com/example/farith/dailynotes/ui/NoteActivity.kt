package com.example.farith.dailynotes.ui

import android.app.*
import com.example.farith.dailynotes.db.NoteDb
import android.database.sqlite.SQLiteDatabase
import android.text.TextUtils
import com.example.farith.dailynotes.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextWatcher
import android.text.Editable
import android.content.*
import android.util.Log
import android.view.View
import android.widget.*
import com.example.farith.dailynotes.receivers.Notification
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class NoteActivity : AppCompatActivity() {
    var noteText: EditText? = null
    var notes: String? = null
    var adapterPosition: String? = null
    var previousTime: String? = null
    var saveBtn: Button? = null
    var finalDatabase: NoteDb? = null
    var database: SQLiteDatabase? = null
    var btnRemindMe: Button? = null
    var formattedDate: String? = null
    var notificationID: String? = null
    var reminderTime: String? = "No reminder Added"
    var reminderText: TextView? = null
    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val cancelNotificationId = intent.getStringExtra("cancel_noti")
            Log.d(TAG, "onReceive: cancelling notification id is")
            val newIntent = Intent(this@NoteActivity, Notification::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                cancelNotificationId!!.toInt(),
                newIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        reminderText = findViewById(R.id.reminder_text)
        noteText = findViewById(R.id.note_text)
        saveBtn = findViewById(R.id.save_btn)
        btnRemindMe = findViewById(R.id.btn_remind_me)
        finalDatabase = NoteDb(this)
        saveBtn?.setOnClickListener(View.OnClickListener {
            //making sure the edit text is not empty
            if (TextUtils.isEmpty(noteText?.text.toString().trim { it <= ' ' })) {
                Toast.makeText(this@NoteActivity, "the Note is empty", Toast.LENGTH_SHORT).show()
                val backToMainActivity = Intent(this@NoteActivity, MainActivity::class.java)
                backToMainActivity.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(backToMainActivity)
            } else {
                //Checking whether the intent is from adapter or add button by verifying the intent extra
                if (!TextUtils.isEmpty(adapterPosition)) {
                    Log.d(
                        TAG,
                        "onClick: save button is clicked " + noteText?.text
                            .toString() + " " + adapterPosition
                    )
                    finalDatabase!!.updateDb(
                        database,
                        noteText?.text.toString(),
                        previousTime,
                        timeInMillisconds,
                        notificationID,
                        reminderTime
                    )
                    val backToMainActivity = Intent(this@NoteActivity, MainActivity::class.java)
                    backToMainActivity.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(backToMainActivity)
                } else {
                    notificationID = createNotificationId()
                    Log.d(TAG, "onClick: notification id created and the id is $notificationID")
                    finalDatabase!!.insertData(
                        database,
                        noteText?.text.toString(),
                        timeInMillisconds,
                        notificationID,
                        reminderTime
                    )
                    val backToMainActivity = Intent(this@NoteActivity, MainActivity::class.java)
                    backToMainActivity.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(backToMainActivity)
                }
            }
        })
        val intent = intent
        notes = intent.getStringExtra("notes")
        adapterPosition = intent.getStringExtra("position")
        previousTime = intent.getStringExtra("previousTime")
        notificationID = intent.getStringExtra("noti")
        reminderTime = intent.getStringExtra("reminder_time")
        Log.d(
            TAG,
            "onCreate:notes " + notes + "onCreate: adapterPosition " + adapterPosition + " reminder time from intent " + reminderTime
        )
        if (TextUtils.isEmpty(notes)) {
        } else {
            noteText?.setText(notes)
            //This line is to take the cursor to the end of the edit text field
            noteText?.setSelection(noteText?.text.toString().length)
            if (TextUtils.isEmpty(reminderTime)) {
                reminderText?.text = "No Reminder Added"
            } else {
                reminderText?.text = "Reminder is set to $reminderTime"
            }
        }
        btnRemindMe?.setOnClickListener(View.OnClickListener {
            if (TextUtils.isEmpty(noteText?.text.toString().trim { it <= ' ' })) {
                noteText?.error = "this field is required"
            } else {
                createDatePicker()
            }
        })
        noteText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                Log.d(TAG, "beforeTextChanged: ")
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                Log.d(TAG, "onTextChanged: ")
            }

            override fun afterTextChanged(s: Editable) {
                if (TextUtils.isEmpty(s)) {
                    noteText?.hint = "Enter your note here"
                }
            }
        })
    }

    /*this method create notification id to send the broadcast, this id is also added in the database and
     retrieved in the main activity while deleting the note */
    private fun createNotificationId(): String {
        val randomNumber = Random()
        val uniqueRandomNumber = randomNumber.nextInt(900000)
        return uniqueRandomNumber.toString()
    }

    //To get the time in milliseconds and this value is stored in the database
    val timeInMillisconds: String
        get() {
            val date = Date()
            val value = date.time
            return value.toString()
        }

    fun createDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { view, year, month, dayOfMonth ->
                var month = month
                month = month + 1
                Log.d(TAG, "onDateSet: year $year month $month day $dayOfMonth")
                createTimePicker(year, month, dayOfMonth)
            },
            Calendar.getInstance()[Calendar.YEAR],
            Calendar.getInstance()[Calendar.MONTH],
            Calendar.getInstance()[Calendar.DAY_OF_MONTH]
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    fun createTimePicker(year: Int, month: Int, dayOfMonth: Int) {
        formattedDate = "$year/$month/$dayOfMonth"
        val timePickerDialog = TimePickerDialog(
            this,
            { view, hourOfDay, minute ->
                Log.d(TAG, "onTimeSet: hour $hourOfDay minute $minute")
                formattedDate = "$formattedDate $hourOfDay:$minute"
                Log.d(TAG, "onTimeSet: formatted date $formattedDate")
                sendNotification(formattedDate!!)
            },
            Calendar.getInstance()[Calendar.HOUR_OF_DAY],
            Calendar.getInstance()[Calendar.MINUTE],
            true
        )
        timePickerDialog.show()
    }

    //call to the notification class that extends Broadcast receiver to display the notification
    fun sendNotification(formattedDate: String) {
        //To make sure user has saved the note before adding reminder
        if (TextUtils.isEmpty(notificationID)) {
            Toast.makeText(
                this,
                "Kindly save the Note before adding the reminder",
                Toast.LENGTH_LONG
            ).show()
        } else {
            if (convertTo24hrsMilliseconds(formattedDate) > System.currentTimeMillis()) {
                val intent = Intent(this@NoteActivity, Notification::class.java)
                if (TextUtils.isEmpty(notes)) {
                    notes = noteText!!.text.toString().trim { it <= ' ' }
                }
                intent.putExtra("notes", notes)
                intent.putExtra("position", notificationID)
                val pendingIntent = PendingIntent.getBroadcast(
                    applicationContext,
                    notificationID!!.toInt(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                alarmManager[AlarmManager.RTC_WAKEUP, convertTo24hrsMilliseconds(formattedDate)] =
                    pendingIntent
                reminderTime = convertToReadableDateFormat(formattedDate)
                reminderText!!.text = "Reminder is set to $reminderTime"
                Toast.makeText(
                    applicationContext,
                    "Reminder Added on " + convertToReadableDateFormat(formattedDate),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "The time is set to past time an so reminder cannot be set, Change it to future time to get Notification",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    //for displaying the date and time in toast
    private fun convertToReadableDateFormat(formattedDate: String): String {
        var formattedDate = formattedDate
        val dateFormat = SimpleDateFormat("EEE, d MMM yyyy, h:mm a")
        val date = Date(formattedDate)
        formattedDate = dateFormat.format(date)
        return formattedDate
    }

    fun convertTo24hrsMilliseconds(formattedDate: String): Long {
        val myDate = formattedDate
        var milliseconds: Long = 1
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm")
        try {
            val date = simpleDateFormat.parse(myDate)
            milliseconds = date.time
            Log.d(TAG, "convertToMilliseconds: $milliseconds")
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return milliseconds
    }

    //converts the Date and Time content got from Date and time picker alertDialog
    private fun convertToMilliseconds(formattedDate: String): Long {
        val myDate = formattedDate
        var milliseconds: Long = 1
        val simpleDateFormat = SimpleDateFormat("yyyy/mm/dd hh:mm")
        try {
            val date = simpleDateFormat.parse(myDate)
            milliseconds = date.time
            Log.d(TAG, "convertToMilliseconds: $milliseconds")
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return milliseconds
    }

    override fun onBackPressed() {
        if (noteText!!.text.toString() != notes) {
            val backPressedAlertDialog = AlertDialog.Builder(this)
            backPressedAlertDialog.setTitle("Note is not Saved")
            backPressedAlertDialog.setMessage("Do you want to continue without saving, The changes made will not be reflected")
            backPressedAlertDialog.setPositiveButton("No") { dialog, which -> }
            backPressedAlertDialog.setNegativeButton("Yes") { dialog, which ->
                val backToMainActivity = Intent(this@NoteActivity, MainActivity::class.java)
                backToMainActivity.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(backToMainActivity)
            }
            backPressedAlertDialog.show()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private val TAG = NoteActivity::class.java.simpleName
    }
}