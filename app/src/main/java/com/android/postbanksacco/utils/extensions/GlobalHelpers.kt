package com.android.postbanksacco.utils.extensions

import android.view.View
import android.view.animation.AnimationUtils
import com.android.postbanksacco.R

fun scrollAnimation(viewToAnimate: View) {
    val animation =
        AnimationUtils.loadAnimation(viewToAnimate.context, R.anim.fall_down_animation)
    animation.duration = 700
    viewToAnimate.startAnimation(animation)
}