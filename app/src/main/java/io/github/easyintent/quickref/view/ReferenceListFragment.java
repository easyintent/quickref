package io.github.easyintent.quickref.view;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.IgnoreWhen;
import org.androidannotations.annotations.UiThread;
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
import io.github.easyintent.quickref.QuickRefActivity;
import io.github.easyintent.quickref.R;
import io.github.easyintent.quickref.adapter.ReferenceItemAdapter;
import io.github.easyintent.quickref.config.FavoriteConfig;
import io.github.easyintent.quickref.databinding.FragmentReferenceListBinding;
import io.github.easyintent.quickref.model.ReferenceItem;
import io.github.easyintent.quickref.util.ReferenceListSelection;
import io.github.easyintent.quickref.viewmodel.ReferenceListViewModel;

import static io.github.easyintent.quickref.view.Dialog.info;


@EFragment
public class ReferenceListFragment extends Fragment
        implements
        AdapterListener<ReferenceItem>,
            ClosableFragment {

    private static final Logger logger = LoggerFactory.getLogger(ReferenceListFragment.class);

    @FragmentArg protected String parentId;
    @FragmentArg protected String query;
    @FragmentArg protected boolean searchMode;

    private ReferenceItemAdapter adapter;
    private ActionMode selectionActionMode;

    private FragmentReferenceListBinding binding;
    private ReferenceListViewModel viewModel;

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

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentReferenceListBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ReferenceListViewModel.class);
    }

    @AfterViews
    protected void configureViews() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setListShown(false);

        viewModel.getListLiveData().observe(getViewLifecycleOwner(), this::showList);

        if (searchMode) {
            viewModel.search(query);
        } else {
            viewModel.loadCategory(parentId);
        }
    }

    @Override
    public boolean allowBack() {
        return adapter == null || !adapter.isSelectionMode();
    }

    @UiThread
    @IgnoreWhen(IgnoreWhen.State.DETACHED)
    protected void showError(String message) {
        info(getParentFragmentManager(), "load_list_error", message);
    }

    @UiThread
    @IgnoreWhen(IgnoreWhen.State.VIEW_DESTROYED)
    protected void showList(List<ReferenceItem> list) {
        adapter = new ReferenceItemAdapter(list, this);
        binding.recyclerView.setAdapter(adapter);

        boolean hasContent = list.size() > 0;
        binding.emptyView.setVisibility(hasContent ? View.GONE : View.VISIBLE);
        binding.recyclerView.setVisibility(hasContent ? View.VISIBLE : View.GONE);

        setListShown(true);
    }

    private void showItem(ReferenceItem referenceItem) {
        if (referenceItem.hasChildren()) {
            showList(referenceItem);
        } else {
            showDetail(referenceItem);
        }
    }

    private void showDetail(ReferenceItem referenceItem) {
        // nothing to do
    }

    private void showList(ReferenceItem referenceItem) {
        String title = referenceItem.getTitle();
        String id = referenceItem.getId();
        Intent intent = QuickRefActivity.newListIntent(getContext(), title, id);
        startActivity(intent);
    }

    @Override
    public void onItemTap(ReferenceItem referenceItem, int index) {
        if (referenceItem != null) {
            showItem(referenceItem);
        }
    }

    @Override
    public void onMultiSelectionStart() {
        ((AppCompatActivity) requireActivity()).startSupportActionMode(new SelectorCallback());
        adapter.startSelectionMode();
    }

    @Override
    public void onSelectedItemsChanged() {
        selectionActionMode.setTitle(String.valueOf(adapter.getSelectedItemCount()));
    }

    private void setListShown(boolean shown) {
        binding.switcher.setDisplayedChild(shown ? 0 : 1);
    }

    private class SelectorCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            requireActivity().getMenuInflater().inflate(R.menu.fragment_reference_select, menu);
            selectionActionMode = actionMode;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }


        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            if (adapter != null) {
                adapter.endSelectionMode();
            }
            selectionActionMode = null;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.add_favorite) {
                addSelectedItemsToFavorites();
            }
            mode.finish();
            return true;
        }

        private void addSelectedItemsToFavorites() {
            if (adapter != null) {
                List<String> favorites = ReferenceListSelection.getSelectedIds(adapter.getSelectedItems());

                FavoriteConfig favoriteConfig = new FavoriteConfig(getActivity());
                favoriteConfig.add(favorites);
                Snackbar.make(binding.getRoot(), R.string.msg_favorite_saved, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

}
