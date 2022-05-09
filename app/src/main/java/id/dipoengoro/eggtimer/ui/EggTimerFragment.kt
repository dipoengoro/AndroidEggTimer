package id.dipoengoro.eggtimer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import id.dipoengoro.eggtimer.R
import id.dipoengoro.eggtimer.databinding.FragmentEggTimerBinding

class EggTimerFragment : Fragment() {
    companion object {
        const val TOPIC = "breakfast"
        fun newInstance() = EggTimerFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentEggTimerBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_egg_timer, container, false
        )

        val eggViewModel = ViewModelProvider(this)[EggTimerViewModel::class.java]

        binding.apply {
            eggTimerViewModel = eggViewModel
            lifecycleOwner = viewLifecycleOwner
            return root
        }
    }

    private fun createChannel(channelId: String, channelName: String) {

    }
}