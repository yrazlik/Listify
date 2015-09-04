package com.yrazlik.listify;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

/**
 * Created by yrazlik on 9/4/15.
 */
public class Utils {

    public static void playDialogAnimation(final Context context, final View dialog){
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.anim_in);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Animation fadeOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
                        fadeOut.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                dialog.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        dialog.setAnimation(fadeOut);
                        dialog.startAnimation(fadeOut);
                    }
                }, 2000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        dialog.setAnimation(anim);
        dialog.setVisibility(View.VISIBLE);
        dialog.startAnimation(anim);
    }

    public static void showSuccesfulDialog(ViewGroup parent, final Context context){
        LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout dialog = (LinearLayout)inflater.inflate(R.layout.dialog_succesful, null);
        parent.addView(dialog);

        Animation anim = AnimationUtils.loadAnimation(context, R.anim.anim_in);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Animation fadeOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
                        fadeOut.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                dialog.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        dialog.setAnimation(fadeOut);
                        dialog.startAnimation(fadeOut);
                    }
                }, 2000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        dialog.setAnimation(anim);
        dialog.setVisibility(View.VISIBLE);
        dialog.startAnimation(anim);
    }
}
