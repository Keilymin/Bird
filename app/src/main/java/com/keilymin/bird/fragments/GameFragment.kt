package com.keilymin.bird.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.keilymin.bird.views.GameView

class GameFragment : Fragment() {
    companion object{
        const val savePreferencesString = "GameSaves"
    }
    private lateinit var gameView: GameView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val pref = activity?.getSharedPreferences(savePreferencesString, Context.MODE_PRIVATE)
        gameView = GameView(requireContext(),pref)

        return gameView
    }

    override fun onResume() {
        super.onResume()
        gameView.resume()
    }

    override fun onPause() {
        super.onPause()
        gameView.pause()
    }
}