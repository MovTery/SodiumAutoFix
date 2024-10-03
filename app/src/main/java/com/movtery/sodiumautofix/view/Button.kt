package com.movtery.sodiumautofix.view

import android.animation.AnimatorInflater
import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import androidx.core.content.res.ResourcesCompat
import com.movtery.sodiumautofix.R

class Button @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.buttonStyle
) : Button(context, attrs, defStyleAttr) {
    init {
        background = ResourcesCompat.getDrawable(resources, R.drawable.button_background, context.theme)
        stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.xml.anim_scale)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        post {
            pivotX = width / 2f
            pivotY = height / 2f
        }
    }
}