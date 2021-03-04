package io.github.easyintent.quickref.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

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
import io.github.easyintent.quickref.adapter.AdapterListener;
import io.github.easyintent.quickref.adapter.ReferenceItemAdapter;
import io.github.easyintent.quickref.databinding.FragmentReferenceListBinding;
import io.github.easyintent.quickref.model.ReferenceItem;
import io.github.easyintent.quickref.model.ReferenceListData;
import io.github.easyintent.quickref.util.ReferenceListSelection;
import io.github.easyintent.quickref.viewmodel.FavoriteListViewModel;

public class FavoriteListFragment extends Fragment
        implements
            ClosableFragment,
            AdapterListener<ReferenceItem> {

    private static final Logger logger  = LoggerFactory.getLogger(FavoriteListFragment.class);

    private ReferenceItemAdapter adapter;
    private ActionMode selectionMode;

    private FavoriteListViewModel viewModel;
    private FragmentReferenceListBinding binding;

    public static FavoriteListFragment newInstance() {
        Bundle args = new Bundle();
        FavoriteListFragment fragment = new FavoriteListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

        requireActivity().setTitle(getString(R.string.lbl_favorites));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        viewModel = new ViewModelProvider(this).get(FavoriteListViewModel.class);

        viewModel.getLiveData().observe(getViewLifecycleOwner(), this::showData);
        viewModel.refresh();
    }

    private void showData(ReferenceListData data) {
        if (data.hasList()) {
            showList(data.getList());
        } else {
            showError(data.getMessage());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    private void showError(String message) {
        Dialog.info(getParentFragmentManager(), "favorite_error", message);
    }

    protected void showList(List<ReferenceItem> list) {
        adapter = new ReferenceItemAdapter(list, this);
        binding.recyclerView.setAdapter(adapter);

        boolean hasContent = list.size() > 0;
        binding.emptyView.setVisibility(hasContent ? View.GONE : View.VISIBLE);
        binding.recyclerView.setVisibility(hasContent ? View.VISIBLE : View.GONE);

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
        binding.switcher.setDisplayedChild(shown ? 0 : 1);
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
            if (item.getItemId() == R.id.delete_favorite) {
                deleteFromFavorites();
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

                Snackbar.make(binding.switcher, R.string.msg_favorite_removed, Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}
