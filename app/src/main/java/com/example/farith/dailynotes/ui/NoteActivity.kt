package com.example.farith.dailynotes.ui

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.farith.dailynotes.R
import com.example.farith.dailynotes.databinding.ActivityNoteBinding
import com.example.farith.dailynotes.modelClass.NoteClass
import com.example.farith.dailynotes.receivers.Notification
import com.example.farith.dailynotes.ui.viewModels.MainViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class NoteActivity : AppCompatActivity() {
    private var formattedDate: String? = null
    private var notificationID: Int? = null
    private var reminderTime: String? = "No reminder Added"
    private var notes: String? = null
    private lateinit var mainViewModel: MainViewModel
    private lateinit var activityNoteBinding: ActivityNoteBinding
    private var createNew: Boolean = true;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityNoteBinding = DataBindingUtil.setContentView(this, R.layout.activity_note)

        bindData()
        setupViewModel()
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    private fun bindData() {
        intent?.let {
            if (!it.getBooleanExtra("ADD_BUTTON", true)) {
                createNew = false;
                val noteClass = it.getSerializableExtra("NOTE_CLASS") as NoteClass
                notes = noteClass.noteContent
                reminderTime = noteClass.reminderTime
                notificationID = noteClass.notificationId
                activityNoteBinding.noteText.setText(notes)
                //This line is to take the cursor to the end of the edit text field
                activityNoteBinding.noteText.setSelection(activityNoteBinding.noteText.text.toString().length)
                if (TextUtils.isEmpty(reminderTime)) {
                    activityNoteBinding.reminderText.text = "No Reminder Added"
                } else {
                    activityNoteBinding.reminderText.text = "Reminder is set to $reminderTime"
                }
            } else {
                createNew = true
            }

        }

        activityNoteBinding.saveBtn.setOnClickListener {
            notes = activityNoteBinding.noteText.text.toString().trim()
            if (TextUtils.isEmpty(notes)) {
                Toast.makeText(it.context, "Please enter some note.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener;
            }
            val currentTime = getCurrentTime()
            val value = if (createNew) {
                val notificationId = currentTime.hashCode()
                NoteClass(
                    noteContent = notes, date = currentTime,
                    notificationId = notificationId, reminderTime = "0"
                )
            } else {
                NoteClass(
                    noteContent = notes, date = currentTime,
                    notificationId = notificationID, reminderTime = "0"
                )
            }
            mainViewModel.insertNotes(value)
            finish()

        }

        activityNoteBinding.btnRemindMe.setOnClickListener {
            if (TextUtils.isEmpty(
                    activityNoteBinding.noteText.text.toString().trim()
                )
            ) {
                activityNoteBinding.noteText.error = "this field is required"
            } else {
                createDatePicker()
            }
        }
        activityNoteBinding.noteText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (TextUtils.isEmpty(s)) {
                    activityNoteBinding.noteText.hint = "Enter your note here"
                }
            }
        })

    }

    private fun getCurrentTime(): String {
        val date = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("EEE, d MMM yyyy, h:mm a")
        return dateFormat.format(date).toString()
    }

    private fun createDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                var month = month
                month += 1
                createTimePicker(year, month, dayOfMonth)
            },
            Calendar.getInstance()[Calendar.YEAR],
            Calendar.getInstance()[Calendar.MONTH],
            Calendar.getInstance()[Calendar.DAY_OF_MONTH]
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun createTimePicker(year: Int, month: Int, dayOfMonth: Int) {
        formattedDate = "$year/$month/$dayOfMonth"
        val timePickerDialog = TimePickerDialog(
            this,
            { view, hourOfDay, minute ->
                formattedDate = "$formattedDate $hourOfDay:$minute"
                sendNotification(formattedDate ?: "")
            },
            Calendar.getInstance()[Calendar.HOUR_OF_DAY],
            Calendar.getInstance()[Calendar.MINUTE],
            true
        )
        timePickerDialog.show()
    }

    //call to the notification class that extends Broadcast receiver to display the notification
    private fun sendNotification(formattedDate: String) {
        //To make sure user has saved the note before adding reminder
        if (notificationID == null) {
            Toast.makeText(
                this,
                "Kindly save the Note before adding the reminder",
                Toast.LENGTH_LONG
            ).show()
        } else {
            if (convertTo24hrsMilliseconds(formattedDate) > System.currentTimeMillis()) {
                val intent = Intent(this@NoteActivity, Notification::class.java)
                if (TextUtils.isEmpty(notes)) {
                    notes = activityNoteBinding.noteText.text.toString().trim { it <= ' ' }
                }
                intent.putExtra("notificationId", notificationID)
                intent.putExtra("notes", notes)
                val pendingIntent: PendingIntent =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        PendingIntent.getBroadcast(
                            applicationContext,
                            notificationID!!.toInt(),
                            intent,
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    } else {
                        PendingIntent.getBroadcast(
                            applicationContext,
                            notificationID!!.toInt(),
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                    }

                val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                alarmManager[AlarmManager.RTC_WAKEUP, convertTo24hrsMilliseconds(formattedDate)] =
                    pendingIntent
                reminderTime = convertToReadableDateFormat(formattedDate)
                activityNoteBinding.reminderText.text = "Reminder is set to $reminderTime"
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

    private fun convertTo24hrsMilliseconds(formattedDate: String): Long {
        val myDate = formattedDate
        var milliseconds: Long = 1
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        try {
            val date: Date? = simpleDateFormat.parse(myDate)
            milliseconds = date?.time ?: -1
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return milliseconds
    }

    //converts the Date and Time content got from Date and time picker alertDialog
    private fun convertToMilliseconds(formattedDate: String): Long {
        val myDate = formattedDate
        var milliseconds: Long = 1
        val simpleDateFormat = SimpleDateFormat("yyyy/mm/dd hh:mm", Locale.getDefault())
        try {
            val date = simpleDateFormat.parse(myDate)
            milliseconds = date.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return milliseconds
    }

    override fun onBackPressed() {
        if (activityNoteBinding.noteText.text.toString() != notes) {
            val backPressedAlertDialog = AlertDialog.Builder(this)
            backPressedAlertDialog.setTitle("Note is not Saved")
            backPressedAlertDialog.setMessage("Do you want to continue without saving, The changes made will not be reflected")
            backPressedAlertDialog.setPositiveButton("No") { dialog, which -> dialog.dismiss() }
            backPressedAlertDialog.setNegativeButton("Yes") { dialog, which ->
                val backToMainActivity =
                    Intent(this@NoteActivity, MainActivity::class.java)
                backToMainActivity.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(backToMainActivity)
            }
            backPressedAlertDialog.show()
        } else {
            super.onBackPressed()
        }
    }
}