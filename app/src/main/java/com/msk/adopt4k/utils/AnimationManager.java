package com.msk.adopt4k.utils;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

public class AnimationManager
{
    private Animation theAnimation;

    public Animation getBlinkAnimation()
    {
        theAnimation = new AlphaAnimation(1, 0.3f);
        theAnimation.setDuration(1500);
        theAnimation.setInterpolator(new LinearInterpolator());
        theAnimation.setRepeatCount(Animation.INFINITE);
        theAnimation.setRepeatMode(Animation.REVERSE);

        return theAnimation;
    }
}
