package com.immotef.web

import android.graphics.Point
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_web_view.*


/**
 *
 */


class WebViewFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_web_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val url = arguments?.getString(getString(R.string.url_key), "")
        if (url.isNullOrBlank()) {
            findNavController().popBackStack()
        }
        webView.webChromeClient = WebChromeClient();
        webView.webViewClient = WebViewClient();
        webView.clearCache(true)
        webView.clearHistory();
        webView.getSettings().javaScriptEnabled = true
        webView.getSettings().javaScriptCanOpenWindowsAutomatically = true

        val display: Display = requireActivity().windowManager.defaultDisplay
        val point = Point()
        display.getSize(point)


        webView.loadUrl(url)
    }
}