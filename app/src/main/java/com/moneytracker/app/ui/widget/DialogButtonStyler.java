package com.moneytracker.app.ui.widget;

import androidx.appcompat.app.AlertDialog;

import com.moneytracker.app.R;

/**
 * Every DialogFragment builds with {@code MaterialAlertDialogBuilder} and
 * {@code R.style.ThemeOverlay_MoneyTracker_Dialog}, which already colors the positive
 * button via {@code colorPrimary}; this just differentiates the negative ("Cancel") and
 * neutral ("Delete") actions so a destructive action doesn't share the same accent as
 * the confirming one.
 *
 * <p>Button views don't exist until the dialog is actually shown, so callers must invoke
 * this from inside their own {@code setOnShowListener} callback - typically the same one
 * already used to override the positive button's click behavior - rather than this class
 * installing a second, mutually-clobbering listener.
 */
public class DialogButtonStyler {

    private DialogButtonStyler() {
    }

    public static void style(AlertDialog dialog) {
        android.widget.Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        if (negative != null) {
            negative.setTextColor(dialog.getContext().getColor(R.color.text_secondary));
        }
        android.widget.Button neutral = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        if (neutral != null) {
            neutral.setTextColor(dialog.getContext().getColor(R.color.expense_red));
        }
    }
}
