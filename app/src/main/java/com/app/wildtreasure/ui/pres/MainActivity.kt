package com.app.wildtreasure.ui.pres

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.webkit.*
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.app.wildtreasure.R
import com.app.wildtreasure.ui.MainState
import com.app.wildtreasure.ui.vm.MainViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.d.collectLatest { state ->
                    setContent {
                        when (state) {
                            is MainState.Loading -> {
                                MainLayout()
                            }
                            is MainState.FBState -> {
                                MainLayout(state.title)
                            }
                            is MainState.AppsFlyerState -> {
                                MainLayout(state.title)
                            }
                            is MainState.NavigateToWeb -> {
                                navigateToWeb(state.url)
                            }
                            is MainState.NavigateToGame -> {
                                navigateToGame()
                            }
                        }
                    }
                }
            }

        }

        viewModel.init(this)


    }

    private fun navigateToWeb(url: String) {
        if (url.isEmpty()) return
        Log.e("NAVIGATE", "url = $url")

        val intet = Intent(this, WActivity::class.java)
        intet.putExtra("web_url", url)
        startActivity(intet)
        finish()
    }

    private fun navigateToGame() {
        val intet = Intent(this, GAG::class.java)
        startActivity(intet)
        finish()
    }

}

@Preview
@Composable
fun MainLayout(title: String = "") {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painterResource(id = R.drawable.main_background),
                contentScale = ContentScale.Crop
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column() {
            LoadingText()
            if (title.isNotEmpty()){
                Text(
                    text = title,
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
        }
    }
}

@Composable
fun LoadingText() {
    Text(
        text = "Loading..",
        style = TextStyle(
            color = Color.Black,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold
        )
    )
}


class WActivity : AppCompatActivity() {
    private lateinit var immediateWebView: WebView
    private lateinit var valueCallback: ValueCallback<Array<Uri?>>
    private val viewModel: MainViewModel by viewModels()


    val data = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) {
        valueCallback.onReceiveValue(it.toTypedArray())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_w)

        window.statusBarColor = resources.getColor(R.color.black, theme)

        immediateWebView = findViewById(R.id.immediate_web_view)
        CookieManager.getInstance().setAcceptThirdPartyCookies(immediateWebView, true)
        CookieManager.getInstance().setAcceptCookie(true)

        immediateWebView.loadUrl(intent.getStringExtra("web_url")!!)
        immediateWebView.settings.loadWithOverviewMode = false

        immediateWebView.settings.javaScriptEnabled = true
        immediateWebView.settings.domStorageEnabled = true

        immediateWebView.settings.userAgentString =
            immediateWebView.settings.userAgentString.replace("wv", "")


        onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (immediateWebView.canGoBack()) {
                        immediateWebView.goBack()
                    } else {
//                        isEnabled = false
                    }
                }
            })


        immediateWebView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
                Snackbar.make(view, error.description, Snackbar.LENGTH_LONG).show()
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                Log.e("onPageFinished", "url is $url")
                CookieManager.getInstance().flush()
                if ((PR + BU) == url){
                    val intent = Intent(this@WActivity, GAG::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    if (url.isNotEmpty() && !url.contains(BU)) {
                        viewModel.svUrl(url)
                        Log.e("mistake here", "$url")
                    }
                }
            }
        }

        immediateWebView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri?>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                valueCallback = filePathCallback
                data.launch(IMT)
                return true
            }

            @SuppressLint("SetJavaScriptEnabled")
            override fun onCreateWindow(
                view: WebView?, isDialog: Boolean,
                isUserGesture: Boolean, resultMsg: Message
            ): Boolean {
                val newWebView = WebView(this@WActivity)
                newWebView.settings.javaScriptEnabled = true
                newWebView.webChromeClient = this
                newWebView.settings.javaScriptCanOpenWindowsAutomatically = true
                newWebView.settings.domStorageEnabled = true
                newWebView.settings.setSupportMultipleWindows(true)
                val transport = resultMsg.obj as WebView.WebViewTransport
                transport.webView = newWebView
                resultMsg.sendToTarget()
                return true
            }
        }
    }

    companion object {
        const val PR = "https://"
        const val BU = "firstwolf.club/"
        private const val IMT = "image/*"
        private const val USER_AGENT =
            "Mozilla/5.0 (Linux; Android 7.0; SM-G930V Build/NRD90M) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.125 Mobile Safari/537.36"
    }

}