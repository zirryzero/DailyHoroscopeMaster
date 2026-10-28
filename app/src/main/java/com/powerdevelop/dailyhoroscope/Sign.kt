package com.powerdevelop.dailyhoroscope

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Sign(
    val id: String,
    @StringRes val nameResId: Int,
    @StringRes val dateRangeResId: Int,
    @DrawableRes val imageResId: Int,
    @DrawableRes val backgroundResId: Int
)
