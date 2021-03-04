package io.github.easyintent.quickref.view;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public final class Dialog {

    public static void info(AppCompatActivity activity, String tag, String message) {
        info(activity.getSupportFragmentManager(), tag, message);
    }

    public static void info(Fragment fragment, String tag, String message) {
        info(fragment.getParentFragmentManager(), tag, message);
    }

    public static void info(FragmentManager manager, String tag, String message) {
        MessageDialogFragment dialogFragment = MessageDialogFragment.newInstance(message);
        dialogFragment.show(manager, tag);
    }

}
