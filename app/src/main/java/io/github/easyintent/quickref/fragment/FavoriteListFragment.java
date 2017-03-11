package io.github.easyintent.quickref.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.easyintent.quickref.QuickRefActivity;
import io.github.easyintent.quickref.R;
import io.github.easyintent.quickref.config.FavoriteConfig;
import io.github.easyintent.quickref.data.ReferenceItem;
import io.github.easyintent.quickref.repository.ReferenceRepository;
import io.github.easyintent.quickref.repository.RepositoryException;
import io.github.easyintent.quickref.repository.RepositoryFactory;

@EFragment
public class FavoriteListFragment extends ListFragment {

    private static final Logger logger  = LoggerFactory.getLogger(FavoriteListFragment.class);

    private RepositoryFactory factory;
    private FavoriteConfig favoriteConfig;

    public static FavoriteListFragment newInstance() {
        Bundle args = new Bundle();
        FavoriteListFragment fragment = new FavoriteListFragmentEx();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText(getString(R.string.msg_favorite_help));
        getActivity().setTitle(getString(R.string.lbl_favorites));

        factory = RepositoryFactory.newInstance(getActivity());
        favoriteConfig = new FavoriteConfig(getActivity());

    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Background
    protected void loadList(RepositoryFactory factory, FavoriteConfig favoriteConfig) {
        List<String> ids = favoriteConfig.list();
        ReferenceRepository repo = factory.createCategoryRepository();
        try {
            List<ReferenceItem> newData = repo.listByIds(ids);
            onLoadDone(true, newData, null);
        } catch (RepositoryException e) {
            logger.debug("Failed to get list", e);
            onLoadDone(false, Collections.<ReferenceItem>emptyList(), e.getMessage());
        }
    }

    @UiThread
    protected void onLoadDone(boolean success, List<ReferenceItem> newList, String message) {
        if (!isAdded()) {
            return;
        }

        show(newList);
        if (!success) {
            Dialog.info(getFragmentManager(), "favorite_error", message);
        }
    }

    protected void show(List<ReferenceItem> list) {
        final ArrayAdapter<ReferenceItem> adapter = new ReferenceAdapter(getActivity(), list);
        setListAdapter(adapter);

        ListView listView = getListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ReferenceItem item = adapter.getItem(i);
                if (item != null) {
                    showItem(item);
                }
            }
        });

        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new MultiModeCallback(list));

        setListShown(true);
    }

    private void showItem(ReferenceItem item) {
        if (item.hasChildren()) {
            String title = item.getTitle();
            String id = item.getId();
            Intent intent = QuickRefActivity.newListIntent(getContext(), title, id);
            startActivity(intent);
        }
    }

    private void reload() {
        setListShown(false);
        loadList(factory, favoriteConfig);
    }

    private class MultiModeCallback implements ListView.MultiChoiceModeListener {

        private List<ReferenceItem> list;

        public MultiModeCallback(List<ReferenceItem> list) {
            this.list = list;
        }

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.fragment_favorite_select, menu);
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete_favorite:
                    deleteFavorites(mode);
                    break;
            }
            return true;
        }

        private void deleteFavorites(ActionMode mode) {
            SparseBooleanArray positions = getListView().getCheckedItemPositions();
            List<String> favorites = new ArrayList<>();
            int n = positions.size();
            for (int i=0; i<n; i++) {
                String id = list.get(positions.keyAt(i)).getId();
                favorites.add(id);
            }
            favoriteConfig.delete(favorites);
            mode.finish();
            reload();
        }

        public void onDestroyActionMode(ActionMode mode) {
        }

        public void onItemCheckedStateChanged(ActionMode mode,  int position, long id, boolean checked) {
            int n = getListView().getCheckedItemCount();
            mode.setTitle(String.valueOf(n));
        }
    }

}
