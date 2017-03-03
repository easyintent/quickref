package io.github.easyintent.quickref;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

import io.github.easyintent.quickref.fragment.MessageDialogFragment;
import io.github.easyintent.quickref.fragment.ReferenceListFragment;

@EActivity
public class QuickRefActivity extends AppCompatActivity
        implements MessageDialogFragment.Listener {

    @Extra
    protected String title;

    @Extra
    protected String category;

    @Extra
    protected String query;

    /** Create new reference list intent.
     *
     * @param context
     *      The activity context.
     * @param category
     *      The category category, if null it is root category.
     * @param title
     *      Title of this reference
     * @return
     */
    @NonNull
    public static Intent newListIntent(Context context, String title, String category) {
        Intent intent = new Intent(Intents.ACTION_LIST);
        intent.putExtra("category", category);
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
    public static Intent newSearchIntent(Context context, String query) {
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(title);
        initFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initFragment() {

        FragmentManager manager = getSupportFragmentManager();
        ReferenceListFragment fragment = (ReferenceListFragment) manager.findFragmentByTag("reference_list");
        if (fragment != null) {
            return;
        }

        boolean searchMode = Intents.ACTION_SEARCH.equals(getIntent().getAction());

        if (searchMode) {
            fragment = ReferenceListFragment.newSearchInstance(query);
        } else {
            fragment = ReferenceListFragment.newListCategoryInstance(category);
        }

        manager.beginTransaction()
                .replace(R.id.content_frame, fragment, "reference_list")
                .commit();
    }

    @Override
    public void onOkClicked(MessageDialogFragment dialogFragment) {
        finish();
    }

}
