package com.moneytracker.app.util;

import android.animation.ValueAnimator;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

/**
 * Small, reusable motion touches shared across screens so the app feels consistently
 * "alive" — unless the user turned animations off in Settings (weaker phones), in
 * which case every method here just snaps straight to the end state instead.
 */
public class ViewAnimations {

    private ViewAnimations() {
    }

    /** Scales a view down on press and springs it back on release, like a physical button. */
    public static void addPressFeedback(View... views) {
        if (views.length == 0 || !animationsEnabled(views[0])) {
            return;
        }
        View.OnTouchListener listener = (v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.94f).scaleY(0.94f)
                            .setDuration(120)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f)
                            .setDuration(180)
                            .setInterpolator(new OvershootInterpolator(1.4f))
                            .start();
                    break;
                default:
                    break;
            }
            return false;
        };
        for (View view : views) {
            if (view != null) {
                view.setOnTouchListener(listener);
            }
        }
    }

    /** Fades + lifts a screen's root content in; call once per fragment view creation. */
    public static void fadeInContent(View root) {
        if (!animationsEnabled(root)) {
            root.setAlpha(1f);
            root.setTranslationY(0f);
            return;
        }
        root.setAlpha(0f);
        root.setTranslationY(24f);
        root.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(40)
                .setDuration(260)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    /**
     * Fades + lifts a RecyclerView row in the first time it's bound (position beyond the
     * highest position already animated), staggered slightly by position. Subsequent rebinds
     * during scroll are left untouched so the effect only plays once per item.
     */
    public static void animateItemEntranceOnce(View itemView, int position, int[] highestAnimatedPosition) {
        if (position <= highestAnimatedPosition[0] || !animationsEnabled(itemView)) {
            itemView.setAlpha(1f);
            itemView.setTranslationY(0f);
            highestAnimatedPosition[0] = Math.max(highestAnimatedPosition[0], position);
            return;
        }
        highestAnimatedPosition[0] = position;
        itemView.setAlpha(0f);
        itemView.setTranslationY(40f);
        itemView.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(Math.min(position, 8) * 35L)
                .setDuration(260)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    /**
     * Progressively masks the TextView's current text left-to-right with {@code •}
     * (spaces are kept as visual anchors), then snaps to {@code finalMask} — used when
     * hiding balances so the digits visibly dissolve instead of vanishing at once.
     * Cancels cleanly if the view detaches mid-animation.
     */
    public static void animateTextMasking(TextView textView, String finalMask) {
        CharSequence current = textView.getText();
        if (!animationsEnabled(textView) || current == null || current.length() == 0) {
            textView.setText(finalMask);
            return;
        }
        String original = current.toString();
        int length = original.length();
        ValueAnimator animator = ValueAnimator.ofInt(0, length);
        animator.setDuration(450);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            if (!textView.isAttachedToWindow()) {
                animation.cancel();
                return;
            }
            int maskedCount = (int) animation.getAnimatedValue();
            if (maskedCount >= length) {
                textView.setText(finalMask);
                return;
            }
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                char c = original.charAt(i);
                sb.append(i < maskedCount && c != ' ' && c != '\u00A0' ? '•' : c);
            }
            textView.setText(sb);
        });
        animator.start();
    }

    private static boolean animationsEnabled(View view) {
        return SettingsPrefs.isAnimationsEnabled(view.getContext());
    }
}
