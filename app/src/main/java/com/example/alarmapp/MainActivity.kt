package com.example.alarmapp

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.example.alarmapp.databinding.ActivityMainBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var picker: MaterialTimePicker
    private lateinit var calendar: Calendar
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()
        calendar = Calendar.getInstance()
        var ampm = " AM"
        if(calendar[Calendar.HOUR_OF_DAY]>12){
            ampm = " PM"
        }
        tvShowTime.text = calendar[Calendar.HOUR_OF_DAY].toString()+":"+calendar[Calendar.MINUTE].toString()+ ampm
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT>=29){
            val name:CharSequence = "LKR_Android"
            val description = "Channel for Alarm Manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            //creating the channel
            val channel = NotificationChannel("LKR",name,importance)
            channel.description = description
            // creating the notification object
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun pickTime(view: View) {
        picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Alarm Time")
            .build()

        picker.show(supportFragmentManager,"LKR")

        picker.addOnPositiveButtonClickListener {

            if(picker.hour>12){
                binding.tvShowTime.text =
                    String.format("%02d",picker.hour-12) + ":" +
                            String.format("%02d",picker.minute)+" PM"
            }
            else{
                binding.tvShowTime.text =
                    String.format("%02d",picker.hour) + ":" +
                            String.format("%02d",picker.minute)+" AM"
            }

            calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY]=picker.hour
            calendar[Calendar.MINUTE] = picker.minute
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0

            setAlarm(view)
        }
    }

    private fun setAlarm(view:View) {

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, pendingIntent
        )

        Toast.makeText(this, "Alarm Set Successfully", Toast.LENGTH_SHORT).show()
    }

    fun cancelAlarm(view: View) {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

        alarmManager.cancel(pendingIntent)

        Toast.makeText(this,"Alarm cancelled Successfully",Toast.LENGTH_SHORT).show()
    }


}