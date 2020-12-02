package com.passbolt.poc.milestones.autofill.test.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.passbolt.poc.R

class AutofillWebViewTestFragment : Fragment(R.layout.fragment_autofill_webview_test) {
  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    setupWebView(view.findViewById(R.id.webview))
  }

  @SuppressLint("SetJavaScriptEnabled")
  private fun setupWebView(webView: WebView) {
    webView.apply {
      webViewClient = WebViewClient()
      settings.javaScriptEnabled = true

      clearHistory()
      clearFormData()
      clearCache(true)
      loadUrl("file:///android_res/raw/webview_sample.html")
    }
  }
}
