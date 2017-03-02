package io.github.easyintent.quickref.fragment;


import android.support.v7.app.AppCompatActivity;

public final class Dialog {
    public static final void info(AppCompatActivity activity, String tag, String message) {
        MessageDialogFragment dialogFragment = MessageDialogFragment.newInstance(message);
        dialogFragment.show(activity.getSupportFragmentManager(), tag);

    }
}
