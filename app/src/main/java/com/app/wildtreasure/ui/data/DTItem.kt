package com.app.wildtreasure.ui.data

import androidx.annotation.DrawableRes

data class DTItem(
    @DrawableRes val resIdfirst: Int,
    @DrawableRes val resIdSecond: Int,
    @DrawableRes val resIdTherd: Int,
    @DrawableRes val resIdFour: Int,
    val id: Int
)
