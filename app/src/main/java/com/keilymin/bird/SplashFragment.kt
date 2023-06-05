package com.keilymin.bird

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation

class SplashFragment : Fragment() {
    private var remoteConfig = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onStart() {
        super.onStart()
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        if (remoteConfig) {
            navController.navigate(R.id.game)
        } else {
            navController.navigate(R.id.webview)
        }
    }
}