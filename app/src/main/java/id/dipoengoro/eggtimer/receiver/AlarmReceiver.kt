package id.dipoengoro.eggtimer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import id.dipoengoro.eggtimer.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            Toast.makeText(it, it.getText(R.string.eggs_ready), Toast.LENGTH_SHORT).show()
        }
    }
}