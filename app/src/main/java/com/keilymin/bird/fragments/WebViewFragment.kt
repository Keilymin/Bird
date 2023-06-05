package com.keilymin.bird.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.keilymin.bird.R

class WebViewFragment : Fragment() {
    companion object{
        const val URL = "com.keilymin.bird.extras.URL"
    }
    private lateinit var webView: WebView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_webview, container, false)
        webView = root.findViewById(R.id.webview)

        webView.settings.javaScriptEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        webView.loadUrl(arguments?.getString(URL)!!)

        return root
    }
}