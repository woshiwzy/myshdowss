package com.common.util;

import android.view.View;
import android.view.animation.Animation;

import com.common.adapter.AnimationListenerAdapter;

/**
 * Created by wangzy on 15/6/5.
 */
public class MyAnimationUtils {


    public static void animationShowView(final View view, final Animation showAnimation) {

        if (view.getVisibility() == View.VISIBLE) {
            return;
        }

        if (null == showAnimation) {
            view.setVisibility(View.VISIBLE);
        }

        showAnimation.setAnimationListener(new AnimationListenerAdapter() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }
        });
        view.startAnimation(showAnimation);
    }

    public static void animationHideview(final View view, final Animation showAnimation) {
        if (view.getVisibility() != View.VISIBLE) {
            return;
        }
        if (null == showAnimation) {
            view.setVisibility(View.GONE);
        }

        showAnimation.setAnimationListener(new AnimationListenerAdapter() {
            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }
        });
        view.startAnimation(showAnimation);
    }


}
