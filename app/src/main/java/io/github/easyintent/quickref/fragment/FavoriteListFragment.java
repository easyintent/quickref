package io.github.easyintent.quickref.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.IgnoreWhen;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
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

@EFragment(R.layout.fragment_favorites)
public class FavoriteListFragment extends Fragment
        implements
            ClosableFragment,
            OnItemTapListener<ReferenceItem> {

    private static final Logger logger  = LoggerFactory.getLogger(FavoriteListFragment.class);

    @ViewById
    protected RecyclerView recyclerView;

    @ViewById
    protected TextView emptyView;

    @ViewById
    protected ViewSwitcher switcher;

    private RepositoryFactory factory;
    private FavoriteConfig favoriteConfig;
    private List<ReferenceItem> list;

    private MultiSelector selector;
    private SelectorCallback selectorCallback;

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
        selector = new MultiSelector();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getString(R.string.lbl_favorites));

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        factory = RepositoryFactory.newInstance(getActivity());
        favoriteConfig = new FavoriteConfig(getActivity());
        selectorCallback = new SelectorCallback(selector);

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
    @IgnoreWhen(IgnoreWhen.State.DETACHED)
    protected void onLoadDone(boolean success, List<ReferenceItem> newList, String message) {
        list = newList;
        show(newList);
        if (!success) {
            Dialog.info(getFragmentManager(), "favorite_error", message);
        }
    }

    protected void show(List<ReferenceItem> list) {
        final ReferenceRecyclerAdapter adapter = new ReferenceRecyclerAdapter(list, selector, this);
        recyclerView.setAdapter(adapter);
        emptyView.setVisibility(list.size() > 0 ? View.GONE : View.VISIBLE);
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

    private void setListShown(boolean shown) {
        switcher.setDisplayedChild(shown ? 0 : 1);
    }

    @Override
    public boolean allowBack() {
        if (selector.isSelectable()) {
            selector.clearSelections();
            selector.setSelectable(false);
            return false;
        }
        return true;
    }

    @Override
    public void onItemTap(ReferenceItem item, int index) {
        if (item != null) {
            showItem(item);
        }
    }

    @Override
    public void onMultiSelectorStart() {
        ((AppCompatActivity) getActivity()).startSupportActionMode(selectorCallback);
    }

    private void deleteFromFavorites() {
        int n = list.size();
        List<String> favorites = new ArrayList<>();
        for (int i=0; i<n; i++) {
            if (selector.isSelected(i, 0)) {
                favorites.add(list.get(i).getId());
            }
        }
        favoriteConfig.delete(favorites);
        reload();

        Toast.makeText(getActivity(), R.string.msg_favorite_removed, Toast.LENGTH_SHORT).show();
    }

    private class SelectorCallback extends ModalMultiSelectorCallback {

        public SelectorCallback(MultiSelector multiSelector) {
            super(multiSelector);
        }

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            super.onCreateActionMode(actionMode, menu);
            getActivity().getMenuInflater().inflate(R.menu.fragment_favorite_select, menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            mode.finish();
            switch (item.getItemId()) {
                case R.id.delete_favorite:
                    deleteFromFavorites();
                    break;
            }
            selector.clearSelections();
            return true;
        }
    }

}
