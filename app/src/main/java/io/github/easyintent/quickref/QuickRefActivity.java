package io.github.easyintent.quickref;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;

import io.github.easyintent.quickref.fragment.MessageDialogFragment;
import io.github.easyintent.quickref.fragment.ReferenceListFragment;

@EActivity
public class QuickRefActivity extends AppCompatActivity
        implements MessageDialogFragment.Listener {

    @Extra
    protected String title;

    @Extra
    protected String parentId;

    @Extra
    protected String query;

    private Toolbar toolbar;

    /** Create new reference list intent.
     *
     * @param context
     *      The activity context.
     * @param parentId
     *      Parent item id, or null for top level list.
     * @param title
     *      Title of this reference
     * @return
     */
    @NonNull
    public static Intent newListIntent(@NonNull Context context, @NonNull String title, @Nullable String parentId) {
        Intent intent = new Intent(Intents.ACTION_LIST);
        intent.putExtra("parentId", parentId);
        intent.putExtra("title", title);
        intent.setClass(context, QuickRefActivityEx.class);
        return intent;
    }

    /** Create intent for searching query.
     *
     * @param context
     * @param query
     * @return
     */
    @NonNull
    public static Intent newSearchIntent(@NonNull Context context, @NonNull String query) {
        Intent intent = new Intent(Intents.ACTION_SEARCH);
        intent.putExtra("query", query);
        intent.putExtra("title", context.getString(R.string.lbl_search_title, query));
        intent.setClass(context, QuickRefActivityEx.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_ref);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(title);
        initFragment();
    }

    @OptionsItem(android.R.id.home)
    protected void upClicked() {
        finish();
    }

    private void initFragment() {

        FragmentManager manager = getSupportFragmentManager();
        ReferenceListFragment fragment = (ReferenceListFragment) manager.findFragmentByTag("reference_list");
        if (fragment != null) {
            return;
        }

        fragment = createReferenceListFragment();
        manager.beginTransaction()
                .replace(R.id.content_frame, fragment, "reference_list")
                .commit();
    }

    @NonNull
    private ReferenceListFragment createReferenceListFragment() {
        boolean searchMode = Intents.ACTION_SEARCH.equals(getIntent().getAction());
        if (searchMode) {
            return ReferenceListFragment.newSearchInstance(query);
        }
        return ReferenceListFragment.newListChildrenInstance(parentId);
    }

    @Override
    public void onOkClicked(MessageDialogFragment dialogFragment) {
        finish();
    }

}
