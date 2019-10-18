package com.example.dell.travelsafe.util

import android.content.Context
import android.preference.PreferenceManager
import com.example.dell.travelsafe.TimerActivity

class Prefutil {
    companion object {
        fun getTimerLenght(context: Context): Int{
            //Placeholder

            return 1
        }

        private const val PREVIOUS_TIMER_LENGTH_SECONDS_ID="com.example.dell.timer.previous_timer_length"
        fun getPreviusTimerLengthSeconds(context: Context): Long{
            val preferences= PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID,0)
        }

        fun setPreviusTimerLengthSeconds(seconds:Long,context: Context){
            val editor= PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID,seconds)
            editor.apply()
        }

        private const val TIMER_STATE_ID="com.example.dell.timer_state"

        fun getTimerState(context: Context): TimerActivity.TimerState{
            val preferences= PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal=preferences.getInt(TIMER_STATE_ID,0)
            return TimerActivity.TimerState.values()[ordinal]
        }

        fun setTimerState(state: TimerActivity.TimerState,context: Context){
            val editor= PreferenceManager.getDefaultSharedPreferences(context).edit()
            val ordinal=state.ordinal
            editor.putInt(TIMER_STATE_ID,ordinal)
            editor.apply()


        }

        private const val SECONDS__REMAINING_ID="com.example.dell.timer.previous_timer_length"
        fun getSecondsRemaining(context: Context): Long{
            val preferences= PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(SECONDS__REMAINING_ID,0)
        }

        fun setSecondsRemaining(seconds:Long,context: Context){
            val editor= PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(SECONDS__REMAINING_ID,seconds)
            editor.apply()
        }

        private const val ALARM_SET_TIME_ID="com.example.dell.background_time"
        fun getAlarmSetTime(context: Context):Long{
            val preferences= PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(ALARM_SET_TIME_ID,0)
        }
        fun setAlarmSetTime(time:Long,context: Context){
            val editor= PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(ALARM_SET_TIME_ID,time)
            editor.apply()

        }

    }
}