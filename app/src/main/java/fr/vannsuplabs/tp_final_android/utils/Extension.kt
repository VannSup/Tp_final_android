package fr.vannsuplabs.tp_final_android.utils

import android.graphics.Color
import android.view.View
import com.google.android.material.textfield.TextInputEditText

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun TextInputEditText.disable() {
    isFocusable = false
    isClickable = false
    isCursorVisible = false
    setTextColor(Color.GRAY)
}