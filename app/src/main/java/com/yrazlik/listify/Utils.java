package com.yrazlik.listify;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

    public static void showFadeOutDialog(ViewGroup parent, final Context context, String text1, String text2, int imageId){
        LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final RelativeLayout dialog = (RelativeLayout)inflater.inflate(R.layout.dialog_succesful, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        dialog.setLayoutParams(params);
        ((TextView)dialog.findViewById(R.id.tv1)).setText(text1);
        ((TextView)dialog.findViewById(R.id.tv2)).setText(text2);
        ((ImageView)dialog.findViewById(R.id.dialogImage)).setBackgroundResource(imageId);
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
