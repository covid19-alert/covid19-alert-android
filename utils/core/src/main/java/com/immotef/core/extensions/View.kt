package com.immotef.core.extensions

import android.view.View

/**
 *
 */


fun View.setVisibleOrGone(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}