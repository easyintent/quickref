package io.github.easyintent.quickref;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import io.github.easyintent.quickref.databinding.ActivityQuickRefBinding;
import io.github.easyintent.quickref.view.MessageDialogFragment;
import io.github.easyintent.quickref.view.ReferenceListFragment;

@EActivity
public class QuickRefActivity extends AppCompatActivity
        implements MessageDialogFragment.Listener {

    @Extra protected String title;
    @Extra protected String parentId;
    @Extra protected String query;

    private ActivityQuickRefBinding binding;
    private ReferenceListFragment fragment;

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

        binding = ActivityQuickRefBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(title);
        initFragment();
    }

    @Override
    public void onBackPressed() {
        if (fragment.allowBack()) {
            finish();
        }
    }

    @OptionsItem(android.R.id.home)
    protected void upClicked() {
        finish();
    }

    private void initFragment() {

        FragmentManager manager = getSupportFragmentManager();
        fragment = (ReferenceListFragment) manager.findFragmentByTag("reference_list");
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
