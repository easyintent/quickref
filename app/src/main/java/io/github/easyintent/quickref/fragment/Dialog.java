package io.github.easyintent.quickref.fragment;


import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

public final class Dialog {

    public static final void info(AppCompatActivity activity, String tag, String message) {
        info(activity.getSupportFragmentManager(), tag, message);
    }

    public static final void info(FragmentManager manager, String tag, String message) {
        MessageDialogFragment dialogFragment = MessageDialogFragment.newInstance(message);
        dialogFragment.show(manager, tag);
    }

}
