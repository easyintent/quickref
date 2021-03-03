package io.github.easyintent.quickref.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.material.snackbar.Snackbar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.IgnoreWhen;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.github.easyintent.quickref.QuickRefActivity;
import io.github.easyintent.quickref.R;
import io.github.easyintent.quickref.adapter.ReferenceItemAdapter;
import io.github.easyintent.quickref.model.ReferenceItem;
import io.github.easyintent.quickref.util.ReferenceListSelection;
import io.github.easyintent.quickref.viewmodel.FavoriteListViewModel;

@EFragment(R.layout.fragment_favorites)
public class FavoriteListFragment extends Fragment
        implements
            ClosableFragment,
            AdapterListener<ReferenceItem> {

    private static final Logger logger  = LoggerFactory.getLogger(FavoriteListFragment.class);

    @ViewById protected RecyclerView recyclerView;
    @ViewById protected TextView emptyView;
    @ViewById protected ViewSwitcher switcher;

    private ReferenceItemAdapter adapter;
    private ActionMode selectionMode;

    private FavoriteListViewModel viewModel;

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
    public void onViewCreated(
            @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FavoriteListViewModel.class);
    }

    @AfterViews
    protected void configureViews() {
        getActivity().setTitle(getString(R.string.lbl_favorites));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        viewModel.getListLiveData().observe(this, this::showList);
        viewModel.refresh();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @UiThread
    @IgnoreWhen(IgnoreWhen.State.DETACHED)
    protected void showError(String message) {
        Dialog.info(getParentFragmentManager(), "favorite_error", message);
    }

    @UiThread
    @IgnoreWhen(IgnoreWhen.State.VIEW_DESTROYED)
    protected void showList(List<ReferenceItem> list) {
        adapter = new ReferenceItemAdapter(list, this);
        recyclerView.setAdapter(adapter);

        boolean hasContent = list.size() > 0;
        emptyView.setVisibility(hasContent ? View.GONE : View.VISIBLE);
        recyclerView.setVisibility(hasContent ? View.VISIBLE : View.GONE);

        setListShown(true);
    }

    private void showItem(ReferenceItem item) {
        if (item.hasChildren()) {
            showChildren(item);
        }
    }

    private void showChildren(ReferenceItem item) {
        String title = item.getTitle();
        String id = item.getId();
        Intent intent = QuickRefActivity.newListIntent(getContext(), title, id);
        startActivity(intent);
    }

    private void setListShown(boolean shown) {
        switcher.setDisplayedChild(shown ? 0 : 1);
    }

    @Override
    public boolean allowBack() {
        return adapter == null || !adapter.isSelectionMode();
    }

    @Override
    public void onItemTap(ReferenceItem item, int index) {
        if (item != null) {
            showItem(item);
        }
    }

    @Override
    public void onMultiSelectionStart() {
        ((AppCompatActivity) getActivity()).startSupportActionMode(new SelectorCallback());
        adapter.startSelectionMode();
    }

    @Override
    public void onSelectedItemsChanged() {
        selectionMode.setTitle(String.valueOf(adapter.getSelectedItemCount()));
    }

    private class SelectorCallback implements ActionMode.Callback  {


        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            getActivity().getMenuInflater().inflate(R.menu.fragment_favorite_select, menu);
            selectionMode = actionMode;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete_favorite:
                    deleteFromFavorites();
                    break;
            }
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            if (adapter != null) {
                adapter.endSelectionMode();
            }
            selectionMode = null;
        }

        private void deleteFromFavorites() {
            if (adapter != null) {
                List<String> favorites = ReferenceListSelection.getSelectedIds(adapter.getSelectedItems());
                viewModel.delete(favorites);

                Snackbar.make(switcher, R.string.msg_favorite_removed, Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}
