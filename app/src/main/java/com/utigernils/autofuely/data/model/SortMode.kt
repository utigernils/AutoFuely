package com.utigernils.autofuely.data.model

import androidx.annotation.StringRes
import com.utigernils.autofuely.R

enum class SortMode(@param:StringRes val displayNameResId: Int) {
    PRICE(R.string.sort_price),
    DISTANCE(R.string.sort_distance)
}