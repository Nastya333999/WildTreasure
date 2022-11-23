package com.app.wildtreasure.ui

import androidx.annotation.DrawableRes

data class Item(
    @DrawableRes val resIdfirst: Int,
    @DrawableRes val resIdSecond: Int,
    @DrawableRes val resIdTherd: Int,
    @DrawableRes val resIdFour: Int,
    val id: Int
)
