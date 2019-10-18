package com.example.dell.travelsafe

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import com.example.dell.travelsafe.util.Prefutil

import kotlinx.android.synthetic.main.activity_timer.*
import kotlinx.android.synthetic.main.content_timer.*
import java.util.*

class TimerActivity : AppCompatActivity() {

    companion object {
        fun setAlarm(context: Context,nowSeconds:Long,secondsRemaing:Long):Long{
            val wakeUpTime=(nowSeconds+secondsRemaing)*1000
            val alarmManager=context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent=Intent(context,TimerExpiredReceiver::class.java)
            val pendingIntent=PendingIntent.getBroadcast(context,0,intent,0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,wakeUpTime,pendingIntent)
            Prefutil.setAlarmSetTime(nowSeconds,context)
            return wakeUpTime
        }
        fun removeAlarm(context: Context){
            val intent=Intent(context,TimerExpiredReceiver::class.java)
            val pendingIntents=PendingIntent.getBroadcast(context,0,intent,0)
            val alarmManager=context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntents)
            Prefutil.setAlarmSetTime(0,context)

        }
        val nowSeconds:Long
            get() = Calendar.getInstance().timeInMillis/1000
    }

    enum class TimerState{
        Stopped,Paused,Running

    }

    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds= 0L
    private var timerState= TimerState.Stopped

    private var secondsRemaining= 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        setSupportActionBar(toolbar)

        fab_start.setOnClickListener { view ->
            startTimer()
            timerState=TimerState.Running
            updateButtons()

        }

        fab_pause.setOnClickListener { view ->
            timer.cancel()
            timerState=TimerState.Paused
            updateButtons()

            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        fab_stop.setOnClickListener { view ->
            timer.cancel()
            timerState=TimerState.Stopped
            onTimerFinished()

            Snackbar.make(view, "User just confermed that NO accident has occured", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }




    }

    override fun onResume() {
        super.onResume()

        initTimer()

        removeAlarm(this)
        //TODO: hide notification

    }

    override fun onPause() {
        super.onPause()

        if(timerState == TimerState.Running){
            timer.cancel()
            val wakeUpTime=setAlarm(this, nowSeconds,secondsRemaining)

            //TODO: show notification
        }
        else if(timerState==TimerState.Paused){
            //TODO: show notification
        }
        Prefutil.setPreviusTimerLengthSeconds(timerLengthSeconds,this)
        Prefutil.setSecondsRemaining(secondsRemaining,this)
        Prefutil.setTimerState(timerState,this)

    }

    private fun initTimer(){
        timerState=Prefutil.getTimerState(this)

        if(timerState==TimerState.Stopped)
            setNewTimerLength()
        else
            setPreviousTimerLength()
        secondsRemaining= if(timerState==TimerState.Running || timerState==TimerState.Paused)
            Prefutil.getSecondsRemaining(this)
        else
            timerLengthSeconds

        val alarmSetTime=Prefutil.getAlarmSetTime(this)
        if(alarmSetTime > 0)
            secondsRemaining -= nowSeconds-alarmSetTime

        if (secondsRemaining <= 0)
            onTimerFinished()
        else if (timerState== TimerState.Running)
            startTimer()

        updateButtons()
        updateCountdownUI()


    }


    fun onTimerFinished(){
        timerState=TimerState.Stopped
        setNewTimerLength()

        progress_countdown.progress=0
        Prefutil.setSecondsRemaining(timerLengthSeconds,this)
        secondsRemaining=timerLengthSeconds
        updateButtons()
        updateCountdownUI()
        val intent=Intent(this,NotificationActivity::class.java)
        startActivity(intent)


    }

    private fun startTimer(){
        timerState=TimerState.Running
        timer=object : CountDownTimer(secondsRemaining*1000,1000){
            override fun onFinish()= onTimerFinished()

            override fun onTick(millisUntilFinished: Long){
                secondsRemaining=millisUntilFinished/1000

                updateCountdownUI()
            }
        }.start()

    }

    private fun setNewTimerLength(){
        val lengthInMinutes=Prefutil.getTimerLenght(this)
        timerLengthSeconds=(lengthInMinutes*60L)
        progress_countdown.max=timerLengthSeconds.toInt()
    }
    private fun setPreviousTimerLength(){
        timerLengthSeconds=Prefutil.getPreviusTimerLengthSeconds(this)
        progress_countdown.max=timerLengthSeconds.toInt()
    }

    private fun updateCountdownUI(){
        val minutesUntilFinished=secondsRemaining/60
        val secondsInMinutesUntilFinished=secondsRemaining-minutesUntilFinished*60
        val secondsStr=secondsInMinutesUntilFinished.toString()
        textView_countdown.text="$minutesUntilFinished:${
        if (secondsStr.length==2)secondsStr
        else "0"+secondsStr}"
        progress_countdown.progress=(timerLengthSeconds-secondsRemaining).toInt()

    }
    private fun updateButtons(){
        when(timerState){
            TimerState.Running->{
                fab_start.isEnabled=false
                fab_pause.isEnabled=true
                fab_stop.isEnabled=true
            }
            TimerState.Stopped->{
                fab_start.isEnabled=true
                fab_pause.isEnabled=false
                fab_stop.isEnabled=false

            }
            TimerState.Paused->{
                fab_start.isEnabled=true
                fab_pause.isEnabled=false
                fab_stop.isEnabled=true

            }
        }

    }

}