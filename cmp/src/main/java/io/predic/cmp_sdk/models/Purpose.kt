package io.predic.cmp_sdk.models

import android.graphics.drawable.Drawable
import io.predic.cmp_sdk.utils.Constants

data class Purpose(
    val icon: Drawable,
    val title: String,
    var content: String,
    var enabled: Boolean,
    var expanded: Boolean = false,
    var visibility: Int = Constants.Visibility.Visible.status
)