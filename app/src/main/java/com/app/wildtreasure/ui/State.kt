package com.app.wildtreasure.ui

import androidx.constraintlayout.motion.utils.ViewState

sealed class MainState : ViewState() {
    object Loading : MainState()
    data class AppsFlyerState(val title: String) : MainState()
    data class FBState(val title: String) : MainState()
    data class NavigateToWeb(val url: String) : MainState()
    object NavigateToGame : MainState()
}
