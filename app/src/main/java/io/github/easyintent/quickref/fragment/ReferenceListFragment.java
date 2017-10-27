package io.github.easyintent.quickref.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.IgnoreWhen;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import io.github.easyintent.quickref.QuickRefActivity;
import io.github.easyintent.quickref.R;
import io.github.easyintent.quickref.data.ReferenceItem;
import io.github.easyintent.quickref.repository.ReferenceRepository;
import io.github.easyintent.quickref.repository.RepositoryException;
import io.github.easyintent.quickref.repository.RepositoryFactory;

import static io.github.easyintent.quickref.fragment.Dialog.info;


@EFragment(R.layout.fragment_reference_list)
public class ReferenceListFragment extends Fragment
        implements OnItemTapListener<ReferenceItem> {

    private static final Logger logger = LoggerFactory.getLogger(ReferenceListFragment.class);

    @FragmentArg
    protected String parentId;

    @FragmentArg
    protected String query;

    @FragmentArg
    protected boolean searchMode;

    @ViewById
    protected RecyclerView recyclerView;

    @ViewById
    protected View emptyView;

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
        factory = RepositoryFactory.newInstance(getActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (list == null) {
            load();
        } else {
            show(list);
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
        final ReferenceRecyclerAdapter adapter = new ReferenceRecyclerAdapter(list, this);
        recyclerView.setAdapter(adapter);
        emptyView.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);

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

    @Override
    public void onItemTap(ReferenceItem referenceItem, int index) {
        if (referenceItem != null) {
            showItem(referenceItem);
        }
    }

}
