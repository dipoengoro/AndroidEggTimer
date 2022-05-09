package id.dipoengoro.eggtimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.dipoengoro.eggtimer.ui.EggTimerFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, EggTimerFragment.newInstance())
            .commitNow()
    }
}