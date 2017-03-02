package io.github.easyintent.quickref.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import io.github.easyintent.quickref.QuickRefActivity;
import io.github.easyintent.quickref.data.ReferenceItem;
import io.github.easyintent.quickref.repository.ReferenceRepository;
import io.github.easyintent.quickref.repository.RepositoryException;
import io.github.easyintent.quickref.repository.RepositoryFactory;

import static io.github.easyintent.quickref.fragment.Dialog.info;


@EFragment
public class ReferenceListFragment extends ListFragment {

    private static final Logger logger = LoggerFactory.getLogger(ReferenceListFragment.class);


    @FragmentArg
    protected String category;
    private List<ReferenceItem> list;

    /** Create category list.
     *
     * @param category
     *      Category category.
     * @return
     */
    @NonNull
    public static ReferenceListFragment newInstance(@Nullable String category) {
        ReferenceListFragment fragment = new ReferenceListFragmentEx();
        Bundle args = new Bundle();
        args.putString("category", category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (list == null) {
            load(getActivity(), category);
        } else {
            show(list);
        }
    }

    @Background
    protected void load(Context context, String category) {
        RepositoryFactory factory = RepositoryFactory.newInstance(context);
        ReferenceRepository repo = factory.createCategoryRepository();
        try {
            list = repo.list(category);
            onLoadDone(true, list, null);
        } catch (RepositoryException e) {
            logger.debug("Failed to get category list", e);
            onLoadDone(false, null, e.getMessage());
        }
    }

    @UiThread
    protected void onLoadDone(boolean success, List<ReferenceItem> newList, String message) {
        if (!isAdded()) {
            return;
        }

        if (!success) {
            info((AppCompatActivity) getActivity(), "load_category_err", message);
            return;
        }

        show(newList);
    }

    private void show(List<ReferenceItem> list) {
        final ReferenceAdapter adapter = new ReferenceAdapter(getContext(), list);
        setListAdapter(adapter);

        // add listener explicitly to list view
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ReferenceItem referenceItem = adapter.getItem(i);
                if (referenceItem != null) {
                    showItem(referenceItem);
                }
            }

        });
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
        Intent intent = QuickRefActivity.newIntent(getContext(), referenceItem.getChildren());
        startActivity(intent);
    }

}
