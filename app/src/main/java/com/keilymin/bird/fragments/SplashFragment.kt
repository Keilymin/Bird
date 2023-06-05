package com.keilymin.bird.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.keilymin.bird.R
import com.keilymin.bird.dialogs.InternetTroubleDialog
import com.keilymin.bird.viewmodels.SplashViewModel

class SplashFragment : Fragment() {
    private val splashViewModel by viewModels<SplashViewModel>()
    private lateinit var navController : NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        splashViewModel.getConfig()
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onStart() {
        super.onStart()
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        splashViewModel.remoteConfig.observe(requireActivity()) {
            if (it) {
                val bundle = Bundle()
                bundle.putString(WebViewFragment.URL, splashViewModel.url)
                navController.navigate(R.id.webview, bundle)
            } else {
                navController.navigate(R.id.game)
            }
        }
        splashViewModel.isShowDialog.observe(requireActivity()){
            if (it){
                val dialog = InternetTroubleDialog(splashViewModel::closeDialog)
                dialog.show(parentFragmentManager, "noInternet")
            }
        }
    }


}