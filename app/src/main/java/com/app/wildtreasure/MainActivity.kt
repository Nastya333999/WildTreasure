package com.app.wildtreasure

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.room.Room
import com.app.mylibrary.AppDatabase
import com.app.wildtreasure.ui.GameActivity
import com.app.wildtreasure.ui.MainState
import com.app.wildtreasure.ui.MainViewModel
import com.app.wildtreasure.ui.WActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.data.collectLatest { state ->
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
        val intet = Intent(this, GameActivity::class.java)
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





