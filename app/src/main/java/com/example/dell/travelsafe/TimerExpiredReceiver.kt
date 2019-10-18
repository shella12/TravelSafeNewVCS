package com.example.dell.travelsafe

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.dell.travelsafe.util.Prefutil

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Prefutil.setTimerState(TimerActivity.TimerState.Stopped,context)
        Prefutil.setAlarmSetTime(0,context)
    }
}
