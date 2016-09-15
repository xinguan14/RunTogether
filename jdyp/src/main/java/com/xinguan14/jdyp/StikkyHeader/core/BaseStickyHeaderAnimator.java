package com.xinguan14.jdyp.stikkyHeader.core;

public class BaseStickyHeaderAnimator extends HeaderAnimator {

    private float mTranslationRatio;

    @Override
    protected void onAnimatorAttached() {
        //nothing to do
    }

    @Override
    protected void onAnimatorReady() {
        //nothing to do
    }

    @Override
    public void onScroll(int scrolledY) {
        StikkyCompat.setTranslationY(mHeader, Math.max(scrolledY, getMaxTranslation()));
        mTranslationRatio = calculateTranslationRatio(scrolledY);
    }

    public float getTranslationRatio() {
        return mTranslationRatio;
    }

    private float calculateTranslationRatio(int scrolledY) {
        //TODO check divisor != 0
        return (float) scrolledY / (float) getMaxTranslation();
    }

}
