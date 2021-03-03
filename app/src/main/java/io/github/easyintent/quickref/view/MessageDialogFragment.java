package io.github.easyintent.quickref.view;


import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import io.github.easyintent.quickref.R;

public class MessageDialogFragment extends DialogFragment {

    public interface Listener {
        void onOkClicked(MessageDialogFragment dialogFragment);
    }

    private String message;
    private Listener listener;

    public static MessageDialogFragment newInstance(String message) {
        MessageDialogFragment fragment = new MessageDialogFragment();
        Bundle args = new Bundle();
        args.putString("message", message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        message = getArguments().getString("message");
        listener = (Listener) getActivity();
        setCancelable(false);
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.lbl_info)
                .setMessage(message)
                .setPositiveButton(R.string.lbl_ok, (dialogInterface, i) -> {
                    listener.onOkClicked(MessageDialogFragment.this);
                    dialogInterface.dismiss();
                })
                .create();
    }

}
