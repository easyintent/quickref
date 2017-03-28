package io.github.easyintent.quickref.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.IgnoreWhen;
import org.androidannotations.annotations.UiThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import io.github.easyintent.quickref.QuickRefActivity;
import io.github.easyintent.quickref.R;
import io.github.easyintent.quickref.config.FavoriteConfig;
import io.github.easyintent.quickref.data.ReferenceItem;
import io.github.easyintent.quickref.repository.ReferenceRepository;
import io.github.easyintent.quickref.repository.RepositoryException;
import io.github.easyintent.quickref.repository.RepositoryFactory;
import io.github.easyintent.quickref.util.ReferenceListSelection;

import static io.github.easyintent.quickref.fragment.Dialog.info;


@EFragment
public class ReferenceListFragment extends ListFragment {

    private static final Logger logger = LoggerFactory.getLogger(ReferenceListFragment.class);

    @FragmentArg
    protected String parentId;

    @FragmentArg
    protected String query;

    @FragmentArg
    protected boolean searchMode;

    private RepositoryFactory factory;

    private List<ReferenceItem> list;

    /** Create list of reference fragment.
     *
     * @param parentId
     *      Parent item id, or null for top level list.
     * @return
     */
    @NonNull
    public static ReferenceListFragment newListChildrenInstance(@Nullable String parentId) {
        ReferenceListFragment fragment = new ReferenceListFragmentEx();
        Bundle args = new Bundle();
        args.putString("parentId", parentId);
        fragment.setArguments(args);
        return fragment;
    }

    /** Create search fragment.
     *
     * @param query
     *      The search query.
     * @return
     */
    @NonNull
    public static ReferenceListFragment newSearchInstance(@NonNull String query) {
        ReferenceListFragment fragment = new ReferenceListFragmentEx();
        Bundle args = new Bundle();
        args.putString("query", query);
        args.putBoolean("searchMode", true);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText(getString(R.string.msg_empty_ref));
        factory = RepositoryFactory.newInstance(getActivity());
        if (list == null) {
            load();
        } else {
            show(list);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ReferenceItem referenceItem = (ReferenceItem) l.getItemAtPosition(position);
        if (referenceItem != null) {
            showItem(referenceItem);
        }
    }

    private void load() {
        if (searchMode) {
            search(factory, query);
        } else {
            loadCategory(factory, parentId);
        }
    }

    @Background
    protected void search(RepositoryFactory factory, String query) {
        ReferenceRepository repo = factory.createCategoryRepository();
        try {
            list = repo.search(query);
            onLoadDone(true, list, null);
        } catch (RepositoryException e) {
            logger.debug("Failed to search reference", e);
            onLoadDone(false, null, e.getMessage());
        }
    }

    @Background
    protected void loadCategory(RepositoryFactory factory, String parentId) {
        ReferenceRepository repo = factory.createCategoryRepository();
        try {
            list = repo.list(parentId);
            onLoadDone(true, list, null);
        } catch (RepositoryException e) {
            logger.debug("Failed to get reference list", e);
            onLoadDone(false, null, e.getMessage());
        }
    }

    @UiThread
    @IgnoreWhen(IgnoreWhen.State.DETACHED)
    protected void onLoadDone(boolean success, List<ReferenceItem> newList, String message) {
        if (!success) {
            info((AppCompatActivity) getActivity(), "load_list_error", message);
            return;
        }
        show(newList);
    }

    private void show(List<ReferenceItem> list) {
        final ReferenceAdapter adapter = new ReferenceAdapter(getContext(), list);
        setListAdapter(adapter);

        ListView listView = getListView();
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new MultiModeCallback());
        listView.setFocusable(false);

    }

    private void showItem(ReferenceItem referenceItem) {
        if (referenceItem.hasChildren()) {
            showList(referenceItem);
        } else {
            showDetail(referenceItem);
        }
    }

    private void showDetail(ReferenceItem referenceItem) {
    }

    private void showList(ReferenceItem referenceItem) {
        String title = referenceItem.getTitle();
        String id = referenceItem.getId();
        Intent intent = QuickRefActivity.newListIntent(getContext(), title, id);
        startActivity(intent);
    }

    private class MultiModeCallback implements ListView.MultiChoiceModeListener {

        private FavoriteConfig favoriteConfig;

        public MultiModeCallback() {
            favoriteConfig = new FavoriteConfig(getActivity());
        }

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.fragment_reference_select, menu);
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.add_favorite:
                    saveFavorites(mode);
                    break;
            }
            return true;
        }

        private void saveFavorites(ActionMode mode) {
            SparseBooleanArray positions = getListView().getCheckedItemPositions();
            List<String> favorites = ReferenceListSelection.getChecked(list, positions);
            favoriteConfig.add(favorites);
            Toast.makeText(getActivity(), R.string.msg_favorite_saved, Toast.LENGTH_SHORT).show();
            mode.finish();
        }

        public void onDestroyActionMode(ActionMode mode) {
        }

        public void onItemCheckedStateChanged(ActionMode mode,  int position, long id, boolean checked) {
            int n = getListView().getCheckedItemCount();
            mode.setTitle(String.valueOf(n));
        }
    }
}
