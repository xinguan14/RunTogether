package com.xinguan14.jdyp.StikkyHeader;

import android.app.Activity;
import android.view.View;

import com.xinguan14.jdyp.StikkyHeader.core.AnimatorBuilder;
import com.xinguan14.jdyp.StikkyHeader.core.HeaderStikkyAnimator;


public class AchievementsAnimator extends HeaderStikkyAnimator {

    private final int resIdLayoutToAnimate;
    private final View homeActionBar;


    public AchievementsAnimator(final Activity activity, int resIdLayoutToAnimate) {

        this.resIdLayoutToAnimate = resIdLayoutToAnimate;

        homeActionBar = activity.findViewById(android.R.id.home);

    }

    @Override
    public AnimatorBuilder getAnimatorBuilder() {

        View mViewToAnimate = getHeader().findViewById(resIdLayoutToAnimate);

        AnimatorBuilder animatorBuilder = AnimatorBuilder.create()
                .applyScale(mViewToAnimate, AnimatorBuilder.buildViewRect(homeActionBar))
                .applyTranslation(mViewToAnimate, AnimatorBuilder.buildPointView(homeActionBar));

        return animatorBuilder;
    }
}
