package com.movtery.anim.animations

import com.movtery.anim.animations.bounce.*
import com.movtery.anim.animations.fade.*

enum class Animations(val animator: BaseAnimator) {
    BounceEnlarge(BounceEnlargeAnimator()),
    BounceShrink(BounceShrinkAnimator()),
    FadeIn(FadeInAnimator()),
    FadeOut(FadeOutAnimator())
}