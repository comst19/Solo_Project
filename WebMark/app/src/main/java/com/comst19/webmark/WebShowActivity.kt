package com.comst19.webmark

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView

class WebShowActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_show)

        val url = intent.getStringExtra("url")
        val webView : WebView = findViewById(R.id.webView)
        webView.loadUrl(url.toString())
        finish()

    }
}