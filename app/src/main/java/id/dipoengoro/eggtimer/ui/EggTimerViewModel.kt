package id.dipoengoro.eggtimer.ui

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.SystemClock
import android.text.format.DateUtils
import androidx.core.app.AlarmManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.dipoengoro.eggtimer.R
import id.dipoengoro.eggtimer.receiver.AlarmReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("UnspecifiedImmutableFlag")
class EggTimerViewModel(app: Application) : AndroidViewModel(app) {
    companion object {
        const val TRIGGER_TIME = "TRIGGER_AT"
        const val REQUEST_CODE = 0
        const val ZERO_TIME = 0L
        const val DEFAULT_ALARM_ON = false
        const val ALARM_IS_ON = true
    }

    private val second: Long = DateUtils.SECOND_IN_MILLIS
    private val minute: Long = 60 * second

    private val timerLengthOptions: IntArray
    private val notifyPendingIntent: PendingIntent

    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private var prefs =
        app.getSharedPreferences("id.dipoengoro.eggtimer", Context.MODE_PRIVATE)
    private val notifyIntent = Intent(app, AlarmReceiver::class.java)

    private val _timeSelection = MutableLiveData<Int>()
    val timeSelection: LiveData<Int>
        get() = _timeSelection

    private val _elapsedTime = MutableLiveData<Long>()
    val elapsedTime: LiveData<Long>
        get() = _elapsedTime

    private val _alarmOn = MutableLiveData<Boolean>()
    val alarmOn: LiveData<Boolean>
        get() = _alarmOn

    private lateinit var timer: CountDownTimer

    init {
        _alarmOn.value = PendingIntent.getBroadcast(
            getApplication(),
            REQUEST_CODE,
            notifyIntent,
            PendingIntent.FLAG_NO_CREATE
        ) != null

        notifyPendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            REQUEST_CODE,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        timerLengthOptions = app.resources.getIntArray(R.array.minutes_array)

        if (_alarmOn.value == ALARM_IS_ON) createTimer()
    }

    fun setAlarm(isChecked: Boolean) = when (isChecked) {
        true -> timeSelection.value?.let { startTimer(it) }
        false -> cancelNotification()
    }

    fun setTimeSelected(timerLengthSelection: Int) =
        _timeSelection.apply { value = timerLengthSelection }

    private fun startTimer(timerLengthSelection: Int) {
        _alarmOn.value?.let {
            if (!it) {
                _alarmOn.value = ALARM_IS_ON
                val selectedInterval = when (timerLengthSelection) {
                    0 -> 10*second
                    else -> timerLengthOptions[timerLengthSelection]*minute
                }
                val triggerTime = SystemClock.elapsedRealtime() + selectedInterval

                AlarmManagerCompat.setExactAndAllowWhileIdle(
                    alarmManager,
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerTime,
                    notifyPendingIntent
                )

                viewModelScope.launch { saveTime(triggerTime) }
            }
        }
        createTimer()
    }

    private fun createTimer() = viewModelScope.launch {
        loadTime().let { triggerTime ->
            timer = object : CountDownTimer(triggerTime, second) {
                override fun onTick(millisUntilFinished: Long) = _elapsedTime.run {
                    value = triggerTime - SystemClock.elapsedRealtime()
                    value?.let { if (it <= ZERO_TIME) resetTimer() }
                    return@run
                }

                override fun onFinish() = resetTimer()
            }
        }
        timer.start()
    }

    private fun cancelNotification() {
        resetTimer()
        alarmManager.cancel(notifyPendingIntent)
    }

    private fun resetTimer() {
        timer.cancel()
        _elapsedTime.value = ZERO_TIME
        _alarmOn.value = DEFAULT_ALARM_ON
    }

    private suspend fun saveTime(triggerTime: Long) = withContext(Dispatchers.IO) {
        prefs.edit().putLong(TRIGGER_TIME, triggerTime).apply()
    }

    private suspend fun loadTime(): Long = withContext(Dispatchers.IO) {
        prefs.getLong(TRIGGER_TIME, ZERO_TIME)
    }
}