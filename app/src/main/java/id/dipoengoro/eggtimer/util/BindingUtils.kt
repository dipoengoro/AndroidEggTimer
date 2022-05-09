package id.dipoengoro.eggtimer.util

import android.text.format.DateUtils
import android.widget.TextView
import androidx.databinding.BindingAdapter

class Util {
    companion object {
        const val MINUTE_IN_SECONDS = 60
    }
}

@BindingAdapter("elapsedTime")
fun TextView.setElapsedTime(value: Long) = (value/DateUtils.SECOND_IN_MILLIS).let {
    text = if (it < Util.MINUTE_IN_SECONDS) it.toString() else DateUtils.formatElapsedTime(it)
}
