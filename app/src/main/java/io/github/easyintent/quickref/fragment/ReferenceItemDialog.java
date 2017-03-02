package io.github.easyintent.quickref.fragment;

import android.app.*;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

import io.github.easyintent.quickref.R;
import io.github.easyintent.quickref.config.BookmarkConfig;

@EFragment
public class ReferenceItemDialog extends DialogFragment {

    @FragmentArg
    protected String id;

    public static ReferenceItemDialog newInstance(String id) {
        Bundle args = new Bundle();
        args.putString("id", id);
        ReferenceItemDialog fragment = new ReferenceItemDialogEx();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setItems(R.array.reference_option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0: // add to bookmark
                                addToBookmark();
                                break;
                            case 1: // share
                                break;
                        }
                    }
                })
                .create();
    }

    private void addToBookmark() {
        BookmarkConfig config = new BookmarkConfig(getActivity());
        config.add(id);
    }
}
