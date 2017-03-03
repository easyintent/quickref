package io.github.easyintent.quickref.fragment;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import io.github.easyintent.quickref.R;

@EFragment(R.layout.fragment_about)
public class AboutFragment extends Fragment {

    @ViewById
    protected TextView versionView;

    public static AboutFragment newInstance() {
        Bundle args = new Bundle();
        AboutFragment fragment = new AboutFragmentEx();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.lbl_about);
    }

    @AfterViews
    protected void afterViews() {
        showVersion(getActivity());
    }

    private void showVersion(Context context) {
        String version;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = "?";
        }
        versionView.setText(getString(R.string.msg_about_version, version));
    }
}
